package com.example.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.example.ui.theme.AmberGold
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalOnSurface
import com.example.ui.theme.RoyalSurface
import com.example.ui.theme.RoyalSurfaceContainer
import com.example.ui.theme.RoyalSurfaceContainerHigh
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

private const val TAG = "SignInScreen"

@Composable
fun SignInScreen(
  onSignedIn: () -> Unit,
  onGuestAccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  fun launchGoogleSignIn() {
    isLoading = true
    errorMessage = null

    coroutineScope.launch {
      try {
        val credentialManager = CredentialManager.create(context)
        val serverClientId = context.getString(R.string.default_web_client_id)

        val googleIdOption = GetGoogleIdOption.Builder()
          .setFilterByAuthorizedAccounts(false)
          .setServerClientId(serverClientId)
          .setAutoSelectEnabled(false)
          .build()

        val request = GetCredentialRequest.Builder()
          .addCredentialOption(googleIdOption)
          .build()

        val result = credentialManager.getCredential(
          request = request,
          context = context
        )

        val credential = result.credential
        if (credential is androidx.credentials.CustomCredential &&
          credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
          val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
          val idToken = googleIdTokenCredential.idToken
          val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
          FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
            .addOnSuccessListener {
              isLoading = false
              onSignedIn()
            }
            .addOnFailureListener { e ->
              isLoading = false
              Log.e(TAG, "Firebase credential sign in failed", e)
              errorMessage = "Error autenticando con Firebase: ${e.localizedMessage}"
            }
        } else {
          isLoading = false
          errorMessage = "Tipo de credencial no reconocido"
        }
      } catch (e: GetCredentialCancellationException) {
        isLoading = false
        Log.i(TAG, "Google Sign-In cancelado por el usuario")
      } catch (e: GetCredentialException) {
        isLoading = false
        Log.e(TAG, "Error en Credential Manager", e)
        errorMessage = "Error de inicio de sesión: ${e.localizedMessage ?: "Verifica tu cuenta de Google"}"
      } catch (e: Exception) {
        isLoading = false
        Log.e(TAG, "Error general de autenticación", e)
        errorMessage = "Error al conectar: ${e.localizedMessage}"
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.radialGradient(
          colors = listOf(Color(0xFF2A1752), RoyalSurface)
        )
      )
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(24.dp, RoundedCornerShape(28.dp))
        .border(2.dp, RadiantGold, RoundedCornerShape(28.dp)),
      colors = CardDefaults.cardColors(containerColor = RoyalSurfaceContainer),
      shape = RoundedCornerShape(28.dp)
    ) {
      Column(
        modifier = Modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Crown Logo
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(Color.White, RadiantGold, AmberGold)
              )
            )
            .border(3.dp, HyperCyan, CircleShape)
            .shadow(16.dp, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "77",
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            color = Color(0xFF472A00)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "ROYAL ARCADE BINGO",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = RadiantGold,
          textAlign = TextAlign.Center,
          letterSpacing = 1.sp
        )

        Text(
          text = "Salas multijugador, minijuegos arcade y progreso en tiempo real con Firebase",
          fontSize = 12.sp,
          color = RoyalOnSurface,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0x33DC2626))
              .border(1.dp, Color(0xFFDC2626), RoundedCornerShape(8.dp))
              .padding(8.dp)
          ) {
            Text(
              text = errorMessage!!,
              color = Color(0xFFFCA5A5),
              fontSize = 12.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth()
            )
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        if (isLoading) {
          CircularProgressIndicator(
            color = RadiantGold,
            modifier = Modifier.size(36.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Conectando con Firebase...",
            fontSize = 12.sp,
            color = HyperCyan
          )
        } else {
          // Google Sign-In Button
          Button(
            onClick = { launchGoogleSignIn() },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("google_sign_in_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = AmberGold,
              contentColor = Color(0xFF472A00)
            ),
            shape = RoundedCornerShape(9999.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFF472A00),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "INICIAR CON GOOGLE",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Quick Play Demo Button
          OutlinedButton(
            onClick = onGuestAccess,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("quick_play_button"),
            shape = RoundedCornerShape(9999.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = RoyalOnSurface
            )
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = HyperCyan,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "ENTRADA RÁPIDA (MODO INVITADO)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }
}
