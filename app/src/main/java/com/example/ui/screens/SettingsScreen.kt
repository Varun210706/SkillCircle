package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryButton
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit,
  onLogout: () -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsState()
  var showLogoutConfirm by remember { mutableStateOf(false) }
  var notifyExchanges by remember { mutableStateOf(true) }
  var notifyMessages by remember { mutableStateOf(true) }
  var resetMessage by remember { mutableStateOf<String?>(null) }

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = { Text("Settings", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("settings_back")) {
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
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Account Info Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
        border = BorderStroke(1.dp, CardBorder)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Account Information",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Logged in as:",
            style = MaterialTheme.typography.labelSmall.copy(color = SkillCircleTextSecondary)
          )
          Text(
            text = currentUser?.email ?: "Not logged in",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.SemiBold,
              color = SkillCircleTextDark
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Neighborhood:",
            style = MaterialTheme.typography.labelSmall.copy(color = SkillCircleTextSecondary)
          )
          Text(
            text = "${currentUser?.neighborhood ?: "N/A"}, ${currentUser?.city ?: ""}",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.SemiBold,
              color = SkillCircleTextDark
            )
          )
        }
      }

      // Notifications Settings
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
        border = BorderStroke(1.dp, CardBorder)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Notification Preferences",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Exchange Request Alerts", fontSize = 14.sp, color = SkillCircleTextDark)
            Switch(
              checked = notifyExchanges,
              onCheckedChange = { notifyExchanges = it },
              colors = SwitchDefaults.colors(checkedThumbColor = SkillCirclePrimary, checkedTrackColor = SkillCircleLightGreen)
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Direct Message Alerts", fontSize = 14.sp, color = SkillCircleTextDark)
            Switch(
              checked = notifyMessages,
              onCheckedChange = { notifyMessages = it },
              colors = SwitchDefaults.colors(checkedThumbColor = SkillCirclePrimary, checkedTrackColor = SkillCircleLightGreen)
            )
          }
        }
      }

      // Neighborhood Data & Sync
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
        border = BorderStroke(1.dp, CardBorder)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Community Data & Persistence",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "SkillCircle syncs data locally and with Firebase Firestore. You can refresh sample neighborhood data if needed.",
            style = MaterialTheme.typography.bodySmall.copy(color = SkillCircleTextSecondary, lineHeight = 18.sp)
          )

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = {
              viewModel.resetDemo()
              resetMessage = "Sample skills & community data refreshed!"
            },
            modifier = Modifier.fillMaxWidth().testTag("settings_reset_data_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkillCircleSecondary)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Re-seed Neighborhood Sample Data")
          }

          if (resetMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(resetMessage ?: "", color = SkillCirclePrimary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }

      // About SkillCircle
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
        border = BorderStroke(1.dp, CardBorder)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "About SkillCircle",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "SkillCircle is a neighborhood skill exchange platform designed to foster mutual aid, local connections, and community skill-sharing without commercial barriers.\n\nVersion 1.0.0",
            style = MaterialTheme.typography.bodySmall.copy(color = SkillCircleTextSecondary, lineHeight = 18.sp)
          )
        }
      }

      // Logout Button
      OutlinedButton(
        onClick = { showLogoutConfirm = true },
        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("settings_logout_button"),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.5.dp, StatusRejected),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRejected)
      ) {
        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Log Out", fontWeight = FontWeight.Bold)
      }
    }
  }

  // Logout confirmation dialog
  if (showLogoutConfirm) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirm = false },
      containerColor = SkillCircleCream,
      titleContentColor = SkillCirclePrimary,
      textContentColor = SkillCircleTextDark,
      title = { Text("Log Out?", fontWeight = FontWeight.Bold, color = SkillCirclePrimary) },
      text = { Text("Are you sure you want to log out of SkillCircle?", color = SkillCircleTextDark) },
      confirmButton = {
        Button(
          onClick = {
            viewModel.logout()
            showLogoutConfirm = false
            onLogout()
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusRejected, contentColor = Color.White)
        ) {
          Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutConfirm = false }) {
          Text("Cancel", color = SkillCirclePrimary, fontWeight = FontWeight.SemiBold)
        }
      }
    )
  }
}
