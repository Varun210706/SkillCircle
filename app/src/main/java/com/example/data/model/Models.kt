package com.example.data.model

data class UserProfile(
  val uid: String = "",
  val name: String = "",
  val email: String = "",
  val phone: String = "",
  val city: String = "",
  val neighborhood: String = "",
  val bio: String = "",
  val profileImageUrl: String = "",
  val skillsCount: Int = 0,
  val rating: Double = 5.0,
  val completedExchanges: Int = 0,
  val communityPoints: Int = 20,
  val createdAt: Long = System.currentTimeMillis()
)

data class Skill(
  val id: String = "",
  val ownerId: String = "",
  val ownerName: String = "",
  val ownerNeighborhood: String = "",
  val ownerImageUrl: String = "",
  val title: String = "",
  val category: String = "Technology",
  val description: String = "",
  val experience: String = "Intermediate",
  val availability: String = "Weekends & Evenings",
  val exchangeType: String = "Skill-for-Skill", // Free Help, Skill-for-Skill, Paid Session
  val neighborhood: String = "",
  val imageUrl: String = "",
  val rating: Double = 5.0,
  val active: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

enum class RequestStatus(val label: String) {
  PENDING("Pending"),
  ACCEPTED("Accepted"),
  REJECTED("Rejected"),
  IN_PROGRESS("In Progress"),
  COMPLETED("Completed"),
  CANCELLED("Cancelled")
}

data class SkillRequest(
  val id: String = "",
  val requesterId: String = "",
  val requesterName: String = "",
  val requesterNeighborhood: String = "",
  val providerId: String = "",
  val providerName: String = "",
  val skillId: String = "",
  val skillTitle: String = "",
  val title: String = "",
  val description: String = "",
  val category: String = "General",
  val date: String = "",
  val time: String = "",
  val neighborhood: String = "",
  val status: String = RequestStatus.PENDING.name,
  val createdAt: Long = System.currentTimeMillis()
)

data class Message(
  val id: String = "",
  val conversationId: String = "",
  val senderId: String = "",
  val receiverId: String = "",
  val senderName: String = "",
  val message: String = "",
  val timestamp: Long = System.currentTimeMillis(),
  val read: Boolean = false
)

data class Conversation(
  val id: String = "",
  val participantIds: List<String> = emptyList(),
  val participantNames: Map<String, String> = emptyMap(),
  val lastMessage: String = "",
  val lastMessageTimestamp: Long = System.currentTimeMillis(),
  val lastSenderId: String = "",
  val skillTitle: String = ""
)

data class AppNotification(
  val id: String = "",
  val userId: String = "",
  val title: String = "",
  val message: String = "",
  val type: String = "GENERAL", // REQUEST_RECEIVED, REQUEST_ACCEPTED, EXCHANGE_COMPLETED, NEW_MESSAGE, NEW_REVIEW
  val referenceId: String = "",
  val isRead: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

data class Review(
  val id: String = "",
  val exchangeId: String = "",
  val reviewerId: String = "",
  val reviewerName: String = "",
  val revieweeId: String = "",
  val rating: Int = 5,
  val comment: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

data class Achievement(
  val id: String = "",
  val title: String = "",
  val description: String = "",
  val pointsRequired: Int = 0,
  val unlocked: Boolean = false,
  val iconName: String = "Star"
)

object SkillCategories {
  val list = listOf(
    "Technology",
    "Education",
    "Cooking",
    "Fitness",
    "Repairs",
    "Art & Design",
    "Languages",
    "Music",
    "Finance",
    "Other"
  )
}

object ExchangeTypes {
  const val FREE_HELP = "Free Help"
  const val SKILL_FOR_SKILL = "Skill-for-Skill"
  const val PAID_SESSION = "Paid Session"
  val list = listOf(FREE_HELP, SKILL_FOR_SKILL, PAID_SESSION)
}
