package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  conversationId: String,
  recipientName: String,
  recipientId: String,
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val conversationMessages by viewModel.getMessagesFlow(conversationId).collectAsState()

  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  val messageCount = conversationMessages.size
  LaunchedEffect(messageCount) {
    if (messageCount > 0) {
      listState.animateScrollToItem(messageCount - 1)
    }
  }

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(name = recipientName, size = 36)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = recipientName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = SkillCircleTextDark
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(SkillCircleTeal)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Active Neighbor",
                  fontSize = 11.sp,
                  color = SkillCircleTextSecondary
                )
              }
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("chat_back_button")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleSurface)
      )
    },
    bottomBar = {
      Surface(
        color = SkillCircleSurface,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("Write a message...") },
            modifier = Modifier
              .weight(1f)
              .testTag("chat_message_input"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = SkillCircleBackground,
              unfocusedContainerColor = SkillCircleBackground,
              focusedBorderColor = SkillCirclePrimary,
              unfocusedBorderColor = CardBorder
            ),
            maxLines = 4
          )

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = {
              if (inputText.isNotBlank()) {
                viewModel.sendMessage(
                  conversationId = conversationId,
                  receiverId = recipientId,
                  text = inputText.trim()
                )
                val newCount = conversationMessages.size
                inputText = ""
                scope.launch {
                  if (newCount > 0) {
                    listState.animateScrollToItem(newCount - 1)
                  }
                }
              }
            },
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(SkillCirclePrimary)
              .testTag("chat_send_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    if (conversationMessages.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Say hello to $recipientName to coordinate your exchange!",
          color = SkillCircleTextSecondary,
          fontSize = 14.sp
        )
      }
    } else {
      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(conversationMessages, key = { it.id }) { msg ->
          val isMe = msg.senderId == currentUser?.uid
          val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))

          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
          ) {
            Surface(
              shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
              ),
              color = if (isMe) SkillCirclePrimary else SkillCircleCream,
              modifier = Modifier
                .widthIn(max = 280.dp)
                .testTag("message_bubble_${msg.id}")
            ) {
              Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                  text = msg.message,
                  color = if (isMe) Color.White else SkillCircleTextDark,
                  fontSize = 14.sp,
                  lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = timeStr,
                  color = if (isMe) SkillCircleLightGreen else SkillCircleTextSecondary,
                  fontSize = 10.sp,
                  modifier = Modifier.align(Alignment.End)
                )
              }
            }
          }
        }
      }
    }
  }
}
