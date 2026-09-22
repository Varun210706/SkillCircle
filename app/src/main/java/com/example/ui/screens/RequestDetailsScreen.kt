package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RequestStatus
import com.example.ui.components.PrimaryButton
import com.example.ui.components.RatingStars
import com.example.ui.components.SecondaryButton
import com.example.ui.components.StatusBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsScreen(
  requestId: String,
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit,
  onNavigateChat: (conversationId: String, otherName: String, otherId: String) -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val allRequests by viewModel.requests.collectAsState()
  val processingRequests by viewModel.processingRequests.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  val request = remember(requestId, allRequests) { viewModel.getRequest(requestId) }
  val isProcessing = request != null && processingRequests.contains(request.id)

  var showReviewDialog by remember { mutableStateOf(false) }
  var reviewRating by remember { mutableIntStateOf(5) }
  var reviewComment by remember { mutableStateOf("") }
  var reviewSubmitted by remember { mutableStateOf(false) }

  val isProvider = currentUser?.uid == request?.providerId
  val isRequester = currentUser?.uid == request?.requesterId

  val otherUserName = if (isProvider) request?.requesterName else request?.providerName
  val otherUserId = if (isProvider) request?.requesterId else request?.providerId

  Scaffold(
    containerColor = SkillCircleBackground,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = { Text("Request Details", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("request_details_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleBackground)
      )
    }
  ) { innerPadding ->
    if (request == null) {
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        Text("Request not found.")
      }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Status & Category Header Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              StatusBadge(status = request.status)
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = SkillCircleCream
              ) {
                Text(
                  text = request.category,
                  color = SkillCircleTextDark,
                  fontWeight = FontWeight.Medium,
                  fontSize = 12.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = request.skillTitle,
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = SkillCircleTextDark,
                fontSize = 20.sp
              )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = request.description,
              style = MaterialTheme.typography.bodyLarge.copy(
                color = SkillCircleTextDark,
                fontSize = 14.5.sp,
                lineHeight = 21.sp
              )
            )
          }
        }

        // Participants Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Exchange Participants",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = SkillCircleTextDark
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Requester row
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              UserAvatar(name = request.requesterName, size = 42)
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = request.requesterName,
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SkillCircleTextDark
                  )
                )
                Text(
                  text = "Requester • ${request.requesterNeighborhood}",
                  style = MaterialTheme.typography.bodySmall.copy(color = SkillCircleTextSecondary)
                )
              }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorder)

            // Provider row
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              UserAvatar(name = request.providerName, size = 42)
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = request.providerName,
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SkillCircleTextDark
                  )
                )
                Text(
                  text = "Skill Provider • ${request.neighborhood}",
                  style = MaterialTheme.typography.bodySmall.copy(color = SkillCircleTextSecondary)
                )
              }
            }
          }
        }

        // Timing & Location Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SkillCircleSecondary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text(text = "Preferred Date: ${request.date.ifBlank { "Flexible" }}", fontSize = 13.5.sp, color = SkillCircleTextDark)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Schedule, contentDescription = null, tint = SkillCircleSecondary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text(text = "Preferred Time: ${request.time.ifBlank { "Flexible" }}", fontSize = 13.5.sp, color = SkillCircleTextDark)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = SkillCircleSecondary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text(text = "Location: ${request.neighborhood}", fontSize = 13.5.sp, color = SkillCircleTextDark)
            }
          }
        }

        // Messaging Button
        if (!otherUserId.isNullOrBlank() && !otherUserName.isNullOrBlank()) {
          SecondaryButton(
            text = "Message $otherUserName",
            onClick = {
              val convId = viewModel.getOrCreateConversation(otherUserId, otherUserName, request.skillTitle)
              onNavigateChat(convId, otherUserName, otherUserId)
            },
            testTag = "request_details_message_button"
          )
        }

        // Status Actions Based on Role
        when (request.status) {
          RequestStatus.PENDING.name -> {
            if (isProvider) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedButton(
                  onClick = {
                    viewModel.rejectRequest(request.id) { _, msg ->
                      coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                  },
                  enabled = !isProcessing,
                  modifier = Modifier.weight(1f).height(48.dp).testTag("details_reject_button"),
                  shape = RoundedCornerShape(24.dp),
                  border = BorderStroke(1.5.dp, StatusRejected),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRejected)
                ) {
                  Text("Decline", fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = {
                    viewModel.acceptRequest(request.id) { _, msg ->
                      coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                  },
                  enabled = !isProcessing,
                  modifier = Modifier.weight(1f).height(48.dp).testTag("details_accept_button"),
                  shape = RoundedCornerShape(24.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SkillCirclePrimary)
                ) {
                  if (isProcessing) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(20.dp),
                      color = Color.White,
                      strokeWidth = 2.dp
                    )
                  } else {
                    Text("Accept", fontWeight = FontWeight.Bold)
                  }
                }
              }
            } else if (isRequester) {
              OutlinedButton(
                onClick = { viewModel.cancelRequest(request.id) },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("details_cancel_button"),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, StatusRejected),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRejected)
              ) {
                Text("Cancel Request", fontWeight = FontWeight.Bold)
              }
            }
          }

          RequestStatus.REJECTED.name -> {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = StatusRejectedBg,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "Request Declined",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = StatusRejected
                  )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "This skill exchange request was declined.",
                  style = MaterialTheme.typography.bodySmall.copy(color = SkillCircleTextDark)
                )
              }
            }
          }

          RequestStatus.ACCEPTED.name -> {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = { viewModel.startExchange(request.id) },
                modifier = Modifier.weight(1f).height(48.dp).testTag("details_start_exchange_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusInProgress)
              ) {
                Text("Start Exchange", fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { viewModel.completeExchange(request.id) },
                modifier = Modifier.weight(1f).height(48.dp).testTag("details_complete_exchange_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkillCirclePrimary)
              ) {
                Text("Mark Completed", fontWeight = FontWeight.Bold)
              }
            }
          }

          RequestStatus.IN_PROGRESS.name -> {
            PrimaryButton(
              text = "Mark Exchange Completed (+10 Pts)",
              onClick = { viewModel.completeExchange(request.id) },
              testTag = "details_mark_completed_in_progress"
            )
          }

          RequestStatus.COMPLETED.name -> {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = StatusCompletedBg,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "🎉 Skill Exchange Completed!",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = SkillCirclePrimary
                  )
                )
                Text(
                  text = "Thank you for building a stronger neighborhood community.",
                  style = MaterialTheme.typography.bodySmall.copy(color = SkillCircleTextDark)
                )
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryButton(
                  text = if (reviewSubmitted) "Review Submitted!" else "Rate & Review Neighbor (+5 Pts)",
                  onClick = { showReviewDialog = true },
                  enabled = !reviewSubmitted,
                  testTag = "leave_review_button"
                )
              }
            }
          }
        }
      }
    }
  }

  // Review Dialog
  if (showReviewDialog && otherUserId != null) {
    AlertDialog(
      onDismissRequest = { showReviewDialog = false },
      containerColor = SkillCircleCream,
      titleContentColor = SkillCirclePrimary,
      textContentColor = SkillCircleTextDark,
      title = { Text("Rate & Review $otherUserName", fontWeight = FontWeight.Bold, color = SkillCirclePrimary) },
      text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("How was your skill exchange experience?", color = SkillCircleTextDark)
          Spacer(modifier = Modifier.height(12.dp))

          // Star selector
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (1..5).forEach { star ->
              IconButton(onClick = { reviewRating = star }) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = "$star stars",
                  tint = if (star <= reviewRating) Color(0xFFF59E0B) else CardBorder,
                  modifier = Modifier.size(32.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = reviewComment,
            onValueChange = { reviewComment = it },
            label = { Text("Your Feedback") },
            placeholder = { Text("Write a brief compliment or helpful feedback...") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth().testTag("review_comment_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White,
              focusedTextColor = SkillCircleTextDark,
              unfocusedTextColor = SkillCircleTextDark,
              focusedBorderColor = SkillCirclePrimary,
              unfocusedBorderColor = CardBorder
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (reviewComment.isNotBlank()) {
              viewModel.submitReview(
                exchangeId = request?.id ?: "ex_custom",
                revieweeId = otherUserId,
                rating = reviewRating,
                comment = reviewComment.trim(),
                onSuccess = {
                  reviewSubmitted = true
                  showReviewDialog = false
                }
              )
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SkillCirclePrimary, contentColor = Color.White),
          modifier = Modifier.testTag("submit_review_dialog_button")
        ) {
          Text("Submit Review", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showReviewDialog = false }) {
          Text("Cancel", color = SkillCirclePrimary, fontWeight = FontWeight.SemiBold)
        }
      }
    )
  }
}
