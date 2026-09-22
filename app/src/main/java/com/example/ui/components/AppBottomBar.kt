package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

sealed class BottomNavItem(
  val route: String,
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
) {
  data object Home : BottomNavItem(
    "home",
    "Home",
    Icons.Filled.Home,
    Icons.Outlined.Home,
    "nav_home"
  )
  data object Discover : BottomNavItem(
    "discover",
    "Discover",
    Icons.Filled.Explore,
    Icons.Outlined.Explore,
    "nav_discover"
  )
  data object Requests : BottomNavItem(
    "requests",
    "Requests",
    Icons.Filled.SwapHoriz,
    Icons.Outlined.SwapHoriz,
    "nav_requests"
  )
  data object Messages : BottomNavItem(
    "messages",
    "Messages",
    Icons.Filled.ChatBubble,
    Icons.Outlined.ChatBubbleOutline,
    "nav_messages"
  )
  data object Profile : BottomNavItem(
    "profile",
    "Profile",
    Icons.Filled.Person,
    Icons.Outlined.Person,
    "nav_profile"
  )
}

@Composable
fun AppBottomBar(
  currentRoute: String,
  onNavigate: (String) -> Unit,
  unreadRequestsCount: Int = 0,
  unreadMessagesCount: Int = 0
) {
  val items = listOf(
    BottomNavItem.Home,
    BottomNavItem.Discover,
    BottomNavItem.Requests,
    BottomNavItem.Messages,
    BottomNavItem.Profile
  )

  NavigationBar(
    containerColor = SkillCircleSurface,
    contentColor = SkillCircleTextDark,
    tonalElevation = 8.dp
  ) {
    items.forEach { item ->
      val selected = currentRoute == item.route || currentRoute.startsWith("${item.route}/")
      NavigationBarItem(
        selected = selected,
        onClick = { if (!selected) onNavigate(item.route) },
        icon = {
          BadgedBox(badge = {
            if (item == BottomNavItem.Requests && unreadRequestsCount > 0) {
              Badge(containerColor = StatusPending) {
                Text(text = "$unreadRequestsCount", fontSize = 10.sp)
              }
            } else if (item == BottomNavItem.Messages && unreadMessagesCount > 0) {
              Badge(containerColor = SkillCircleTeal) {
                Text(text = "$unreadMessagesCount", fontSize = 10.sp)
              }
            }
          }) {
            Icon(
              imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
              contentDescription = item.title,
              modifier = Modifier.size(24.dp)
            )
          }
        },
        label = {
          Text(
            text = item.title,
            fontSize = 11.sp,
            color = if (selected) SkillCirclePrimary else SkillCircleTextSecondary
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = SkillCirclePrimary,
          selectedTextColor = SkillCirclePrimary,
          unselectedIconColor = SkillCircleTextSecondary,
          unselectedTextColor = SkillCircleTextSecondary,
          indicatorColor = SkillCircleLightGreen
        ),
        modifier = Modifier.testTag(item.testTag)
      )
    }
  }
}
