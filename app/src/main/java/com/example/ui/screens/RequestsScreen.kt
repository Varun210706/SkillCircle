package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyState
import com.example.ui.components.RequestCard
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
  viewModel: SkillCircleViewModel,
  onNavigateRequestDetails: (requestId: String) -> Unit,
  onNavigateDiscover: () -> Unit,
  onNavigateChat: ((conversationId: String, otherUserName: String, otherUserId: String) -> Unit)? = null
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Received, 1 = Sent

  LaunchedEffect(Unit) {
    viewModel.reloadRequests()
  }

  LaunchedEffect(selectedTab) {
    viewModel.reloadRequests()
  }

  val receivedRequests by viewModel.receivedRequests.collectAsState()
  val sentRequests by viewModel.sentRequests.collectAsState()
  val processingRequests by viewModel.processingRequests.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    containerColor = SkillCircleBackground,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = { Text("Skill Exchanges", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleBackground)
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Tab selector: Received vs Sent
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = SkillCircleSurface,
        contentColor = SkillCirclePrimary,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = SkillCirclePrimary
          )
        },
        modifier = Modifier.padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Text(
              text = "Received (${receivedRequests.size})",
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
              fontSize = 14.sp
            )
          },
          modifier = Modifier.testTag("tab_received_requests")
        )

        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              text = "Sent (${sentRequests.size})",
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
              fontSize = 14.sp
            )
          },
          modifier = Modifier.testTag("tab_sent_requests")
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (selectedTab == 0) {
        // Received Requests
        if (receivedRequests.isEmpty()) {
          EmptyState(
            title = "No Received Requests",
            message = "When neighbors request your skills, they will appear here for you to accept or decline.",
            modifier = Modifier.weight(1f)
          )
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .weight(1f)
              .testTag("received_requests_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(receivedRequests, key = { it.id }) { req ->
              RequestCard(
                request = req,
                isReceived = true,
                onRequestClick = { onNavigateRequestDetails(req.id) },
                onAcceptClick = {
                  viewModel.acceptRequest(req.id) { _, msg ->
                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                  }
                },
                onRejectClick = {
                  viewModel.rejectRequest(req.id) { _, msg ->
                    coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                  }
                },
                onCompleteClick = { viewModel.completeExchange(req.id) },
                onMessageClick = onNavigateChat?.let { navigateChat ->
                  {
                    val convId = viewModel.getOrCreateConversation(req.requesterId, req.requesterName, req.skillTitle)
                    navigateChat(convId, req.requesterName, req.requesterId)
                  }
                },
                isProcessing = processingRequests.contains(req.id)
              )
            }
          }
        }
      } else {
        // Sent Requests
        if (sentRequests.isEmpty()) {
          EmptyState(
            title = "No Sent Requests",
            message = "You haven't requested any skills yet. Explore nearby neighbors and connect!",
            actionText = "Discover Skills",
            onActionClick = onNavigateDiscover,
            modifier = Modifier.weight(1f)
          )
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .weight(1f)
              .testTag("sent_requests_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(sentRequests, key = { it.id }) { req ->
              RequestCard(
                request = req,
                isReceived = false,
                onRequestClick = { onNavigateRequestDetails(req.id) },
                onCompleteClick = { viewModel.completeExchange(req.id) },
                onMessageClick = onNavigateChat?.let { navigateChat ->
                  {
                    val convId = viewModel.getOrCreateConversation(req.providerId, req.providerName, req.skillTitle)
                    navigateChat(convId, req.providerName, req.providerId)
                  }
                }
              )
            }
          }
        }
      }
    }
  }
}
