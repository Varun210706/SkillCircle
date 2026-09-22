package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UiState
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SkillCircleLogo
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@Composable
fun LoginScreen(
  viewModel: SkillCircleViewModel,
  onNavigateRegister: () -> Unit,
  onNavigateForgotPassword: () -> Unit,
  onLoginSuccess: () -> Unit
) {
  var email by remember { mutableStateOf("elena@neighborhood.local") }
  var password by remember { mutableStateOf("password123") }
  var passwordVisible by remember { mutableStateOf(false) }

  val authState by viewModel.authState.collectAsState()

  LaunchedEffect(authState) {
    if (authState is UiState.Success) {
      onLoginSuccess()
    }
  }

  Scaffold(
    containerColor = SkillCircleBackground
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      SkillCircleLogo()

      Spacer(modifier = Modifier.height(28.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
        ) {
          Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark,
              fontSize = 24.sp
            )
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Connect, share and grow with your community.",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = SkillCircleTextSecondary,
              fontSize = 14.sp,
              lineHeight = 20.sp
            )
          )

          Spacer(modifier = Modifier.height(22.dp))

          // Email Field
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("you@neighborhood.local") },
            leadingIcon = {
              Icon(Icons.Default.Email, contentDescription = null, tint = SkillCircleSecondary)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_email_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = SkillCirclePrimary,
              unfocusedBorderColor = CardBorder
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Password Field
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = null, tint = SkillCircleSecondary)
            },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = if (passwordVisible) "Hide password" else "Show password",
                  tint = SkillCircleTextSecondary
                )
              }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_password_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = SkillCirclePrimary,
              unfocusedBorderColor = CardBorder
            )
          )

          // Forgot Password Button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(
              onClick = onNavigateForgotPassword,
              modifier = Modifier.testTag("login_forgot_password_button")
            ) {
              Text(
                text = "Forgot Password?",
                color = SkillCircleSecondary,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          // Error Display
          if (authState is UiState.Error) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = StatusRejectedBg,
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("login_error_message")
            ) {
              Text(
                text = (authState as UiState.Error).message,
                color = StatusRejected,
                fontSize = 13.sp,
                modifier = Modifier.padding(12.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Login Button
          PrimaryButton(
            text = "Login",
            onClick = {
              if (authState !is UiState.Loading) {
                viewModel.login(email.trim(), password)
              }
            },
            enabled = authState !is UiState.Loading,
            isLoading = authState is UiState.Loading,
            testTag = "login_submit_button"
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Register Link
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = "Don't have an account?",
          color = SkillCircleTextSecondary,
          fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Sign Up",
          color = SkillCirclePrimary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier
            .clickable { onNavigateRegister() }
            .testTag("login_create_account_link")
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
