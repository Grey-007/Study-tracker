package com.example.ui
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.border
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.rememberTextMeasurer

import java.util.Calendar
import androidx.compose.ui.text.drawText

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Topic
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyllabusApp(viewModel: SyllabusViewModel) {
    val setupState by viewModel.setupState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = setupState,
        transitionSpec = {
            fadeIn(animationSpec = tween(800)) togetherWith fadeOut(animationSpec = tween(800))
        },
        label = "setup_transition"
    ) { state ->
        when (state) {
            AppState.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            AppState.NEEDS_SETUP -> {
                SetupScreen(
                    onComplete = { exam, subjects ->
                        viewModel.completeSetup(exam, subjects)
                    }
                )
            }
            AppState.COMPLETE -> {
                DashboardScreen(viewModel)
            }
        }
    }
}

@Composable
fun SetupScreen(onComplete: (Set<String>, Set<String>) -> Unit) {
    val exams = listOf("JEE", "NEET", "CUET")
    val getSubjectsForExam = { exam: String ->
        when (exam) {
            "JEE" -> listOf("Physics", "Chemistry", "Maths")
            "NEET" -> listOf("Physics", "Chemistry", "Biology")
            "CUET" -> listOf("General Test", "English")
            else -> emptyList()
        }
    }

    var selectedExams by remember { mutableStateOf(setOf(exams.first())) }
    var selectedSubjects by remember { mutableStateOf(getSubjectsForExam(exams.first()).toSet()) }

    LaunchedEffect(selectedExams) {
        val newSubjects = selectedExams.flatMap { getSubjectsForExam(it) }.toSet()
        selectedSubjects = selectedSubjects.intersect(newSubjects) + newSubjects
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Syllabus Tracker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Select your target exams:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                exams.forEach { exam ->
                    val isSelected = selectedExams.contains(exam)
                    Surface(
                        onClick = { 
                            selectedExams = if (isSelected && selectedExams.size > 1) {
                                selectedExams - exam
                            } else {
                                selectedExams + exam
                            }
                        },
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary) else null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                            Text(
                                text = exam,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Select your subjects:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            val availableSubjects = selectedExams.flatMap { getSubjectsForExam(it) }.toSet().toList()
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableSubjects) { subject ->
                    val isSelected = selectedSubjects.contains(subject)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
                            .clickable {
                                selectedSubjects = if (isSelected) {
                                    selectedSubjects - subject
                                } else {
                                    selectedSubjects + subject
                                }
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = subject,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = { onComplete(selectedExams, selectedSubjects) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedExams.isNotEmpty() && selectedSubjects.isNotEmpty()
            ) {
                Text("Start Tracking")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: SyllabusViewModel) {
    val currentExam by viewModel.currentExam.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val selectedExams by viewModel.selectedExams.collectAsStateWithLifecycle()
    val selectedSubjects by viewModel.selectedSubjects.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }
    val userName by viewModel.userName.collectAsStateWithLifecycle()



    if (showProfile) {
        ProfileScreen(viewModel = viewModel, onBack = { showProfile = false })
        return
    }
    
    if (showSettings) {
        SettingsScreen(viewModel = viewModel, onBack = { showSettings = false })
        return
    }
        Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { showProfile = true }
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            if (userName?.isNotBlank() == true) Text(userName!!.take(1).uppercase(), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) else if (userName?.isNotBlank() == true) Text(userName!!.take(1).uppercase(), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) else Text("S", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Study Dashboard", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.titleMedium)
                            Text("$currentExam Prep", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Syllabus") },
                    label = { Text("Syllabus") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.Insights, contentDescription = "Analysis") },
                    label = { Text("Analysis") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(Icons.Default.Timer, contentDescription = "Timer") },
                    label = { Text("Timer") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (selectedExams.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedExams.forEach { exam ->
                        val isSelected = currentExam == exam
                        Surface(
                            onClick = { viewModel.selectExam(exam) },
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = exam,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                },
                label = "tab_transition",
                modifier = Modifier.weight(1f)
            ) { tab ->
                if (tab == 0) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        
                        ExamCountdownCard(currentExam)
                        val overallCompletion = viewModel.getCompletionPercentage(topics)
                        OverallProgressCard(progress = overallCompletion)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        val subjectTopicsMap = selectedSubjects.associateWith { subject ->
                            topics.filter { it.subject == subject }
                        }
                        
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            subjectTopicsMap.forEach { (subject, subjectTopics) ->
                                item(key = subject) {
                                    SubjectCard(
                                        subject = subject,
                                        topics = subjectTopics,
                                        onTopicToggle = { viewModel.toggleTopicCompletion(it) },
                                        onDeleteTopic = { viewModel.deleteTopic(it) },
                                        onUpdateTopicLinks = { topic, links -> viewModel.updateTopicLinks(topic, links) },
                                        onAddTopic = { name -> viewModel.addTopic(currentExam ?: "", subject, name) }
                                    )
                                }
                            }
                        }

                    }
                } else if (tab == 1) {
                    AnalysisScreen(viewModel)
                } else {
                    TimerScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun OverallProgressCard(progress: Float) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")
    val displayProgress = (animatedProgress * 100).roundToInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "OVERALL SYLLABUS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$displayProgress",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp)) {
                    val trackColor = MaterialTheme.colorScheme.tertiary
                    val progressColor = MaterialTheme.colorScheme.primary
                    Canvas(modifier = Modifier.size(48.dp)) {
                        drawArc(
                            color = trackColor,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                                ,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun SubjectCard(
    subject: String,
    topics: List<Topic>,
    onTopicToggle: (Topic) -> Unit,
    onDeleteTopic: (Topic) -> Unit,
    onUpdateTopicLinks: (Topic, String) -> Unit,
    onAddTopic: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddTopicDialog by remember { mutableStateOf(false) }
    val completedCount = topics.count { it.isCompleted }
    val progress = if (topics.isEmpty()) 0f else completedCount.toFloat() / topics.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp)) {
                    val trackColor = MaterialTheme.colorScheme.tertiary
                    val progressColor = MaterialTheme.colorScheme.primary
                    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")
                    Canvas(modifier = Modifier.size(48.dp)) {
                        drawArc(
                            color = trackColor,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "${(animatedProgress * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$completedCount / ${topics.size} Topics Completed",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    topics.forEach { topic ->
                        TopicItem(
                            topic = topic,
                            onTopicToggle = onTopicToggle,
                            onDeleteTopic = onDeleteTopic,
                            onUpdateTopicLinks = onUpdateTopicLinks
                        )
                    }
                    
                    TextButton(
                        onClick = { showAddTopicDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Chapter")
                    }
                }
            }
        }
    }

    if (showAddTopicDialog) {
        var newTopicName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddTopicDialog = false },
            title = { Text("Add Chapter to $subject") },
            text = {
                OutlinedTextField(
                    value = newTopicName,
                    onValueChange = { newTopicName = it },
                    label = { Text("Chapter Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTopicName.isNotBlank()) {
                            onAddTopic(newTopicName)
                            showAddTopicDialog = false
                        }
                    },
                    enabled = newTopicName.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTopicDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AnalysisScreen(viewModel: SyllabusViewModel) {
    val testMarks by viewModel.testMarks.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    var showAddTestDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Performance Analysis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    if (testMarks.isNotEmpty()) {
                        val averageScore = testMarks.map { (it.score / it.maxScore) * 100 }.average().toFloat()
                        val highestScore = testMarks.maxOf { (it.score / it.maxScore) * 100 }
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatCard("Average", String.format("%.1f%%", averageScore), Modifier.weight(1f))
                            StatCard("Highest", String.format("%.1f%%", highestScore), Modifier.weight(1f))
                            StatCard("Tests", testMarks.size.toString(), Modifier.weight(1f))
                        }
                    }
                }
                
                item {
                    Text("Productivity Heatmap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Peak Study Hours (Completed Topics)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            ProductivityHeatmap(topics = topics, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
                
                if (testMarks.isNotEmpty()) {
                    item {
                        Text("Test Scores Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                TestScoresChart(testMarks)
                            }
                        }
                    }
                    
                    item {
                        Text("Recent Tests", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    
                    items(testMarks.reversed()) { test ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(test.testName, fontWeight = FontWeight.SemiBold)
                                    val date = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(test.dateMillis))
                                    Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                val percentage = (test.score / test.maxScore) * 100
                                val color = if (percentage >= 80) androidx.compose.ui.graphics.Color(0xFF4CAF50) else if (percentage >= 50) androidx.compose.ui.graphics.Color(0xFFFFC107) else MaterialTheme.colorScheme.error
                                Text(
                                    text = String.format("%.1f%%", percentage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No test marks recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddTestDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Test Mark")
        }
    }

    if (showAddTestDialog) {
        var testName by remember { mutableStateOf("") }
        var scoreStr by remember { mutableStateOf("") }
        var maxScoreStr by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showAddTestDialog = false },
            title = { Text("Add Test Score") },
            text = {
                Column {
                    OutlinedTextField(
                        value = testName,
                        onValueChange = { testName = it },
                        label = { Text("Test Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = scoreStr,
                            onValueChange = { scoreStr = it },
                            label = { Text("Your Score") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = maxScoreStr,
                            onValueChange = { maxScoreStr = it },
                            label = { Text("Max Score") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val score = scoreStr.toFloatOrNull()
                        val maxScore = maxScoreStr.toFloatOrNull()
                        if (testName.isNotBlank() && score != null && maxScore != null && maxScore > 0 && score <= maxScore) {
                            viewModel.addTestMark(testName, score, maxScore)
                            showAddTestDialog = false
                        }
                    },
                    enabled = testName.isNotBlank() && scoreStr.isNotBlank() && maxScoreStr.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTestDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
        }
    }
}


@Composable
fun TestScoresChart(testMarks: List<com.example.data.TestMark>) {
    val scores = testMarks.map { if (it.maxScore > 0) (it.score / it.maxScore) * 100f else 0f }
    val maxScoreVal = 100f
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.onSurface
    
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (scores.isEmpty()) return@Canvas
        
        val width = size.width
        val height = size.height
        val stepX = if (scores.size > 1) width / (scores.size - 1) else width
        
        // Draw axis lines
        drawLine(color = surfaceColor.copy(alpha = 0.2f), start = androidx.compose.ui.geometry.Offset(0f, height), end = androidx.compose.ui.geometry.Offset(width, height), strokeWidth = 2f)
        
        val path = androidx.compose.ui.graphics.Path()
        val points = mutableListOf<androidx.compose.ui.geometry.Offset>()
        
        scores.forEachIndexed { index, score ->
            val x = index * stepX
            val y = height - (score / maxScoreVal) * height
            points.add(androidx.compose.ui.geometry.Offset(x, y))
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = primaryColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )
        
        points.forEach { point ->
            drawCircle(
                color = primaryColor,
                radius = 12f,
                center = point
            )
            drawCircle(
                color = androidx.compose.ui.graphics.Color.White,
                radius = 6f,
                center = point
            )
        }
    }
}

@Composable
fun TopicItem(
    topic: Topic,
    onTopicToggle: (Topic) -> Unit,
    onDeleteTopic: (Topic) -> Unit,
    onUpdateTopicLinks: (Topic, String) -> Unit
) {
    var showLinksDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { onTopicToggle(topic) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (topic.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (topic.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                contentDescription = "Toggle completion",
                tint = if (topic.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = topic.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (topic.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
            )
            if (!topic.studyLinks.isNullOrBlank()) {
                Text(
                    text = "View links attached",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        IconButton(onClick = { showLinksDialog = true }) {
            Icon(Icons.Default.Link, contentDescription = "Study Links", tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = { onDeleteTopic(topic) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Topic", tint = MaterialTheme.colorScheme.error)
        }
    }

    if (showLinksDialog) {
        var links by remember { mutableStateOf(topic.studyLinks ?: "") }
        AlertDialog(
            onDismissRequest = { showLinksDialog = false },
            title = { Text("Study Links & Notes") },
            text = {
                OutlinedTextField(
                    value = links,
                    onValueChange = { links = it },
                    label = { Text("Links / Notes") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    maxLines = 5
                )
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateTopicLinks(topic, links)
                    showLinksDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showLinksDialog = false }) {
                    androidx.compose.material3.Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TimerScreen(viewModel: SyllabusViewModel) {
    val timeInSeconds by viewModel.timerTime.collectAsStateWithLifecycle()
    val totalTime by viewModel.timerTotalTime.collectAsStateWithLifecycle()
    val isRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()

    val minutes = timeInSeconds / 60
    val seconds = timeInSeconds % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)
    
    val progress = if (totalTime > 0) timeInSeconds.toFloat() / totalTime else 0f
    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.runtime.LaunchedEffect(timeInSeconds) {
        if (timeInSeconds == 0 && totalTime > 0) {
            try {
                val notification = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                val r = android.media.RingtoneManager.getRingtone(context, notification)
                r.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Notification
            val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = android.app.NotificationChannel(
                    "timer_channel",
                    "Timer Notifications",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
            
            val builder = androidx.core.app.NotificationCompat.Builder(context, "timer_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Time's Up!")
                .setContentText("Your study session is complete. Take a break!")
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                
            notificationManager.notify(1001, builder.build())
        }
    }
    
    var showCustomTimeDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Study Timer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(48.dp))
            
            Box(
                modifier = Modifier
                    .size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 64.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isRunning) "Focus Mode" else if (timeInSeconds == 0) "Time's Up!" else "Ready",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                if (isRunning) {
                    FloatingActionButton(
                        onClick = { viewModel.pauseTimer() },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause")
                    }
                } else {
                    FloatingActionButton(
                        onClick = { 
                            if (timeInSeconds == 0) viewModel.resetTimer(25)
                            viewModel.startTimer() 
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                    }
                }
                
                FloatingActionButton(
                    onClick = { viewModel.resetTimer(25) },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            androidx.compose.material3.TextButton(onClick = { showCustomTimeDialog = true }) {
                Text("Set Custom Time")
            }
        }
    }

    if (showCustomTimeDialog) {
        var minutesStr by remember { mutableStateOf("") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showCustomTimeDialog = false },
            title = { Text("Set Timer (Minutes)") },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = minutesStr,
                    onValueChange = { minutesStr = it },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = minutesStr.toIntOrNull()
                        if (mins != null && mins > 0) {
                            viewModel.resetTimer(mins)
                            showCustomTimeDialog = false
                        }
                    }
                ) {
                    Text("Set")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showCustomTimeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun ExamCountdownCard(exam: String?) {
    if (exam == null) return
    
    val examDate = when (exam.uppercase()) {
        "JEE" -> java.time.LocalDate.of(2027, 1, 24)
        "NEET" -> java.time.LocalDate.of(2027, 5, 2)
        "CUET" -> java.time.LocalDate.of(2027, 5, 15)
        else -> return
    }
    
    val today = java.time.LocalDate.now()
    val daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, examDate)
    
    if (daysLeft < 0) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$exam 2027 Countdown",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Keep up the hard work!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$daysLeft",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Days Left",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
@Composable
fun ProductivityHeatmap(topics: List<Topic>, modifier: Modifier = Modifier) {
    val completedTopics = topics.filter { it.isCompleted && it.completedDateMillis != null }
    
    // 7 days (Sun-Sat), 6 time blocks (0-3, 4-7, 8-11, 12-15, 16-19, 20-23)
    val heatMatrix = Array(7) { IntArray(6) }
    var maxCount = 0
    
    for (topic in completedTopics) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = topic.completedDateMillis!!
        }
        val day = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 (Sun) to 6 (Sat)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val timeBlock = hour / 4
        heatMatrix[day][timeBlock]++
        if (heatMatrix[day][timeBlock] > maxCount) {
            maxCount = heatMatrix[day][timeBlock]
        }
    }
    
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val timeLabels = listOf("12a", "4a", "8a", "12p", "4p", "8p")
    
    val baseColor = MaterialTheme.colorScheme.primary
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant
    
    val textMeasurer = androidx.compose.ui.text.rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    
    Canvas(modifier = modifier) {
        val cellWidth = (size.width - 40.dp.toPx()) / 6
        val cellHeight = (size.height - 20.dp.toPx()) / 7
        val padding = 4.dp.toPx()
        
        // Draw X-axis labels (Time Blocks)
        for (i in 0..5) {
            val textLayoutResult = textMeasurer.measure(text = timeLabels[i], style = labelStyle)
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(40.dp.toPx() + i * cellWidth + (cellWidth - textLayoutResult.size.width) / 2, 0f)
            )
        }
        
        // Draw Y-axis labels and Heatmap cells
        for (day in 0..6) {
            val textLayoutResult = textMeasurer.measure(text = days[day], style = labelStyle)
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset((40.dp.toPx() - textLayoutResult.size.width) / 2, 20.dp.toPx() + day * cellHeight + (cellHeight - textLayoutResult.size.height) / 2)
            )
            
            for (block in 0..5) {
                val count = heatMatrix[day][block]
                val alpha = if (maxCount == 0) 0f else 0.2f + 0.8f * (count.toFloat() / maxCount)
                val color = if (count == 0) emptyColor else baseColor.copy(alpha = alpha)
                
                drawRoundRect(
                    color = color,
                    topLeft = Offset(40.dp.toPx() + block * cellWidth + padding, 20.dp.toPx() + day * cellHeight + padding),
                    size = Size(cellWidth - 2 * padding, cellHeight - 2 * padding),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }
    }
}
@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ProfileScreen(viewModel: SyllabusViewModel, onBack: () -> Unit) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userAim by viewModel.userAim.collectAsStateWithLifecycle()
    val profilePicUri by viewModel.profilePicUri.collectAsStateWithLifecycle()
    
    var nameInput by remember { mutableStateOf(userName ?: "") }
    var aimInput by remember { mutableStateOf(userAim ?: "") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                if (userName?.isNotBlank() == true) {
                    Text(userName!!.take(1).uppercase(), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.displayMedium)
                } else {
                    Text("S", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.displayMedium)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Student Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = aimInput,
                onValueChange = { aimInput = it },
                label = { Text("Your Aim / Target Score") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { 
                    viewModel.updateUserProfile(nameInput, null, aimInput)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Profile")
            }
        }
    }
}


@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun SettingsScreen(viewModel: SyllabusViewModel, onBack: () -> Unit) {
    val themeColor by viewModel.themeColor.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    
    val colors = listOf(
        0xFF6200EE.toInt(), // Default Deep Purple
        0xFF03DAC5.toInt(), // Teal
        0xFFB00020.toInt(), // Red
        0xFF3700B3.toInt(), // Dark Purple
        0xFF018786.toInt(), // Dark Teal
        0xFF4CAF50.toInt(), // Green
        0xFFFF9800.toInt(), // Orange
        0xFF2196F3.toInt()  // Blue
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Theme Color", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                colors.forEach { colorInt ->
                    val color = androidx.compose.ui.graphics.Color(colorInt)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { viewModel.updateThemeColor(colorInt) }
                            .then(
                                if (themeColor == colorInt) {
                                    Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = isDarkMode ?: false, // Assuming system default if null, but let's toggle explicitly
                    onCheckedChange = { viewModel.updateDarkMode(it) }
                )
            }
        }
    }
}
