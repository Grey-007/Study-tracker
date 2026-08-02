package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Topic
import com.example.data.TopicRepository
import com.example.data.TestMark
import com.example.data.TestMarkRepository
import com.example.data.Flashcard
import com.example.data.FlashcardRepository
import com.example.data.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppState { LOADING, NEEDS_SETUP, COMPLETE }

class SyllabusViewModel(
    private val repository: TopicRepository,
    private val testMarkRepository: TestMarkRepository,
    private val flashcardRepository: FlashcardRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    val setupState: StateFlow<AppState> = userPreferences.isSetupComplete
        .map { if (it) AppState.COMPLETE else AppState.NEEDS_SETUP }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppState.LOADING)

    val selectedExams: StateFlow<Set<String>> = userPreferences.selectedExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val selectedSubjects: StateFlow<Set<String>> = userPreferences.selectedSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
        
    val userName: StateFlow<String?> = userPreferences.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val profilePicUri: StateFlow<String?> = userPreferences.profilePicUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val userAim: StateFlow<String?> = userPreferences.userAim
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val themeColor: StateFlow<Int?> = userPreferences.themeColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        
    val isDarkMode: StateFlow<Boolean?> = userPreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentExam = MutableStateFlow<String?>(null)
    val currentExam: StateFlow<String?> = _currentExam
    
    init {
        viewModelScope.launch {
            selectedExams.collect { exams ->
                if (_currentExam.value == null && exams.isNotEmpty()) {
                    _currentExam.value = exams.first()
                }
            }
        }
    }

    private val _timerTime = MutableStateFlow(25 * 60)
    private val _timerTotalTime = MutableStateFlow(25 * 60)
    val timerTotalTime: StateFlow<Int> = _timerTotalTime
    val timerTime: StateFlow<Int> = _timerTime

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    private var timerJob: kotlinx.coroutines.Job? = null

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerTime.value > 0) {
                kotlinx.coroutines.delay(1000)
                _timerTime.value -= 1
                if (_timerTime.value == 0) {
                    _isTimerRunning.value = false
                    // Trigger sound/notification logic via event or callback
                }
            }
            _isTimerRunning.value = false
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
    }

    fun resetTimer(minutes: Int = 25) {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _timerTime.value = minutes * 60
        _timerTotalTime.value = minutes * 60
    }

    val topics: StateFlow<List<Topic>> = combine(_currentExam, selectedSubjects) { exam, subjects ->
        exam to subjects
    }.flatMapLatest { (exam, subjects) ->
        if (exam == null) {
            flowOf(emptyList())
        } else {
            repository.getTopicsByExam(exam).map { topicList ->
                topicList.filter { it.subject in subjects }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTopics: StateFlow<List<Topic>> = repository.getAllTopics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    val testMarks: StateFlow<List<com.example.data.TestMark>> = _currentExam.flatMapLatest { exam ->
        if (exam == null) {
            flowOf(emptyList())
        } else {
            testMarkRepository.getTestMarksByExam(exam)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectExam(exam: String) {
        _currentExam.value = exam
    }

    fun completeSetup(exams: Set<String>, subjects: Set<String>) {
        viewModelScope.launch {
            userPreferences.saveSelectedExams(exams)
            userPreferences.saveSelectedSubjects(subjects)
            userPreferences.saveSetupComplete(true)
            _currentExam.value = exams.firstOrNull()
        }
    }
    
    fun updateUserProfile(name: String, uri: String?, aim: String? = null) {
        viewModelScope.launch {
            userPreferences.saveUserProfile(name, uri, aim)
        }
    }
    
    fun updateThemeColor(color: Int) {
        viewModelScope.launch {
            userPreferences.saveThemeColor(color)
        }
    }
    
    fun updateDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            userPreferences.saveDarkMode(isDark)
        }
    }

    fun addTopic(exam: String, subject: String, name: String) {
        viewModelScope.launch {
            repository.insertTopic(Topic(exam = exam, subject = subject, name = name))
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch {
            repository.deleteTopic(topic)
        }
    }

    fun toggleTopicCompletion(topic: Topic) {
        viewModelScope.launch {
            val newIsCompleted = !topic.isCompleted
            val timestamp = if (newIsCompleted) System.currentTimeMillis() else null
            repository.updateTopic(topic.copy(isCompleted = newIsCompleted, completedDateMillis = timestamp))
        }
    }

    
    fun addTestMark(testName: String, score: Float, maxScore: Float) {
        val exam = _currentExam.value ?: return
        viewModelScope.launch {
            testMarkRepository.insertTestMark(
                TestMark(
                    exam = exam,
                    testName = testName,
                    score = score,
                    maxScore = maxScore,
                    dateMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun getCompletionPercentage(topics: List<Topic>): Float {
        if (topics.isEmpty()) return 0f
        val completed = topics.count { it.isCompleted }
        return completed.toFloat() / topics.size
    }

    // Streak
    val studyStreak: StateFlow<Int> = userPreferences.studyStreak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    fun incrementStreak() {
        viewModelScope.launch {
            val lastStudyDate = userPreferences.lastStudyDate.first()
            val today = java.time.LocalDate.now().toEpochDay()
            if (lastStudyDate != today) {
                val currentStreak = userPreferences.studyStreak.first()
                if (lastStudyDate == today - 1) {
                    userPreferences.updateStudyStreak(currentStreak + 1)
                } else {
                    userPreferences.updateStudyStreak(1)
                }
                userPreferences.updateLastStudyDate(today)
            }
        }
    }
    
    // Topics extensions
    fun toggleDailyGoal(topic: Topic) {
        viewModelScope.launch {
            repository.updateTopic(topic.copy(isDailyGoal = !topic.isDailyGoal))
        }
    }
    
    fun updateTopicNotes(topic: Topic, notes: String) {
        viewModelScope.launch {
            repository.updateTopic(topic.copy(notes = notes))
        }
    }
    
    fun updateTopicLinks(topic: Topic, links: String) {
        viewModelScope.launch {
            repository.updateTopic(topic.copy(studyLinks = links))
        }
    }
    
    // Flashcards
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val flashcards: StateFlow<List<Flashcard>> = currentExam
        .flatMapLatest { exam -> flashcardRepository.getFlashcardsForExam(exam ?: "") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    fun addFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            flashcardRepository.insertFlashcard(flashcard)
        }
    }
    
    fun deleteFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            flashcardRepository.deleteFlashcard(flashcard)
        }
    }
    
    fun updateFlashcardProgress(flashcard: Flashcard, quality: Int) {
        viewModelScope.launch {
            // SuperMemo-2 algorithm simplified
            var repetitions = flashcard.repetitions
            var interval = flashcard.intervalDays
            var ease = flashcard.easeFactor
            
            if (quality >= 3) {
                if (repetitions == 0) {
                    interval = 1
                } else if (repetitions == 1) {
                    interval = 6
                } else {
                    interval = (interval * ease).toInt()
                }
                repetitions++
            } else {
                repetitions = 0
                interval = 1
            }
            
            ease += (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
            if (ease < 1.3f) ease = 1.3f
            
            val nextDate = System.currentTimeMillis() + interval * 24L * 60L * 60L * 1000L
            
            flashcardRepository.updateFlashcard(
                flashcard.copy(
                    repetitions = repetitions,
                    intervalDays = interval,
                    easeFactor = ease,
                    nextReviewDateMillis = nextDate
                )
            )
            
            // Whenever they review a flashcard, it counts as studying
            incrementStreak()
        }
    }
}
