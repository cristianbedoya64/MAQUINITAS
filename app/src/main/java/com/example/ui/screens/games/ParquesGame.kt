package com.example.ui.screens.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.theme.AmberGold
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalOnSurface
import com.example.ui.theme.RoyalSurfaceContainer
import com.example.ui.theme.RoyalSurfaceContainerHigh
import kotlin.random.Random

data class Pawn(
  val id: Int,
  val color: String, // "Rojo", "Azul", "Verde", "Amarillo"
  val inJail: Boolean = true,
  val position: Int = 0, // 0 to 68 on board track
  val isHome: Boolean = false
)

@Composable
fun ParquesGame(
  user: User?,
  onReward: (coinsDelta: Long, xpGain: Long, desc: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Player color is RED (Rojo), Opponents are Azul, Amarillo, Verde
  val playerPawns = remember {
    mutableStateListOf(
      Pawn(1, "Rojo", inJail = true),
      Pawn(2, "Rojo", inJail = true),
      Pawn(3, "Rojo", inJail = false, position = 5),
      Pawn(4, "Rojo", inJail = false, position = 12)
    )
  }

  var dice1 by remember { mutableIntStateOf(5) }
  var dice2 by remember { mutableIntStateOf(5) }
  var isRolling by remember { mutableStateOf(false) }
  var canMove by remember { mutableStateOf(false) }
  var gameLog by remember { mutableStateOf("¡Lanza los dos dados para mover tus fichas o salir de la cárcel!") }
  var consecutivePairs by remember { mutableIntStateOf(0) }
  var totalCaptured by remember { mutableIntStateOf(0) }
  var totalCoronadas by remember { mutableIntStateOf(0) }

  fun vibrate(duration: Long) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        @Suppress("DEPRECATION")
        v?.vibrate(duration)
      }
    } catch (_: Exception) {}
  }

  fun rollDice() {
    vibrate(60)
    dice1 = Random.nextInt(1, 7)
    dice2 = Random.nextInt(1, 7)
    val isPair = dice1 == dice2

    if (isPair) {
      consecutivePairs++
      gameLog = "🎲 ¡PARES ($dice1-$dice2)! Tienes tiro extra y puedes sacar fichas de la cárcel."
    } else {
      consecutivePairs = 0
      val total = dice1 + dice2
      gameLog = "🎲 Sacaste $dice1 + $dice2 = $total. Selecciona una ficha roja para avanzar."
    }
    canMove = true
  }

  fun movePawn(pawnIndex: Int) {
    if (!canMove) return
    val pawn = playerPawns[pawnIndex]
    val totalMoves = dice1 + dice2
    val isPair = dice1 == dice2

    if (pawn.inJail) {
      if (isPair) {
        vibrate(80)
        playerPawns[pawnIndex] = pawn.copy(inJail = false, position = 1)
        gameLog = "⭐ ¡Ficha #${pawn.id} libre de la Cárcel a la Salida!"
        canMove = false
      } else {
        gameLog = "⚠️ Necesitas pares para sacar la ficha #${pawn.id} de la cárcel."
      }
      return
    }

    if (pawn.isHome) return

    vibrate(40)
    val newPos = pawn.position + totalMoves
    if (newPos >= 68) {
      // Coronó la ficha
      vibrate(150)
      playerPawns[pawnIndex] = pawn.copy(isHome = true, position = 68)
      totalCoronadas++
      gameLog = "👑 ¡CORONASTE LA FICHA #${pawn.id}! +1,000 Coins ganadas."
      onReward(1000L, 50L, "Parqués: ¡Ficha Coronada!")
    } else {
      // Check capture simulation
      val randomCapture = Random.nextDouble() < 0.25
      if (randomCapture && newPos % 7 != 0) { // Not on safe space (Seguro)
        vibrate(100)
        totalCaptured++
        gameLog = "⚔️ ¡COMISTE FICHA RIVAL en casilla $newPos! +300 Coins de botín."
        onReward(300L, 25L, "Parqués: ¡Captura de Ficha!")
      } else {
        gameLog = "Avanzaste la Ficha #${pawn.id} a la casilla $newPos."
      }
      playerPawns[pawnIndex] = pawn.copy(position = newPos)
    }
    canMove = false
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF13062A))
      .verticalScroll(rememberScrollState())
      .padding(14.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header Banner
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF1F2937))
        .border(1.5.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
        .padding(vertical = 10.dp, horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Casino, contentDescription = null, tint = RadiantGold, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("PARQUÉS REAL COLOMBIANO", fontWeight = FontWeight.Black, fontSize = 14.sp, color = RadiantGold)
          Text("Tablero Tradicional • 4 Colores • Dados & Estrategia", fontSize = 10.sp, color = RoyalOnSurface)
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(Color(0xFFEF4444))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text("TÚ: ROJO", fontWeight = FontWeight.Black, fontSize = 10.sp, color = Color.White)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Parqués Board Visual
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(290.dp)
        .border(2.dp, RadiantGold, RoundedCornerShape(20.dp)),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1033)),
      shape = RoundedCornerShape(20.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Jails (Amarillo & Azul)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          JailBox(name = "CÁRCEL AMARILLO", color = Color(0xFFFBBF24), pawnsInside = 4)
          JailBox(name = "CÁRCEL AZUL", color = Color(0xFF3B82F6), pawnsInside = 3)
        }

        // Center Board Heaven & Safe Zones
        Box(
          modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A1752))
            .border(2.dp, RadiantGold, RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = RadiantGold, modifier = Modifier.size(32.dp))
            Text("CIELO", fontWeight = FontWeight.Black, fontSize = 12.sp, color = RadiantGold)
            Text("$totalCoronadas / 4", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
          }
        }

        // Bottom Jails (Verde & Rojo - Player)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          JailBox(name = "CÁRCEL VERDE", color = Color(0xFF10B981), pawnsInside = 4)
          JailBox(name = "TU CÁRCEL (ROJO)", color = Color(0xFFEF4444), pawnsInside = playerPawns.count { it.inJail })
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Dice Deck & Roll Button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(RoyalSurfaceContainer)
        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DiceFace(number = dice1)
        DiceFace(number = dice2)
      }

      Button(
        onClick = { rollDice() },
        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
        shape = RoundedCornerShape(9999.dp),
        modifier = Modifier.testTag("roll_parques_dice_button")
      ) {
        Icon(Icons.Default.Casino, contentDescription = null, tint = Color(0xFF472A00))
        Spacer(modifier = Modifier.width(6.dp))
        Text("¡TIRAR DADOS!", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF472A00))
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Game Log
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF211538))
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = gameLog,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Player Pawns Interactive Action Strip
    Text("TUS 4 FICHAS ROJAS (TOCA PARA MOVER):", fontWeight = FontWeight.Black, fontSize = 11.sp, color = RadiantGold)
    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      playerPawns.forEachIndexed { idx, pawn ->
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (canMove) Color(0xFF3B2E53) else RoyalSurfaceContainer)
            .border(
              1.dp,
              if (canMove) RadiantGold else Color(0x33FFFFFF),
              RoundedCornerShape(10.dp)
            )
            .clickable(enabled = canMove) { movePawn(idx) }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444)),
              contentAlignment = Alignment.Center
            ) {
              Text("${pawn.id}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = when {
                pawn.isHome -> "👑 Corona"
                pawn.inJail -> "🔒 Cárcel"
                else -> "Cas. ${pawn.position}"
              },
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (pawn.isHome) RadiantGold else Color.White
            )
          }
        }
      }
    }
  }
}

@Composable
fun JailBox(name: String, color: Color, pawnsInside: Int) {
  Box(
    modifier = Modifier
      .size(width = 130.dp, height = 65.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(color.copy(alpha = 0.2f))
      .border(1.dp, color, RoundedCornerShape(10.dp))
      .padding(6.dp)
  ) {
    Column {
      Text(name, fontSize = 9.sp, fontWeight = FontWeight.Black, color = color)
      Spacer(modifier = Modifier.height(4.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(pawnsInside) {
          Box(
            modifier = Modifier
              .size(14.dp)
              .clip(CircleShape)
              .background(color)
          )
        }
      }
    }
  }
}

@Composable
fun DiceFace(number: Int) {
  Box(
    modifier = Modifier
      .size(44.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Color.White)
      .border(1.5.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
      .shadow(4.dp, RoundedCornerShape(8.dp)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = number.toString(),
      fontWeight = FontWeight.Black,
      fontSize = 22.sp,
      color = Color(0xFF1E1035)
    )
  }
}
