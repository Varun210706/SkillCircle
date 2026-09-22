package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillCategories
import com.example.ui.components.CategoryChip
import com.example.ui.components.SectionHeader
import com.example.ui.components.SkillCard
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@Composable
fun HomeScreen(
  viewModel: SkillCircleViewModel,
  onNavigateDiscover: (category: String?) -> Unit,
  onNavigateSkillDetails: (skillId: String) -> Unit,
  onNavigateCreateSkill: () -> Unit,
  onNavigateRequestHelp: () -> Unit,
  onNavigateNotifications: () -> Unit
) {
  val currentUser by viewModel.currentUser.collectAsState()
  val skills by viewModel.skills.collectAsState()
  val unreadNotifications by viewModel.unreadNotificationsCount.collectAsState()

  var searchText by remember { mutableStateOf("") }
  var activeCategory by remember { mutableStateOf("All") }

  val greeting = remember {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    when {
      hour < 12 -> "Good morning"
      hour < 17 -> "Good afternoon"
      else -> "Good evening"
    }
  }

  val activeSkills = remember(skills, activeCategory) {
    skills.filter { it.active && (activeCategory == "All" || it.category.equals(activeCategory, ignoreCase = true)) }
      .take(6)
  }

  Scaffold(
    containerColor = SkillCircleBackground
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("home_screen_content"),
      contentPadding = PaddingValues(bottom = 24.dp)
    ) {
      // 1. Top Green Header Card with Greeting & Notifications
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(SkillCirclePrimary)
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 26.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(
                  name = currentUser?.name ?: "Neighbor",
                  size = 44
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "$greeting, ${currentUser?.name?.substringBefore(" ") ?: "Neighbor"}!",
                    style = MaterialTheme.typography.titleLarge.copy(
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 20.sp
                    )
                  )
                  Text(
                    text = currentUser?.neighborhood ?: "Oakridge Community",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = SkillCircleLightGreen,
                      fontSize = 12.5.sp
                    )
                  )
                }
              }

              // Notification bell button
              IconButton(
                onClick = onNavigateNotifications,
                modifier = Modifier
                  .clip(CircleShape)
                  .background(SkillCircleSecondary.copy(alpha = 0.5f))
                  .testTag("home_notifications_button")
              ) {
                BadgedBox(badge = {
                  if (unreadNotifications > 0) {
                    Badge(containerColor = SkillCircleCream) {
                      Text("$unreadNotifications", color = SkillCirclePrimary, fontSize = 10.sp)
                    }
                  }
                }) {
                  Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
              text = "Connect, share and grow with your community.",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = SkillCircleLightGreen,
                fontSize = 14.sp
              )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onNavigateDiscover(null) }
                .testTag("home_search_bar"),
              color = SkillCircleSurface,
              shape = RoundedCornerShape(16.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = "Search",
                  tint = SkillCircleSecondary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Search skills, people or requests...",
                  color = SkillCircleTextSecondary,
                  fontSize = 14.sp
                )
              }
            }
          }
        }
      }

      // 2. Quick Action Cards: Offer a Skill, Find a Skill, Request Help
      item {
        Spacer(modifier = Modifier.height(18.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          QuickActionCard(
            title = "Offer a Skill",
            subtitle = "Share expertise",
            icon = Icons.Default.AddCircle,
            backgroundColor = SkillCirclePrimary,
            textColor = Color.White,
            modifier = Modifier.weight(1f).testTag("quick_action_offer"),
            onClick = onNavigateCreateSkill
          )

          QuickActionCard(
            title = "Find a Skill",
            subtitle = "Browse neighbors",
            icon = Icons.Default.Explore,
            backgroundColor = SkillCircleLightGreen,
            textColor = SkillCirclePrimary,
            modifier = Modifier.weight(1f).testTag("quick_action_find"),
            onClick = { onNavigateDiscover(null) }
          )

          QuickActionCard(
            title = "Request Help",
            subtitle = "Post a need",
            icon = Icons.Default.Handshake,
            backgroundColor = SkillCircleCream,
            textColor = SkillCircleTextDark,
            modifier = Modifier.weight(1f).testTag("quick_action_request"),
            onClick = onNavigateRequestHelp
          )
        }
      }

      // 3. Categories Horizontal Section
      item {
        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(
          title = "Explore Categories",
          actionText = "See All",
          onActionClick = { onNavigateDiscover(null) },
          modifier = Modifier.padding(horizontal = 20.dp)
        )

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          item {
            CategoryChip(
              category = "All",
              isSelected = activeCategory == "All",
              onSelected = { activeCategory = "All" }
            )
          }
          items(SkillCategories.list) { cat ->
            CategoryChip(
              category = cat,
              isSelected = activeCategory == cat,
              onSelected = { activeCategory = cat }
            )
          }
        }
      }

      // 4. Community Activity & Milestones Section
      item {
        Spacer(modifier = Modifier.height(20.dp))
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SkillCircleCream),
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(SkillCirclePrimary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Diversity1,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Community Milestone",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = SkillCirclePrimary,
                  fontSize = 14.sp
                )
              )
              Text(
                text = "Over 48 neighbor skill exchanges completed this month in Oakridge!",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = SkillCircleTextDark,
                  fontSize = 12.5.sp
                )
              )
            }
          }
        }
      }

      // 5. Nearby Skills Section
      item {
        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(
          title = "Nearby Skills",
          actionText = "View All (${skills.size})",
          onActionClick = { onNavigateDiscover(null) },
          modifier = Modifier.padding(horizontal = 20.dp)
        )
      }

      if (activeSkills.isEmpty()) {
        item {
          Text(
            text = "No skills found in this category yet. Be the first to offer one!",
            color = SkillCircleTextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
          )
        }
      } else {
        items(activeSkills, key = { it.id }) { skill ->
          SkillCard(
            skill = skill,
            onSkillClick = { onNavigateSkillDetails(skill.id) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun QuickActionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  backgroundColor: Color,
  textColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .height(108.dp)
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = textColor,
        modifier = Modifier.size(26.dp)
      )
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 13.5.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall.copy(
            color = textColor.copy(alpha = 0.8f),
            fontSize = 10.5.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
