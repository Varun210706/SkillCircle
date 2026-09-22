package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  viewModel: SkillCircleViewModel,
  onNavigateEditProfile: () -> Unit,
  onNavigateMySkills: () -> Unit,
  onNavigateSettings: () -> Unit,
  onNavigateLogout: () -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val achievements by viewModel.achievements.collectAsState()
  val allReviews by viewModel.reviews.collectAsState()
  val mySkills by viewModel.mySkills.collectAsState()

  val myReviews = remember(allReviews, currentUser) {
    allReviews.filter { it.revieweeId == currentUser?.uid }
  }

  var selectedSection by remember { mutableStateOf("Achievements") } // Achievements, Reviews, Skills

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = { Text("Profile & Community", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        actions = {
          IconButton(onClick = onNavigateSettings, modifier = Modifier.testTag("profile_settings_button")) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = SkillCircleTextDark)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleBackground)
      )
    }
  ) { innerPadding ->
    val user = currentUser
    if (user == null) {
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SkillCirclePrimary)
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Profile Header with statistics
        item {
          ProfileHeader(
            user = user,
            onEditClick = onNavigateEditProfile
          )
        }

        // Action Buttons Row: Edit Profile & Manage My Skills
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onNavigateEditProfile,
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("profile_edit_button"),
              shape = RoundedCornerShape(22.dp),
              border = BorderStroke(1.5.dp, SkillCirclePrimary),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = SkillCirclePrimary)
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Edit Profile", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }

            Button(
              onClick = onNavigateMySkills,
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("profile_my_skills_button"),
              shape = RoundedCornerShape(22.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SkillCirclePrimary)
            ) {
              Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("My Skills (${mySkills.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
          }
        }

        // Section Selector Tabs (Achievements, Reviews, My Skills Overview)
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("Achievements", "Reviews", "Skills Offered").forEach { sec ->
              val isSelected = selectedSection == sec
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .clickable { selectedSection = sec },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) SkillCirclePrimary else SkillCircleCream
              ) {
                Text(
                  text = sec,
                  color = if (isSelected) Color.White else SkillCircleTextDark,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  modifier = Modifier.padding(vertical = 8.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }
        }

        // Section Content
        when (selectedSection) {
          "Achievements" -> {
            items(achievements, key = { it.id }) { ach ->
              AchievementCard(achievement = ach)
            }
          }

          "Reviews" -> {
            if (myReviews.isEmpty()) {
              item {
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = SkillCircleCream,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = "No reviews yet. Complete your first skill exchange to receive feedback!",
                    color = SkillCircleTextSecondary,
                    fontSize = 13.5.sp,
                    modifier = Modifier.padding(16.dp)
                  )
                }
              }
            } else {
              items(myReviews, key = { it.id }) { rev ->
                ReviewCard(review = rev)
              }
            }
          }

          "Skills Offered" -> {
            if (mySkills.isEmpty()) {
              item {
                Surface(
                  shape = RoundedCornerShape(16.dp),
                  color = SkillCircleCream,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = "You haven't offered any skills yet.",
                    color = SkillCircleTextSecondary,
                    fontSize = 13.5.sp,
                    modifier = Modifier.padding(16.dp)
                  )
                }
              }
            } else {
              items(mySkills, key = { it.id }) { skill ->
                SkillCard(skill = skill, onSkillClick = onNavigateMySkills)
              }
            }
          }
        }
      }
    }
  }
}
