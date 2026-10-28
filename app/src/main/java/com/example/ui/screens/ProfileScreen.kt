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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.ColumnB
import com.example.ui.theme.ColumnG
import com.example.ui.theme.ColumnI
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalOnSurface
import com.example.ui.theme.RoyalSurface
import com.example.ui.theme.RoyalSurfaceContainer
import com.example.ui.theme.RoyalSurfaceContainerHigh

data class DauberSkin(
  val id: String,
  val name: String,
  val color: Color
)

val AVAILABLE_DAUBERS = listOf(
  DauberSkin("royal_gold", "Corona de Oro", RadiantGold),
  DauberSkin("neon_magenta", "Magenta Eléctrico", ElectricMagenta),
  DauberSkin("hyper_cyan", "Ciber Cian", HyperCyan),
  DauberSkin("emerald_star", "Estrella Esmeralda", ColumnG)
)

@Composable
fun ProfileScreen(
  user: User?,
  onDauberSelected: (dauberId: String) -> Unit,
  onAdminClick: () -> Unit,
  onSignOut: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(RoyalSurface)
      .verticalScroll(rememberScrollState())
      .padding(16.dp)
  ) {
    // Top Profile Header Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = RoyalSurfaceContainerHigh),
      shape = RoundedCornerShape(20.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(RoyalSurfaceContainer)
            .border(2.5.dp, RadiantGold, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = (user?.displayName ?: "Player").take(1).uppercase(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = RadiantGold
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = user?.displayName ?: "Jugador Real",
          fontWeight = FontWeight.Black,
          fontSize = 20.sp,
          color = Color.White
        )

        Text(
          text = user?.email ?: "ID: ${user?.id?.take(10)}...",
          fontSize = 12.sp,
          color = RoyalOnSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Level & XP Bar
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "NIVEL ${user?.level ?: 1}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = RadiantGold
            )
            Text(
              text = "${(user?.xp ?: 0) % 500} / 500 XP",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = HyperCyan
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          val xpProgress = (((user?.xp ?: 0) % 500).toFloat() / 500f).coerceIn(0f, 1f)
          LinearProgressIndicator(
            progress = { xpProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(9999.dp)),
            color = AmberGold,
            trackColor = Color(0xFF13062A)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Lifetime Stats Grid
    Text(
      text = "📊 ESTADÍSTICAS DEL JUGADOR",
      fontSize = 13.sp,
      fontWeight = FontWeight.Black,
      color = RadiantGold,
      letterSpacing = 0.5.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      StatCard(
        title = "BINGOS",
        value = (user?.bingosWon ?: 0).toString(),
        icon = Icons.Default.EmojiEvents,
        tint = AmberGold,
        modifier = Modifier.weight(1f)
      )
      StatCard(
        title = "PARTIDAS",
        value = (user?.gamesPlayed ?: 0).toString(),
        icon = Icons.Default.SportsEsports,
        tint = HyperCyan,
        modifier = Modifier.weight(1f)
      )
      StatCard(
        title = "RANGO",
        value = if (user?.isAdmin == true) "ADMIN" else "VIP",
        icon = Icons.Default.MilitaryTech,
        tint = if (user?.isAdmin == true) Color(0xFFEF4444) else RadiantGold,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Dauber Customizer
    Text(
      text = "🎨 SELECCIÓN DE DAUBER (MARCADOR)",
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
      Column(modifier = Modifier.padding(14.dp)) {
        AVAILABLE_DAUBERS.forEach { dauber ->
          val isSelected = (user?.selectedDauber ?: "royal_gold") == dauber.id
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) Color(0xFF3B2E53) else Color.Transparent)
              .clickable { onDauberSelected(dauber.id) }
              .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(dauber.color)
                  .border(1.dp, Color.White, CircleShape)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = dauber.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
              )
            }

            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = RadiantGold,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    // Admin Panel Section (Exclusively visible from Profile for Admins)
    if (user?.isAdmin == true) {
      Spacer(modifier = Modifier.height(20.dp))
      Text(
        text = "🛡️ ZONA DE ADMINISTRACIÓN (EXCLUSIVO)",
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFFEF4444),
        letterSpacing = 0.5.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1015)),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(
          modifier = Modifier
            .border(1.5.dp, Color(0xFFDC2626), RoundedCornerShape(16.dp))
            .padding(14.dp)
        ) {
          Text(
            text = "Acceso a la consola de inyección de monedas, jackpot global y roles de jugador.",
            fontSize = 12.sp,
            color = Color(0xFFFCA5A5)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = onAdminClick,
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("profile_admin_access_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ABRIR SUBMENÚ DE ADMINISTRADOR", fontWeight = FontWeight.Black, fontSize = 12.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Sign out button
    OutlinedButton(
      onClick = onSignOut,
      modifier = Modifier
        .fillMaxWidth()
        .height(46.dp)
        .testTag("sign_out_button"),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(Icons.Default.ExitToApp, contentDescription = null, tint = RoyalOnSurface)
      Spacer(modifier = Modifier.width(6.dp))
      Text("Cerrar Sesión", color = RoyalOnSurface, fontWeight = FontWeight.Bold)
    }
  }
}

@Composable
fun StatCard(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = RoyalSurfaceContainer),
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = RoyalOnSurface)
    }
  }
}
