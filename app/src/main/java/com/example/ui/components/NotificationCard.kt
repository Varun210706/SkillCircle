package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNotification
import com.example.data.model.SkillRequest
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationCard(
  notification: AppNotification,
  request: SkillRequest? = null,
  isProcessing: Boolean = false,
  onClick: () -> Unit,
  onAcceptClick: (() -> Unit)? = null,
  onRejectClick: (() -> Unit)? = null,
  onMessageClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val (icon, iconTint, iconBg) = when (notification.type) {
    "REQUEST_RECEIVED" -> Triple(Icons.Default.VolunteerActivism, SkillCirclePrimary, SkillCircleLightGreen)
    "REQUEST_ACCEPTED" -> Triple(Icons.Default.CheckCircle, StatusAccepted, StatusAcceptedBg)
    "REQUEST_REJECTED" -> Triple(Icons.Default.Cancel, StatusRejected, StatusRejectedBg)
    "EXCHANGE_COMPLETED" -> Triple(Icons.Default.EmojiEvents, SkillCirclePrimary, SkillCircleCream)
    "NEW_MESSAGE" -> Triple(Icons.Default.ChatBubble, SkillCircleTeal, InfoPillBg)
    "NEW_REVIEW" -> Triple(Icons.Default.Star, Color(0xFFF59E0B), Color(0xFFFEF3C7))
    else -> Triple(Icons.Default.Notifications, SkillCirclePrimary, SkillCircleLightGreen)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("notification_card_${notification.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (notification.isRead) SkillCircleSurface else SkillCircleCream.copy(alpha = 0.6f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 1.dp else 2.dp),
    border = BorderStroke(1.dp, if (notification.isRead) CardBorder else SkillCirclePrimary.copy(alpha = 0.2f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(iconBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = notification.title,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
              color = SkillCircleTextDark,
              fontSize = 14.5.sp
            )
          )
          if (!notification.isRead) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(SkillCircleTeal)
            )
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = notification.message,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = SkillCircleTextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
        )

        // Show Accept and Decline buttons or Status if this is a REQUEST_RECEIVED notification
        if (notification.type == "REQUEST_RECEIVED") {
          val status = request?.status ?: "PENDING"
          when (status) {
            "PENDING" -> {
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedButton(
                  onClick = { onRejectClick?.invoke() },
                  enabled = !isProcessing,
                  modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .testTag("notif_decline_${notification.id}"),
                  shape = RoundedCornerShape(18.dp),
                  border = BorderStroke(1.dp, StatusRejected),
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRejected)
                ) {
                  Text(if (isProcessing) "Declining..." else "Decline", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                  onClick = { onAcceptClick?.invoke() },
                  enabled = !isProcessing,
                  modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .testTag("notif_accept_${notification.id}"),
                  shape = RoundedCornerShape(18.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SkillCirclePrimary)
                ) {
                  Text(if (isProcessing) "Accepting..." else "Accept", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }
            "ACCEPTED" -> {
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  StatusBadge(status = "ACCEPTED")
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    "You accepted this request.",
                    fontSize = 12.sp,
                    color = StatusAccepted,
                    fontWeight = FontWeight.Medium
                  )
                }
                if (onMessageClick != null) {
                  OutlinedButton(
                    onClick = onMessageClick,
                    modifier = Modifier.height(32.dp).testTag("notif_msg_${notification.id}"),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    border = BorderStroke(1.dp, SkillCirclePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SkillCirclePrimary)
                  ) {
                    Text("Message", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                  }
                }
              }
            }
            "REJECTED" -> {
              Spacer(modifier = Modifier.height(8.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(status = "REJECTED")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  "You declined this request.",
                  fontSize = 12.sp,
                  color = StatusRejected,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        } else if (notification.type == "REQUEST_ACCEPTED" && onMessageClick != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            StatusBadge(status = "ACCEPTED")
            OutlinedButton(
              onClick = onMessageClick,
              modifier = Modifier.height(32.dp).testTag("notif_msg_${notification.id}"),
              shape = RoundedCornerShape(16.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
              border = BorderStroke(1.dp, SkillCirclePrimary),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = SkillCirclePrimary)
            ) {
              Text("Message", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        val dateStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(notification.createdAt))
        Text(
          text = dateStr,
          style = MaterialTheme.typography.labelSmall.copy(
            color = SkillCircleTextSecondary.copy(alpha = 0.8f),
            fontSize = 11.sp
          )
        )
      }
    }
  }
}
