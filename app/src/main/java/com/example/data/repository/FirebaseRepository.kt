package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.GameRoom
import com.example.data.model.Jackpot
import com.example.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "FirebaseRepository"

class FirebaseRepository(
  private val db: FirebaseFirestore,
  val auth: FirebaseAuth = Firebase.auth
) {
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { authState ->
      trySend(authState.currentUser)
    }
    auth.addAuthStateListener(listener)
    awaitClose { auth.removeAuthStateListener(listener) }
  }

  fun observeUser(userId: String): Flow<User?> = callbackFlow {
    if (userId.isEmpty()) {
      trySend(null)
      close()
      return@callbackFlow
    }

    val docRef = db.collection("users").document(userId)
    val listener: ListenerRegistration = docRef.addSnapshotListener { snapshot, error ->
      if (error != null) {
        Log.e(TAG, "Error observing user profile $userId", error)
        return@addSnapshotListener
      }
      if (snapshot != null && snapshot.exists()) {
        val user = snapshot.toObject(User::class.java)
        trySend(user)
      } else {
        // Document does not exist yet; try creating initial profile
        trySend(null)
      }
    }
    awaitClose { listener.remove() }
  }

  suspend fun ensureUserProfile(firebaseUser: FirebaseUser): User {
    val uid = firebaseUser.uid
    val docRef = db.collection("users").document(uid)
    val snapshot = docRef.get().await()

    if (snapshot.exists()) {
      val existing = snapshot.toObject(User::class.java)
      if (existing != null) {
        // Super-admin email check: grant admin if email matches
        val isSuperAdmin = firebaseUser.email.equals("cristianalexanderbedoyamarin8@gmail.com", ignoreCase = true)
        if (isSuperAdmin && existing.role != "admin") {
          docRef.update("role", "admin").await()
          return existing.copy(role = "admin")
        }
        return existing
      }
    }

    val isSuperAdmin = firebaseUser.email.equals("cristianalexanderbedoyamarin8@gmail.com", ignoreCase = true)
    val newUser = User(
      id = uid,
      displayName = firebaseUser.displayName ?: (firebaseUser.email?.substringBefore("@") ?: "Royal Player"),
      email = firebaseUser.email ?: "",
      photoUrl = firebaseUser.photoUrl?.toString() ?: "",
      coins = 2500, // Welcome bonus
      gems = 50,
      tickets = 10,
      level = 1,
      xp = 0,
      bingosWon = 0,
      gamesPlayed = 0,
      highScore = 0,
      role = if (isSuperAdmin) "admin" else "user",
      selectedDauber = "royal_gold",
      unlockedDaubers = listOf("royal_gold"),
      lastLoginAt = System.currentTimeMillis()
    )
    docRef.set(newUser).await()
    return newUser
  }

  suspend fun updateCoinsAndXp(userId: String, coinsDelta: Long, xpDelta: Long = 0) {
    if (userId.isEmpty()) return
    val docRef = db.collection("users").document(userId)
    docRef.update(
      mapOf(
        "coins" to FieldValue.increment(coinsDelta),
        "xp" to FieldValue.increment(xpDelta)
      )
    ).await()
  }

  suspend fun recordBingoWin(userId: String, coinsWon: Long) {
    if (userId.isEmpty()) return
    val docRef = db.collection("users").document(userId)
    docRef.update(
      mapOf(
        "coins" to FieldValue.increment(coinsWon),
        "bingosWon" to FieldValue.increment(1),
        "gamesPlayed" to FieldValue.increment(1),
        "xp" to FieldValue.increment(150)
      )
    ).await()
  }

  suspend fun recordGamePlayed(userId: String) {
    if (userId.isEmpty()) return
    val docRef = db.collection("users").document(userId)
    docRef.update("gamesPlayed", FieldValue.increment(1)).await()
  }

  suspend fun adminGrantCurrency(targetUserId: String, coins: Long, gems: Long, tickets: Long) {
    val docRef = db.collection("users").document(targetUserId)
    docRef.update(
      mapOf(
        "coins" to FieldValue.increment(coins),
        "gems" to FieldValue.increment(gems),
        "tickets" to FieldValue.increment(tickets)
      )
    ).await()
  }

  suspend fun adminSetRole(targetUserId: String, newRole: String) {
    val docRef = db.collection("users").document(targetUserId)
    docRef.update("role", newRole).await()
  }

  suspend fun updateSelectedDauber(userId: String, dauberId: String) {
    val docRef = db.collection("users").document(userId)
    docRef.update("selectedDauber", dauberId).await()
  }

  fun observeGameRooms(): Flow<List<GameRoom>> = callbackFlow {
    val listener = db.collection("game_rooms")
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Error loading game rooms", error)
          return@addSnapshotListener
        }
        val rooms = snapshot?.documents?.mapNotNull { it.toObject(GameRoom::class.java) } ?: emptyList()
        if (rooms.isEmpty()) {
          // Provide default curated rooms
          trySend(getDefaultRooms())
        } else {
          trySend(rooms)
        }
      }
    awaitClose { listener.remove() }
  }

  fun observeLeaderboard(): Flow<List<User>> = callbackFlow {
    val listener = db.collection("users")
      .orderBy("coins", Query.Direction.DESCENDING)
      .limit(10)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Error loading leaderboard", error)
          return@addSnapshotListener
        }
        val users = snapshot?.documents?.mapNotNull { it.toObject(User::class.java) } ?: emptyList()
        trySend(users)
      }
    awaitClose { listener.remove() }
  }

  fun observeJackpot(): Flow<Jackpot> = callbackFlow {
    val listener = db.collection("jackpots").document("global")
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Error loading jackpot", error)
          return@addSnapshotListener
        }
        if (snapshot != null && snapshot.exists()) {
          val jp = snapshot.toObject(Jackpot::class.java) ?: Jackpot()
          trySend(jp)
        } else {
          trySend(Jackpot())
        }
      }
    awaitClose { listener.remove() }
  }

  suspend fun adminTriggerJackpotDrop(amount: Long = 100000L) {
    val docRef = db.collection("jackpots").document("global")
    docRef.set(
      Jackpot(
        currentPool = 1500000L + (10000..50000).random().toLong(),
        lastWinner = "JackpotEvent! +${amount} to players",
        updatedAt = System.currentTimeMillis()
      )
    ).await()
  }

  fun getDefaultRooms(): List<GameRoom> = listOf(
    GameRoom(
      id = "royal_palace_75",
      name = "Royal Palace 75",
      ticketCost = 1,
      prizePool = 5000L,
      status = "active",
      activePlayers = 24
    ),
    GameRoom(
      id = "neon_cyber_bingo",
      name = "Neon Cyber Blitz",
      ticketCost = 2,
      prizePool = 12000L,
      status = "active",
      activePlayers = 42
    ),
    GameRoom(
      id = "diamond_high_roller",
      name = "Diamond High Roller",
      ticketCost = 5,
      prizePool = 35000L,
      status = "active",
      activePlayers = 16
    ),
    GameRoom(
      id = "arcade_party",
      name = "Arcade Speed Bash",
      ticketCost = 1,
      prizePool = 3000L,
      status = "active",
      activePlayers = 31
    )
  )
}
