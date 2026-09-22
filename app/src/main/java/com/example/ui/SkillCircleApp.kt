package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.AppBottomBar
import com.example.ui.screens.*
import com.example.viewmodel.SkillCircleViewModel

@Composable
fun SkillCircleApp(
  viewModel: SkillCircleViewModel
) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: "splash"

  val currentUser by viewModel.currentUser.collectAsState()
  val receivedRequests by viewModel.receivedRequests.collectAsState()
  val unreadMessagesCount = 0 // can be derived from conversations
  val pendingReceivedCount = receivedRequests.count { it.status == "PENDING" }

  val showBottomBar = currentRoute in listOf("home", "discover", "requests", "messages", "profile")

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      if (showBottomBar) {
        AppBottomBar(
          currentRoute = currentRoute,
          onNavigate = { route ->
            navController.navigate(route) {
              popUpTo("home") { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          unreadRequestsCount = pendingReceivedCount,
          unreadMessagesCount = unreadMessagesCount
        )
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = "splash",
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Splash Screen
      composable("splash") {
        SplashScreen(
          isAuthenticated = currentUser != null,
          onNavigateNext = { destination ->
            navController.navigate(destination) {
              popUpTo("splash") { inclusive = true }
            }
          }
        )
      }

      // 2. Onboarding Screen
      composable("onboarding") {
        OnboardingScreen(
          onFinishOnboarding = {
            navController.navigate("login") {
              popUpTo("onboarding") { inclusive = true }
            }
          }
        )
      }

      // 3. Login Screen
      composable("login") {
        LoginScreen(
          viewModel = viewModel,
          onNavigateRegister = { navController.navigate("register") },
          onNavigateForgotPassword = { navController.navigate("forgot_password") },
          onLoginSuccess = {
            navController.navigate("home") {
              popUpTo("login") { inclusive = true }
            }
          }
        )
      }

      // Forgot Password Screen
      composable("forgot_password") {
        ForgotPasswordScreen(
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 4. Register Screen
      composable("register") {
        RegisterScreen(
          viewModel = viewModel,
          onNavigateLogin = { navController.popBackStack() },
          onRegisterSuccess = {
            navController.navigate("home") {
              popUpTo("register") { inclusive = true }
              popUpTo("login") { inclusive = true }
            }
          }
        )
      }

      // 5. Home Screen
      composable("home") {
        HomeScreen(
          viewModel = viewModel,
          onNavigateDiscover = { category ->
            if (category != null) {
              navController.navigate("discover?category=$category")
            } else {
              navController.navigate("discover")
            }
          },
          onNavigateSkillDetails = { skillId ->
            navController.navigate("skill_details/$skillId")
          },
          onNavigateCreateSkill = { navController.navigate("create_skill") },
          onNavigateRequestHelp = { navController.navigate("request_help") },
          onNavigateNotifications = { navController.navigate("notifications") }
        )
      }

      // 6. Discover Screen
      composable(
        route = "discover?category={category}",
        arguments = listOf(navArgument("category") {
          type = NavType.StringType
          nullable = true
          defaultValue = null
        })
      ) { backStackEntry ->
        val category = backStackEntry.arguments?.getString("category")
        DiscoverScreen(
          viewModel = viewModel,
          initialCategory = category,
          onNavigateSkillDetails = { skillId ->
            navController.navigate("skill_details/$skillId")
          },
          onNavigateCreateSkill = { navController.navigate("create_skill") }
        )
      }

      // 7. Skill Details Screen
      composable(
        route = "skill_details/{skillId}",
        arguments = listOf(navArgument("skillId") { type = NavType.StringType })
      ) { backStackEntry ->
        val skillId = backStackEntry.arguments?.getString("skillId") ?: ""
        SkillDetailsScreen(
          skillId = skillId,
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateRequestHelp = { targetSkillId ->
            navController.navigate("request_help?skillId=$targetSkillId")
          },
          onNavigateChat = { convId, name, otherId ->
            navController.navigate("chat/$convId/$name/$otherId")
          }
        )
      }

      // 8. Create Skill Screen
      composable("create_skill") {
        CreateSkillScreen(
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() },
          onSkillCreated = {
            navController.navigate("my_skills") {
              popUpTo("create_skill") { inclusive = true }
            }
          }
        )
      }

      // 9. My Skills Screen
      composable("my_skills") {
        MySkillsScreen(
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateCreateSkill = { navController.navigate("create_skill") },
          onNavigateSkillDetails = { skillId -> navController.navigate("skill_details/$skillId") }
        )
      }

      // 10. Request Help Screen
      composable(
        route = "request_help?skillId={skillId}",
        arguments = listOf(navArgument("skillId") {
          type = NavType.StringType
          nullable = true
          defaultValue = null
        })
      ) { backStackEntry ->
        val targetSkillId = backStackEntry.arguments?.getString("skillId")
        RequestHelpScreen(
          skillId = targetSkillId,
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() },
          onRequestSubmitted = { requestId ->
            navController.navigate("request_details/$requestId") {
              popUpTo("request_help") { inclusive = true }
            }
          }
        )
      }

      // 11. Requests Screen (Exchanges)
      composable("requests") {
        RequestsScreen(
          viewModel = viewModel,
          onNavigateRequestDetails = { reqId ->
            navController.navigate("request_details/$reqId")
          },
          onNavigateDiscover = { navController.navigate("discover") },
          onNavigateChat = { convId, name, otherId ->
            navController.navigate("chat/$convId/$name/$otherId")
          }
        )
      }

      // 12. Request Details Screen
      composable(
        route = "request_details/{requestId}",
        arguments = listOf(navArgument("requestId") { type = NavType.StringType })
      ) { backStackEntry ->
        val reqId = backStackEntry.arguments?.getString("requestId") ?: ""
        RequestDetailsScreen(
          requestId = reqId,
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateChat = { convId, name, otherId ->
            navController.navigate("chat/$convId/$name/$otherId")
          }
        )
      }

      // 13. Messages Screen
      composable("messages") {
        MessagesScreen(
          viewModel = viewModel,
          onNavigateChat = { convId, name, otherId ->
            navController.navigate("chat/$convId/$name/$otherId")
          },
          onNavigateDiscover = { navController.navigate("discover") }
        )
      }

      // 14. Chat Screen
      composable(
        route = "chat/{conversationId}/{recipientName}/{recipientId}",
        arguments = listOf(
          navArgument("conversationId") { type = NavType.StringType },
          navArgument("recipientName") { type = NavType.StringType },
          navArgument("recipientId") { type = NavType.StringType }
        )
      ) { backStackEntry ->
        val convId = backStackEntry.arguments?.getString("conversationId") ?: ""
        val recName = backStackEntry.arguments?.getString("recipientName") ?: "Neighbor"
        val recId = backStackEntry.arguments?.getString("recipientId") ?: ""
        ChatScreen(
          conversationId = convId,
          recipientName = recName,
          recipientId = recId,
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 15. Notifications Screen
      composable("notifications") {
        NotificationsScreen(
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() },
          onNavigateRequestDetails = { reqId ->
            navController.navigate("request_details/$reqId")
          },
          onNavigateChat = { convId, name, otherId ->
            navController.navigate("chat/$convId/$name/$otherId")
          }
        )
      }

      // 16. Profile Screen
      composable("profile") {
        ProfileScreen(
          viewModel = viewModel,
          onNavigateEditProfile = { navController.navigate("edit_profile") },
          onNavigateMySkills = { navController.navigate("my_skills") },
          onNavigateSettings = { navController.navigate("settings") },
          onNavigateLogout = {
            navController.navigate("login") {
              popUpTo(0) { inclusive = true }
            }
          }
        )
      }

      // 17. Edit Profile Screen
      composable("edit_profile") {
        EditProfileScreen(
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 18. Settings Screen
      composable("settings") {
        SettingsScreen(
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() },
          onLogout = {
            navController.navigate("login") {
              popUpTo(0) { inclusive = true }
            }
          }
        )
      }
    }
  }
}
