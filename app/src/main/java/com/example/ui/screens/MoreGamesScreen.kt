package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.screens.games.DrawPokerGame
import com.example.ui.screens.games.ParquesGame
import com.example.ui.screens.games.RocketCrashGame
import com.example.ui.theme.AmberGold
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalSurface
import com.example.ui.theme.RoyalSurfaceContainerHigh

enum class ExtraGameType(val tabName: String) {
  ROCKET_CRASH("🚀 COHETE 1000X"),
  PARQUES("🎲 PARQUÉS"),
  DRAW_POKER("🃏 DRAW POKER")
}

@Composable
fun MoreGamesScreen(
  user: User?,
  onReward: (coinsDelta: Long, xpGain: Long, desc: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedGame by remember { mutableStateOf(ExtraGameType.ROCKET_CRASH) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(RoyalSurface)
  ) {
    // Top Tabs Switcher
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalSurfaceContainerHigh)
        .padding(horizontal = 10.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ExtraGameType.values().forEach { game ->
        val isSelected = selectedGame == game
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
            .clickable { selectedGame = game }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = game.tabName,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            color = if (isSelected) Color(0xFF472A00) else Color.White
          )
        }
      }
    }

    // Active Game Container
    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
      when (selectedGame) {
        ExtraGameType.ROCKET_CRASH -> {
          RocketCrashGame(
            user = user,
            onReward = onReward
          )
        }
        ExtraGameType.PARQUES -> {
          ParquesGame(
            user = user,
            onReward = onReward
          )
        }
        ExtraGameType.DRAW_POKER -> {
          DrawPokerGame(
            user = user,
            onReward = onReward
          )
        }
      }
    }
  }
}
