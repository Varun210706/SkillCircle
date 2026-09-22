package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SkillCircleColorScheme = lightColorScheme(
  primary = SkillCirclePrimary,
  onPrimary = Color.White,
  primaryContainer = SkillCircleLightGreen,
  onPrimaryContainer = SkillCirclePrimary,
  inversePrimary = SkillCircleTeal,

  secondary = SkillCircleSecondary,
  onSecondary = Color.White,
  secondaryContainer = SkillCircleLightGreen,
  onSecondaryContainer = SkillCirclePrimary,

  tertiary = SkillCircleTeal,
  onTertiary = Color.White,
  tertiaryContainer = SkillCircleCream,
  onTertiaryContainer = SkillCircleTextDark,

  background = SkillCircleBackground,
  onBackground = SkillCircleTextDark,

  // Set surface and container hierarchy to Cream (#F7F3E8) and Light Green (#DDEFE6)
  // so dialogs, dropdowns, bottom sheets, and pickers never default to unreadable white
  surface = SkillCircleCream,
  onSurface = SkillCircleTextDark,
  surfaceVariant = SkillCircleLightGreen,
  onSurfaceVariant = SkillCircleTextSecondary,

  surfaceContainerLowest = SkillCircleCream,
  surfaceContainerLow = SkillCircleCream,
  surfaceContainer = SkillCircleCream,
  surfaceContainerHigh = SkillCircleCream,
  surfaceContainerHighest = SkillCircleLightGreen,

  surfaceTint = SkillCirclePrimary,
  outline = CardBorder,
  outlineVariant = SkillCircleLightGreen,
  scrim = Color(0x66000000)
)

@Composable
fun SkillCircleTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = SkillCircleColorScheme,
    typography = Typography,
    content = content
  )
}
