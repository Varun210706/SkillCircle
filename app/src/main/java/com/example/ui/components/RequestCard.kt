package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RequestStatus
import com.example.data.model.SkillRequest
import com.example.ui.theme.*

@Composable
fun RequestCard(
  request: SkillRequest,
  isReceived: Boolean,
  onRequestClick: () -> Unit,
  onAcceptClick: (() -> Unit)? = null,
  onRejectClick: (() -> Unit)? = null,
  onCompleteClick: (() -> Unit)? = null,
  onMessageClick: (() -> Unit)? = null,
  isProcessing: Boolean = false,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .clickable { onRequestClick() }
      .testTag("request_card_${request.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, CardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header: Status badge and Skill category
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
            fontSize = 11.sp,
            color = SkillCircleTextDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Skill Title & Request summary
      Text(
        text = request.skillTitle,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = SkillCircleTextDark,
          fontSize = 16.sp
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = request.description,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = SkillCircleTextSecondary,
          fontSize = 13.sp,
          lineHeight = 18.sp
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Counterpart Info (Requester if received, Provider if sent)
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val personName = if (isReceived) request.requesterName else request.providerName
        val personRole = if (isReceived) "Requester" else "Provider"

        UserAvatar(name = personName, size = 32)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = personName,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.SemiBold,
              color = SkillCircleTextDark,
              fontSize = 13.5.sp
            )
          )
          Text(
            text = personRole,
            style = MaterialTheme.typography.bodySmall.copy(
              color = SkillCircleTextSecondary,
              fontSize = 11.5.sp
            )
          )
        }

        if (request.date.isNotBlank()) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = SkillCircleSecondary,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = request.date,
              fontSize = 11.5.sp,
              color = SkillCircleTextSecondary
            )
          }
        }
      }

      // Action Buttons for Received Pending requests
      if (isReceived && request.status == RequestStatus.PENDING.name && onAcceptClick != null && onRejectClick != null) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = { onRejectClick() },
            enabled = !isProcessing,
            modifier = Modifier
              .weight(1f)
              .height(38.dp)
              .testTag("reject_request_button_${request.id}"),
            shape = RoundedCornerShape(19.dp),
            border = BorderStroke(1.dp, StatusRejected.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRejected)
          ) {
            Text(if (isProcessing) "Declining..." else "Decline", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }

          Button(
            onClick = { onAcceptClick() },
            enabled = !isProcessing,
            modifier = Modifier
              .weight(1f)
              .height(38.dp)
              .testTag("accept_request_button_${request.id}"),
            shape = RoundedCornerShape(19.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkillCirclePrimary)
          ) {
            Text(if (isProcessing) "Accepting..." else "Accept", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      }

      // Actions when accepted or in-progress
      if (request.status == RequestStatus.ACCEPTED.name || request.status == RequestStatus.IN_PROGRESS.name) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (onMessageClick != null) {
            OutlinedButton(
              onClick = onMessageClick,
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("message_button_${request.id}"),
              shape = RoundedCornerShape(19.dp),
              border = BorderStroke(1.dp, SkillCirclePrimary),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = SkillCirclePrimary)
            ) {
              Icon(
                Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = SkillCirclePrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Message", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          if (onCompleteClick != null) {
            Button(
              onClick = onCompleteClick,
              modifier = Modifier
                .weight(if (onMessageClick != null) 1.3f else 2f)
                .height(38.dp)
                .testTag("complete_request_button_${request.id}"),
              shape = RoundedCornerShape(19.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SkillCircleSecondary)
            ) {
              Text("Mark Completed", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      } else if (request.status == RequestStatus.COMPLETED.name && onMessageClick != null) {
        Spacer(modifier = Modifier.height(14.dp))
        OutlinedButton(
          onClick = onMessageClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .testTag("message_button_${request.id}"),
          shape = RoundedCornerShape(19.dp),
          border = BorderStroke(1.dp, SkillCirclePrimary),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = SkillCirclePrimary)
        ) {
          Icon(
            Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = SkillCirclePrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Message Neighbor", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
