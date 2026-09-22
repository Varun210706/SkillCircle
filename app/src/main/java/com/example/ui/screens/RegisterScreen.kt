package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UiState
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
  viewModel: SkillCircleViewModel,
  onNavigateLogin: () -> Unit,
  onRegisterSuccess: () -> Unit
) {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var city by remember { mutableStateOf("Oakridge") }
  var neighborhood by remember { mutableStateOf("Maplewood Heights") }
  var bio by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var localError by remember { mutableStateOf<String?>(null) }

  val authState by viewModel.authState.collectAsState()

  LaunchedEffect(authState) {
    if (authState is UiState.Success) {
      onRegisterSuccess()
    }
  }

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Create Account",
            fontWeight = FontWeight.Bold,
            color = SkillCircleTextDark
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateLogin) {
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
        text = "Join Your Local Skill Network",
        style = MaterialTheme.typography.titleMedium.copy(
          color = SkillCircleTextSecondary,
          fontSize = 15.sp
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
          // Full Name
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            placeholder = { Text("e.g. Maya Lin") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SkillCircleSecondary) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("register_name_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Email
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("name@neighborhood.local") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SkillCircleSecondary) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("register_email_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Phone
          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            placeholder = { Text("+1 (555) 123-4567") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SkillCircleSecondary) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth().testTag("register_phone_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // City & Neighborhood Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = city,
              onValueChange = { city = it },
              label = { Text("City") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("register_city_input"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = neighborhood,
              onValueChange = { neighborhood = it },
              label = { Text("Neighborhood") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("register_neighborhood_input"),
              shape = RoundedCornerShape(12.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Bio
          OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Short Bio") },
            placeholder = { Text("Tell neighbors what you enjoy doing or learning...") },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth().testTag("register_bio_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Password
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SkillCircleSecondary) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null
                )
              }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().testTag("register_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Confirm Password
          OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SkillCircleSecondary) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().testTag("register_confirm_password_input"),
            shape = RoundedCornerShape(12.dp)
          )

          // Local or VM Error display
          val errorMessage = localError ?: (authState as? UiState.Error)?.message
          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = StatusRejectedBg,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = errorMessage,
                color = StatusRejected,
                fontSize = 13.sp,
                modifier = Modifier.padding(10.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          PrimaryButton(
            text = "Sign Up",
            onClick = {
              localError = null
              if (name.isBlank()) {
                localError = "Full Name cannot be empty."
                return@PrimaryButton
              }
              if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                localError = "Please enter a valid email address."
                return@PrimaryButton
              }
              if (phone.isBlank()) {
                localError = "Phone number cannot be empty."
                return@PrimaryButton
              }
              if (city.isBlank()) {
                localError = "City cannot be empty."
                return@PrimaryButton
              }
              if (neighborhood.isBlank()) {
                localError = "Neighborhood cannot be empty."
                return@PrimaryButton
              }
              if (password.length < 6) {
                localError = "Password must be at least 6 characters long."
                return@PrimaryButton
              }
              if (password != confirmPassword) {
                localError = "Confirm Password must match Password."
                return@PrimaryButton
              }
              viewModel.register(
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                password = password,
                city = city.trim(),
                neighborhood = neighborhood.trim(),
                bio = bio.trim()
              )
            },
            isLoading = authState is UiState.Loading,
            testTag = "register_submit_button"
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text("Already have an account?", color = SkillCircleTextSecondary, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Log in",
          color = SkillCirclePrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.clickable { onNavigateLogin() }.testTag("register_login_link")
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
