package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
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
import com.example.data.model.SkillCategories
import com.example.ui.components.CategoryChip
import com.example.ui.components.EmptyState
import com.example.ui.components.SkillCard
import com.example.ui.components.SkillCircleDropdownMenu
import com.example.ui.components.SkillCircleDropdownMenuItem
import com.example.ui.theme.*
import com.example.viewmodel.SkillCircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
  viewModel: SkillCircleViewModel,
  initialCategory: String? = null,
  onNavigateSkillDetails: (skillId: String) -> Unit,
  onNavigateCreateSkill: () -> Unit
) {
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val selectedSort by viewModel.selectedSort.collectAsState()
  val selectedNeighborhood by viewModel.selectedNeighborhood.collectAsState()
  val filteredSkills by viewModel.filteredSkills.collectAsState()

  LaunchedEffect(initialCategory) {
    if (initialCategory != null && initialCategory != "All") {
      viewModel.selectedCategory.value = initialCategory
    }
  }

  val neighborhoods = listOf("All", "Maplewood Heights", "Riverside Commons", "Greenbriar North", "Pine Crest")

  Scaffold(
    containerColor = SkillCircleBackground,
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Discover Skills",
            fontWeight = FontWeight.Bold,
            color = SkillCircleTextDark
          )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SkillCircleBackground)
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.searchQuery.value = it },
        placeholder = { Text("Search skills, topics, neighbors...") },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = SkillCircleSecondary)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", tint = SkillCircleTextSecondary)
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .testTag("discover_search_input"),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = SkillCircleSurface,
          unfocusedContainerColor = SkillCircleSurface,
          focusedBorderColor = SkillCirclePrimary,
          unfocusedBorderColor = CardBorder
        )
      )

      // Category filter chips
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          CategoryChip(
            category = "All",
            isSelected = selectedCategory == "All",
            onSelected = { viewModel.selectedCategory.value = "All" }
          )
        }
        items(SkillCategories.list) { cat ->
          CategoryChip(
            category = cat,
            isSelected = selectedCategory == cat,
            onSelected = { viewModel.selectedCategory.value = cat }
          )
        }
      }

      // Secondary Filters Row: Sort & Neighborhood Dropdown
      var neighborhoodMenuExpanded by remember { mutableStateOf(false) }
      val neighborhoods = listOf("All", "Maplewood Heights", "Oakridge", "Pinecrest", "Cedar Hill")

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Sort toggle
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SkillCircleCream)
            .padding(2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          listOf("Newest", "Top Rated").forEach { sortOption ->
            val isSelected = selectedSort == sortOption
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { viewModel.selectedSort.value = sortOption },
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) SkillCirclePrimary else Color.Transparent
            ) {
              Text(
                text = sortOption,
                color = if (isSelected) Color.White else SkillCircleTextDark,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              )
            }
          }
        }

        // Neighborhood Dropdown Filter
        Box {
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable { neighborhoodMenuExpanded = true }
              .testTag("neighborhood_filter_button"),
            shape = RoundedCornerShape(12.dp),
            color = SkillCircleCream,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = SkillCircleSecondary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (selectedNeighborhood == "All") "Neighborhood" else selectedNeighborhood,
                fontSize = 11.5.sp,
                color = SkillCircleTextDark,
                fontWeight = FontWeight.Medium
              )
              Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = SkillCircleSecondary,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          SkillCircleDropdownMenu(
            expanded = neighborhoodMenuExpanded,
            onDismissRequest = { neighborhoodMenuExpanded = false }
          ) {
            neighborhoods.forEach { neigh ->
              val isSelected = selectedNeighborhood == neigh
              SkillCircleDropdownMenuItem(
                text = neigh,
                isSelected = isSelected,
                onClick = {
                  viewModel.selectedNeighborhood.value = neigh
                  neighborhoodMenuExpanded = false
                },
                leadingIcon = {
                  Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (isSelected) SkillCirclePrimary else SkillCircleSecondary,
                    modifier = Modifier.size(16.dp)
                  )
                }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Skill Cards List or Empty State
      if (filteredSkills.isEmpty()) {
        EmptyState(
          title = "No Skills Found",
          message = if (searchQuery.isNotBlank()) {
            "No skills matched \"$searchQuery\". Try checking your spelling or clearing filters."
          } else {
            "No skills in this category yet. Be the first neighbor to offer one!"
          },
          actionText = "Offer a Skill",
          onActionClick = onNavigateCreateSkill,
          modifier = Modifier.weight(1f)
        )
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .testTag("discover_skills_list"),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredSkills, key = { it.id }) { skill ->
            SkillCard(
              skill = skill,
              onSkillClick = { onNavigateSkillDetails(skill.id) }
            )
          }
        }
      }
    }
  }
}
