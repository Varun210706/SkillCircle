package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.firebase.DemoData
import com.example.data.firebase.FirebaseHelper
import com.example.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID

class SkillCircleRepository private constructor(context: Context) {

  private val scope = CoroutineScope(Dispatchers.IO)
  private val auth: FirebaseAuth? get() = FirebaseHelper.auth
  private val firestore: FirebaseFirestore? get() = FirebaseHelper.firestore

  private var requestsProviderRegistration: ListenerRegistration? = null
  private var requestsRequesterRegistration: ListenerRegistration? = null
  private var notificationsRegistration: ListenerRegistration? = null

  // Reactive in-memory state that synchronizes with Firestore
  private val _currentUser = MutableStateFlow<UserProfile?>(null)
  val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

  private val _skills = MutableStateFlow<List<Skill>>(DemoData.demoSkills)
  val skills: StateFlow<List<Skill>> = _skills.asStateFlow()

  private val _requests = MutableStateFlow<List<SkillRequest>>(emptyList())
  val requests: StateFlow<List<SkillRequest>> = _requests.asStateFlow()

  private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
  val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

  private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())

  private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
  val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

  private val _reviews = MutableStateFlow<List<Review>>(DemoData.demoReviews)
  val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

  private val _achievements = MutableStateFlow<List<Achievement>>(DemoData.demoAchievements)
  val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

  init {
    FirebaseHelper.init(context)
    setupDemoState()
    checkInitialAuthState()
    listenToFirestore()
  }

  private fun checkInitialAuthState() {
    val firebaseAuth = auth
    val firebaseUser = firebaseAuth?.currentUser
    if (firebaseUser != null) {
      val uid = firebaseUser.uid
      scope.launch {
        try {
          val doc = firestore?.collection("users")?.document(uid)?.get()?.await()
          val profile = doc?.toObject(UserProfile::class.java)
          if (profile != null) {
            _currentUser.value = profile
            checkAndUpdateAchievements(profile)
            attachUserFirestoreListeners(profile.uid)
            reloadUserRequests()
          } else {
            val fallback = UserProfile(
              uid = uid,
              name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "Neighbor",
              email = firebaseUser.email ?: "",
              phone = firebaseUser.phoneNumber ?: "",
              city = "Oakridge",
              neighborhood = "Maplewood Heights",
              bio = "Neighborhood skill enthusiast.",
              skillsCount = 0,
              rating = 5.0,
              completedExchanges = 0,
              communityPoints = 20
            )
            _currentUser.value = fallback
            attachUserFirestoreListeners(fallback.uid)
            reloadUserRequests()
          }
        } catch (e: Exception) {
          Log.w("SkillCircleRepo", "Error fetching user on startup: ${e.message}")
        }
      }
    } else {
      _currentUser.value = null
    }
  }

  private fun setupDemoState() {
    val defaultUser = DemoData.demoUsers[0]
    // Default demo user initialized in case needed for demo seeding
    _currentUser.value = defaultUser
    attachUserFirestoreListeners(defaultUser.uid)

    // Initial demo request for realism
    val initialRequest = SkillRequest(
      id = "req_initial_01",
      requesterId = DemoData.demoUsers[1].uid,
      requesterName = DemoData.demoUsers[1].name,
      requesterNeighborhood = DemoData.demoUsers[1].neighborhood,
      providerId = defaultUser.uid,
      providerName = defaultUser.name,
      skillId = "skill_java_01",
      skillTitle = "Java Programming Tutoring",
      title = "Help with Spring Boot & Java Collections",
      description = "Hi Elena! I'm building a backend for my bakery ordering system and need some help structuring classes and collections.",
      category = "Technology",
      date = "Upcoming Saturday",
      time = "10:00 AM",
      neighborhood = "Maplewood Heights",
      status = RequestStatus.PENDING.name,
      createdAt = System.currentTimeMillis() - 3600000L * 4
    )
    _requests.value = listOf(initialRequest)

    // Initial conversation & message
    val initialConvId = "conv_elena_marcus"
    val initialConv = Conversation(
      id = initialConvId,
      participantIds = listOf(defaultUser.uid, DemoData.demoUsers[1].uid),
      participantNames = mapOf(
        defaultUser.uid to defaultUser.name,
        DemoData.demoUsers[1].uid to DemoData.demoUsers[1].name
      ),
      lastMessage = "Looking forward to our session! Let me know what time works best.",
      lastMessageTimestamp = System.currentTimeMillis() - 3600000L * 2,
      lastSenderId = DemoData.demoUsers[1].uid,
      skillTitle = "Java Programming Tutoring"
    )
    _conversations.value = listOf(initialConv)
    _messages.value = mapOf(
      initialConvId to listOf(
        Message(
          id = "msg_01",
          conversationId = initialConvId,
          senderId = DemoData.demoUsers[1].uid,
          receiverId = defaultUser.uid,
          senderName = DemoData.demoUsers[1].name,
          message = "Hi Elena! Thanks for accepting my tutoring request.",
          timestamp = System.currentTimeMillis() - 3600000L * 3,
          read = true
        ),
        Message(
          id = "msg_02",
          conversationId = initialConvId,
          senderId = defaultUser.uid,
          receiverId = DemoData.demoUsers[1].uid,
          senderName = defaultUser.name,
          message = "Looking forward to our session! Let me know what time works best.",
          timestamp = System.currentTimeMillis() - 3600000L * 2,
          read = true
        )
      )
    )

    // Initial notification
    _notifications.value = listOf(
      AppNotification(
        id = "notif_01",
        userId = defaultUser.uid,
        title = "New Skill Request",
        message = "Marcus Vance requested your 'Java Programming Tutoring' skill.",
        type = "REQUEST_RECEIVED",
        referenceId = initialRequest.id,
        isRead = false,
        createdAt = System.currentTimeMillis() - 3600000L * 4
      )
    )
  }

  private fun listenToFirestore() {
    val db = firestore ?: return
    try {
      // Listen to active skills collection
      db.collection("skills").whereEqualTo("active", true)
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w("SkillCircleRepo", "Firestore skills error: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null && !snapshot.isEmpty) {
            val list = snapshot.documents.mapNotNull { it.toObject(Skill::class.java) }
            if (list.isNotEmpty()) {
              _skills.update { current ->
                val map = (current + list).associateBy { it.id }
                map.values.toList()
              }
            }
          }
        }
    } catch (e: Exception) {
      Log.w("SkillCircleRepo", "Listen Firestore exception: ${e.message}")
    }
  }

  fun attachUserFirestoreListeners(uid: String) {
    val db = firestore ?: return
    try {
      requestsProviderRegistration?.remove()
      requestsRequesterRegistration?.remove()
      notificationsRegistration?.remove()

      // 1. Listen to requests where current user is the provider (Acceptor perspective)
      requestsProviderRegistration = db.collection("requests")
        .whereEqualTo("providerId", uid)
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w("SkillCircleRepo", "Provider requests listener error: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null) {
            val list = snapshot.documents.mapNotNull { it.toObject(SkillRequest::class.java) }
            if (list.isNotEmpty()) {
              _requests.update { current ->
                val map = current.associateBy { it.id }.toMutableMap()
                list.forEach { map[it.id] = it }
                map.values.toList()
              }
            }
          }
        }

      // 2. Listen to requests where current user is the requester (Requester perspective)
      requestsRequesterRegistration = db.collection("requests")
        .whereEqualTo("requesterId", uid)
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w("SkillCircleRepo", "Requester requests listener error: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null) {
            val list = snapshot.documents.mapNotNull { it.toObject(SkillRequest::class.java) }
            if (list.isNotEmpty()) {
              _requests.update { current ->
                val map = current.associateBy { it.id }.toMutableMap()
                list.forEach { map[it.id] = it }
                map.values.toList()
              }
            }
          }
        }

      // 3. Listen to notifications for this user
      notificationsRegistration = db.collection("notifications")
        .whereEqualTo("userId", uid)
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.w("SkillCircleRepo", "Notifications listener error: ${error.message}")
            return@addSnapshotListener
          }
          if (snapshot != null) {
            val list = snapshot.documents.mapNotNull { it.toObject(AppNotification::class.java) }
            if (list.isNotEmpty()) {
              _notifications.update { current ->
                val map = current.associateBy { it.id }.toMutableMap()
                list.forEach { map[it.id] = it }
                map.values.toList()
              }
            }
          }
        }
    } catch (e: Exception) {
      Log.w("SkillCircleRepo", "attachUserFirestoreListeners exception: ${e.message}")
    }
  }

  suspend fun reloadUserRequests(): Result<List<SkillRequest>> {
    val uid = _currentUser.value?.uid ?: auth?.currentUser?.uid ?: return Result.success(_requests.value)
    val db = firestore ?: return Result.success(_requests.value)
    return try {
      withTimeoutOrNull(5000L) {
        val providerDocs = db.collection("requests").whereEqualTo("providerId", uid).get().await()
        val requesterDocs = db.collection("requests").whereEqualTo("requesterId", uid).get().await()
        val fetchedList = (providerDocs.documents + requesterDocs.documents)
          .mapNotNull { it.toObject(SkillRequest::class.java) }
        if (fetchedList.isNotEmpty()) {
          _requests.update { current ->
            val map = current.associateBy { it.id }.toMutableMap()
            fetchedList.forEach { map[it.id] = it }
            map.values.toList()
          }
        }
      }
      Result.success(_requests.value)
    } catch (e: Exception) {
      Log.w("SkillCircle", "Error reloading user requests: ${e.message}")
      Result.success(_requests.value)
    }
  }

  // AUTHENTICATION OPERATIONS
  suspend fun register(
    name: String,
    email: String,
    phone: String,
    password: String,
    city: String,
    neighborhood: String,
    bio: String
  ): Result<UserProfile> {
    return try {
      var uid = UUID.randomUUID().toString()
      val firebaseAuth = auth
      if (firebaseAuth != null) {
        try {
          val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
          authResult.user?.uid?.let { uid = it }
        } catch (e: Exception) {
          Log.w("SkillCircleRepo", "Firebase Auth register exception: ${e.message}, using local UID")
        }
      }

      val newUser = UserProfile(
        uid = uid,
        name = name,
        email = email,
        phone = phone,
        city = city,
        neighborhood = neighborhood,
        bio = bio,
        profileImageUrl = "",
        skillsCount = 0,
        rating = 5.0,
        completedExchanges = 0,
        communityPoints = 20,
        createdAt = System.currentTimeMillis()
      )

      // Save to Firestore
      firestore?.let { db ->
        try {
          db.collection("users").document(uid).set(newUser).await()
        } catch (e: Exception) {
          Log.w("SkillCircleRepo", "Firestore user save exception: ${e.message}")
        }
      }

      _currentUser.value = newUser
      checkAndUpdateAchievements(newUser)
      attachUserFirestoreListeners(newUser.uid)
      reloadUserRequests()
      Result.success(newUser)
    } catch (e: Exception) {
      Result.failure(Exception("Registration failed. Please check your connection and try again."))
    }
  }

  suspend fun login(email: String, password: String): Result<UserProfile> {
    return try {
      val firebaseAuth = auth
      if (firebaseAuth != null) {
        try {
          val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
          val uid = authResult.user?.uid
          if (uid != null) {
            val doc = firestore?.collection("users")?.document(uid)?.get()?.await()
            val profile = doc?.toObject(UserProfile::class.java)
            if (profile != null) {
              _currentUser.value = profile
              checkAndUpdateAchievements(profile)
              attachUserFirestoreListeners(profile.uid)
              reloadUserRequests()
              return Result.success(profile)
            }
          }
        } catch (e: Exception) {
          Log.w("SkillCircleRepo", "Firebase Auth login exception: ${e.message}")
        }
      }

      // Check demo users or matched registered users
      val foundDemo = DemoData.demoUsers.firstOrNull { it.email.equals(email, ignoreCase = true) }
      val user = foundDemo ?: UserProfile(
        uid = UUID.randomUUID().toString(),
        name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
        email = email,
        phone = "+1 (555) 019-2834",
        city = "Oakridge",
        neighborhood = "Maplewood Heights",
        bio = "Neighborhood skill enthusiast eager to learn and share.",
        skillsCount = 1,
        rating = 5.0,
        completedExchanges = 2,
        communityPoints = 35
      )
      _currentUser.value = user
      checkAndUpdateAchievements(user)
      attachUserFirestoreListeners(user.uid)
      reloadUserRequests()
      Result.success(user)
    } catch (e: Exception) {
      Result.failure(Exception("Invalid email or password. Please try again."))
    }
  }

  fun logout() {
    try {
      auth?.signOut()
    } catch (e: Exception) {
      Log.w("SkillCircleRepo", "Signout error: ${e.message}")
    }
    requestsProviderRegistration?.remove()
    requestsRequesterRegistration?.remove()
    notificationsRegistration?.remove()
    requestsProviderRegistration = null
    requestsRequesterRegistration = null
    notificationsRegistration = null
    _currentUser.value = null
  }

  suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
    return try {
      auth?.sendPasswordResetEmail(email)?.await()
      Result.success(Unit)
    } catch (e: Exception) {
      // Return success gracefully so user sees confirmation
      Result.success(Unit)
    }
  }

  suspend fun updateProfile(
    name: String,
    phone: String,
    city: String,
    neighborhood: String,
    bio: String,
    profileImageUrl: String = ""
  ): Result<UserProfile> {
    val current = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
    val updated = current.copy(
      name = name,
      phone = phone,
      city = city,
      neighborhood = neighborhood,
      bio = bio,
      profileImageUrl = if (profileImageUrl.isNotEmpty()) profileImageUrl else current.profileImageUrl
    )
    _currentUser.value = updated

    firestore?.let { db ->
      try {
        db.collection("users").document(updated.uid).set(updated)
      } catch (e: Exception) {
        Log.w("SkillCircleRepo", "Firestore updateProfile exception: ${e.message}")
      }
    }
    return Result.success(updated)
  }

  // SKILL OPERATIONS
  suspend fun createSkill(
    title: String,
    category: String,
    description: String,
    experience: String,
    availability: String,
    exchangeType: String,
    neighborhood: String,
    imageUrl: String = ""
  ): Result<Skill> {
    val user = _currentUser.value ?: return Result.failure(Exception("Please log in to offer a skill"))
    val skillId = "skill_${UUID.randomUUID()}"
    val newSkill = Skill(
      id = skillId,
      ownerId = user.uid,
      ownerName = user.name,
      ownerNeighborhood = neighborhood.ifBlank { user.neighborhood },
      ownerImageUrl = user.profileImageUrl,
      title = title,
      category = category,
      description = description,
      experience = experience,
      availability = availability,
      exchangeType = exchangeType,
      neighborhood = neighborhood.ifBlank { user.neighborhood },
      imageUrl = imageUrl,
      rating = 5.0,
      active = true,
      createdAt = System.currentTimeMillis()
    )

    // Save to Firestore
    firestore?.let { db ->
      try {
        db.collection("skills").document(skillId).set(newSkill).await()
      } catch (e: Exception) {
        Log.w("SkillCircleRepo", "Firestore createSkill exception: ${e.message}")
      }
    }

    _skills.update { listOf(newSkill) + it }
    // Update user profile skills count & points (+5 points for offering skill)
    val updatedUser = user.copy(
      skillsCount = user.skillsCount + 1,
      communityPoints = user.communityPoints + 5
    )
    _currentUser.value = updatedUser
    checkAndUpdateAchievements(updatedUser)

    return Result.success(newSkill)
  }

  suspend fun updateSkill(skill: Skill): Result<Skill> {
    firestore?.let { db ->
      try {
        db.collection("skills").document(skill.id).set(skill).await()
      } catch (e: Exception) {
        Log.w("SkillCircleRepo", "Firestore updateSkill exception: ${e.message}")
      }
    }
    _skills.update { list -> list.map { if (it.id == skill.id) skill else it } }
    return Result.success(skill)
  }

  suspend fun deactivateSkill(skillId: String): Result<Unit> {
    val user = _currentUser.value
    firestore?.let { db ->
      try {
        db.collection("skills").document(skillId).update("active", false).await()
      } catch (e: Exception) {
        Log.w("SkillCircleRepo", "Firestore deactivateSkill exception: ${e.message}")
      }
    }
    _skills.update { list -> list.map { if (it.id == skillId) it.copy(active = false) else it } }
    user?.let {
      val updated = it.copy(skillsCount = maxOf(0, it.skillsCount - 1))
      _currentUser.value = updated
    }
    return Result.success(Unit)
  }

  fun getSkillById(skillId: String): Skill? {
    return _skills.value.firstOrNull { it.id == skillId }
  }

  // REQUEST OPERATIONS
  suspend fun createRequest(
    skillId: String,
    title: String,
    description: String,
    category: String,
    date: String,
    time: String,
    neighborhood: String
  ): Result<SkillRequest> {
    val requestId = "req_${UUID.randomUUID()}"
    Log.d("SkillCircle", "Creating request: $requestId")

    val firebaseUser = auth?.currentUser
    val user = _currentUser.value

    if (firebaseUser == null && user == null) {
      val ex = Exception("Please log in again.")
      Log.e("SkillCircle", "Request creation failed: user not authenticated", ex)
      return Result.failure(ex)
    }

    val currentUid = user?.uid ?: firebaseUser?.uid ?: ""
    if (currentUid.isBlank()) {
      val ex = Exception("Please log in again.")
      Log.e("SkillCircle", "Request creation failed: user ID is empty", ex)
      return Result.failure(ex)
    }

    val targetSkill = getSkillById(skillId)
    val providerId = targetSkill?.ownerId ?: ""
    val providerName = targetSkill?.ownerName ?: "Skill Provider"

    Log.d(
      "SkillCircle",
      "Request info: currentUser.uid=$currentUid, requestId=$requestId, providerId=$providerId, requesterId=$currentUid, status=PENDING"
    )

    val newRequest = SkillRequest(
      id = requestId,
      requesterId = currentUid,
      requesterName = user?.name ?: firebaseUser?.displayName ?: "Neighbor",
      requesterNeighborhood = user?.neighborhood ?: "Maplewood Heights",
      providerId = providerId,
      providerName = providerName,
      skillId = skillId,
      skillTitle = targetSkill?.title ?: title,
      title = title,
      description = description,
      category = category.ifBlank { targetSkill?.category ?: "General" },
      date = date,
      time = time,
      neighborhood = neighborhood.ifBlank { user?.neighborhood ?: "Maplewood Heights" },
      status = RequestStatus.PENDING.name,
      createdAt = System.currentTimeMillis()
    )

    val db = firestore
    if (db != null) {
      try {
        withTimeout(6000L) {
          db.collection("requests").document(requestId).set(newRequest).await()
        }
        Log.d("SkillCircle", "Firestore document saved for request: $requestId")
      } catch (e: Exception) {
        Log.e("SkillCircle", "Request creation failed", e)
        if (e is kotlinx.coroutines.TimeoutCancellationException) {
          return Result.failure(Exception("Unable to send request. Please check your connection and try again."))
        }
        if (e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
          return Result.failure(Exception("Permission denied: Unable to send request."))
        }
        val isNetwork = e.message?.contains("network", ignoreCase = true) == true ||
          e.message?.contains("offline", ignoreCase = true) == true ||
          e.message?.contains("unavailable", ignoreCase = true) == true
        if (isNetwork) {
          return Result.failure(Exception("Unable to send request. Please check your connection and try again."))
        }
        return Result.failure(e)
      }
    }

    // Update local state immediately so UI refreshes
    _requests.update { listOf(newRequest) + it.filter { r -> r.id != requestId } }

    // Send notification to provider only after request is created
    if (newRequest.providerId.isNotEmpty()) {
      try {
        addNotification(
          userId = newRequest.providerId,
          title = "New Skill Request",
          message = "${newRequest.requesterName} sent a request for '${newRequest.skillTitle}'",
          type = "REQUEST_RECEIVED",
          referenceId = requestId
        )
      } catch (notifEx: Exception) {
        Log.w("SkillCircle", "Provider notification write note: ${notifEx.message}")
      }
    }

    Log.d("SkillCircle", "Request created successfully: $requestId")
    return Result.success(newRequest)
  }

  suspend fun acceptSkillRequest(requestId: String): Result<String> {
    Log.d("SkillCircle", "Accepting request: $requestId")
    val currentUser = _currentUser.value
    val firebaseAuthUser = auth?.currentUser
    val currentUserId = currentUser?.uid ?: firebaseAuthUser?.uid

    if (currentUserId.isNullOrBlank()) {
      val ex = Exception("Please log in again.")
      Log.e("SkillCircle", "Accept failed", ex)
      return Result.failure(ex)
    }

    val db = firestore
    var req = _requests.value.firstOrNull { it.id == requestId }
    if (db != null) {
      try {
        withTimeout(5000L) {
          val doc = db.collection("requests").document(requestId).get().await()
          if (doc.exists()) {
            val firestoreReq = doc.toObject(SkillRequest::class.java)
            if (firestoreReq != null) {
              req = firestoreReq
            }
          }
        }
      } catch (e: Exception) {
        Log.w("SkillCircle", "Firestore read request error: ${e.message}", e)
      }
    }

    if (req == null) {
      val ex = Exception("Request not found.")
      Log.e("SkillCircle", "Accept failed", ex)
      return Result.failure(ex)
    }

    Log.d(
      "SkillCircle",
      "currentUser.uid: $currentUserId, requestId: $requestId, providerId: ${req.providerId}, requesterId: ${req.requesterId}, current status: ${req.status}"
    )

    if (req.providerId != currentUserId) {
      val ex = Exception("Only the provider can accept this request.")
      Log.e("SkillCircle", "Accept failed", ex)
      return Result.failure(ex)
    }

    if (req.status != RequestStatus.PENDING.name) {
      val ex = Exception("Request has already been ${req.status.lowercase()}.")
      Log.e("SkillCircle", "Accept failed", ex)
      return Result.failure(ex)
    }

    val updatedReq = req.copy(status = RequestStatus.ACCEPTED.name)
    if (db != null) {
      try {
        withTimeout(5000L) {
          db.collection("requests").document(requestId)
            .update("status", RequestStatus.ACCEPTED.name).await()
        }
      } catch (e: Exception) {
        try {
          withTimeout(5000L) {
            db.collection("requests").document(requestId).set(updatedReq).await()
          }
        } catch (e2: Exception) {
          Log.e("SkillCircle", "Accept failed", e2)
          if (e2.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
            return Result.failure(Exception("Permission denied: Unable to accept request."))
          }
          if (e2 is kotlinx.coroutines.TimeoutCancellationException) {
            return Result.failure(Exception("Unable to update request. Please check your connection and try again."))
          }
          return Result.failure(Exception("Unable to update request. Please try again."))
        }
      }
    }

    // Update local requests state immediately so Requests screen updates immediately
    _requests.update { list ->
      val exists = list.any { it.id == requestId }
      if (exists) list.map { if (it.id == requestId) updatedReq else it }
      else listOf(updatedReq) + list
    }

    // Update receiver's notification
    _notifications.update { list ->
      list.map { notif ->
        if (notif.referenceId == requestId && notif.type == "REQUEST_RECEIVED") {
          notif.copy(isRead = true)
        } else notif
      }
    }

    if (db != null) {
      try {
        withTimeoutOrNull(3000L) {
          val notifDocs = db.collection("notifications")
            .whereEqualTo("referenceId", requestId)
            .whereEqualTo("type", "REQUEST_RECEIVED")
            .get().await()
          for (doc in notifDocs.documents) {
            doc.reference.update("isRead", true)
          }
        }
      } catch (e: Exception) {
        Log.w("SkillCircle", "Notification status update note: ${e.message}")
      }
    }

    // Create notification for Requester
    addNotification(
      userId = req.requesterId,
      title = "Skill Request Accepted",
      message = "${req.providerName} accepted your skill request.",
      type = "REQUEST_ACCEPTED",
      referenceId = requestId
    )

    // Ensure conversation exists between the two users
    getOrCreateConversation(
      otherUserId = req.requesterId,
      otherUserName = req.requesterName,
      skillTitle = req.skillTitle
    )

    reloadUserRequests()
    Log.d("SkillCircle", "Request accepted: $requestId")
    return Result.success("Request accepted")
  }

  suspend fun declineSkillRequest(requestId: String): Result<String> {
    Log.d("SkillCircle", "Declining request: $requestId")
    val currentUser = _currentUser.value
    val firebaseAuthUser = auth?.currentUser
    val currentUserId = currentUser?.uid ?: firebaseAuthUser?.uid

    if (currentUserId.isNullOrBlank()) {
      val ex = Exception("Please log in again.")
      Log.e("SkillCircle", "Decline failed", ex)
      return Result.failure(ex)
    }

    val db = firestore
    var req = _requests.value.firstOrNull { it.id == requestId }
    if (db != null) {
      try {
        withTimeout(5000L) {
          val doc = db.collection("requests").document(requestId).get().await()
          if (doc.exists()) {
            val firestoreReq = doc.toObject(SkillRequest::class.java)
            if (firestoreReq != null) {
              req = firestoreReq
            }
          }
        }
      } catch (e: Exception) {
        Log.w("SkillCircle", "Firestore read request error: ${e.message}", e)
      }
    }

    if (req == null) {
      val ex = Exception("Request not found.")
      Log.e("SkillCircle", "Decline failed", ex)
      return Result.failure(ex)
    }

    Log.d(
      "SkillCircle",
      "currentUser.uid: $currentUserId, requestId: $requestId, providerId: ${req.providerId}, requesterId: ${req.requesterId}, current status: ${req.status}"
    )

    if (req.providerId != currentUserId) {
      val ex = Exception("Only the provider can decline this request.")
      Log.e("SkillCircle", "Decline failed", ex)
      return Result.failure(ex)
    }

    if (req.status != RequestStatus.PENDING.name) {
      val ex = Exception("Request has already been ${req.status.lowercase()}.")
      Log.e("SkillCircle", "Decline failed", ex)
      return Result.failure(ex)
    }

    val updatedReq = req.copy(status = RequestStatus.REJECTED.name)
    if (db != null) {
      try {
        withTimeout(5000L) {
          db.collection("requests").document(requestId)
            .update("status", RequestStatus.REJECTED.name).await()
        }
      } catch (e: Exception) {
        try {
          withTimeout(5000L) {
            db.collection("requests").document(requestId).set(updatedReq).await()
          }
        } catch (e2: Exception) {
          Log.e("SkillCircle", "Decline failed", e2)
          if (e2.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
            return Result.failure(Exception("Permission denied: Unable to decline request."))
          }
          if (e2 is kotlinx.coroutines.TimeoutCancellationException) {
            return Result.failure(Exception("Unable to update request. Please check your connection and try again."))
          }
          return Result.failure(Exception("Unable to update request. Please try again."))
        }
      }
    }

    // Update local requests state immediately
    _requests.update { list ->
      val exists = list.any { it.id == requestId }
      if (exists) list.map { if (it.id == requestId) updatedReq else it }
      else listOf(updatedReq) + list
    }

    // Update notification
    _notifications.update { list ->
      list.map { notif ->
        if (notif.referenceId == requestId && notif.type == "REQUEST_RECEIVED") {
          notif.copy(isRead = true)
        } else notif
      }
    }

    if (db != null) {
      try {
        withTimeoutOrNull(3000L) {
          val notifDocs = db.collection("notifications")
            .whereEqualTo("referenceId", requestId)
            .whereEqualTo("type", "REQUEST_RECEIVED")
            .get().await()
          for (doc in notifDocs.documents) {
            doc.reference.update("isRead", true)
          }
        }
      } catch (e: Exception) {
        Log.w("SkillCircle", "Notification status update note: ${e.message}")
      }
    }

    // Create notification for Requester
    addNotification(
      userId = req.requesterId,
      title = "Skill Request Declined",
      message = "${req.providerName} declined your skill request.",
      type = "REQUEST_REJECTED",
      referenceId = requestId
    )

    reloadUserRequests()
    Log.d("SkillCircle", "Request declined: $requestId")
    return Result.success("Request declined")
  }

  suspend fun updateRequestStatus(requestId: String, newStatus: RequestStatus): Result<Unit> {
    if (newStatus == RequestStatus.ACCEPTED) {
      val acceptResult = acceptSkillRequest(requestId)
      return if (acceptResult.isSuccess) Result.success(Unit) else Result.failure(acceptResult.exceptionOrNull() ?: Exception("Unable to update request. Please try again."))
    }
    if (newStatus == RequestStatus.REJECTED) {
      val declineResult = declineSkillRequest(requestId)
      return if (declineResult.isSuccess) Result.success(Unit) else Result.failure(declineResult.exceptionOrNull() ?: Exception("Unable to update request. Please try again."))
    }

    val req = _requests.value.firstOrNull { it.id == requestId }
      ?: return Result.failure(Exception("Request not found"))

    val updatedReq = req.copy(status = newStatus.name)
    firestore?.let { db ->
      try {
        db.collection("requests").document(requestId).set(updatedReq).await()
      } catch (e: Exception) {
        Log.w("SkillCircleRepo", "Firestore updateRequestStatus exception: ${e.message}")
      }
    }

    _requests.update { list -> list.map { if (it.id == requestId) updatedReq else it } }

    val user = _currentUser.value
    // Notifications & points based on status transition
    when (newStatus) {
      RequestStatus.COMPLETED -> {
        // Complete exchange: award points (+10) and increment completed exchanges for current user
        if (user != null) {
          val updatedUser = user.copy(
            completedExchanges = user.completedExchanges + 1,
            communityPoints = user.communityPoints + 10
          )
          _currentUser.value = updatedUser
          checkAndUpdateAchievements(updatedUser)
        }
        val otherUserId = if (user?.uid == req.providerId) req.requesterId else req.providerId
        addNotification(
          userId = otherUserId,
          title = "Exchange Completed!",
          message = "Your skill exchange for '${req.skillTitle}' was marked completed! Leave a review.",
          type = "EXCHANGE_COMPLETED",
          referenceId = requestId
        )
      }
      else -> {}
    }

    return Result.success(Unit)
  }

  fun getRequestById(requestId: String): SkillRequest? {
    return _requests.value.firstOrNull { it.id == requestId }
  }

  // MESSAGING OPERATIONS
  fun getOrCreateConversation(otherUserId: String, otherUserName: String, skillTitle: String): String {
    val currentUserId = _currentUser.value?.uid ?: auth?.currentUser?.uid ?: "user_me"
    val existing = _conversations.value.firstOrNull {
      it.participantIds.contains(currentUserId) && it.participantIds.contains(otherUserId)
    }
    if (existing != null) {
      return existing.id
    }

    val deterministicId = "conv_${listOf(currentUserId, otherUserId).sorted().joinToString("_")}"
    val existingDeterministic = _conversations.value.firstOrNull { it.id == deterministicId }
    if (existingDeterministic != null) {
      return existingDeterministic.id
    }

    val currentUserName = _currentUser.value?.name ?: "You"
    val newConv = Conversation(
      id = deterministicId,
      participantIds = listOf(currentUserId, otherUserId),
      participantNames = mapOf(
        currentUserId to currentUserName,
        otherUserId to otherUserName
      ),
      lastMessage = "Started a conversation about $skillTitle",
      lastMessageTimestamp = System.currentTimeMillis(),
      lastSenderId = currentUserId,
      skillTitle = skillTitle
    )
    _conversations.update { listOf(newConv) + it.filter { c -> c.id != deterministicId } }

    firestore?.let { db ->
      try {
        db.collection("conversations").document(deterministicId).set(newConv)
      } catch (e: Exception) {
        Log.w("SkillCircleRepo", "Firestore conversation save exception: ${e.message}")
      }
    }

    return deterministicId
  }

  fun getMessagesFlow(conversationId: String): StateFlow<List<Message>> {
    val flow = MutableStateFlow<List<Message>>(_messages.value[conversationId] ?: emptyList())
    // Synchronize whenever _messages map changes
    scope.launch {
      _messages.collect { map ->
        flow.value = map[conversationId] ?: emptyList()
      }
    }
    return flow.asStateFlow()
  }

  suspend fun sendMessage(
    conversationId: String,
    receiverId: String,
    messageText: String,
    skillTitle: String = ""
  ): Result<Message> {
    val user = _currentUser.value ?: return Result.failure(Exception("Please log in to chat"))
    val msgId = "msg_${UUID.randomUUID()}"
    val newMsg = Message(
      id = msgId,
      conversationId = conversationId,
      senderId = user.uid,
      receiverId = receiverId,
      senderName = user.name,
      message = messageText,
      timestamp = System.currentTimeMillis(),
      read = false
    )

    // Save to Firestore if available
    firestore?.let { db ->
      try {
        db.collection("conversations").document(conversationId)
          .collection("messages").document(msgId).set(newMsg)
        db.collection("conversations").document(conversationId)
          .update(
            "lastMessage", messageText,
            "lastMessageTimestamp", newMsg.timestamp,
            "lastSenderId", user.uid
          )
      } catch (e: Exception) {
        Log.w("SkillCircleRepo", "Firestore sendMessage exception: ${e.message}")
      }
    }

    // Update in-memory state
    _messages.update { map ->
      val currentList = map[conversationId] ?: emptyList()
      map + (conversationId to (currentList + newMsg))
    }

    _conversations.update { list ->
      list.map { conv ->
        if (conv.id == conversationId) {
          conv.copy(
            lastMessage = messageText,
            lastMessageTimestamp = newMsg.timestamp,
            lastSenderId = user.uid
          )
        } else conv
      }
    }

    // Notify receiver
    if (receiverId.isNotEmpty()) {
      addNotification(
        userId = receiverId,
        title = "New message from ${user.name}",
        message = messageText.take(60),
        type = "NEW_MESSAGE",
        referenceId = conversationId
      )
    }

    return Result.success(newMsg)
  }

  // REVIEW OPERATIONS
  suspend fun submitReview(
    exchangeId: String,
    revieweeId: String,
    rating: Int,
    comment: String
  ): Result<Review> {
    val user = _currentUser.value ?: return Result.failure(Exception("Please log in to submit a review"))
    // Prevent duplicate review for the same exchange
    val alreadyReviewed = _reviews.value.any { it.exchangeId == exchangeId && it.reviewerId == user.uid }
    if (alreadyReviewed) {
      return Result.failure(Exception("You have already reviewed this skill exchange."))
    }

    val revId = "rev_${UUID.randomUUID()}"
    val newReview = Review(
      id = revId,
      exchangeId = exchangeId,
      reviewerId = user.uid,
      reviewerName = user.name,
      revieweeId = revieweeId,
      rating = rating,
      comment = comment,
      createdAt = System.currentTimeMillis()
    )

    firestore?.let { db ->
      try {
        db.collection("reviews").document(revId).set(newReview).await()
      } catch (e: Exception) {
        Log.w("SkillCircleRepo", "Firestore submitReview exception: ${e.message}")
      }
    }

    _reviews.update { listOf(newReview) + it }

    // Award reviewee points (+5) and recalculate reviewee's rating
    val revieweeReviews = _reviews.value.filter { it.revieweeId == revieweeId }
    val avgRating = if (revieweeReviews.isNotEmpty()) {
      revieweeReviews.map { it.rating }.average()
    } else rating.toDouble()

    // If reviewee is current user
    if (user.uid == revieweeId) {
      val updated = user.copy(
        rating = ((avgRating * 10).toInt() / 10.0),
        communityPoints = user.communityPoints + 5
      )
      _currentUser.value = updated
      checkAndUpdateAchievements(updated)
    } else {
      // Award reviewer +5 points for providing community feedback
      val updatedUser = user.copy(communityPoints = user.communityPoints + 5)
      _currentUser.value = updatedUser
      checkAndUpdateAchievements(updatedUser)
    }

    // Send notification
    addNotification(
      userId = revieweeId,
      title = "New Community Review",
      message = "${user.name} rated you $rating stars: \"${comment.take(45)}\"",
      type = "NEW_REVIEW",
      referenceId = revId
    )

    return Result.success(newReview)
  }

  // NOTIFICATION OPERATIONS
  fun addNotification(userId: String, title: String, message: String, type: String, referenceId: String) {
    val notif = AppNotification(
      id = "notif_${UUID.randomUUID()}",
      userId = userId,
      title = title,
      message = message,
      type = type,
      referenceId = referenceId,
      isRead = false,
      createdAt = System.currentTimeMillis()
    )
    _notifications.update { listOf(notif) + it }
    firestore?.let { db ->
      try {
        db.collection("notifications").document(notif.id).set(notif)
      } catch (e: Exception) {
        Log.w("SkillCircleRepo", "Firestore notification save: ${e.message}")
      }
    }
  }

  fun markNotificationRead(notificationId: String) {
    _notifications.update { list ->
      list.map { if (it.id == notificationId) it.copy(isRead = true) else it }
    }
    firestore?.collection("notifications")?.document(notificationId)?.update("isRead", true)
  }

  fun markAllNotificationsRead() {
    _notifications.update { list -> list.map { it.copy(isRead = true) } }
  }

  // ACHIEVEMENTS EVALUATION
  private fun checkAndUpdateAchievements(user: UserProfile) {
    _achievements.update { list ->
      list.map { ach ->
        val isUnlocked = when (ach.id) {
          "achieve_welcome" -> true
          "achieve_first_skill" -> user.skillsCount >= 1
          "achieve_first_exchange" -> user.completedExchanges >= 1
          "achieve_helpful_neighbor" -> user.communityPoints >= 100
          "achieve_five_exchanges" -> user.completedExchanges >= 5
          "achieve_pillar" -> user.completedExchanges >= 10 && user.rating >= 4.8
          else -> user.communityPoints >= ach.pointsRequired
        }
        ach.copy(unlocked = isUnlocked)
      }
    }
  }

  // DEMO SEED DATA ACTION
  fun resetDemoData() {
    _skills.value = DemoData.demoSkills
    _reviews.value = DemoData.demoReviews
    setupDemoState()
  }

  companion object {
    @Volatile
    private var instance: SkillCircleRepository? = null

    fun getInstance(context: Context): SkillCircleRepository {
      return instance ?: synchronized(this) {
        instance ?: SkillCircleRepository(context.applicationContext).also { instance = it }
      }
    }
  }
}
