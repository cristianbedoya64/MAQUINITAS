package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRoom
import com.example.data.model.Jackpot
import com.example.data.model.User
import com.example.data.repository.FirebaseRepository
import com.example.ui.components.RoyalTopBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ArcadeGamesScreen
import com.example.ui.screens.BingoRoomScreen
import com.example.ui.screens.LobbyScreen
import com.example.ui.screens.MoreGamesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalArcadeBingoTheme
import com.example.ui.theme.RoyalOnSurface
import com.example.ui.theme.RoyalSurface
import com.example.ui.theme.RoyalSurfaceContainer
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

enum class ScreenTab {
  LOBBY,
  BINGO,
  ARCADE,
  MORE_GAMES,
  ADMIN,
  PROFILE
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val repository = FirebaseRepository(applicationContext)

    setContent {
      RoyalArcadeBingoTheme {
        RoyalAppRoot(repository = repository)
      }
    }
  }
}

@Composable
fun RoyalAppRoot(
  repository: FirebaseRepository
) {
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  // Auth state from Firebase Auth
  val firebaseUser by repository.authStateFlow().collectAsState(initial = repository.auth.currentUser)
  var isGuestMode by remember { mutableStateOf(false) }

  // Guest fallback user
  var guestUser by remember {
    mutableStateOf(
      User(
        id = "guest_player",
        displayName = "Rey Arcade (Invitado)",
        email = "cristianalexanderbedoyamarin8@gmail.com", // Recognized as admin
        coins = 5000,
        gems = 100,
        tickets = 15,
        role = "admin"
      )
    )
  }

  // Active firestore profile
  var firestoreUser by remember { mutableStateOf<User?>(null) }

  LaunchedEffect(firebaseUser) {
    val u = firebaseUser
    if (u != null) {
      val profile = repository.ensureUserProfile(u)
      firestoreUser = profile

      // Listen to real-time changes
      repository.observeUser(u.uid).collect { updated ->
        if (updated != null) {
          firestoreUser = updated
        }
      }
    }
  }

  val activeUser = if (firebaseUser != null) (firestoreUser ?: guestUser) else if (isGuestMode) guestUser else null

  // Navigation State
  var currentScreen by remember { mutableStateOf(ScreenTab.LOBBY) }
  var currentRoomName by remember { mutableStateOf("Royal Palace 75") }

  // Realtime game data
  val rooms by repository.observeGameRooms().collectAsState(initial = repository.getDefaultRooms())
  val jackpot by repository.observeJackpot().collectAsState(initial = Jackpot())
  val leaderboard by repository.observeLeaderboard().collectAsState(initial = emptyList())

  // Back handling
  BackHandler(enabled = currentScreen != ScreenTab.LOBBY) {
    if (currentScreen == ScreenTab.ADMIN) {
      currentScreen = ScreenTab.PROFILE
    } else {
      currentScreen = ScreenTab.LOBBY
    }
  }

  // Auth Gate
  if (activeUser == null) {
    SignInScreen(
      onSignedIn = {
        // Handled reactively
      },
      onGuestAccess = {
        isGuestMode = true
      }
    )
    return
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      RoyalTopBar(
        user = activeUser,
        onAdminClick = {
          // Admin accessible only via profile
          currentScreen = ScreenTab.PROFILE
        },
        onProfileClick = {
          currentScreen = ScreenTab.PROFILE
        }
      )
    },
    bottomBar = {
      RoyalBottomNav(
        currentScreen = currentScreen,
        onTabSelected = { tab -> currentScreen = tab }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(RoyalSurface)
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        ScreenTab.LOBBY -> {
          LobbyScreen(
            user = activeUser,
            jackpot = jackpot,
            rooms = rooms,
            leaderboard = leaderboard,
            onJoinRoom = { room ->
              currentRoomName = room.name
              currentScreen = ScreenTab.BINGO
            },
            onOpenArcade = {
              currentScreen = ScreenTab.ARCADE
            },
            onClaimDailyBonus = {
              coroutineScope.launch {
                val coinsGain = 500L
                val gemsGain = 5L
                if (firebaseUser != null) {
                  repository.adminGrantCurrency(activeUser.id, coinsGain, gemsGain, 0)
                } else {
                  guestUser = guestUser.copy(
                    coins = guestUser.coins + coinsGain,
                    gems = guestUser.gems + gemsGain
                  )
                }
                snackbarHostState.showSnackbar("🎁 ¡Bono Diario Reclamado: +500 Coins & +5 Gemas!")
              }
            }
          )
        }

        ScreenTab.BINGO -> {
          BingoRoomScreen(
            user = activeUser,
            roomName = currentRoomName,
            onBingoClaimed = { coinsWon ->
              coroutineScope.launch {
                if (firebaseUser != null) {
                  repository.recordBingoWin(activeUser.id, coinsWon)
                } else {
                  guestUser = guestUser.copy(
                    coins = guestUser.coins + coinsWon,
                    bingosWon = guestUser.bingosWon + 1,
                    gamesPlayed = guestUser.gamesPlayed + 1,
                    xp = guestUser.xp + 150
                  )
                }
                snackbarHostState.showSnackbar("👑 ¡BINGO REAL GANADO! +$coinsWon COINS")
              }
            },
            onExitRoom = {
              currentScreen = ScreenTab.LOBBY
            }
          )
        }

        ScreenTab.ARCADE -> {
          ArcadeGamesScreen(
            user = activeUser,
            onRewardWon = { coinsDelta, xpGain, desc ->
              coroutineScope.launch {
                if (firebaseUser != null) {
                  repository.updateCoinsAndXp(activeUser.id, coinsDelta, xpGain)
                } else {
                  guestUser = guestUser.copy(
                    coins = (guestUser.coins + coinsDelta).coerceAtLeast(0),
                    xp = guestUser.xp + xpGain
                  )
                }
                if (coinsDelta > 0) {
                  snackbarHostState.showSnackbar("🎰 $desc: +$coinsDelta Coins!")
                }
              }
            },
            onWheelRewardWon = { coins, gems, tickets, xp, label ->
              coroutineScope.launch {
                if (firebaseUser != null) {
                  repository.adminGrantCurrency(activeUser.id, coins, gems, tickets)
                } else {
                  guestUser = guestUser.copy(
                    coins = guestUser.coins + coins,
                    gems = guestUser.gems + gems,
                    tickets = guestUser.tickets + tickets,
                    xp = guestUser.xp + xp
                  )
                }
                snackbarHostState.showSnackbar("🎉 Ruleta: ¡$label ganado!")
              }
            }
          )
        }

        ScreenTab.MORE_GAMES -> {
          MoreGamesScreen(
            user = activeUser,
            onReward = { coinsDelta, xpGain, desc ->
              coroutineScope.launch {
                if (firebaseUser != null) {
                  repository.updateCoinsAndXp(activeUser.id, coinsDelta, xpGain)
                } else {
                  guestUser = guestUser.copy(
                    coins = (guestUser.coins + coinsDelta).coerceAtLeast(0),
                    xp = guestUser.xp + xpGain
                  )
                }
                if (coinsDelta > 0) {
                  snackbarHostState.showSnackbar("⭐ $desc: +$coinsDelta Coins!")
                }
              }
            }
          )
        }

        ScreenTab.ADMIN -> {
          AdminScreen(
            user = activeUser,
            repository = repository,
            onNavigateBack = { currentScreen = ScreenTab.PROFILE }
          )
        }

        ScreenTab.PROFILE -> {
          ProfileScreen(
            user = activeUser,
            onDauberSelected = { dauberId ->
              coroutineScope.launch {
                if (firebaseUser != null) {
                  repository.updateSelectedDauber(activeUser.id, dauberId)
                } else {
                  guestUser = guestUser.copy(selectedDauber = dauberId)
                }
                snackbarHostState.showSnackbar("Marcador actualizado a: $dauberId")
              }
            },
            onAdminClick = { currentScreen = ScreenTab.ADMIN },
            onSignOut = {
              repository.auth.signOut()
              firestoreUser = null
              isGuestMode = false
              currentScreen = ScreenTab.LOBBY
            }
          )
        }
      }
    }
  }
}

@Composable
fun RoyalBottomNav(
  currentScreen: ScreenTab,
  onTabSelected: (ScreenTab) -> Unit
) {
  NavigationBar(
    containerColor = RoyalSurfaceContainer,
    tonalElevation = 8.dp,
    modifier = Modifier
      .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
      .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
  ) {
    NavigationBarItem(
      selected = currentScreen == ScreenTab.LOBBY,
      onClick = { onTabSelected(ScreenTab.LOBBY) },
      icon = { Icon(Icons.Default.GridView, contentDescription = "Lobby") },
      label = { Text("Lobby", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
      colors = navItemColors(),
      modifier = Modifier.testTag("nav_tab_lobby")
    )

    NavigationBarItem(
      selected = currentScreen == ScreenTab.BINGO,
      onClick = { onTabSelected(ScreenTab.BINGO) },
      icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Bingo") },
      label = { Text("Bingo 75", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
      colors = navItemColors(),
      modifier = Modifier.testTag("nav_tab_bingo")
    )

    NavigationBarItem(
      selected = currentScreen == ScreenTab.ARCADE,
      onClick = { onTabSelected(ScreenTab.ARCADE) },
      icon = { Icon(Icons.Default.Casino, contentDescription = "Arcade") },
      label = { Text("Arcade JS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
      colors = navItemColors(),
      modifier = Modifier.testTag("nav_tab_arcade")
    )

    // EXTRA GAMES TAB: Cohete 1000X, Parqués, Draw Poker
    NavigationBarItem(
      selected = currentScreen == ScreenTab.MORE_GAMES,
      onClick = { onTabSelected(ScreenTab.MORE_GAMES) },
      icon = { Icon(Icons.Default.RocketLaunch, contentDescription = "Más Juegos") },
      label = { Text("Más Juegos", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
      colors = navItemColors(),
      modifier = Modifier.testTag("nav_tab_more_games")
    )

    NavigationBarItem(
      selected = currentScreen == ScreenTab.PROFILE,
      onClick = { onTabSelected(ScreenTab.PROFILE) },
      icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
      label = { Text("Perfil", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
      colors = navItemColors(),
      modifier = Modifier.testTag("nav_tab_profile")
    )
  }
}

@Composable
fun navItemColors() = NavigationBarItemDefaults.colors(
  selectedIconColor = RadiantGold,
  selectedTextColor = RadiantGold,
  unselectedIconColor = RoyalOnSurface.copy(alpha = 0.6f),
  unselectedTextColor = RoyalOnSurface.copy(alpha = 0.6f),
  indicatorColor = Color(0xFF2A1752)
)
