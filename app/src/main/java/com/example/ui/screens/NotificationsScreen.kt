package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyState
import com.example.ui.components.NotificationCard
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit,
  onNavigateRequestDetails: (requestId: String) -> Unit,
  onNavigateChat: ((conversationId: String, otherUserName: String, otherUserId: String) -> Unit)? = null
) {
  val notifications by viewModel.notifications.collectAsState()
  val requests by viewModel.requests.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()
  val processingRequests by viewModel.processingRequests.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    containerColor = SkillCircleBackground,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = { Text("Notifications", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("notifications_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          if (notifications.any { !it.isRead }) {
            TextButton(
              onClick = { viewModel.markAllNotificationsRead() },
              modifier = Modifier.testTag("notifications_mark_all_read")
            ) {
              Text("Mark all read", color = SkillCirclePrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleBackground)
      )
    }
  ) { innerPadding ->
    if (notifications.isEmpty()) {
      EmptyState(
        title = "You're all caught up!",
        message = "No new notifications right now. Check back when neighbors request or message you.",
        modifier = Modifier.padding(innerPadding)
      )
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("notifications_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(notifications, key = { it.id }) { notif ->
          val associatedRequest = requests.firstOrNull { it.id == notif.referenceId }
          val isProcessing = processingRequests.contains(notif.referenceId)
          val otherUser = if (associatedRequest != null) {
            if (associatedRequest.providerId == currentUser?.uid) {
              Pair(associatedRequest.requesterId, associatedRequest.requesterName)
            } else {
              Pair(associatedRequest.providerId, associatedRequest.providerName)
            }
          } else null

          NotificationCard(
            notification = notif,
            request = associatedRequest,
            isProcessing = isProcessing,
            onClick = {
              viewModel.markNotificationRead(notif.id)
              if (notif.referenceId.isNotBlank()) {
                onNavigateRequestDetails(notif.referenceId)
              }
            },
            onAcceptClick = {
              viewModel.acceptRequest(notif.referenceId) { success, msg ->
                coroutineScope.launch {
                  snackbarHostState.showSnackbar(msg)
                }
              }
            },
            onRejectClick = {
              viewModel.rejectRequest(notif.referenceId) { success, msg ->
                coroutineScope.launch {
                  snackbarHostState.showSnackbar(msg)
                }
              }
            },
            onMessageClick = if (otherUser != null && onNavigateChat != null && associatedRequest != null) {
              {
                val convId = viewModel.getOrCreateConversation(otherUser.first, otherUser.second, associatedRequest.skillTitle)
                onNavigateChat(convId, otherUser.second, otherUser.first)
              }
            } else null
          )
        }
      }
    }
  }
}
