package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillCategories
import com.example.data.model.UiState
import com.example.ui.components.CategoryChip
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestHelpScreen(
  skillId: String? = null,
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit,
  onRequestSubmitted: (requestId: String) -> Unit
) {
  val targetSkill = remember(skillId) { if (!skillId.isNullOrBlank()) viewModel.getSkill(skillId) else null }
  val currentUser by viewModel.currentUser.collectAsState()
  val requestActionState by viewModel.requestActionState.collectAsState()

  var title by remember {
    mutableStateOf(if (targetSkill != null) "Request for ${targetSkill.title}" else "")
  }
  var selectedCategory by remember {
    mutableStateOf(targetSkill?.category ?: SkillCategories.list[0])
  }
  var description by remember { mutableStateOf("") }
  var preferredDate by remember { mutableStateOf("This coming weekend") }
  var preferredTime by remember { mutableStateOf("Afternoon (2:00 PM)") }
  var neighborhood by remember {
    mutableStateOf(targetSkill?.neighborhood ?: (currentUser?.neighborhood ?: "Maplewood Heights"))
  }
  var localError by remember { mutableStateOf<String?>(null) }
  var showDatePicker by remember { mutableStateOf(false) }
  var showTimePicker by remember { mutableStateOf(false) }
  val datePickerState = rememberDatePickerState()
  val timePickerState = rememberTimePickerState()

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = {
          Text(
            if (targetSkill != null) "Request Skill" else "Ask for Help",
            fontWeight = FontWeight.Bold,
            color = SkillCircleTextDark
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("request_help_back")) {
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
      if (targetSkill != null) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = SkillCircleLightGreen,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Sending request to:",
              style = MaterialTheme.typography.labelSmall.copy(color = SkillCirclePrimary, fontWeight = FontWeight.Bold)
            )
            Text(
              text = "${targetSkill.ownerName} • ${targetSkill.title}",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = SkillCircleTextDark
              )
            )
          }
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

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
          // What do you need help with?
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("What do you need help with? *") },
            placeholder = { Text("e.g. Help repairing flat bicycle tire") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("request_title_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Category
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

          // Detailed Description
          OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Describe what you need *") },
            placeholder = { Text("Explain what you want to learn, tools you have, or the specific problem...") },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth().testTag("request_description_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Preferred Date & Time
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = preferredDate,
              onValueChange = { preferredDate = it },
              label = { Text("Preferred Date") },
              leadingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                  Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", tint = SkillCircleSecondary)
                }
              },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("request_date_input"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = preferredTime,
              onValueChange = { preferredTime = it },
              label = { Text("Preferred Time") },
              leadingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                  Icon(Icons.Default.Schedule, contentDescription = "Pick Time", tint = SkillCircleSecondary)
                }
              },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("request_time_input"),
              shape = RoundedCornerShape(12.dp)
            )
          }

          // Themed Date Picker Dialog
          if (showDatePicker) {
            DatePickerDialog(
              onDismissRequest = { showDatePicker = false },
              confirmButton = {
                Button(
                  onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                      val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                      preferredDate = sdf.format(java.util.Date(millis))
                    }
                    showDatePicker = false
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = SkillCirclePrimary, contentColor = Color.White)
                ) {
                  Text("Select", color = Color.White, fontWeight = FontWeight.Bold)
                }
              },
              dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                  Text("Cancel", color = SkillCirclePrimary, fontWeight = FontWeight.SemiBold)
                }
              },
              colors = DatePickerDefaults.colors(
                containerColor = SkillCircleCream
              )
            ) {
              DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                  containerColor = SkillCircleCream,
                  titleContentColor = SkillCirclePrimary,
                  headlineContentColor = SkillCirclePrimary,
                  weekdayContentColor = SkillCircleSecondary,
                  subheadContentColor = SkillCircleTextDark,
                  yearContentColor = SkillCircleTextDark,
                  currentYearContentColor = SkillCirclePrimary,
                  selectedYearContentColor = Color.White,
                  selectedYearContainerColor = SkillCirclePrimary,
                  dayContentColor = SkillCircleTextDark,
                  selectedDayContentColor = Color.White,
                  selectedDayContainerColor = SkillCirclePrimary,
                  todayContentColor = SkillCirclePrimary,
                  todayDateBorderColor = SkillCirclePrimary
                )
              )
            }
          }

          // Themed Time Picker Dialog
          if (showTimePicker) {
            AlertDialog(
              onDismissRequest = { showTimePicker = false },
              containerColor = SkillCircleCream,
              titleContentColor = SkillCirclePrimary,
              title = { Text("Select Time", fontWeight = FontWeight.Bold, color = SkillCirclePrimary) },
              text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                  TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                      clockDialColor = SkillCircleLightGreen,
                      clockDialSelectedContentColor = Color.White,
                      clockDialUnselectedContentColor = SkillCircleTextDark,
                      selectorColor = SkillCirclePrimary,
                      containerColor = SkillCircleCream,
                      periodSelectorBorderColor = SkillCircleSecondary,
                      periodSelectorSelectedContainerColor = SkillCirclePrimary,
                      periodSelectorUnselectedContainerColor = SkillCircleCream,
                      periodSelectorSelectedContentColor = Color.White,
                      periodSelectorUnselectedContentColor = SkillCircleTextDark,
                      timeSelectorSelectedContainerColor = SkillCirclePrimary,
                      timeSelectorUnselectedContainerColor = SkillCircleLightGreen,
                      timeSelectorSelectedContentColor = Color.White,
                      timeSelectorUnselectedContentColor = SkillCircleTextDark
                    )
                  )
                }
              },
              confirmButton = {
                Button(
                  onClick = {
                    val cal = java.util.Calendar.getInstance()
                    cal.set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                    cal.set(java.util.Calendar.MINUTE, timePickerState.minute)
                    val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                    preferredTime = sdf.format(cal.time)
                    showTimePicker = false
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = SkillCirclePrimary, contentColor = Color.White)
                ) {
                  Text("Select", color = Color.White, fontWeight = FontWeight.Bold)
                }
              },
              dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                  Text("Cancel", color = SkillCirclePrimary, fontWeight = FontWeight.SemiBold)
                }
              }
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Neighborhood
          OutlinedTextField(
            value = neighborhood,
            onValueChange = { neighborhood = it },
            label = { Text("Neighborhood / Area") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("request_neighborhood_input"),
            shape = RoundedCornerShape(12.dp)
          )

          // Error message
          val error = localError ?: (requestActionState as? UiState.Error)?.message
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
            text = "Submit Skill Request",
            onClick = {
              localError = null
              if (title.isBlank() || description.isBlank()) {
                localError = "Please describe what you need help with."
                return@PrimaryButton
              }
              viewModel.createRequest(
                skillId = targetSkill?.id ?: "",
                title = title.trim(),
                description = description.trim(),
                category = selectedCategory,
                date = preferredDate.trim(),
                time = preferredTime.trim(),
                neighborhood = neighborhood.trim(),
                onSuccess = { req -> onRequestSubmitted(req.id) }
              )
            },
            isLoading = requestActionState is UiState.Loading,
            testTag = "submit_request_button"
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
