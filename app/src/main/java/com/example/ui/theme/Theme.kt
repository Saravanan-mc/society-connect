package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = TealPrimaryDark,
  onPrimary = TealOnPrimaryDark,
  primaryContainer = TealPrimaryContainerDark,
  onPrimaryContainer = TealOnPrimaryContainerDark,
  secondary = SlateSecondaryDark,
  onSecondary = SlateOnSecondaryDark,
  secondaryContainer = SlateSecondaryContainerDark,
  onSecondaryContainer = SlateOnSecondaryContainerDark,
  tertiary = AmberTertiaryDark,
  onTertiary = AmberOnTertiaryDark,
  background = NeutralBackgroundDark,
  onBackground = NeutralOnBackgroundDark,
  surface = NeutralSurfaceDark,
  onSurface = NeutralOnSurfaceDark,
  surfaceVariant = NeutralSurfaceVariantDark,
  onSurfaceVariant = NeutralOnSurfaceVariantDark,
  outline = NeutralOutlineDark
)

private val LightColorScheme = lightColorScheme(
  primary = TealPrimary,
  onPrimary = TealOnPrimary,
  primaryContainer = TealPrimaryContainer,
  onPrimaryContainer = TealOnPrimaryContainer,
  secondary = SlateSecondary,
  onSecondary = SlateOnSecondary,
  secondaryContainer = SlateSecondaryContainer,
  onSecondaryContainer = SlateOnSecondaryContainer,
  tertiary = AmberTertiary,
  onTertiary = AmberOnTertiary,
  background = NeutralBackground,
  onBackground = NeutralOnBackground,
  surface = NeutralSurface,
  onSurface = NeutralOnSurface,
  surfaceVariant = NeutralSurfaceVariant,
  onSurfaceVariant = NeutralOnSurfaceVariant,
  outline = NeutralOutline
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our brand colors consistently
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
