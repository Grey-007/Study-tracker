package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Topic
import com.example.data.TopicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppState { LOADING, NEEDS_SETUP, COMPLETE }

@OptIn(ExperimentalCoroutinesApi::class)
class SyllabusViewModel(
    private val repository: TopicRepository,
    private val testMarkRepository: com.example.data.TestMarkRepository,
    private val userPreferences: com.example.data.UserPreferencesRepository
) : ViewModel() {

    val setupState: StateFlow<AppState> = userPreferences.isSetupComplete
        .map { if (it) AppState.COMPLETE else AppState.NEEDS_SETUP }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppState.LOADING)

    val selectedExams: StateFlow<Set<String>> = userPreferences.selectedExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val selectedSubjects: StateFlow<Set<String>> = userPreferences.selectedSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _currentExam = MutableStateFlow<String?>(null)
    val currentExam: StateFlow<String?> = _currentExam

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

    fun addTestMark(testName: String, score: Float, maxScore: Float) {
        val exam = _currentExam.value ?: return
        viewModelScope.launch {
            testMarkRepository.insertTestMark(
                com.example.data.TestMark(
                    exam = exam,
                    testName = testName,
                    dateMillis = System.currentTimeMillis(),
                    score = score,
                    maxScore = maxScore
                )
            )
        }
    }

    init {
        viewModelScope.launch {
            if (repository.getTopicCount() == 0) {
                repository.insertTopics(getInitialTopics())
            }
        }
        viewModelScope.launch {
            selectedExams.collect { exams ->
                if (_currentExam.value == null && exams.isNotEmpty()) {
                    _currentExam.value = exams.first()
                }
            }
        }
    }

    fun completeSetup(exams: Set<String>, subjects: Set<String>) {
        viewModelScope.launch {
            userPreferences.saveSelectedExams(exams)
            userPreferences.saveSelectedSubjects(subjects)
            userPreferences.saveSetupComplete(true)
            if (exams.isNotEmpty()) {
                _currentExam.value = exams.first()
            }
        }
    }

    fun selectExam(exam: String) {
        _currentExam.value = exam
    }

    fun toggleTopicCompletion(topic: Topic) {
        viewModelScope.launch {
            repository.updateTopic(topic.copy(isCompleted = !topic.isCompleted))
        }
    }

    fun getCompletionPercentage(subjectTopics: List<Topic>): Float {
        if (subjectTopics.isEmpty()) return 0f
        val completed = subjectTopics.count { it.isCompleted }
        return completed.toFloat() / subjectTopics.size
    }

    private fun getInitialTopics(): List<Topic> {
        return listOf(
            // JEE Physics
            Topic(exam = "JEE", subject = "Physics", name = "Kinematics"),
            Topic(exam = "JEE", subject = "Physics", name = "Laws of Motion"),
            Topic(exam = "JEE", subject = "Physics", name = "Work, Energy and Power"),
            Topic(exam = "JEE", subject = "Physics", name = "Rotational Motion"),
            Topic(exam = "JEE", subject = "Physics", name = "Gravitation"),
            Topic(exam = "JEE", subject = "Physics", name = "Mechanical Properties of Solids"),
            Topic(exam = "JEE", subject = "Physics", name = "Mechanical Properties of Fluids"),
            Topic(exam = "JEE", subject = "Physics", name = "Thermal Properties of Matter"),
            Topic(exam = "JEE", subject = "Physics", name = "Thermodynamics"),
            Topic(exam = "JEE", subject = "Physics", name = "Kinetic Theory of Gases"),
            Topic(exam = "JEE", subject = "Physics", name = "Oscillations"),
            Topic(exam = "JEE", subject = "Physics", name = "Waves"),
            Topic(exam = "JEE", subject = "Physics", name = "Electrostatics"),
            Topic(exam = "JEE", subject = "Physics", name = "Current Electricity"),
            Topic(exam = "JEE", subject = "Physics", name = "Magnetic Effects of Current"),
            Topic(exam = "JEE", subject = "Physics", name = "Electromagnetic Induction"),
            Topic(exam = "JEE", subject = "Physics", name = "Alternating Currents"),
            Topic(exam = "JEE", subject = "Physics", name = "Electromagnetic Waves"),
            Topic(exam = "JEE", subject = "Physics", name = "Optics"),
            Topic(exam = "JEE", subject = "Physics", name = "Dual Nature of Matter"),
            Topic(exam = "JEE", subject = "Physics", name = "Atoms and Nuclei"),
            Topic(exam = "JEE", subject = "Physics", name = "Electronic Devices"),
            
            // JEE Chemistry
            Topic(exam = "JEE", subject = "Chemistry", name = "Some Basic Concepts of Chemistry"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Structure of Atom"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Classification of Elements"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Chemical Bonding"),
            Topic(exam = "JEE", subject = "Chemistry", name = "States of Matter"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Thermodynamics"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Equilibrium"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Redox Reactions"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Hydrogen"),
            Topic(exam = "JEE", subject = "Chemistry", name = "s-Block Elements"),
            Topic(exam = "JEE", subject = "Chemistry", name = "p-Block Elements"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Organic Chemistry Principles"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Hydrocarbons"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Environmental Chemistry"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Solid State"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Solutions"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Electrochemistry"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Chemical Kinetics"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Surface Chemistry"),
            Topic(exam = "JEE", subject = "Chemistry", name = "General Principles of Isolation"),
            Topic(exam = "JEE", subject = "Chemistry", name = "d and f Block Elements"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Coordination Compounds"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Haloalkanes and Haloarenes"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Alcohols Phenols and Ethers"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Aldehydes Ketones and Carboxylic Acids"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Organic Compounds with Nitrogen"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Biomolecules"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Polymers"),
            Topic(exam = "JEE", subject = "Chemistry", name = "Chemistry in Everyday Life"),

            // JEE Maths
            Topic(exam = "JEE", subject = "Maths", name = "Sets Relations and Functions"),
            Topic(exam = "JEE", subject = "Maths", name = "Complex Numbers"),
            Topic(exam = "JEE", subject = "Maths", name = "Matrices and Determinants"),
            Topic(exam = "JEE", subject = "Maths", name = "Permutations and Combinations"),
            Topic(exam = "JEE", subject = "Maths", name = "Mathematical Induction"),
            Topic(exam = "JEE", subject = "Maths", name = "Binomial Theorem"),
            Topic(exam = "JEE", subject = "Maths", name = "Sequences and Series"),
            Topic(exam = "JEE", subject = "Maths", name = "Limit Continuity and Differentiability"),
            Topic(exam = "JEE", subject = "Maths", name = "Integral Calculus"),
            Topic(exam = "JEE", subject = "Maths", name = "Differential Equations"),
            Topic(exam = "JEE", subject = "Maths", name = "Coordinate Geometry"),
            Topic(exam = "JEE", subject = "Maths", name = "Three Dimensional Geometry"),
            Topic(exam = "JEE", subject = "Maths", name = "Vector Algebra"),
            Topic(exam = "JEE", subject = "Maths", name = "Statistics and Probability"),
            Topic(exam = "JEE", subject = "Maths", name = "Trigonometry"),
            Topic(exam = "JEE", subject = "Maths", name = "Mathematical Reasoning"),

            // NEET Biology
            Topic(exam = "NEET", subject = "Biology", name = "Diversity in Living World"),
            Topic(exam = "NEET", subject = "Biology", name = "Structural Organisation"),
            Topic(exam = "NEET", subject = "Biology", name = "Cell Structure and Function"),
            Topic(exam = "NEET", subject = "Biology", name = "Plant Physiology"),
            Topic(exam = "NEET", subject = "Biology", name = "Human Physiology"),
            Topic(exam = "NEET", subject = "Biology", name = "Reproduction"),
            Topic(exam = "NEET", subject = "Biology", name = "Genetics and Evolution"),
            Topic(exam = "NEET", subject = "Biology", name = "Biology and Human Welfare"),
            Topic(exam = "NEET", subject = "Biology", name = "Biotechnology and Its Applications"),
            Topic(exam = "NEET", subject = "Biology", name = "Ecology and Environment"),

            // NEET Physics
            Topic(exam = "NEET", subject = "Physics", name = "Physical World and Measurement"),
            Topic(exam = "NEET", subject = "Physics", name = "Kinematics"),
            Topic(exam = "NEET", subject = "Physics", name = "Laws of Motion"),
            Topic(exam = "NEET", subject = "Physics", name = "Work, Energy and Power"),
            Topic(exam = "NEET", subject = "Physics", name = "Motion of System of Particles"),
            Topic(exam = "NEET", subject = "Physics", name = "Gravitation"),
            Topic(exam = "NEET", subject = "Physics", name = "Properties of Bulk Matter"),
            Topic(exam = "NEET", subject = "Physics", name = "Thermodynamics"),
            Topic(exam = "NEET", subject = "Physics", name = "Behavior of Perfect Gas"),
            Topic(exam = "NEET", subject = "Physics", name = "Oscillations and Waves"),
            Topic(exam = "NEET", subject = "Physics", name = "Electrostatics"),
            Topic(exam = "NEET", subject = "Physics", name = "Current Electricity"),
            Topic(exam = "NEET", subject = "Physics", name = "Magnetic Effects of Current"),
            Topic(exam = "NEET", subject = "Physics", name = "Electromagnetic Induction"),
            Topic(exam = "NEET", subject = "Physics", name = "Electromagnetic Waves"),
            Topic(exam = "NEET", subject = "Physics", name = "Optics"),
            Topic(exam = "NEET", subject = "Physics", name = "Dual Nature of Matter"),
            Topic(exam = "NEET", subject = "Physics", name = "Atoms and Nuclei"),
            Topic(exam = "NEET", subject = "Physics", name = "Electronic Devices"),

            // NEET Chemistry
            Topic(exam = "NEET", subject = "Chemistry", name = "Some Basic Concepts of Chemistry"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Structure of Atom"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Classification of Elements"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Chemical Bonding"),
            Topic(exam = "NEET", subject = "Chemistry", name = "States of Matter"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Thermodynamics"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Equilibrium"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Redox Reactions"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Hydrogen"),
            Topic(exam = "NEET", subject = "Chemistry", name = "s-Block Elements"),
            Topic(exam = "NEET", subject = "Chemistry", name = "p-Block Elements"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Organic Chemistry Principles"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Hydrocarbons"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Environmental Chemistry"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Solid State"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Solutions"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Electrochemistry"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Chemical Kinetics"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Surface Chemistry"),
            Topic(exam = "NEET", subject = "Chemistry", name = "General Principles of Isolation"),
            Topic(exam = "NEET", subject = "Chemistry", name = "d and f Block Elements"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Coordination Compounds"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Haloalkanes and Haloarenes"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Alcohols Phenols and Ethers"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Aldehydes Ketones and Carboxylic Acids"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Organic Compounds with Nitrogen"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Biomolecules"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Polymers"),
            Topic(exam = "NEET", subject = "Chemistry", name = "Chemistry in Everyday Life"),

            // CUET
            Topic(exam = "CUET", subject = "General Test", name = "Quantitative Aptitude"),
            Topic(exam = "CUET", subject = "General Test", name = "Logical Reasoning"),
            Topic(exam = "CUET", subject = "General Test", name = "General Awareness"),
            Topic(exam = "CUET", subject = "English", name = "Reading Comprehension"),
            Topic(exam = "CUET", subject = "English", name = "Vocabulary"),
            Topic(exam = "CUET", subject = "English", name = "Grammar")
        )
    }
}
