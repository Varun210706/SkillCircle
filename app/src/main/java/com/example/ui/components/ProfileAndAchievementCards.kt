package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.model.Achievement
import com.example.data.model.Review
import com.example.data.model.UserProfile
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileHeader(
  user: UserProfile,
  onEditClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("profile_header_card"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, CardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      UserAvatar(name = user.name, size = 72)

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = user.name,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = SkillCircleTextDark,
          fontSize = 20.sp
        )
      )

      Spacer(modifier = Modifier.height(2.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.LocationOn,
          contentDescription = null,
          tint = SkillCircleSecondary,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = "${user.neighborhood}, ${user.city}",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = SkillCircleTextSecondary,
            fontSize = 13.sp
          )
        )
      }

      if (user.bio.isNotBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = user.bio,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = SkillCircleTextDark,
            fontSize = 13.5.sp,
            lineHeight = 19.sp
          ),
          modifier = Modifier.padding(horizontal = 8.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Stats Row: Skills Offered, Completed, Community Points, Rating
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(SkillCircleCream)
          .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        ProfileStatItem(title = "Points", value = "${user.communityPoints}", icon = "🌱")
        VerticalDivider(modifier = Modifier.height(32.dp), color = CardBorder)
        ProfileStatItem(title = "Exchanges", value = "${user.completedExchanges}", icon = "🔄")
        VerticalDivider(modifier = Modifier.height(32.dp), color = CardBorder)
        ProfileStatItem(title = "Skills", value = "${user.skillsCount}", icon = "💡")
        VerticalDivider(modifier = Modifier.height(32.dp), color = CardBorder)
        ProfileStatItem(title = "Rating", value = String.format("%.1f", user.rating), icon = "⭐")
      }
    }
  }
}

@Composable
private fun ProfileStatItem(title: String, value: String, icon: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        color = SkillCirclePrimary,
        fontSize = 16.sp
      )
    )
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        color = SkillCircleTextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    )
  }
}

@Composable
fun AchievementCard(
  achievement: Achievement,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("achievement_card_${achievement.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (achievement.unlocked) SkillCircleSurface else SkillCircleCream.copy(alpha = 0.5f)
    ),
    border = BorderStroke(
      1.dp,
      if (achievement.unlocked) SkillCircleTeal.copy(alpha = 0.4f) else CardBorder
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (achievement.unlocked) 2.dp else 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(if (achievement.unlocked) SkillCircleLightGreen else CardBorder.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
      ) {
        val icon = when (achievement.iconName) {
          "Lightbulb" -> Icons.Default.Lightbulb
          "SwapHoriz" -> Icons.Default.SwapHoriz
          "Favorite" -> Icons.Default.Favorite
          "EmojiEvents" -> Icons.Default.EmojiEvents
          "WorkspacePremium" -> Icons.Default.WorkspacePremium
          else -> Icons.Default.Star
        }
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (achievement.unlocked) SkillCirclePrimary else SkillCircleTextSecondary.copy(alpha = 0.6f),
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = achievement.title,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = if (achievement.unlocked) SkillCircleTextDark else SkillCircleTextSecondary,
              fontSize = 15.sp
            )
          )
          if (achievement.unlocked) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = SkillCircleLightGreen
            ) {
              Text(
                text = "Unlocked",
                color = SkillCirclePrimary,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
              )
            }
          } else {
            Text(
              text = "${achievement.pointsRequired} pts",
              color = SkillCircleTextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = achievement.description,
          style = MaterialTheme.typography.bodySmall.copy(
            color = SkillCircleTextSecondary,
            fontSize = 12.5.sp
          )
        )
      }
    }
  }
}

@Composable
fun ReviewCard(
  review: Review,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("review_card_${review.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    border = BorderStroke(1.dp, CardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          UserAvatar(name = review.reviewerName, size = 30)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = review.reviewerName,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark,
              fontSize = 13.5.sp
            )
          )
        }
        RatingStars(rating = review.rating.toDouble(), starSize = 15)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = review.comment,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = SkillCircleTextDark,
          fontSize = 13.sp,
          lineHeight = 18.sp
        )
      )

      Spacer(modifier = Modifier.height(6.dp))

      val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(review.createdAt))
      Text(
        text = dateStr,
        style = MaterialTheme.typography.labelSmall.copy(
          color = SkillCircleTextSecondary,
          fontSize = 11.sp
        )
      )
    }
  }
}
