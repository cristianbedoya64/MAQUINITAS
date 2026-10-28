package com.example.data.model

data class User(
  val id: String = "",
  val displayName: String = "Royal Player",
  val email: String = "",
  val photoUrl: String = "",
  val coins: Long = 2500,
  val gems: Long = 50,
  val tickets: Long = 10,
  val level: Int = 1,
  val xp: Long = 0,
  val bingosWon: Int = 0,
  val gamesPlayed: Int = 0,
  val highScore: Long = 0,
  val role: String = "user", // "user" or "admin"
  val selectedDauber: String = "royal_gold",
  val unlockedDaubers: List<String> = listOf("royal_gold"),
  val lastLoginAt: Long = System.currentTimeMillis()
) {
  val isAdmin: Boolean
    get() = role.equals("admin", ignoreCase = true) || 
            email.equals("cristianalexanderbedoyamarin8@gmail.com", ignoreCase = true)
}

data class GameRoom(
  val id: String = "",
  val name: String = "",
  val ticketCost: Int = 1,
  val prizePool: Long = 5000,
  val status: String = "active",
  val activePlayers: Int = 12
)

data class Jackpot(
  val id: String = "global",
  val currentPool: Long = 1250000,
  val lastWinner: String = "LuckyKing77",
  val updatedAt: Long = System.currentTimeMillis()
)
