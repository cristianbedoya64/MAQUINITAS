package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameRoom
import com.example.data.model.Jackpot
import com.example.data.model.User
import com.example.ui.components.formatNumber
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalOnSurface
import com.example.ui.theme.RoyalSurface
import com.example.ui.theme.RoyalSurfaceContainer
import com.example.ui.theme.RoyalSurfaceContainerHigh

@Composable
fun LobbyScreen(
  user: User?,
  jackpot: Jackpot,
  rooms: List<GameRoom>,
  leaderboard: List<User>,
  onJoinRoom: (room: GameRoom) -> Unit,
  onOpenArcade: () -> Unit,
  onClaimDailyBonus: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(RoyalSurface)
      .verticalScroll(rememberScrollState())
      .padding(bottom = 24.dp)
  ) {
    // 1. MEGA JACKPOT TICKER HERO BANNER
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(
          Brush.verticalGradient(
            colors = listOf(Color(0xFF2A1752), Color(0xFF13062A))
          )
        )
        .border(2.dp, RadiantGold, RoundedCornerShape(20.dp))
        .shadow(16.dp, RoundedCornerShape(20.dp))
        .padding(18.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = RadiantGold,
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = "ROYAL PROGRESSIVE JACKPOT",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = RadiantGold,
            letterSpacing = 1.sp
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "${formatNumber(jackpot.currentPool)} COINS",
          fontSize = 32.sp,
          fontWeight = FontWeight.Black,
          color = Color.White,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Último ganador: ${jackpot.lastWinner}",
          fontSize = 12.sp,
          color = HyperCyan,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // 2. DAILY BONUS / WELCOME CARD
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(RoyalSurfaceContainer)
        .border(1.dp, Color(0x33FFD700), RoundedCornerShape(16.dp))
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color(0xFF3B2E53)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Stars,
              contentDescription = null,
              tint = AmberGold,
              modifier = Modifier.size(26.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Bono Diario Real",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color.White
            )
            Text(
              text = "+500 Coins & +5 Gemas gratis",
              fontSize = 11.sp,
              color = RoyalOnSurface
            )
          }
        }

        Button(
          onClick = onClaimDailyBonus,
          shape = RoundedCornerShape(9999.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
          modifier = Modifier.testTag("claim_daily_bonus_button")
        ) {
          Text(
            text = "RECLAMAR",
            color = Color(0xFF472A00),
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // 3. LIVE BINGO ROOMS SECTION
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "SALAS DE BINGO EN VIVO (75 BOLAS)",
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        color = RadiantGold,
        letterSpacing = 0.5.sp
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.FiberManualRecord,
          contentDescription = null,
          tint = Color(0xFF10B981),
          modifier = Modifier.size(10.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "EN DIRECTO",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF10B981)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
      items(rooms) { room ->
        GameRoomCard(
          room = room,
          onJoin = { onJoinRoom(room) }
        )
      }
    }

    Spacer(modifier = Modifier.height(22.dp))

    // 4. ARCADE ZONE PROMO CARD
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(
          Brush.horizontalGradient(
            colors = listOf(Color(0xFF2A1752), Color(0xFFAA0266))
          )
        )
        .border(1.5.dp, HyperCyan, RoundedCornerShape(18.dp))
        .clickable { onOpenArcade() }
        .padding(16.dp)
        .testTag("arcade_banner_button")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Casino,
              contentDescription = null,
              tint = RadiantGold,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ZONA ARCADE (PAGO EN COINS)",
              fontWeight = FontWeight.Black,
              fontSize = 14.sp,
              color = Color.White
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Tragamonedas Vegas, Ruleta de la Fortuna y Cyber Reflejos con JS puro e imágenes en alta resolución.",
            fontSize = 11.sp,
            color = RoyalOnSurface
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .clip(CircleShape)
            .background(RadiantGold)
            .padding(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = Color(0xFF472A00),
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(22.dp))

    // 5. LIVE LEADERBOARD (HALL OF FAME)
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = "🏆 TABLA DE LÍDERES REALES",
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        color = RadiantGold,
        letterSpacing = 0.5.sp
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RoyalSurfaceContainer),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          if (leaderboard.isEmpty()) {
            Text(
              text = "Conectando con la tabla de posiciones en tiempo real...",
              fontSize = 12.sp,
              color = RoyalOnSurface,
              modifier = Modifier.padding(vertical = 8.dp)
            )
          } else {
            leaderboard.take(5).forEachIndexed { idx, player ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "#${idx + 1}",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = if (idx == 0) RadiantGold else RoyalOnSurface,
                    modifier = Modifier.width(28.dp)
                  )
                  Text(
                    text = player.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = RadiantGold,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = formatNumber(player.coins),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = RadiantGold
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun GameRoomCard(
  room: GameRoom,
  onJoin: () -> Unit
) {
  Card(
    modifier = Modifier
      .width(200.dp)
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
      .clickable(onClick = onJoin)
      .testTag("room_card_${room.id}"),
    colors = CardDefaults.cardColors(containerColor = RoyalSurfaceContainerHigh),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = room.name,
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        color = Color.White
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.EmojiEvents,
          contentDescription = null,
          tint = AmberGold,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${formatNumber(room.prizePool)} COINS",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 13.sp,
          color = RadiantGold
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Groups,
            contentDescription = null,
            tint = HyperCyan,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${room.activePlayers} jug.",
            fontSize = 11.sp,
            color = RoyalOnSurface
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ConfirmationNumber,
            contentDescription = null,
            tint = ElectricMagenta,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${room.ticketCost} Tkt",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricMagenta
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = onJoin,
        modifier = Modifier.fillMaxWidth().height(36.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
        shape = RoundedCornerShape(9999.dp)
      ) {
        Text(
          text = "ENTRAR AHORA",
          fontWeight = FontWeight.Black,
          fontSize = 11.sp,
          color = Color(0xFF472A00)
        )
      }
    }
  }
}
