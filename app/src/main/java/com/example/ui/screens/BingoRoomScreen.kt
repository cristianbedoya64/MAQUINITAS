package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BingoBall
import com.example.data.model.BingoCard
import com.example.data.model.BingoCell
import com.example.data.model.BingoGenerator
import com.example.data.model.User
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BingoCardSurface
import com.example.ui.theme.BingoCardText
import com.example.ui.theme.BingoCellBorder
import com.example.ui.theme.BingoCellUnmarked
import com.example.ui.theme.ColumnB
import com.example.ui.theme.ColumnG
import com.example.ui.theme.ColumnI
import com.example.ui.theme.ColumnN
import com.example.ui.theme.ColumnO
import com.example.ui.theme.DeepBronze
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalOnSurface
import com.example.ui.theme.RoyalSurface
import com.example.ui.theme.RoyalSurfaceContainer
import com.example.ui.theme.RoyalSurfaceContainerHigh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BingoRoomScreen(
  user: User?,
  onBingoClaimed: (coinsWon: Long) -> Unit,
  onExitRoom: () -> Unit,
  roomName: String = "Royal Palace 75",
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // Cards state: 2 playable cards
  val card1 = remember { mutableStateOf(BingoGenerator.generateCard("card_1")) }
  val card2 = remember { mutableStateOf(BingoGenerator.generateCard("card_2")) }
  var selectedCardIndex by remember { mutableIntStateOf(0) }

  // Game state
  var isPlaying by remember { mutableStateOf(true) }
  var ballCallDelayMs by remember { mutableStateOf(3200L) }
  val calledBalls = remember { mutableStateListOf<BingoBall>() }
  var currentBall by remember { mutableStateOf<BingoBall?>(null) }
  val remainingBallNumbers = remember { (1..75).toMutableList().also { it.shuffle() } }

  // Modals & Feedback
  var showWinDialog by remember { mutableStateOf(false) }
  var winAmount by remember { mutableStateOf(5000L) }
  var showInvalidBingoAlert by remember { mutableStateOf(false) }

  // Vibrator utility
  fun triggerVibrate(durationMs: Long = 40) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }

  // Caller loop coroutine
  LaunchedEffect(isPlaying) {
    while (isPlaying && remainingBallNumbers.isNotEmpty()) {
      delay(ballCallDelayMs)
      val nextNum = remainingBallNumbers.removeAt(0)
      val ball = BingoBall.fromNumber(nextNum)
      currentBall = ball
      calledBalls.add(0, ball) // latest ball at start
      triggerVibrate(35)
    }
  }

  // Daub action
  fun daubCell(cardIndex: Int, rowIndex: Int, colIndex: Int) {
    val activeCardState = if (cardIndex == 0) card1 else card2
    val currentGrid = activeCardState.value.grid
    val cell = currentGrid[rowIndex][colIndex]

    if (cell.isDaubed || cell.isFree) return

    val isCalled = calledBalls.any { it.number == cell.number }
    if (isCalled) {
      triggerVibrate(60)
      val newGrid = currentGrid.mapIndexed { r, rowList ->
        rowList.mapIndexed { c, item ->
          if (r == rowIndex && c == colIndex) item.copy(isDaubed = true) else item
        }
      }
      activeCardState.value = activeCardState.value.copy(grid = newGrid)
    } else {
      triggerVibrate(20)
    }
  }

  // Auto-daub boost
  fun executeAutoDaub() {
    triggerVibrate(90)
    val calledNumbers = calledBalls.map { it.number }.toSet()
    
    // Auto daub Card 1
    val newGrid1 = card1.value.grid.map { row ->
      row.map { cell ->
        if (cell.number in calledNumbers) cell.copy(isDaubed = true) else cell
      }
    }
    card1.value = card1.value.copy(grid = newGrid1)

    // Auto daub Card 2
    val newGrid2 = card2.value.grid.map { row ->
      row.map { cell ->
        if (cell.number in calledNumbers) cell.copy(isDaubed = true) else cell
      }
    }
    card2.value = card2.value.copy(grid = newGrid2)
  }

  // BINGO Callout Handler
  fun onCallBingoClick() {
    val activeCard = if (selectedCardIndex == 0) card1.value else card2.value
    val isWin = BingoGenerator.checkWin(activeCard)

    if (isWin) {
      triggerVibrate(250)
      winAmount = 5000L + (user?.level ?: 1) * 500L
      showWinDialog = true
      onBingoClaimed(winAmount)
    } else {
      triggerVibrate(120)
      showInvalidBingoAlert = true
      coroutineScope.launch {
        delay(3000)
        showInvalidBingoAlert = false
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(RoyalSurface)
  ) {
    // Top Room Subheader
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalSurfaceContainerHigh)
        .padding(horizontal = 14.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(Color(0xFF10B981))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = roomName,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = RadiantGold
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Auto-daub power-up button
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(Color(0x3306B6D4))
            .border(1.dp, HyperCyan, RoundedCornerShape(9999.dp))
            .clickable { executeAutoDaub() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("auto_daub_boost_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Auto Daub",
              tint = HyperCyan,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "AUTO-MARCAR",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = onExitRoom,
          modifier = Modifier
            .size(28.dp)
            .testTag("exit_bingo_room_button")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Exit Room",
            tint = RoyalOnSurface
          )
        }
      }
    }

    // TOP HUD: Glossy Ball Caller Tube
    BallCallerTube(
      currentBall = currentBall,
      historyBalls = calledBalls.take(12)
    )

    // Multi-card Switcher Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      CardTabPill(
        title = "CARTÓN #1",
        isSelected = selectedCardIndex == 0,
        onClick = { selectedCardIndex = 0 },
        testTag = "card_tab_1"
      )
      Spacer(modifier = Modifier.width(12.dp))
      CardTabPill(
        title = "CARTÓN #2",
        isSelected = selectedCardIndex == 1,
        onClick = { selectedCardIndex = 1 },
        testTag = "card_tab_2"
      )
    }

    // Alert if user calls false Bingo
    AnimatedVisibility(
      visible = showInvalidBingoAlert,
      enter = fadeIn() + scaleIn(),
      exit = fadeOut() + scaleOut()
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 4.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF991B1B))
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "⚠️ ¡Aún no completas una línea o 4 esquinas! Sigue marcando.",
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // CENTER STAGE: 5x5 Bingo Play Card
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      val displayCard = if (selectedCardIndex == 0) card1.value else card2.value
      BingoMatrixCard(
        card = displayCard,
        onCellClick = { row, col -> daubCell(selectedCardIndex, row, col) }
      )
    }

    // LOWER ACTION DECK: Giant BINGO! button
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalSurfaceContainer)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      BingoCallButton(
        onClick = { onCallBingoClick() }
      )
    }
  }

  // Victory / Jackpot Celebration Modal
  if (showWinDialog) {
    Dialog(onDismissRequest = { showWinDialog = false }) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .border(2.dp, RadiantGold, RoundedCornerShape(24.dp))
          .testTag("bingo_win_dialog"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1035))
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            imageVector = Icons.Default.Celebration,
            contentDescription = null,
            tint = RadiantGold,
            modifier = Modifier.size(64.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "¡BINGO REAL!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = RadiantGold,
            letterSpacing = 1.sp
          )

          Text(
            text = "¡Has completado el juego con éxito!",
            fontSize = 14.sp,
            color = RoyalOnSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF2A1752))
              .border(1.dp, HyperCyan, RoundedCornerShape(16.dp))
              .padding(16.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "PREMIO GANADO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HyperCyan,
                letterSpacing = 1.sp
              )
              Text(
                text = "+$winAmount COINS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = RadiantGold
              )
              Text(
                text = "+150 XP Real",
                fontSize = 13.sp,
                color = ElectricMagenta,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              showWinDialog = false
              // Regenerate fresh cards for continuous play
              card1.value = BingoGenerator.generateCard("card_1_${System.currentTimeMillis()}")
              card2.value = BingoGenerator.generateCard("card_2_${System.currentTimeMillis()}")
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("claim_bingo_prize_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = AmberGold,
              contentColor = Color(0xFF472A00)
            ),
            shape = RoundedCornerShape(9999.dp)
          ) {
            Text(
              text = "¡RECLAMAR Y SEGUIR!",
              fontWeight = FontWeight.Black,
              fontSize = 16.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun BallCallerTube(
  currentBall: BingoBall?,
  historyBalls: List<BingoBall>,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(84.dp)
      .background(Color(0xFF13062A))
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxSize(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Active current ball (Enlarged with Pulsing Cyan/Magenta glow)
      Box(
        modifier = Modifier
          .size(66.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              colors = listOf(Color.White, RadiantGold, AmberGold)
            )
          )
          .border(2.5.dp, HyperCyan, CircleShape)
          .shadow(12.dp, CircleShape)
          .testTag("active_caller_ball"),
        contentAlignment = Alignment.Center
      ) {
        if (currentBall != null) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = currentBall.column.toString(),
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              color = Color(0xFF472A00),
              lineHeight = 12.sp
            )
            Text(
              text = currentBall.number.toString(),
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              color = Color(0xFF472A00),
              lineHeight = 22.sp
            )
          }
        } else {
          Text(
            text = "⏳",
            fontSize = 22.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Rolling ball tube slot
      LazyRow(
        modifier = Modifier
          .weight(1f)
          .height(54.dp)
          .clip(RoundedCornerShape(9999.dp))
          .background(Color(0xFF1C1033))
          .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(9999.dp))
          .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(historyBalls) { ball ->
          MiniHistoryBall(ball = ball)
        }
      }
    }
  }
}

@Composable
fun MiniHistoryBall(ball: BingoBall) {
  val colColor = when (ball.column) {
    'B' -> ColumnB
    'I' -> ColumnI
    'N' -> ColumnN
    'G' -> ColumnG
    else -> ColumnO
  }

  Box(
    modifier = Modifier
      .size(38.dp)
      .clip(CircleShape)
      .background(Color(0xFF2A1752))
      .border(1.5.dp, colColor, CircleShape),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = ball.column.toString(),
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        color = colColor,
        lineHeight = 8.sp
      )
      Text(
        text = ball.number.toString(),
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        color = Color.White,
        lineHeight = 13.sp
      )
    }
  }
}

@Composable
fun CardTabPill(
  title: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(9999.dp))
      .background(if (isSelected) AmberGold else Color(0xFF2A1752))
      .border(
        width = 1.dp,
        color = if (isSelected) RadiantGold else Color(0x33FFFFFF),
        shape = RoundedCornerShape(9999.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag(testTag)
  ) {
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      color = if (isSelected) Color(0xFF472A00) else Color.White
    )
  }
}

@Composable
fun BingoMatrixCard(
  card: BingoCard,
  onCellClick: (rowIndex: Int, colIndex: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .shadow(16.dp, RoundedCornerShape(20.dp))
      .clip(RoundedCornerShape(20.dp))
      .border(2.dp, Color(0x44FFD700), RoundedCornerShape(20.dp)),
    colors = CardDefaults.cardColors(containerColor = BingoCardSurface),
    shape = RoundedCornerShape(20.dp)
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      // Header B - I - N - G - O
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        ColumnHeaderChip("B", ColumnB)
        ColumnHeaderChip("I", ColumnI)
        ColumnHeaderChip("N", ColumnN)
        ColumnHeaderChip("G", ColumnG)
        ColumnHeaderChip("O", ColumnO)
      }

      // 5 Rows of 5 Cells
      card.grid.forEachIndexed { rowIndex, row ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          row.forEachIndexed { colIndex, cell ->
            BingoMatrixCell(
              cell = cell,
              onClick = { onCellClick(rowIndex, colIndex) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun ColumnHeaderChip(letter: String, color: Color) {
  Box(
    modifier = Modifier
      .size(width = 54.dp, height = 30.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(color),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = letter,
      fontWeight = FontWeight.Black,
      fontSize = 18.sp,
      color = Color.White
    )
  }
}

@Composable
fun BingoMatrixCell(
  cell: BingoCell,
  onClick: () -> Unit
) {
  val isDaubed = cell.isDaubed
  val isFree = cell.isFree

  Box(
    modifier = Modifier
      .size(54.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(
        when {
          isFree -> AmberGold
          isDaubed -> ElectricMagenta
          else -> BingoCellUnmarked
        }
      )
      .border(
        width = 1.dp,
        color = if (isDaubed || isFree) RadiantGold else BingoCellBorder,
        shape = RoundedCornerShape(10.dp)
      )
      .clickable(onClick = onClick)
      .testTag("bingo_cell_${cell.column}_${cell.number}"),
    contentAlignment = Alignment.Center
  ) {
    if (isFree) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.Star,
          contentDescription = "Free space",
          tint = Color(0xFF472A00),
          modifier = Modifier.size(20.dp)
        )
        Text(
          text = "FREE",
          fontWeight = FontWeight.Black,
          fontSize = 9.sp,
          color = Color(0xFF472A00),
          lineHeight = 9.sp
        )
      }
    } else {
      Text(
        text = cell.number.toString(),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        color = if (isDaubed) Color.White else BingoCardText
      )
    }
  }
}

@Composable
fun BingoCallButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Pulsing animation for high excitement
  val infiniteTransition = rememberInfiniteTransition(label = "bingo_glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.03f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .scale(pulseScale)
      .clip(RoundedCornerShape(9999.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(RadiantGold, AmberGold)
        )
      )
      .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(9999.dp))
      .shadow(12.dp, RoundedCornerShape(9999.dp))
      .clickable(onClick = onClick)
      .testTag("bingo_call_action_button"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "¡BINGO!",
      fontWeight = FontWeight.Black,
      fontSize = 24.sp,
      color = Color(0xFF472A00),
      letterSpacing = 2.sp
    )
  }
}
