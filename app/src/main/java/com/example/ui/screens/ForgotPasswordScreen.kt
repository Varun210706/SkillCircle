package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
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
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit
) {
  var email by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var successMessage by remember { mutableStateOf<String?>(null) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Reset Password",
            fontWeight = FontWeight.Bold,
            color = SkillCircleTextDark
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("forgot_password_back")) {
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
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

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
          Box(
            modifier = Modifier
              .size(52.dp)
              .background(SkillCircleLightGreen, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.LockReset,
              contentDescription = null,
              tint = SkillCirclePrimary,
              modifier = Modifier.size(30.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Reset Password",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark,
              fontSize = 22.sp
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Enter the email associated with your SkillCircle account and we will send you a link to reset your password.",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = SkillCircleTextSecondary,
              fontSize = 14.sp,
              lineHeight = 20.sp
            )
          )

          Spacer(modifier = Modifier.height(24.dp))

          OutlinedTextField(
            value = email,
            onValueChange = {
              email = it
              errorMessage = null
            },
            label = { Text("Email") },
            placeholder = { Text("you@neighborhood.local") },
            leadingIcon = {
              Icon(Icons.Default.Email, contentDescription = null, tint = SkillCircleSecondary)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("forgot_password_email_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = SkillCirclePrimary,
              unfocusedBorderColor = CardBorder
            )
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = StatusRejectedBg,
              modifier = Modifier.fillMaxWidth().testTag("forgot_password_error_message")
            ) {
              Text(
                text = errorMessage ?: "",
                color = StatusRejected,
                fontSize = 13.sp,
                modifier = Modifier.padding(12.dp)
              )
            }
          }

          if (successMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = StatusAcceptedBg,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("forgot_password_success_banner")
            ) {
              Text(
                text = successMessage ?: "",
                color = StatusAccepted,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                modifier = Modifier.padding(12.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          PrimaryButton(
            text = "Reset Password",
            onClick = {
              if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                errorMessage = "Please enter a valid email address."
                return@PrimaryButton
              }
              isLoading = true
              errorMessage = null
              viewModel.forgotPassword(email.trim()) { success, _ ->
                isLoading = false
                if (success) {
                  successMessage = "Password reset email sent. Please check your inbox."
                } else {
                  errorMessage = "Unable to send password reset email. Please try again."
                }
              }
            },
            isLoading = isLoading,
            enabled = !isLoading,
            testTag = "forgot_password_submit_button"
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("forgot_password_back_to_login_button"),
            shape = RoundedCornerShape(25.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SkillCirclePrimary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SkillCirclePrimary)
          ) {
            Text("Back to Login", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
          }
        }
      }
    }
  }
}
