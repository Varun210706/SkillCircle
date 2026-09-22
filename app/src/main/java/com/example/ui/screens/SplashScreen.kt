package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SkillCircleCream
import com.example.ui.theme.SkillCircleLightGreen
import com.example.ui.theme.SkillCirclePrimary
import com.example.ui.theme.SkillCircleTeal
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  isAuthenticated: Boolean,
  onNavigateNext: (destination: String) -> Unit
) {
  val scale = remember { Animatable(0.8f) }
  val alpha = remember { Animatable(0f) }

  LaunchedEffect(key1 = true) {
    scale.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    alpha.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 600)
    )
    delay(1200)
    if (isAuthenticated) {
      onNavigateNext("home")
    } else {
      onNavigateNext("onboarding")
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(SkillCirclePrimary)
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    // Subtle background community circle motifs
    Box(
      modifier = Modifier
        .size(340.dp)
        .clip(CircleShape)
        .background(SkillCircleTeal.copy(alpha = 0.08f))
    )
    Box(
      modifier = Modifier
        .size(240.dp)
        .clip(CircleShape)
        .background(SkillCircleLightGreen.copy(alpha = 0.12f))
    )

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .scale(scale.value)
        .alpha(alpha.value)
    ) {
      // Stylized Center Logo
      Box(
        modifier = Modifier
          .size(90.dp)
          .clip(CircleShape)
          .background(SkillCircleCream),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.VolunteerActivism,
          contentDescription = "SkillCircle Emblem",
          tint = SkillCirclePrimary,
          modifier = Modifier.size(50.dp)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "SkillCircle",
        style = MaterialTheme.typography.headlineLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 36.sp,
          color = Color.White,
          letterSpacing = 1.sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Share Skills. Build Connections.",
        style = MaterialTheme.typography.bodyLarge.copy(
          color = SkillCircleLightGreen,
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium
        )
      )
    }
  }
}
