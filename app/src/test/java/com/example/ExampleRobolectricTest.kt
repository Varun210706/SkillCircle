package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RequestStatus
import com.example.data.repository.SkillCircleRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Before
  fun setUp() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SkillCircleRepository.getInstance(context)
    if (repository.currentUser.value == null) {
      repository.login("elena@neighborhood.local", "password123")
    }
  }

  @Test
  fun read_string_from_context() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SkillCircle", appName)
  }

  @Test
  fun test_repository_initialization_and_skills() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SkillCircleRepository.getInstance(context)

    // Should have seeded skills available
    val skills = repository.skills.value
    assertTrue("Should have initial skills", skills.isNotEmpty())

    // Current demo user is seeded
    val user = repository.currentUser.value
    assertNotNull("Should have default demo user", user)
    assertEquals("Elena Rostova", user?.name)

    // Should have community points
    assertTrue((user?.communityPoints ?: 0) >= 20)
  }

  @Test
  fun test_skill_creation_and_request_cycle() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SkillCircleRepository.getInstance(context)

    // 1. Create a new skill
    val newSkillResult = repository.createSkill(
      title = "Intro to Watercolor Painting",
      category = "Art & Design",
      description = "Learn washes and color blending in a peaceful setting.",
      experience = "3 years watercolor artist",
      availability = "Saturday mornings",
      exchangeType = "Skill-for-Skill",
      neighborhood = "Oakridge"
    )
    assertTrue("Skill creation succeeds", newSkillResult.isSuccess)
    val skill = newSkillResult.getOrThrow()

    // 2. Submit a request for this skill
    val requestResult = repository.createRequest(
      skillId = skill.id,
      title = "Want to learn basic landscape painting",
      description = "Looking forward to learning watercolor brush techniques.",
      category = skill.category,
      date = "Next Saturday",
      time = "10:00 AM",
      neighborhood = "Oakridge"
    )
    assertTrue("Request creation succeeds", requestResult.isSuccess)
    val request = requestResult.getOrThrow()

    // 3. Update request status: Accept
    repository.updateRequestStatus(request.id, RequestStatus.ACCEPTED)
    val accepted = repository.getRequestById(request.id)
    assertEquals(RequestStatus.ACCEPTED.name, accepted?.status)

    // 4. Update request status: In Progress
    repository.updateRequestStatus(request.id, RequestStatus.IN_PROGRESS)
    val inProgress = repository.getRequestById(request.id)
    assertEquals(RequestStatus.IN_PROGRESS.name, inProgress?.status)

    // 5. Update request status: Completed
    repository.updateRequestStatus(request.id, RequestStatus.COMPLETED)
    val completed = repository.getRequestById(request.id)
    assertEquals(RequestStatus.COMPLETED.name, completed?.status)
  }

  @Test
  fun test_accept_and_decline_skill_request_flow() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SkillCircleRepository.getInstance(context)

    // Create a new skill and a pending request
    val skill = repository.createSkill(
      title = "Guitar Lessons",
      category = "Music & Audio",
      description = "Acoustic fundamentals",
      experience = "5 years",
      availability = "Weekends",
      exchangeType = "Skill-for-Skill",
      neighborhood = "Oakridge"
    ).getOrThrow()

    val req1 = repository.createRequest(
      skillId = skill.id,
      title = "Help with fingerpicking",
      description = "Looking to learn basic fingerpicking",
      category = skill.category,
      date = "Tomorrow",
      time = "2:00 PM",
      neighborhood = "Oakridge"
    ).getOrThrow()

    // Test Accept
    val acceptResult = repository.acceptSkillRequest(req1.id)
    assertTrue("Accept should succeed", acceptResult.isSuccess)
    assertEquals("Request accepted", acceptResult.getOrThrow())
    val acceptedReq = repository.getRequestById(req1.id)
    assertEquals(RequestStatus.ACCEPTED.name, acceptedReq?.status)

    // Verify accepted notification was generated
    val acceptNotif = repository.notifications.value.find { it.referenceId == req1.id && it.type == "REQUEST_ACCEPTED" }
    assertNotNull("Accept notification created", acceptNotif)
    assertEquals("Skill Request Accepted", acceptNotif?.title)

    // Create another request to test Decline
    val req2 = repository.createRequest(
      skillId = skill.id,
      title = "Help with music theory",
      description = "Want to learn chord progressions",
      category = skill.category,
      date = "Sunday",
      time = "4:00 PM",
      neighborhood = "Oakridge"
    ).getOrThrow()

    // Test Decline
    val declineResult = repository.declineSkillRequest(req2.id)
    assertTrue("Decline should succeed", declineResult.isSuccess)
    assertEquals("Request declined", declineResult.getOrThrow())
    val declinedReq = repository.getRequestById(req2.id)
    assertEquals(RequestStatus.REJECTED.name, declinedReq?.status)

    // Verify decline notification was generated
    val declineNotif = repository.notifications.value.find { it.referenceId == req2.id && it.type == "REQUEST_REJECTED" }
    assertNotNull("Decline notification created", declineNotif)
    assertEquals("Skill Request Declined", declineNotif?.title)

    // Test: Prevent a rejected request from being accepted later
    val tryAcceptRejected = repository.acceptSkillRequest(req2.id)
    assertTrue("Attempt to accept already rejected request fails", tryAcceptRejected.isFailure)
    val stillDeclinedReq = repository.getRequestById(req2.id)
    assertEquals(RequestStatus.REJECTED.name, stillDeclinedReq?.status)
  }

  @Test
  fun test_login_logout_and_password_reset() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = SkillCircleRepository.getInstance(context)

    // Test login with valid credentials
    val loginResult = repository.login("elena@neighborhood.local", "password123")
    assertTrue("Login succeeds", loginResult.isSuccess)
    assertNotNull("Current user is populated", repository.currentUser.value)

    // Test logout clears session
    repository.logout()
    // Current user in repository is cleared
    org.junit.Assert.assertNull(repository.currentUser.value)

    // Test password reset email
    val resetResult = repository.sendPasswordResetEmail("elena@neighborhood.local")
    assertTrue("Password reset email triggers cleanly", resetResult.isSuccess)
  }
}
