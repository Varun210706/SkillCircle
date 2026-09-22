package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SkillCircleApp
import com.example.ui.theme.SkillCircleTheme
import com.example.viewmodel.SkillCircleViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SkillCircleTheme {
        val viewModel: SkillCircleViewModel = viewModel()
        SkillCircleApp(viewModel = viewModel)
      }
    }
  }
}

