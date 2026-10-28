package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.User
import com.example.ui.theme.AmberGold
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalSurface
import com.example.ui.theme.RoyalSurfaceContainerHigh

class AndroidBridge(
  private val context: Context,
  private val onReward: (coins: Long, xp: Long, description: String) -> Unit,
  private val onWheelPrize: (coins: Long, gems: Long, tickets: Long, xp: Long, label: String) -> Unit
) {
  @JavascriptInterface
  fun onGameReward(coins: Long, xp: Long, gameName: String) {
    onReward(coins, xp, gameName)
  }

  @JavascriptInterface
  fun onWheelPrize(coins: Long, gems: Long, tickets: Long, xp: Long, prizeLabel: String) {
    onWheelPrize(coins, gems, tickets, xp, prizeLabel)
  }

  @JavascriptInterface
  fun onGamePlayed(deltaCoins: Long, xp: Long, details: String) {
    onReward(deltaCoins, xp, details)
  }

  @JavascriptInterface
  fun vibrate(durationMs: Long) {
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

  @JavascriptInterface
  fun showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }
}

enum class ArcadeGameType(val title: String, val assetFile: String) {
  SLOTS("ROYAL SLOTS", "royal_slots.html"),
  WHEEL("RULETA DE LA SUERTE", "lucky_wheel.html"),
  REFLEX("CYBER REFLEJOS", "cyber_tap.html")
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ArcadeGamesScreen(
  user: User?,
  onRewardWon: (coins: Long, xp: Long, desc: String) -> Unit,
  onWheelRewardWon: (coins: Long, gems: Long, tickets: Long, xp: Long, label: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var activeGame by remember { mutableStateOf(ArcadeGameType.SLOTS) }

  val bridge = remember(user?.id) {
    AndroidBridge(
      context = context,
      onReward = { coins, xp, desc ->
        onRewardWon(coins, xp, desc)
      },
      onWheelPrize = { coins, gems, tickets, xp, label ->
        onWheelRewardWon(coins, gems, tickets, xp, label)
      }
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(RoyalSurface)
  ) {
    // Game Switcher Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalSurfaceContainerHigh)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ArcadeGameType.values().forEach { game ->
        val isSelected = activeGame == game
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(9999.dp))
            .background(if (isSelected) AmberGold else Color(0xFF2A1752))
            .border(
              width = 1.dp,
              color = if (isSelected) RadiantGold else Color(0x33FFFFFF),
              shape = RoundedCornerShape(9999.dp)
            )
            .clickable { activeGame = game }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = game.title,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = if (isSelected) Color(0xFF472A00) else Color.White
          )
        }
      }
    }

    // Embedded HTML5 / Pure JS WebView
    AndroidView(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .testTag("arcade_webview_${activeGame.name.lowercase()}"),
      factory = { ctx ->
        WebView(ctx).apply {
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          settings.loadWithOverviewMode = true
          settings.useWideViewPort = true
          settings.builtInZoomControls = false
          settings.displayZoomControls = false
          settings.cacheMode = WebSettings.LOAD_NO_CACHE
          setBackgroundColor(0xFF180C2F.toInt())

          addJavascriptInterface(bridge, "AndroidBridge")

          webViewClient = object : WebViewClient() {}
          webChromeClient = object : WebChromeClient() {}

          loadUrl("file:///android_asset/${activeGame.assetFile}")
        }
      },
      update = { webView ->
        webView.loadUrl("file:///android_asset/${activeGame.assetFile}")
      }
    )
  }
}
