package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyState
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
  viewModel: SkillCircleViewModel,
  onNavigateChat: (conversationId: String, otherName: String, otherId: String) -> Unit,
  onNavigateDiscover: () -> Unit
) {
  val conversations by viewModel.conversations.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = { Text("Neighborhood Chats", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleBackground)
      )
    }
  ) { innerPadding ->
    if (conversations.isEmpty()) {
      EmptyState(
        title = "No Messages Yet",
        message = "When you request a skill or accept one, you can chat with neighbors here to coordinate sessions.",
        actionText = "Find Neighbors",
        onActionClick = onNavigateDiscover,
        modifier = Modifier.padding(innerPadding)
      )
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("conversations_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(conversations, key = { it.id }) { conv ->
          val otherId = conv.participantIds.firstOrNull { it != currentUser?.uid } ?: ""
          val otherName = conv.participantNames[otherId] ?: "Neighbor"
          val isUnread = conv.lastSenderId.isNotEmpty() && conv.lastSenderId != currentUser?.uid

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .clickable { onNavigateChat(conv.id, otherName, otherId) }
              .testTag("conversation_item_${conv.id}"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isUnread) SkillCircleCream else SkillCircleSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = BorderStroke(1.dp, CardBorder)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              UserAvatar(name = otherName, size = 48)

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = otherName,
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = SkillCircleTextDark,
                      fontSize = 15.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )

                  val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(conv.lastMessageTimestamp))
                  Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = SkillCircleTextSecondary,
                      fontSize = 11.5.sp
                    )
                  )
                }

                if (conv.skillTitle.isNotBlank()) {
                  Text(
                    text = "Re: ${conv.skillTitle}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = SkillCirclePrimary,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 11.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = conv.lastMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      color = if (isUnread) SkillCircleTextDark else SkillCircleTextSecondary,
                      fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                      fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                  )

                  if (isUnread) {
                    Box(
                      modifier = Modifier
                        .padding(start = 8.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(SkillCirclePrimary)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
