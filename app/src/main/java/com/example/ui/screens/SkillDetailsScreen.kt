package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailsScreen(
  skillId: String,
  viewModel: SkillCircleViewModel,
  onNavigateBack: () -> Unit,
  onNavigateRequestHelp: (skillId: String) -> Unit,
  onNavigateChat: (conversationId: String, otherName: String, otherId: String) -> Unit
) {
  val skill = remember(skillId) { viewModel.getSkill(skillId) }
  val currentUser by viewModel.currentUser.collectAsState()
  val allReviews by viewModel.reviews.collectAsState()

  val isOwnSkill = currentUser?.uid == skill?.ownerId
  val providerReviews = remember(skill, allReviews) {
    allReviews.filter { it.revieweeId == skill?.ownerId }
  }

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = { Text("Skill Details", fontWeight = FontWeight.Bold, color = SkillCircleTextDark) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("skill_details_back")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleBackground)
      )
    },
    bottomBar = {
      if (skill != null) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = SkillCircleSurface,
          shadowElevation = 8.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (!isOwnSkill) {
              OutlinedButton(
                onClick = {
                  val convId = viewModel.getOrCreateConversation(
                    otherUserId = skill.ownerId,
                    otherUserName = skill.ownerName,
                    skillTitle = skill.title
                  )
                  onNavigateChat(convId, skill.ownerName, skill.ownerId)
                },
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("skill_details_message_button"),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, SkillCirclePrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SkillCirclePrimary)
              ) {
                Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Message", fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { onNavigateRequestHelp(skill.id) },
                modifier = Modifier
                  .weight(1.3f)
                  .height(48.dp)
                  .testTag("skill_details_request_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkillCirclePrimary)
              ) {
                Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Request Skill", fontWeight = FontWeight.Bold)
              }
            } else {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = SkillCircleLightGreen,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "This is your offered skill.",
                  color = SkillCirclePrimary,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 14.sp,
                  modifier = Modifier.padding(12.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }
        }
      }
    }
  ) { innerPadding ->
    if (skill == null) {
      Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        Text("Skill not found or has been removed.")
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
      ) {
        // Provider Info Card
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
            border = BorderStroke(1.dp, CardBorder)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              UserAvatar(name = skill.ownerName, size = 52)
              Spacer(modifier = Modifier.width(14.dp))
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = skill.ownerName,
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = SkillCircleTextDark,
                      fontSize = 17.sp
                    )
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified Neighbor",
                    tint = SkillCircleSecondary,
                    modifier = Modifier.size(17.dp)
                  )
                }

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
                    color = SkillCircleTextSecondary,
                    fontSize = 12.5.sp
                  )
                }
              }

              RatingStars(rating = skill.rating, starSize = 18)
            }
          }
        }

        // Title and Category Header
        item {
          Spacer(modifier = Modifier.height(16.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = SkillCircleLightGreen
            ) {
              Text(
                text = skill.category,
                color = SkillCirclePrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = SkillCircleCream
            ) {
              Text(
                text = skill.exchangeType,
                color = SkillCircleTextDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = skill.title,
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark,
              fontSize = 22.sp
            )
          )
        }

        // Description Section
        item {
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "About This Skill",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = SkillCircleTextDark,
              fontSize = 16.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = skill.description,
            style = MaterialTheme.typography.bodyLarge.copy(
              color = SkillCircleTextDark,
              fontSize = 14.5.sp,
              lineHeight = 22.sp
            )
          )
        }

        // Skill Details Grid (Experience, Availability)
        item {
          Spacer(modifier = Modifier.height(16.dp))
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SkillCircleSurface),
            border = BorderStroke(1.dp, CardBorder)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              DetailRow(
                icon = Icons.Default.WorkspacePremium,
                label = "Experience / Background",
                value = skill.experience
              )
              HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)
              DetailRow(
                icon = Icons.Default.Schedule,
                label = "Availability",
                value = skill.availability
              )
              HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CardBorder)
              DetailRow(
                icon = Icons.Default.SwapHoriz,
                label = "Exchange Format",
                value = skill.exchangeType
              )
            }
          }
        }

        // Reviews Section
        item {
          Spacer(modifier = Modifier.height(20.dp))
          SectionHeader(
            title = "Neighbor Reviews (${providerReviews.size})"
          )
        }

        if (providerReviews.isEmpty()) {
          item {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = SkillCircleCream,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "No reviews yet for this neighbor. Be the first to exchange skills and leave feedback!",
                color = SkillCircleTextSecondary,
                fontSize = 13.5.sp,
                modifier = Modifier.padding(16.dp)
              )
            }
          }
        } else {
          items(providerReviews, key = { it.id }) { review ->
            ReviewCard(review = review, modifier = Modifier.padding(vertical = 4.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun DetailRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  value: String
) {
  Row(verticalAlignment = Alignment.Top) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = SkillCircleSecondary,
      modifier = Modifier.size(20.dp).padding(top = 2.dp)
    )
    Spacer(modifier = Modifier.width(12.dp))
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
          color = SkillCircleTextSecondary,
          fontSize = 12.sp
        )
      )
      Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = SkillCircleTextDark,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.5.sp
        )
      )
    }
  }
}
