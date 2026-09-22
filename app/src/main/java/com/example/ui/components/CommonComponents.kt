package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RequestStatus
import com.example.ui.theme.*

@Composable
fun SkillCircleLogo(
  modifier: Modifier = Modifier,
  isDark: Boolean = false,
  showTagline: Boolean = true
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(if (isDark) SkillCircleLightGreen else SkillCirclePrimary),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (isDark) SkillCirclePrimary else SkillCircleCream)
        )
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(SkillCircleTeal)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = "SkillCircle",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 26.sp,
          color = if (isDark) Color.White else SkillCirclePrimary
        )
      )
    }
    if (showTagline) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Share Skills. Build Connections.",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = if (isDark) SkillCircleLightGreen.copy(alpha = 0.9f) else SkillCircleTextSecondary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium
        )
      )
    }
  }
}

@Composable
fun PrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  testTag: String = "primary_button"
) {
  Button(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .testTag(testTag),
    enabled = enabled && !isLoading,
    shape = RoundedCornerShape(26.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = SkillCirclePrimary,
      contentColor = Color.White,
      disabledContainerColor = SkillCirclePrimary.copy(alpha = 0.4f),
      disabledContentColor = Color.White.copy(alpha = 0.8f)
    )
  ) {
    if (isLoading) {
      CircularProgressIndicator(
        modifier = Modifier.size(22.dp),
        color = Color.White,
        strokeWidth = 2.5.dp
      )
    } else {
      Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.SemiBold,
          fontSize = 16.sp
        )
      )
    }
  }
}

@Composable
fun SecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  testTag: String = "secondary_button"
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .height(50.dp)
      .testTag(testTag),
    enabled = enabled,
    shape = RoundedCornerShape(25.dp),
    border = ButtonDefaults.outlinedButtonBorder.copy(
      width = 1.5.dp,
      brush = androidx.compose.ui.graphics.SolidColor(SkillCirclePrimary)
    ),
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = SkillCirclePrimary
    )
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
      )
    )
  }
}

@Composable
fun UserAvatar(
  name: String,
  modifier: Modifier = Modifier,
  size: Int = 44,
  imageUrl: String = ""
) {
  Box(
    modifier = modifier
      .size(size.dp)
      .clip(CircleShape)
      .background(SkillCircleLightGreen)
      .border(1.dp, SkillCircleTeal.copy(alpha = 0.3f), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    if (name.isNotBlank()) {
      val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
      Text(
        text = initials.ifBlank { "U" },
        style = MaterialTheme.typography.titleMedium.copy(
          color = SkillCirclePrimary,
          fontWeight = FontWeight.Bold,
          fontSize = (size * 0.4).sp
        )
      )
    } else {
      Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "User Avatar",
        tint = SkillCirclePrimary,
        modifier = Modifier.size((size * 0.6).dp)
      )
    }
  }
}

@Composable
fun StatusBadge(
  status: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, label) = when (status.uppercase()) {
    RequestStatus.PENDING.name -> Triple(StatusPendingBg, StatusPending, "Pending")
    RequestStatus.ACCEPTED.name -> Triple(StatusAcceptedBg, StatusAccepted, "Accepted")
    RequestStatus.REJECTED.name -> Triple(StatusRejectedBg, StatusRejected, "Declined")
    RequestStatus.IN_PROGRESS.name -> Triple(StatusInProgressBg, StatusInProgress, "In Progress")
    RequestStatus.COMPLETED.name -> Triple(StatusCompletedBg, StatusCompleted, "Completed")
    RequestStatus.CANCELLED.name -> Triple(Color(0xFFF1F5F9), Color(0xFF64748B), "Cancelled")
    else -> Triple(SkillCircleCream, SkillCircleTextSecondary, status)
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = bgColor
  ) {
    Text(
      text = label,
      color = textColor,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
    )
  }
}

@Composable
fun RatingStars(
  rating: Double,
  modifier: Modifier = Modifier,
  starSize: Int = 16,
  showValue: Boolean = true
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.Star,
      contentDescription = "Rating $rating",
      tint = Color(0xFFF59E0B),
      modifier = Modifier.size(starSize.dp)
    )
    if (showValue) {
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = String.format("%.1f", rating),
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = SkillCircleTextDark
      )
    }
  }
}

@Composable
fun CategoryChip(
  category: String,
  isSelected: Boolean,
  onSelected: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .clickable { onSelected() },
    shape = RoundedCornerShape(20.dp),
    color = if (isSelected) SkillCirclePrimary else SkillCircleCream,
    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
  ) {
    Text(
      text = category,
      color = if (isSelected) Color.White else SkillCircleTextDark,
      fontSize = 13.sp,
      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
    )
  }
}

@Composable
fun SectionHeader(
  title: String,
  modifier: Modifier = Modifier,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        color = SkillCircleTextDark,
        fontSize = 20.sp
      )
    )
    if (actionText != null && onActionClick != null) {
      TextButton(onClick = onActionClick) {
        Text(
          text = actionText,
          color = SkillCircleSecondary,
          fontWeight = FontWeight.SemiBold,
          fontSize = 14.sp
        )
      }
    }
  }
}

@Composable
fun EmptyState(
  title: String,
  message: String,
  modifier: Modifier = Modifier,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(72.dp)
        .clip(CircleShape)
        .background(SkillCircleCream),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "🤝",
        fontSize = 32.sp
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        color = SkillCircleTextDark,
        fontSize = 18.sp,
        textAlign = TextAlign.Center
      )
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = message,
      style = MaterialTheme.typography.bodyMedium.copy(
        color = SkillCircleTextSecondary,
        fontSize = 14.sp,
        textAlign = TextAlign.Center
      ),
      modifier = Modifier.padding(horizontal = 16.dp)
    )
    if (actionText != null && onActionClick != null) {
      Spacer(modifier = Modifier.height(18.dp))
      PrimaryButton(
        text = actionText,
        onClick = onActionClick,
        modifier = Modifier.widthIn(max = 220.dp)
      )
    }
  }
}

@Composable
fun LoadingView(
  message: String = "Loading...",
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      CircularProgressIndicator(
        color = SkillCirclePrimary,
        strokeWidth = 3.dp
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = message,
        color = SkillCircleTextSecondary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillCircleTopBar(
  title: String,
  onBackClick: (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {}
) {
  TopAppBar(
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          color = SkillCircleTextDark
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    },
    navigationIcon = {
      if (onBackClick != null) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = SkillCircleTextDark
          )
        }
      }
    },
    actions = actions,
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = SkillCircleBackground,
      titleContentColor = SkillCircleTextDark
    )
  )
}

@Composable
fun SkillCircleDropdownMenu(
  expanded: Boolean,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit
) {
  DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    modifier = modifier
      .background(SkillCircleCream)
      .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
    shape = RoundedCornerShape(12.dp),
    containerColor = SkillCircleCream,
    content = content
  )
}

@Composable
fun SkillCircleDropdownMenuItem(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isSelected: Boolean = false,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null
) {
  DropdownMenuItem(
    text = {
      Text(
        text = text,
        color = if (isSelected) SkillCirclePrimary else SkillCircleTextDark,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 14.sp
      )
    },
    onClick = onClick,
    modifier = modifier.background(if (isSelected) SkillCircleLightGreen else Color.Transparent),
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    colors = MenuDefaults.itemColors(
      textColor = SkillCircleTextDark,
      leadingIconColor = SkillCircleSecondary,
      trailingIconColor = SkillCircleSecondary
    )
  )
}
