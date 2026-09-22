package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPageData(
  val title: String,
  val description: String,
  val icon: ImageVector,
  val highlightText: String
)

@Composable
fun OnboardingScreen(
  onFinishOnboarding: () -> Unit
) {
  val pages = listOf(
    OnboardingPageData(
      title = "Share What You Know",
      description = "Offer your skills and help people in your neighborhood. Everyone has valuable knowledge to share.",
      icon = Icons.Default.Lightbulb,
      highlightText = "Knowledge Sharing"
    ),
    OnboardingPageData(
      title = "Discover Local Talent",
      description = "Find people nearby who can help you learn or solve a problem, from cooking and coding to bike repair.",
      icon = Icons.Default.Search,
      highlightText = "Neighborhood Skills"
    ),
    OnboardingPageData(
      title = "Grow Together",
      description = "Build meaningful connections through skill exchange. Create a resilient, connected community.",
      icon = Icons.Default.Diversity3,
      highlightText = "Stronger Community"
    )
  )

  val pagerState = rememberPagerState(pageCount = { pages.size })
  val scope = rememberCoroutineScope()

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "SkillCircle",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = SkillCirclePrimary,
            fontSize = 18.sp
          )
        )
        if (pagerState.currentPage < pages.size - 1) {
          TextButton(
            onClick = onFinishOnboarding,
            modifier = Modifier.testTag("onboarding_skip_button")
          ) {
            Text(
              text = "Skip",
              color = SkillCircleTextSecondary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      HorizontalPager(
        state = pagerState,
        modifier = Modifier.weight(1f)
      ) { pageIndex ->
        val page = pages[pageIndex]
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          // Illustration circle
          Box(
            modifier = Modifier
              .size(190.dp)
              .clip(CircleShape)
              .background(SkillCircleCream),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(SkillCircleLightGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = SkillCirclePrimary,
                modifier = Modifier.size(64.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(36.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = SkillCircleLightGreen
          ) {
            Text(
              text = page.highlightText,
              color = SkillCirclePrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark,
              fontSize = 26.sp,
              textAlign = TextAlign.Center
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge.copy(
              color = SkillCircleTextSecondary,
              fontSize = 15.sp,
              lineHeight = 22.sp,
              textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
          )
        }
      }

      // Indicator dots
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 16.dp)
      ) {
        repeat(pages.size) { index ->
          val isSelected = pagerState.currentPage == index
          Box(
            modifier = Modifier
              .padding(4.dp)
              .height(8.dp)
              .width(if (isSelected) 24.dp else 8.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(if (isSelected) SkillCirclePrimary else CardBorder)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom Button: Next or Get Started
      val isLastPage = pagerState.currentPage == pages.size - 1
      PrimaryButton(
        text = if (isLastPage) "Get Started" else "Next",
        onClick = {
          if (isLastPage) {
            onFinishOnboarding()
          } else {
            scope.launch {
              pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
          }
        },
        modifier = Modifier.padding(bottom = 24.dp),
        testTag = if (isLastPage) "onboarding_get_started_button" else "onboarding_next_button"
      )
    }
  }
}
