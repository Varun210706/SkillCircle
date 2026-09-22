package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirebaseHelper
import com.example.data.model.*
import com.example.data.repository.SkillCircleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SkillCircleViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = SkillCircleRepository.getInstance(application)

  val currentUser: StateFlow<UserProfile?> = repository.currentUser
  val skills: StateFlow<List<Skill>> = repository.skills
  val requests: StateFlow<List<SkillRequest>> = repository.requests
  val conversations: StateFlow<List<Conversation>> = repository.conversations
  val notifications: StateFlow<List<AppNotification>> = repository.notifications
  val reviews: StateFlow<List<Review>> = repository.reviews
  val achievements: StateFlow<List<Achievement>> = repository.achievements

  // UI States
  val authState = MutableStateFlow<UiState<UserProfile>>(UiState.Idle)
  val skillActionState = MutableStateFlow<UiState<Skill>>(UiState.Idle)
  val requestActionState = MutableStateFlow<UiState<SkillRequest>>(UiState.Idle)
  val reviewActionState = MutableStateFlow<UiState<Review>>(UiState.Idle)
  val generalMessage = MutableStateFlow<String?>(null)

  // Filters & Search
  val searchQuery = MutableStateFlow("")
  val selectedCategory = MutableStateFlow("All")
  val selectedSort = MutableStateFlow("Newest") // "Newest", "Top Rated"
  val selectedNeighborhood = MutableStateFlow("All")

  // Filtered skills
  val filteredSkills: StateFlow<List<Skill>> = combine(
    skills,
    searchQuery,
    selectedCategory,
    selectedSort,
    selectedNeighborhood
  ) { allSkills, query, cat, sort, neigh ->
    allSkills.filter { skill ->
      skill.active &&
        (query.isBlank() ||
          skill.title.contains(query, ignoreCase = true) ||
          skill.description.contains(query, ignoreCase = true) ||
          skill.ownerName.contains(query, ignoreCase = true) ||
          skill.category.contains(query, ignoreCase = true) ||
          skill.neighborhood.contains(query, ignoreCase = true)
        ) &&
        (cat == "All" || skill.category.equals(cat, ignoreCase = true)) &&
        (neigh == "All" || skill.neighborhood.equals(neigh, ignoreCase = true))
    }.let { list ->
      when (sort) {
        "Top Rated" -> list.sortedByDescending { it.rating }
        else -> list.sortedByDescending { it.createdAt }
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Sent and Received Requests
  val receivedRequests: StateFlow<List<SkillRequest>> = combine(requests, currentUser) { reqs, user ->
    if (user == null) emptyList()
    else reqs.filter { it.providerId == user.uid }.sortedByDescending { it.createdAt }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  val sentRequests: StateFlow<List<SkillRequest>> = combine(requests, currentUser) { reqs, user ->
    if (user == null) emptyList()
    else reqs.filter { it.requesterId == user.uid }.sortedByDescending { it.createdAt }
  }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  // My skills
  val mySkills: StateFlow<List<Skill>> = combine(skills, currentUser) { allSkills, user ->
    if (user == null) emptyList()
    else allSkills.filter { it.ownerId == user.uid }.sortedByDescending { it.createdAt }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Unread notification count
  val unreadNotificationsCount: StateFlow<Int> = combine(notifications, currentUser) { notifs, user ->
    if (user == null) 0
    else notifs.count { it.userId == user.uid && !it.isRead }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  // AUTH ACTIONS
  fun register(
    name: String,
    email: String,
    phone: String,
    password: String,
    city: String,
    neighborhood: String,
    bio: String
  ) {
    if (name.isBlank() || email.isBlank() || password.isBlank() || neighborhood.isBlank()) {
      authState.value = UiState.Error("Please fill in all required fields.")
      return
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      authState.value = UiState.Error("Please enter a valid email address.")
      return
    }
    if (password.length < 6) {
      authState.value = UiState.Error("Password must be at least 6 characters long.")
      return
    }

    authState.value = UiState.Loading
    viewModelScope.launch {
      val result = repository.register(name, email, phone, password, city, neighborhood, bio)
      result.onSuccess { user ->
        authState.value = UiState.Success(user)
      }.onFailure { err ->
        authState.value = UiState.Error(err.message ?: "Registration failed")
      }
    }
  }

  fun login(email: String, password: String) {
    if (email.isBlank() || password.isBlank()) {
      authState.value = UiState.Error("Please enter both email and password.")
      return
    }
    authState.value = UiState.Loading
    viewModelScope.launch {
      val result = repository.login(email, password)
      result.onSuccess { user ->
        authState.value = UiState.Success(user)
      }.onFailure { err ->
        authState.value = UiState.Error("Invalid email or password.")
      }
    }
  }

  fun logout() {
    repository.logout()
    authState.value = UiState.Idle
  }

  fun forgotPassword(email: String, onComplete: (Boolean, String) -> Unit) {
    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      onComplete(false, "Please enter a valid email address.")
      return
    }
    viewModelScope.launch {
      repository.sendPasswordResetEmail(email)
      onComplete(true, "Password reset instructions have been sent to $email.")
    }
  }

  fun updateProfile(
    name: String,
    phone: String,
    city: String,
    neighborhood: String,
    bio: String,
    imageUrl: String = "",
    onResult: (Boolean, String) -> Unit
  ) {
    viewModelScope.launch {
      val result = repository.updateProfile(name, phone, city, neighborhood, bio, imageUrl)
      result.onSuccess {
        onResult(true, "Profile updated successfully!")
      }.onFailure {
        onResult(false, it.message ?: "Failed to update profile.")
      }
    }
  }

  // SKILL ACTIONS
  fun createSkill(
    title: String,
    category: String,
    description: String,
    experience: String,
    availability: String,
    exchangeType: String,
    neighborhood: String,
    imageUrl: String = "",
    onSuccess: () -> Unit
  ) {
    if (title.isBlank() || description.isBlank()) {
      skillActionState.value = UiState.Error("Title and description are required.")
      return
    }
    skillActionState.value = UiState.Loading
    viewModelScope.launch {
      val result = repository.createSkill(
        title = title,
        category = category,
        description = description,
        experience = experience,
        availability = availability,
        exchangeType = exchangeType,
        neighborhood = neighborhood,
        imageUrl = imageUrl
      )
      result.onSuccess {
        skillActionState.value = UiState.Success(it)
        onSuccess()
      }.onFailure {
        skillActionState.value = UiState.Error(it.message ?: "Failed to create skill")
      }
    }
  }

  fun deactivateSkill(skillId: String) {
    viewModelScope.launch {
      repository.deactivateSkill(skillId)
    }
  }

  fun getSkill(skillId: String): Skill? {
    return repository.getSkillById(skillId)
  }

  // REQUEST ACTIONS
  fun createRequest(
    skillId: String,
    title: String,
    description: String,
    category: String,
    date: String,
    time: String,
    neighborhood: String,
    onSuccess: (SkillRequest) -> Unit
  ) {
    val authUser = FirebaseHelper.auth?.currentUser
    val userProfile = currentUser.value
    if (authUser == null && userProfile == null) {
      requestActionState.value = UiState.Error("Please log in again.")
      return
    }

    if (title.isBlank() || description.isBlank()) {
      requestActionState.value = UiState.Error("Please enter what you need help with.")
      return
    }
    requestActionState.value = UiState.Loading
    viewModelScope.launch {
      try {
        val result = repository.createRequest(
          skillId = skillId,
          title = title,
          description = description,
          category = category,
          date = date,
          time = time,
          neighborhood = neighborhood
        )
        result.onSuccess { req ->
          requestActionState.value = UiState.Success(req)
          generalMessage.value = "Request sent successfully"
          onSuccess(req)
        }.onFailure { err ->
          val msg = if (err is kotlinx.coroutines.TimeoutCancellationException ||
            err.message?.contains("connection", ignoreCase = true) == true ||
            err.message?.contains("network", ignoreCase = true) == true) {
            "Unable to send request. Please check your connection and try again."
          } else {
            err.message ?: "Failed to send request"
          }
          requestActionState.value = UiState.Error(msg)
        }
      } catch (e: Exception) {
        Log.e("SkillCircle", "Request creation failed in ViewModel", e)
        val msg = if (e is kotlinx.coroutines.TimeoutCancellationException ||
          e.message?.contains("connection", ignoreCase = true) == true) {
          "Unable to send request. Please check your connection and try again."
        } else {
          e.message ?: "Unable to send request. Please try again."
        }
        requestActionState.value = UiState.Error(msg)
      }
    }
  }

  val processingRequests = MutableStateFlow<Set<String>>(emptySet())

  fun reloadRequests() {
    viewModelScope.launch {
      repository.reloadUserRequests()
    }
  }

  fun acceptRequest(requestId: String, onResult: ((Boolean, String) -> Unit)? = null) {
    val authUser = FirebaseHelper.auth?.currentUser
    val userProfile = currentUser.value
    if (authUser == null && userProfile == null) {
      onResult?.invoke(false, "Please log in again.")
      return
    }

    if (processingRequests.value.contains(requestId)) return
    processingRequests.update { it + requestId }
    viewModelScope.launch {
      try {
        val result = repository.acceptSkillRequest(requestId)
        result.onSuccess { msg ->
          repository.reloadUserRequests()
          onResult?.invoke(true, msg)
        }.onFailure { err ->
          val friendlyMsg = if (err.message?.contains("already", ignoreCase = true) == true) {
            err.message ?: "Unable to update request. Please try again."
          } else if (err is kotlinx.coroutines.TimeoutCancellationException ||
            err.message?.contains("connection", ignoreCase = true) == true) {
            "Unable to update request. Please check your connection and try again."
          } else {
            err.message ?: "Unable to update request. Please try again."
          }
          onResult?.invoke(false, friendlyMsg)
        }
      } catch (e: Exception) {
        Log.e("SkillCircle", "Accept failed in ViewModel", e)
        val msg = if (e is kotlinx.coroutines.TimeoutCancellationException ||
          e.message?.contains("connection", ignoreCase = true) == true) {
          "Unable to update request. Please check your connection and try again."
        } else {
          e.message ?: "Unable to update request. Please try again."
        }
        onResult?.invoke(false, msg)
      } finally {
        processingRequests.update { it - requestId }
      }
    }
  }

  fun rejectRequest(requestId: String, onResult: ((Boolean, String) -> Unit)? = null) {
    val authUser = FirebaseHelper.auth?.currentUser
    val userProfile = currentUser.value
    if (authUser == null && userProfile == null) {
      onResult?.invoke(false, "Please log in again.")
      return
    }

    if (processingRequests.value.contains(requestId)) return
    processingRequests.update { it + requestId }
    viewModelScope.launch {
      try {
        val result = repository.declineSkillRequest(requestId)
        result.onSuccess { msg ->
          repository.reloadUserRequests()
          onResult?.invoke(true, msg)
        }.onFailure { err ->
          val friendlyMsg = if (err.message?.contains("already", ignoreCase = true) == true) {
            err.message ?: "Unable to update request. Please try again."
          } else if (err is kotlinx.coroutines.TimeoutCancellationException ||
            err.message?.contains("connection", ignoreCase = true) == true) {
            "Unable to update request. Please check your connection and try again."
          } else {
            err.message ?: "Unable to update request. Please try again."
          }
          onResult?.invoke(false, friendlyMsg)
        }
      } catch (e: Exception) {
        Log.e("SkillCircle", "Decline failed in ViewModel", e)
        val msg = if (e is kotlinx.coroutines.TimeoutCancellationException ||
          e.message?.contains("connection", ignoreCase = true) == true) {
          "Unable to update request. Please check your connection and try again."
        } else {
          e.message ?: "Unable to update request. Please try again."
        }
        onResult?.invoke(false, msg)
      } finally {
        processingRequests.update { it - requestId }
      }
    }
  }

  fun cancelRequest(requestId: String) {
    viewModelScope.launch {
      repository.updateRequestStatus(requestId, RequestStatus.CANCELLED)
    }
  }

  fun startExchange(requestId: String) {
    viewModelScope.launch {
      repository.updateRequestStatus(requestId, RequestStatus.IN_PROGRESS)
    }
  }

  fun completeExchange(requestId: String) {
    viewModelScope.launch {
      repository.updateRequestStatus(requestId, RequestStatus.COMPLETED)
    }
  }

  fun getRequest(requestId: String): SkillRequest? {
    return repository.getRequestById(requestId)
  }

  // CHAT ACTIONS
  fun getOrCreateConversation(otherUserId: String, otherUserName: String, skillTitle: String): String {
    return repository.getOrCreateConversation(otherUserId, otherUserName, skillTitle)
  }

  fun getMessagesFlow(conversationId: String): StateFlow<List<Message>> {
    return repository.getMessagesFlow(conversationId)
  }

  fun sendMessage(conversationId: String, receiverId: String, text: String, skillTitle: String = "") {
    if (text.isBlank()) return
    viewModelScope.launch {
      repository.sendMessage(conversationId, receiverId, text, skillTitle)
    }
  }

  // REVIEWS
  fun submitReview(
    exchangeId: String,
    revieweeId: String,
    rating: Int,
    comment: String,
    onSuccess: () -> Unit
  ) {
    if (comment.isBlank()) {
      reviewActionState.value = UiState.Error("Please write a short review.")
      return
    }
    reviewActionState.value = UiState.Loading
    viewModelScope.launch {
      val result = repository.submitReview(exchangeId, revieweeId, rating, comment)
      result.onSuccess {
        reviewActionState.value = UiState.Success(it)
        onSuccess()
      }.onFailure {
        reviewActionState.value = UiState.Error(it.message ?: "Failed to submit review.")
      }
    }
  }

  // NOTIFICATIONS
  fun markNotificationRead(id: String) {
    repository.markNotificationRead(id)
  }

  fun markAllNotificationsRead() {
    repository.markAllNotificationsRead()
  }

  fun resetDemo() {
    repository.resetDemoData()
  }
}
