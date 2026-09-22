package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.ExchangeTypes
import com.example.data.model.SkillCategories
import com.example.data.model.UiState
import com.example.ui.components.CategoryChip
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSkillScreen(
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit,
  onSkillCreated: () -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val skillActionState by viewModel.skillActionState.collectAsState()

  var title by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(SkillCategories.list[0]) }
  var description by remember { mutableStateOf("") }
  var experience by remember { mutableStateOf("Experienced practitioner") }
  var availability by remember { mutableStateOf("Weekends & Evenings") }
  var exchangeType by remember { mutableStateOf(ExchangeTypes.SKILL_FOR_SKILL) }
  var neighborhood by remember { mutableStateOf(currentUser?.neighborhood ?: "Maplewood Heights") }
  var localError by remember { mutableStateOf<String?>(null) }

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = { Text("Offer a Skill", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("create_skill_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleBackground)
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Share what you know with your local community. Earn community points and meet great neighbors!",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = SkillCircleTextSecondary,
          fontSize = 13.5.sp,
          lineHeight = 19.sp
        ),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          // Skill Title
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Skill Title *") },
            placeholder = { Text("e.g. Java Programming Tutoring") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("create_skill_title_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Category Selector
          Text(
            text = "Category *",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = SkillCircleTextDark,
              fontSize = 13.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(SkillCategories.list) { cat ->
              CategoryChip(
                category = cat,
                isSelected = selectedCategory == cat,
                onSelected = { selectedCategory = cat }
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Description
          OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description *") },
            placeholder = { Text("Explain what you can teach or help with, format of sessions, etc.") },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth().testTag("create_skill_description_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Experience
          OutlinedTextField(
            value = experience,
            onValueChange = { experience = it },
            label = { Text("Your Experience / Background") },
            placeholder = { Text("e.g. 5 years hobbyist / professional") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("create_skill_experience_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Availability
          OutlinedTextField(
            value = availability,
            onValueChange = { availability = it },
            label = { Text("Availability") },
            placeholder = { Text("e.g. Weekends, Tuesday evenings") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("create_skill_availability_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Preferred Exchange Type
          Text(
            text = "Preferred Exchange Format *",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = SkillCircleTextDark,
              fontSize = 13.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ExchangeTypes.list.forEach { type ->
              val isSelected = exchangeType == type
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .clickable { exchangeType = type },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) SkillCirclePrimary else SkillCircleCream
              ) {
                Text(
                  text = type,
                  color = if (isSelected) Color.White else SkillCircleTextDark,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Neighborhood
          OutlinedTextField(
            value = neighborhood,
            onValueChange = { neighborhood = it },
            label = { Text("Neighborhood / Area") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("create_skill_neighborhood_input"),
            shape = RoundedCornerShape(12.dp)
          )

          // Error Display
          val error = localError ?: (skillActionState as? UiState.Error)?.message
          if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = StatusRejectedBg,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = error,
                color = StatusRejected,
                fontSize = 13.sp,
                modifier = Modifier.padding(10.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          PrimaryButton(
            text = "Publish Skill (+5 Community Pts)",
            onClick = {
              localError = null
              if (title.isBlank() || description.isBlank()) {
                localError = "Please fill in title and description."
                return@PrimaryButton
              }
              viewModel.createSkill(
                title = title.trim(),
                category = selectedCategory,
                description = description.trim(),
                experience = experience.trim(),
                availability = availability.trim(),
                exchangeType = exchangeType,
                neighborhood = neighborhood.trim(),
                onSuccess = onSkillCreated
              )
            },
            isLoading = skillActionState is UiState.Loading,
            testTag = "create_skill_submit_button"
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
