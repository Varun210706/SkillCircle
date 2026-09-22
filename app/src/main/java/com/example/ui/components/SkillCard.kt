package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
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
import com.example.data.model.Skill
import com.example.ui.theme.*

@Composable
fun SkillCard(
  skill: Skill,
  onSkillClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .clickable { onSkillClick() }
      .testTag("skill_card_${skill.id}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = SkillCircleSurface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, CardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header: Provider Info & Category
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f, fill = false)
        ) {
          UserAvatar(name = skill.ownerName, size = 36)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = skill.ownerName,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = SkillCircleTextDark,
                fontSize = 14.sp
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = SkillCircleSecondary,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = skill.neighborhood,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = SkillCircleTextSecondary,
                  fontSize = 12.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        // Category Tag
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = SkillCircleLightGreen
        ) {
          Text(
            text = skill.category,
            style = MaterialTheme.typography.labelSmall.copy(
              color = SkillCirclePrimary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Skill Title
      Text(
        text = skill.title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = SkillCircleTextDark,
          fontSize = 17.sp
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Description
      Text(
        text = skill.description,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = SkillCircleTextSecondary,
          fontSize = 13.5.sp,
          lineHeight = 19.sp
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Footer: Rating, Exchange Type, Action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          RatingStars(rating = skill.rating, starSize = 16)
          Spacer(modifier = Modifier.width(12.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = SkillCircleCream
          ) {
            Text(
              text = skill.exchangeType,
              style = MaterialTheme.typography.labelSmall.copy(
                color = SkillCircleTextDark,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Button(
          onClick = onSkillClick,
          modifier = Modifier
            .height(36.dp)
            .testTag("view_skill_button_${skill.id}"),
          shape = RoundedCornerShape(18.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = SkillCirclePrimary,
            contentColor = Color.White
          ),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
        ) {
          Text(
            text = "View Skill",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
