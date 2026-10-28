package com.example.ui.screens.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalOnSurface
import com.example.ui.theme.RoyalSurfaceContainer
import com.example.ui.theme.RoyalSurfaceContainerHigh
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class RocketState {
  IDLE,
  LAUNCHING,
  FLYING,
  CRASHED,
  CASHED_OUT
}

@Composable
fun RocketCrashGame(
  user: User?,
  onReward: (coinsDelta: Long, xpGain: Long, desc: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var betAmount by remember { mutableLongStateOf(100L) }
  var gameState by remember { mutableStateOf(RocketState.IDLE) }
  var currentMultiplier by remember { mutableDoubleStateOf(1.00) }
  var crashPoint by remember { mutableDoubleStateOf(2.50) }
  var flightProgress by remember { mutableFloatStateOf(0f) }
  var cashOutEarnings by remember { mutableLongStateOf(0L) }
  var lastCrashMsg by remember { mutableStateOf("¡Elige tu apuesta y despega hacia las estrellas!") }

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

  // Rocket Flight Coroutine
  LaunchedEffect(gameState) {
    if (gameState == RocketState.LAUNCHING) {
      delay(600)
      gameState = RocketState.FLYING
      currentMultiplier = 1.00
      flightProgress = 0f

      // Generate crash point (exponential distribution: 1.01x up to 100x+)
      val rand = Random.nextDouble()
      crashPoint = when {
        rand < 0.08 -> 1.05 + Random.nextDouble() * 0.15 // early crash 8%
        rand < 0.65 -> 1.20 + Random.nextDouble() * 3.50 // medium 1.2x to 4.7x
        rand < 0.90 -> 4.70 + Random.nextDouble() * 8.00 // high 4.7x to 12.7x
        else -> 12.70 + Random.nextDouble() * 50.00      // mega high roller
      }

      var elapsed = 0.0
      while (gameState == RocketState.FLYING) {
        delay(40)
        elapsed += 0.04
        // Growth curve
        currentMultiplier += (0.015 * currentMultiplier * 0.8)
        flightProgress = (flightProgress + 0.008f).coerceAtMost(1f)

        if (currentMultiplier >= crashPoint) {
          gameState = RocketState.CRASHED
          vibrate(180)
          lastCrashMsg = "💥 ¡EL COHETE EXPLOTÓ A ${String.format("%.2f", currentMultiplier)}X!"
          // Deduct bet
          onReward(-betAmount, 10, "Cohete Crash (Perdido)")
          break
        }
      }
    }
  }

  fun startFlight() {
    if (gameState == RocketState.FLYING || gameState == RocketState.LAUNCHING) return
    if ((user?.coins ?: 0) < betAmount) {
      lastCrashMsg = "⚠️ ¡No tienes suficientes monedas para apostar $betAmount!"
      return
    }
    vibrate(40)
    gameState = RocketState.LAUNCHING
    lastCrashMsg = "🚀 ¡DESPEGANDO! Retira tus ganancias antes de que explote..."
  }

  fun cashOut() {
    if (gameState != RocketState.FLYING) return
    vibrate(100)
    gameState = RocketState.CASHED_OUT
    val multiplier = currentMultiplier
    cashOutEarnings = (betAmount * multiplier).toLong()
    val netProfit = cashOutEarnings - betAmount
    val xpGain = (multiplier * 20).toLong()
    lastCrashMsg = "🎉 ¡RETIRADO A TIEMPO A ${String.format("%.2f", multiplier)}X! +$cashOutEarnings COINS"
    onReward(netProfit, xpGain, "¡Cohete Multiplicador ${String.format("%.2f", multiplier)}x!")
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF13062A))
      .padding(14.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Title Banner
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF2A1752))
        .border(1.dp, RadiantGold, RoundedCornerShape(12.dp))
        .padding(vertical = 8.dp)
    ) {
      Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = RadiantGold, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "¡DESPEGUE MILLONARIO 1000X!",
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        color = RadiantGold,
        letterSpacing = 1.sp
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Flight Canvas Arena
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
        .border(2.dp, if (gameState == RocketState.CRASHED) Color(0xFFEF4444) else HyperCyan, RoundedCornerShape(20.dp)),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0C041C)),
      shape = RoundedCornerShape(20.dp)
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        // Curve Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height

          // Draw grid lines
          for (i in 1..4) {
            val y = h * (i / 5f)
            drawLine(
              color = Color(0x22FFFFFF),
              start = Offset(0f, y),
              end = Offset(w, y),
              strokeWidth = 1f
            )
          }

          // Draw trajectory curve when active
          if (gameState == RocketState.FLYING || gameState == RocketState.CASHED_OUT || gameState == RocketState.CRASHED) {
            val path = Path()
            path.moveTo(30f, h - 30f)
            val endX = 30f + (w - 70f) * flightProgress
            val endY = (h - 30f) - (h - 70f) * (flightProgress * flightProgress)

            path.quadraticTo(
              (30f + endX) / 2,
              h - 30f,
              endX,
              endY
            )

            val lineColor = when (gameState) {
              RocketState.CRASHED -> Color(0xFFEF4444)
              RocketState.CASHED_OUT -> Color(0xFF10B981)
              else -> RadiantGold
            }

            drawPath(
              path = path,
              color = lineColor,
              style = Stroke(width = 4.dp.toPx())
            )
          }
        }

        // Live Multiplier Display in Center
        Column(
          modifier = Modifier.align(Alignment.Center),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "${String.format("%.2f", currentMultiplier)}x",
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            color = when (gameState) {
              RocketState.CRASHED -> Color(0xFFEF4444)
              RocketState.CASHED_OUT -> Color(0xFF10B981)
              RocketState.FLYING -> RadiantGold
              else -> Color.White
            },
            letterSpacing = 1.sp
          )

          Text(
            text = when (gameState) {
              RocketState.CRASHED -> "¡EXPLOSIÓN!"
              RocketState.CASHED_OUT -> "¡GANANCIA ASEGURADA!"
              RocketState.FLYING -> "SUBIENDO..."
              RocketState.LAUNCHING -> "PROPULSIÓN..."
              RocketState.IDLE -> "LISTO PARA DESPEGAR"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = RoyalOnSurface
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Status Message Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFF211538))
        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
        .padding(horizontal = 12.dp, vertical = 8.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = lastCrashMsg,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Bet Selector Chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Apuesta:",
        color = AmberGold,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(50L, 100L, 250L, 500L).forEach { amount ->
          val isSelected = betAmount == amount
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) AmberGold else Color(0xFF2A1752))
              .border(1.dp, if (isSelected) RadiantGold else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
              .clickable(enabled = gameState != RocketState.FLYING) { betAmount = amount }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "$amount",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = if (isSelected) Color(0xFF472A00) else Color.White
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Main Big Action Button
    if (gameState == RocketState.FLYING) {
      Button(
        onClick = { cashOut() },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("rocket_cashout_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
        shape = RoundedCornerShape(9999.dp)
      ) {
        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "¡RETIRAR ${(betAmount * currentMultiplier).toLong()} COINS!",
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          color = Color.White
        )
      }
    } else {
      Button(
        onClick = { startFlight() },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("rocket_launch_button"),
        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
        shape = RoundedCornerShape(9999.dp)
      ) {
        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color(0xFF472A00))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "¡DESPEGAR (APOSTAR $betAmount)!",
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          color = Color(0xFF472A00)
        )
      }
    }
  }
}
