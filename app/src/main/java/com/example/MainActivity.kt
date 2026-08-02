package com.example

import android.Manifest
import android.content.pm.PackageManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.TopicRepository
import com.example.data.TestMarkRepository
import com.example.data.FlashcardRepository
import androidx.lifecycle.ViewModelProvider
import com.example.data.UserPreferencesRepository
import com.example.ui.SyllabusViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.SyllabusApp
import com.example.util.NotificationUtil

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            NotificationUtil.scheduleDailyReminder(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationUtil.scheduleDailyReminder(this)
            }
        } else {
            NotificationUtil.scheduleDailyReminder(this)
        }
        
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "syllabus-database"
        ).addMigrations(AppDatabase.MIGRATION_3_4).fallbackToDestructiveMigration().build()
        val repository = TopicRepository(database.topicDao())
        val testMarkRepository = TestMarkRepository(database.testMarkDao())
        val flashcardRepository = FlashcardRepository(database.flashcardDao())
        val userPrefs = UserPreferencesRepository(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SyllabusViewModel(repository, testMarkRepository, flashcardRepository, userPrefs) as T
            }
        }
        val viewModel = ViewModelProvider(this, factory)[SyllabusViewModel::class.java]

        setContent {
            val themeColor by viewModel.themeColor.collectAsStateWithLifecycle()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            
            MyApplicationTheme(
                darkTheme = isDarkMode ?: androidx.compose.foundation.isSystemInDarkTheme(),
                customPrimaryColor = themeColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SyllabusApp(viewModel = viewModel)
                }
            }
        }
    }
}
