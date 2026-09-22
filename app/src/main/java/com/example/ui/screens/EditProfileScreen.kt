package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryButton
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsState()

  var name by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
  var phone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }
  var city by remember(currentUser) { mutableStateOf(currentUser?.city ?: "") }
  var neighborhood by remember(currentUser) { mutableStateOf(currentUser?.neighborhood ?: "") }
  var bio by remember(currentUser) { mutableStateOf(currentUser?.bio ?: "") }
  var isSaving by remember { mutableStateOf(false) }

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("edit_profile_back")) {
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
      UserAvatar(name = name.ifBlank { "Neighbor" }, size = 70)

      Spacer(modifier = Modifier.height(20.dp))

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
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("edit_name_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("edit_phone_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = city,
              onValueChange = { city = it },
              label = { Text("City") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("edit_city_input"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = neighborhood,
              onValueChange = { neighborhood = it },
              label = { Text("Neighborhood") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("edit_neighborhood_input"),
              shape = RoundedCornerShape(12.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            placeholder = { Text("Tell your neighbors about your background and interests...") },
            minLines = 3,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth().testTag("edit_bio_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(20.dp))

          PrimaryButton(
            text = "Save Profile Changes",
            onClick = {
              isSaving = true
              viewModel.updateProfile(
                name = name.trim(),
                phone = phone.trim(),
                city = city.trim(),
                neighborhood = neighborhood.trim(),
                bio = bio.trim(),
                onResult = { success, _ ->
                  isSaving = false
                  if (success) {
                    onNavigateBack()
                  }
                }
              )
            },
            isLoading = isSaving,
            testTag = "save_profile_button"
          )
        }
      }
    }
  }
}
