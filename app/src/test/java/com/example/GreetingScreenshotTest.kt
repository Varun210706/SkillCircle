package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.model.Skill
import com.example.ui.components.SkillCard
import com.example.ui.theme.SkillCircleTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun skill_card_screenshot() {
    val sampleSkill = Skill(
      id = "test_skill_1",
      ownerName = "Elena Rostova",
      neighborhood = "Maplewood Heights",
      category = "Technology",
      title = "Java Programming Tutoring",
      description = "Friendly coding mentorship from basics to Spring Boot.",
      rating = 4.9,
      exchangeType = "Skill-for-Skill"
    )

    composeTestRule.setContent {
      SkillCircleTheme {
        Box(modifier = Modifier.padding(16.dp)) {
          SkillCard(skill = sampleSkill, onSkillClick = {})
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/skill_card.png")
  }
}
