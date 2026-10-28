package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

@Composable
fun RoyalTopBar(
  user: User?,
  onAdminClick: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(RoyalSurfaceContainer)
      .padding(horizontal = 14.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Left: Player Profile & Level
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(9999.dp))
        .clickable(onClick = onProfileClick)
        .padding(4.dp)
        .testTag("profile_header_chip")
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(RoyalSurfaceContainerHigh)
          .border(1.5.dp, RadiantGold, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if ((user?.displayName ?: "").isNotEmpty()) user!!.displayName.take(1).uppercase() else "P",
          fontWeight = FontWeight.Black,
          fontSize = 15.sp,
          color = RadiantGold
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(6.dp))
          .background(AmberGold)
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = "LVL ${user?.level ?: 1}",
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = Color(0xFF472A00)
        )
      }
    }

    // Center/Right: Currency counters
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Coins Chip
      CurrencyChip(
        icon = Icons.Default.MonetizationOn,
        iconTint = RadiantGold,
        value = formatNumber(user?.coins ?: 0),
        testTag = "user_coins_chip"
      )

      // Gems Chip
      CurrencyChip(
        icon = Icons.Default.Diamond,
        iconTint = HyperCyan,
        value = formatNumber(user?.gems ?: 0),
        testTag = "user_gems_chip"
      )

      // Tickets Chip
      CurrencyChip(
        icon = Icons.Default.ConfirmationNumber,
        iconTint = ElectricMagenta,
        value = (user?.tickets ?: 0).toString(),
        testTag = "user_tickets_chip"
      )
    }
  }
}

@Composable
fun CurrencyChip(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  value: String,
  testTag: String
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(9999.dp))
      .background(Color(0x99180C2F))
      .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(9999.dp))
      .padding(horizontal = 7.dp, vertical = 4.dp)
      .testTag(testTag)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier.size(14.dp)
      )
      Text(
        text = value,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = RoyalOnSurface
      )
    }
  }
}

fun formatNumber(number: Long): String {
  return when {
    number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
    number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
    else -> number.toString()
  }
}
