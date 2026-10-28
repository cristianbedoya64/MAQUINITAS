package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RoyalColorScheme = darkColorScheme(
  primary = RoyalPrimary,
  onPrimary = RoyalOnPrimary,
  primaryContainer = RoyalPrimaryContainer,
  onPrimaryContainer = RoyalOnPrimaryContainer,
  secondary = RoyalSecondary,
  onSecondary = RoyalOnSecondary,
  secondaryContainer = RoyalSecondaryContainer,
  onSecondaryContainer = RoyalOnSecondaryContainer,
  tertiary = RoyalTertiary,
  onTertiary = RoyalOnTertiary,
  tertiaryContainer = RoyalTertiaryContainer,
  onTertiaryContainer = RoyalOnTertiaryContainer,
  background = RoyalSurface,
  onBackground = RoyalOnSurface,
  surface = RoyalSurface,
  onSurface = RoyalOnSurface,
  surfaceVariant = RoyalSurfaceContainerHighest,
  onSurfaceVariant = RoyalOnSurfaceVariant,
  outline = RoyalOutline,
  outlineVariant = RoyalOutlineVariant
)

@Composable
fun RoyalArcadeBingoTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = RoyalColorScheme,
    typography = Typography,
    content = content
  )
}
