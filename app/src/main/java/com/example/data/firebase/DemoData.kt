package com.example.data.firebase

import com.example.data.model.Achievement
import com.example.data.model.AppNotification
import com.example.data.model.Conversation
import com.example.data.model.Message
import com.example.data.model.Review
import com.example.data.model.Skill
import com.example.data.model.SkillRequest
import com.example.data.model.UserProfile

object DemoData {

  val demoUsers = listOf(
    UserProfile(
      uid = "user_elena_01",
      name = "Elena Rostova",
      email = "elena@neighborhood.local",
      phone = "+1 (555) 234-8901",
      city = "Oakridge",
      neighborhood = "Maplewood Heights",
      bio = "Passionate software engineer and open-source enthusiast. Love helping beginners grasp Java, Kotlin, and basic algorithms.",
      profileImageUrl = "",
      skillsCount = 2,
      rating = 4.9,
      completedExchanges = 14,
      communityPoints = 185
    ),
    UserProfile(
      uid = "user_marcus_02",
      name = "Marcus Vance",
      email = "marcus@neighborhood.local",
      phone = "+1 (555) 345-6789",
      city = "Oakridge",
      neighborhood = "Riverside Commons",
      bio = "Amateur chef and sourdough baker. Certified bicycle mechanic on the weekends. Happy to swap baking tips for guitar lessons!",
      profileImageUrl = "",
      skillsCount = 2,
      rating = 4.8,
      completedExchanges = 9,
      communityPoints = 120
    ),
    UserProfile(
      uid = "user_priya_03",
      name = "Priya Sharma",
      email = "priya@neighborhood.local",
      phone = "+1 (555) 456-7890",
      city = "Oakridge",
      neighborhood = "Greenbriar North",
      bio = "High school math teacher & STEM mentor. Enthusiastic about making calculus and algebra fun and intuitive for everyone.",
      profileImageUrl = "",
      skillsCount = 1,
      rating = 5.0,
      completedExchanges = 18,
      communityPoints = 230
    ),
    UserProfile(
      uid = "user_david_04",
      name = "David Chen",
      email = "david@neighborhood.local",
      phone = "+1 (555) 567-8901",
      city = "Oakridge",
      neighborhood = "Pine Crest",
      bio = "Freelance UI/UX designer and digital illustrator. Looking to learn Italian cooking and urban gardening.",
      profileImageUrl = "",
      skillsCount = 1,
      rating = 4.7,
      completedExchanges = 6,
      communityPoints = 95
    )
  )

  val demoSkills = listOf(
    Skill(
      id = "skill_java_01",
      ownerId = "user_elena_01",
      ownerName = "Elena Rostova",
      ownerNeighborhood = "Maplewood Heights",
      title = "Java Programming Tutoring",
      category = "Technology",
      description = "Friendly 1-on-1 tutoring covering Java basics, OOP principles, data structures, and debugging. Perfect for students or career switchers.",
      experience = "7 years software engineering",
      availability = "Tuesday & Thursday evenings, Saturday mornings",
      exchangeType = "Skill-for-Skill",
      neighborhood = "Maplewood Heights",
      rating = 4.9,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 5
    ),
    Skill(
      id = "skill_cooking_02",
      ownerId = "user_marcus_02",
      ownerName = "Marcus Vance",
      ownerNeighborhood = "Riverside Commons",
      title = "Authentic Italian Home Cooking",
      category = "Cooking",
      description = "Learn to make handmade pasta from scratch, classic marinara sauce, and artisan focaccia. Hands-on kitchen session with tasting!",
      experience = "10+ years culinary enthusiast",
      availability = "Sunday afternoons",
      exchangeType = "Free Help",
      neighborhood = "Riverside Commons",
      rating = 4.9,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 7
    ),
    Skill(
      id = "skill_bike_03",
      ownerId = "user_marcus_02",
      ownerName = "Marcus Vance",
      ownerNeighborhood = "Riverside Commons",
      title = "Bicycle Repair & Tune-Up",
      category = "Repairs",
      description = "Full brake adjustments, derailleur tuning, flat tire repair, and chain cleaning. Bring your bike over and learn to maintain it yourself.",
      experience = "Community bike shop volunteer",
      availability = "Saturday afternoons & Wednesday after 6 PM",
      exchangeType = "Skill-for-Skill",
      neighborhood = "Riverside Commons",
      rating = 4.8,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 10
    ),
    Skill(
      id = "skill_design_04",
      ownerId = "user_david_04",
      ownerName = "David Chen",
      ownerNeighborhood = "Pine Crest",
      title = "Graphic Design & Branding Basics",
      category = "Art & Design",
      description = "Creating eye-catching logos, social media banners, and typography hierarchy using Figma or Canva. Great for local business owners.",
      experience = "5 years freelance brand designer",
      availability = "Flexible weekday evenings",
      exchangeType = "Skill-for-Skill",
      neighborhood = "Pine Crest",
      rating = 4.7,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 3
    ),
    Skill(
      id = "skill_math_05",
      ownerId = "user_priya_03",
      ownerName = "Priya Sharma",
      ownerNeighborhood = "Greenbriar North",
      title = "Mathematics & Algebra Tutoring",
      category = "Education",
      description = "Patient step-by-step guidance in algebra, geometry, pre-calculus, or standardized test prep. All ages welcome.",
      experience = "High School Teacher (M.Sc. Math)",
      availability = "Monday to Thursday afternoons",
      exchangeType = "Free Help",
      neighborhood = "Greenbriar North",
      rating = 5.0,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 12
    ),
    Skill(
      id = "skill_english_06",
      ownerId = "user_elena_01",
      ownerName = "Elena Rostova",
      ownerNeighborhood = "Maplewood Heights",
      title = "English Conversation Practice",
      category = "Languages",
      description = "Casual conversational practice for ESL learners wanting to improve fluency, idioms, and natural pronunciation in relaxed dialogue.",
      experience = "Bilingual fluent speaker",
      availability = "Weekends",
      exchangeType = "Free Help",
      neighborhood = "Maplewood Heights",
      rating = 4.9,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 2
    ),
    Skill(
      id = "skill_photo_07",
      ownerId = "user_david_04",
      ownerName = "David Chen",
      ownerNeighborhood = "Pine Crest",
      title = "Portrait & Nature Photography",
      category = "Art & Design",
      description = "Mastering manual camera controls, lighting composition, and beginner Lightroom photo editing. Bring your DSLR or smartphone.",
      experience = "Published landscape photographer",
      availability = "Golden hour weekend mornings",
      exchangeType = "Paid Session",
      neighborhood = "Pine Crest",
      rating = 4.8,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 15
    ),
    Skill(
      id = "skill_fitness_08",
      ownerId = "user_marcus_02",
      ownerName = "Marcus Vance",
      ownerNeighborhood = "Riverside Commons",
      title = "Community Fitness & Calisthenics",
      category = "Fitness",
      description = "Bodyweight strength workouts, core stability routines, and mobility drills at the neighborhood park.",
      experience = "Certified group trainer",
      availability = "Mon/Wed/Fri 7:00 AM",
      exchangeType = "Free Help",
      neighborhood = "Riverside Commons",
      rating = 4.9,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 8
    ),
    Skill(
      id = "skill_garden_09",
      ownerId = "user_priya_03",
      ownerName = "Priya Sharma",
      ownerNeighborhood = "Greenbriar North",
      title = "Organic Gardening & Composting",
      category = "Other",
      description = "Starting heirloom tomato seedlings, building raised garden beds, pest control without chemicals, and composting organic waste.",
      experience = "Master gardener certificate",
      availability = "Saturday mornings",
      exchangeType = "Skill-for-Skill",
      neighborhood = "Greenbriar North",
      rating = 5.0,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 14
    ),
    Skill(
      id = "skill_comp_10",
      ownerId = "user_elena_01",
      ownerName = "Elena Rostova",
      ownerNeighborhood = "Maplewood Heights",
      title = "Basic Computer & Smartphone Help",
      category = "Technology",
      description = "Patient guidance for seniors or tech novices. Setting up secure email, cloud backup, online banking security, and video calling.",
      experience = "Community digital literacy volunteer",
      availability = "Sunday mornings",
      exchangeType = "Free Help",
      neighborhood = "Maplewood Heights",
      rating = 5.0,
      active = true,
      createdAt = System.currentTimeMillis() - 86400000L * 20
    )
  )

  val demoAchievements = listOf(
    Achievement(
      id = "achieve_welcome",
      title = "First Step",
      description = "Joined the SkillCircle neighborhood community",
      pointsRequired = 0,
      unlocked = true,
      iconName = "Handshake"
    ),
    Achievement(
      id = "achieve_first_skill",
      title = "Skill Sharer",
      description = "Offered your first neighborhood skill",
      pointsRequired = 25,
      unlocked = true,
      iconName = "Lightbulb"
    ),
    Achievement(
      id = "achieve_first_exchange",
      title = "First Exchange",
      description = "Successfully completed your first skill swap",
      pointsRequired = 50,
      unlocked = true,
      iconName = "SwapHoriz"
    ),
    Achievement(
      id = "achieve_helpful_neighbor",
      title = "Helpful Neighbor",
      description = "Accumulated 100+ community points helping others",
      pointsRequired = 100,
      unlocked = false,
      iconName = "Favorite"
    ),
    Achievement(
      id = "achieve_five_exchanges",
      title = "5 Exchanges Completed",
      description = "Helped 5 different neighbors in your area",
      pointsRequired = 150,
      unlocked = false,
      iconName = "EmojiEvents"
    ),
    Achievement(
      id = "achieve_pillar",
      title = "Community Pillar",
      description = "Maintained a 4.8+ rating across 10+ completed exchanges",
      pointsRequired = 250,
      unlocked = false,
      iconName = "WorkspacePremium"
    )
  )

  val demoReviews = listOf(
    Review(
      id = "rev_01",
      exchangeId = "ex_01",
      reviewerId = "user_marcus_02",
      reviewerName = "Marcus Vance",
      revieweeId = "user_elena_01",
      rating = 5,
      comment = "Elena was incredibly patient! She helped me understand loops and classes in Java in under two hours. Highly recommended tutor.",
      createdAt = System.currentTimeMillis() - 86400000L * 4
    ),
    Review(
      id = "rev_02",
      exchangeId = "ex_02",
      reviewerId = "user_priya_03",
      reviewerName = "Priya Sharma",
      revieweeId = "user_marcus_02",
      rating = 5,
      comment = "Marcus tuned my road bike gear shifting and replaced the worn chain. Works like brand new! Exchanged with homemade peach jam.",
      createdAt = System.currentTimeMillis() - 86400000L * 6
    )
  )
}
