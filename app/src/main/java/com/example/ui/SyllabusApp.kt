package com.example.ui

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Add
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

    var currentTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("S", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Study Dashboard", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.titleMedium)
                            Text("$currentExam Prep", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
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
                        if (topics.isNotEmpty()) {
                            val overallCompletion = viewModel.getCompletionPercentage(topics)
                            OverallProgressCard(progress = overallCompletion)

                            Spacer(modifier = Modifier.height(16.dp))

                            val subjects = topics.groupBy { it.subject }
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                subjects.forEach { (subject, subjectTopics) ->
                                    item(key = subject) {
                                        SubjectCard(
                                            subject = subject,
                                            topics = subjectTopics,
                                            onTopicToggle = { viewModel.toggleTopicCompletion(it) }
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                } else {
                    AnalysisScreen(viewModel)
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
                    .clip(CircleShape),
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
    onTopicToggle: (Topic) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
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
                                Text(
                                    text = "${topic.subject} • ${topic.exam}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisScreen(viewModel: SyllabusViewModel) {
    val testMarks by viewModel.testMarks.collectAsStateWithLifecycle()
    var showAddTestDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Performance Analysis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (testMarks.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No test marks recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Add your offline test marks to see trends.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Test Scores Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        TestScoresChart(testMarks)
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
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
            title = { Text("Add Test Mark") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = testName,
                        onValueChange = { testName = it },
                        label = { Text("Test Name (e.g. Mock 1)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = scoreStr,
                        onValueChange = { scoreStr = it },
                        label = { Text("Score obtained") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = maxScoreStr,
                        onValueChange = { maxScoreStr = it },
                        label = { Text("Maximum Score") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val score = scoreStr.toFloatOrNull()
                        val maxScore = maxScoreStr.toFloatOrNull()
                        if (testName.isNotBlank() && score != null && maxScore != null) {
                            viewModel.addTestMark(testName, score, maxScore)
                            showAddTestDialog = false
                        }
                    },
                    enabled = testName.isNotBlank() && scoreStr.isNotBlank() && maxScoreStr.isNotBlank()
                ) {
                    Text("Add")
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
