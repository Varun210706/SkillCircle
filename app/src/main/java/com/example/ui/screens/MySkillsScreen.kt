package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Skill
import com.example.ui.components.EmptyState
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySkillsScreen(
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit,
  onNavigateCreateSkill: () -> Unit,
  onNavigateSkillDetails: (skillId: String) -> Unit
) {
  val mySkills by viewModel.mySkills.collectAsState()
  var skillToDeactivate by remember { mutableStateOf<Skill?>(null) }

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = { Text("My Offered Skills", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("my_skills_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleBackground)
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onNavigateCreateSkill,
        containerColor = SkillCirclePrimary,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("my_skills_fab_add")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Skill")
      }
    }
  ) { innerPadding ->
    if (mySkills.isEmpty()) {
      EmptyState(
        title = "No Skills Offered Yet",
        message = "Share your talents, tutoring, hobbies or repair expertise with your neighbors.",
        actionText = "Offer a Skill",
        onActionClick = onNavigateCreateSkill,
        modifier = Modifier.padding(innerPadding)
      )
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(mySkills, key = { it.id }) { skill ->
          Card(
            modifier = Modifier.fillMaxWidth().testTag("my_skill_card_${skill.id}"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
            border = BorderStroke(1.dp, CardBorder)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (skill.active) StatusAcceptedBg else CardBorder
                ) {
                  Text(
                    text = if (skill.active) "Active Offering" else "Deactivated",
                    color = if (skill.active) StatusAccepted else SkillCircleTextSecondary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = SkillCircleCream
                ) {
                  Text(
                    text = skill.category,
                    color = SkillCircleTextDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = skill.title,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = SkillCircleTextDark,
                  fontSize = 17.sp
                )
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = skill.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = SkillCircleTextSecondary,
                  fontSize = 13.sp
                ),
                maxLines = 2
              )

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Format: ${skill.exchangeType}",
                  color = SkillCircleTextSecondary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  if (skill.active) {
                    IconButton(
                      onClick = { skillToDeactivate = skill },
                      modifier = Modifier.testTag("deactivate_skill_button_${skill.id}")
                    ) {
                      Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Deactivate Skill",
                        tint = StatusRejected
                      )
                    }
                  }

                  TextButton(
                    onClick = { onNavigateSkillDetails(skill.id) },
                    modifier = Modifier.testTag("view_details_button_${skill.id}")
                  ) {
                    Text("View", color = SkillCirclePrimary, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Deactivation confirmation dialog
  if (skillToDeactivate != null) {
    AlertDialog(
      onDismissRequest = { skillToDeactivate = null },
      containerColor = SkillCircleCream,
      titleContentColor = SkillCirclePrimary,
      textContentColor = SkillCircleTextDark,
      title = { Text("Deactivate Skill?", fontWeight = FontWeight.Bold, color = SkillCirclePrimary) },
      text = {
        Text("Are you sure you want to deactivate '${skillToDeactivate?.title}'? It will no longer appear in neighbor searches.", color = SkillCircleTextDark)
      },
      confirmButton = {
        Button(
          onClick = {
            skillToDeactivate?.let { viewModel.deactivateSkill(it.id) }
            skillToDeactivate = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusRejected, contentColor = Color.White)
        ) {
          Text("Deactivate", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { skillToDeactivate = null }) {
          Text("Cancel", color = SkillCirclePrimary, fontWeight = FontWeight.SemiBold)
        }
      }
    )
  }
}
