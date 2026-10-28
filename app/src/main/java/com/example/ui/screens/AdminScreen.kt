package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.User
import com.example.data.repository.FirebaseRepository
import com.example.ui.theme.AmberGold
import com.example.ui.theme.ElectricMagenta
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalOnSurface
import com.example.ui.theme.RoyalSurface
import com.example.ui.theme.RoyalSurfaceContainer
import com.example.ui.theme.RoyalSurfaceContainerHigh
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
  user: User?,
  repository: FirebaseRepository,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var targetUserIdInput by remember { mutableStateOf(user?.id ?: "") }
  var feedbackMsg by remember { mutableStateOf("") }

  // Security gate: If not admin, block screen
  if (user?.isAdmin != true) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(RoyalSurface)
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.AdminPanelSettings,
          contentDescription = null,
          tint = Color(0xFFEF4444),
          modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "ACCESO RESTRINGIDO",
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFFEF4444)
        )
        Text(
          text = "Este submenú solo está disponible para usuarios con rol de Administrador.",
          fontSize = 14.sp,
          color = RoyalOnSurface,
          modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
          onClick = onNavigateBack,
          colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
        ) {
          Text("Volver al Lobby", color = Color(0xFF472A00), fontWeight = FontWeight.Bold)
        }
      }
    }
    return
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(RoyalSurface)
      .verticalScroll(rememberScrollState())
  ) {
    // Header Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF7F1D1D))
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onNavigateBack,
        modifier = Modifier.testTag("admin_back_button")
      ) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Back",
          tint = Color.White
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(
          text = "PANEL DE ADMINISTRADOR",
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
        Text(
          text = "Control Maestro & Gestión en Tiempo Real",
          fontSize = 11.sp,
          color = Color(0xFFFCA5A5)
        )
      }
    }

    Column(modifier = Modifier.padding(16.dp)) {
      // Admin Identity Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RoyalSurfaceContainerHigh),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = Color(0xFF10B981),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ADMINISTRADOR AUTORIZADO",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF10B981)
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Usuario: ${user?.displayName} (${user?.email})",
            fontSize = 13.sp,
            color = RoyalOnSurface
          )
          Text(
            text = "UID: ${user?.id}",
            fontSize = 11.sp,
            color = Color(0xFF9CA3AF)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 1: Inyección de Monedas y Recursos
      Text(
        text = "⚡ INYECCIÓN DE RECURSOS (TIEMPO REAL)",
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
          Text(
            text = "Otorga saldo al jugador activo o a cualquier UID:",
            fontSize = 12.sp,
            color = RoyalOnSurface
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = targetUserIdInput,
            onValueChange = { targetUserIdInput = it },
            label = { Text("UID del Jugador Destino") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_target_uid_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = HyperCyan,
              unfocusedBorderColor = Color(0x44FFFFFF)
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Añadir Monedas de Oro:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AmberGold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AdminActionChip(
              text = "+5,000",
              onClick = {
                coroutineScope.launch {
                  repository.adminGrantCurrency(targetUserIdInput, 5000, 0, 0)
                  Toast.makeText(context, "+5,000 Coins otorgadas!", Toast.LENGTH_SHORT).show()
                }
              }
            )
            AdminActionChip(
              text = "+25,000",
              onClick = {
                coroutineScope.launch {
                  repository.adminGrantCurrency(targetUserIdInput, 25000, 0, 0)
                  Toast.makeText(context, "+25,000 Coins otorgadas!", Toast.LENGTH_SHORT).show()
                }
              }
            )
            AdminActionChip(
              text = "+100,000",
              onClick = {
                coroutineScope.launch {
                  repository.adminGrantCurrency(targetUserIdInput, 100000, 0, 0)
                  Toast.makeText(context, "+100,000 Coins otorgadas!", Toast.LENGTH_SHORT).show()
                }
              }
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Añadir Gemas y Tickets:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = HyperCyan
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AdminActionChip(
              text = "+50 Gemas",
              onClick = {
                coroutineScope.launch {
                  repository.adminGrantCurrency(targetUserIdInput, 0, 50, 0)
                  Toast.makeText(context, "+50 Gemas otorgadas!", Toast.LENGTH_SHORT).show()
                }
              }
            )
            AdminActionChip(
              text = "+10 Tickets",
              onClick = {
                coroutineScope.launch {
                  repository.adminGrantCurrency(targetUserIdInput, 0, 0, 10)
                  Toast.makeText(context, "+10 Tickets otorgados!", Toast.LENGTH_SHORT).show()
                }
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 2: Mega Jackpot Event
      Text(
        text = "🎰 ACTIVACIÓN DE JACKPOT GLOBAL",
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
          Text(
            text = "Dispara una alerta de Mega Jackpot para todos los usuarios en vivo en Firebase.",
            fontSize = 12.sp,
            color = RoyalOnSurface
          )
          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = {
              coroutineScope.launch {
                repository.adminTriggerJackpotDrop(150000)
                Toast.makeText(context, "¡Mega Jackpot de 150K lanzado a las salas!", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_trigger_jackpot_button"),
            colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
          ) {
            Icon(Icons.Default.Casino, contentDescription = null, tint = Color(0xFF472A00))
            Spacer(modifier = Modifier.width(6.dp))
            Text("¡DISPARAR MEGA JACKPOT!", color = Color(0xFF472A00), fontWeight = FontWeight.Black)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 3: Gestión de Roles de Admin
      Text(
        text = "👑 GESTIÓN DE ROLES DE USUARIO",
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
          Text(
            text = "Asignar rol de Administrador o Usuario al UID ingresado arriba:",
            fontSize = 12.sp,
            color = RoyalOnSurface
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                coroutineScope.launch {
                  repository.adminSetRole(targetUserIdInput, "admin")
                  Toast.makeText(context, "Rol de Admin asignado!", Toast.LENGTH_SHORT).show()
                }
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
              Text("HACER ADMIN", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
              onClick = {
                coroutineScope.launch {
                  repository.adminSetRole(targetUserIdInput, "user")
                  Toast.makeText(context, "Rol revocado a Usuario regular", Toast.LENGTH_SHORT).show()
                }
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B5563))
            ) {
              Text("REVOCAR", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminActionChip(
  text: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF2A1752))
      .border(1.dp, AmberGold, RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Text(
      text = text,
      color = Color.White,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )
  }
}
