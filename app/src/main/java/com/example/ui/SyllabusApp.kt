package com.example.ui

import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable


import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Timer

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.Flashcard
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
                var showWelcome by remember { mutableStateOf(true) }
                AnimatedContent(
                    targetState = showWelcome,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "welcome_transition"
                ) { isWelcome ->
                    if (isWelcome) {
                        WelcomeScreen(onStart = { showWelcome = false })
                    } else {
                        SetupScreen(
                            onComplete = { exam, subjects ->
                                viewModel.completeSetup(exam, subjects)
                            }
                        )
                    }
                }
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

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold { paddingValues ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(animationSpec = tween(800), initialOffsetY = { 50 }),
            modifier = Modifier.fillMaxSize()
        ) {
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
                        
                        val bgColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, label = "bgColor")
                        val textColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, label = "textColor")
                        
                        Surface(
                            onClick = { 
                                selectedExams = if (isSelected && selectedExams.size > 1) {
                                    selectedExams - exam
                                } else {
                                    selectedExams + exam
                                }
                            },
                            shape = CircleShape,
                            color = bgColor,
                            border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                                Text(
                                    text = exam,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = textColor
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
                    modifier = Modifier.weight(1f, fill = false).animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableSubjects, key = { it }) { subject ->
                        val isSelected = selectedSubjects.contains(subject)
                        
                        val bgAlpha by animateFloatAsState(targetValue = if (isSelected) 0.1f else 0f, label = "bgAlpha")
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = bgAlpha))
                                .clickable {
                                    selectedSubjects = if (isSelected) {
                                        selectedSubjects - subject
                                    } else {
                                        selectedSubjects + subject
                                    }
                                }
                                .padding(16.dp)
                                .animateItem(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedContent(targetState = isSelected, label = "icon") { state ->
                                if (state) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Circle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
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
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: SyllabusViewModel) {
    val currentExam by viewModel.currentExam.collectAsStateWithLifecycle()
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    val selectedExams by viewModel.selectedExams.collectAsStateWithLifecycle()
    val selectedSubjects by viewModel.selectedSubjects.collectAsStateWithLifecycle()
    val allTopics by viewModel.allTopics.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }
    var showCountdown by remember { mutableStateOf(false) }
    
    if (showCountdown) {
        ExamCountdownScreen(exam = currentExam, onBack = { showCountdown = false })
        return
    }

    if (showSettings) {
        SettingsScreen(viewModel = viewModel, onBack = { showSettings = false })
        return
    }
    
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.statusBars)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Hello, Student", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Your Custom\nSyllabus", 
                            style = MaterialTheme.typography.displaySmall, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 40.sp
                        )
                    }
                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tabs = listOf(
                            "Overview" to Icons.Default.Home,
                            "Syllabus" to Icons.Default.Menu,
                            "Cards" to Icons.Default.ViewCarousel,
                            "Vault" to Icons.Default.Folder,
                            "Timer" to Icons.Default.Timer
                        )
                        tabs.forEachIndexed { index, pair ->
                            val selected = currentTab == index
                            IconButton(
                                onClick = { currentTab = index },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        pair.second, 
                                        contentDescription = pair.first,
                                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(if (selected) 28.dp else 24.dp)
                                    )
                                    if (selected) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentTab == 1 && selectedExams.size > 1) {
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
                    val direction = if (targetState > initialState) 1 else -1
                    (fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { width -> direction * width }).togetherWith(
                        fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) { width -> -direction * width }
                    )
                },
                label = "tab_transition",
                modifier = Modifier.weight(1f)
            ) { tab ->
                when (tab) {
                    0 -> OverviewScreen(viewModel, onExamClick = { exam ->
                        viewModel.selectExam(exam)
                        currentTab = 1
                    })
                    1 -> Column(modifier = Modifier.fillMaxSize()) {
                        ExamCountdownCard(currentExam, onClick = { showCountdown = true })
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
                                        onUpdateTopicNotes = { topic, notes -> viewModel.updateTopicNotes(topic, notes) },
                                        onToggleDailyGoal = { topic -> viewModel.toggleDailyGoal(topic) },
                                        onAddTopic = { name -> viewModel.addTopic(currentExam ?: "", subject, name) }
                                    )
                                }
                            }
                        }
                    }
                    2 -> FlashcardsScreen(viewModel)
                    3 -> VaultScreen(viewModel)
                    4 -> TimerScreen(viewModel)
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
    onDeleteTopic: ((Topic) -> Unit)? = null,
    onUpdateTopicLinks: ((Topic, String) -> Unit)? = null,
    onUpdateTopicNotes: ((Topic, String) -> Unit)? = null,
    onToggleDailyGoal: ((Topic) -> Unit)? = null,
    onAddTopic: ((String) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val completedCount = topics.count { it.isCompleted }
    val totalCount = topics.size
    val progress = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer,
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${(progress * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) androidx.compose.material.icons.Icons.Default.ExpandLess else androidx.compose.material.icons.Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (expanded) {
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                Column(modifier = Modifier.fillMaxWidth()) {
                    topics.forEachIndexed { index, topic ->
                        TopicItem(
                            topic = topic,
                            onToggle = { onTopicToggle(topic) },
                            onDelete = if (onDeleteTopic != null) { { onDeleteTopic(topic) } } else null,
                            onUpdateLinks = onUpdateTopicLinks,
                            onUpdateNotes = onUpdateTopicNotes,
                            onToggleDailyGoal = onToggleDailyGoal
                        )
                        if (index < topics.size - 1) {
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                    if (onAddTopic != null) {
                        var showAddDialog by remember { mutableStateOf(false) }
                        
                        TextButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Topic")
                        }
                        
                        if (showAddDialog) {
                            var newTopicName by remember { mutableStateOf("") }
                            AlertDialog(
                                onDismissRequest = { showAddDialog = false },
                                title = { Text("Add Topic to $subject") },
                                text = {
                                    OutlinedTextField(
                                        value = newTopicName,
                                        onValueChange = { newTopicName = it },
                                        label = { Text("Topic Name") },
                                        singleLine = true
                                    )
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        if (newTopicName.isNotBlank()) {
                                            onAddTopic(newTopicName)
                                            showAddDialog = false
                                        }
                                    }) { Text("Add") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopicItem(
    topic: Topic,
    onToggle: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onUpdateLinks: ((Topic, String) -> Unit)? = null,
    onUpdateNotes: ((Topic, String) -> Unit)? = null,
    onToggleDailyGoal: ((Topic) -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    var showLinkDialog by remember { mutableStateOf(false) }
    var showNotesDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = topic.isCompleted, label = "toggleIcon") { completed ->
                if (completed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Toggle completion",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = "Toggle completion",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = topic.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (topic.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!topic.studyLinks.isNullOrBlank()) {
                    Text(
                        text = "Links attached",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (!topic.notes.isNullOrBlank()) {
                    Text(
                        text = "Notes attached",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                if (topic.isDailyGoal) {
                    Text(
                        text = "Daily Goal",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                if (onToggleDailyGoal != null) {
                    DropdownMenuItem(
                        text = { Text(if (topic.isDailyGoal) "Remove Daily Goal" else "Set as Daily Goal") },
                        onClick = { onToggleDailyGoal(topic); showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) }
                    )
                }
                if (onUpdateNotes != null) {
                    DropdownMenuItem(
                        text = { Text("Add/Edit Notes") },
                        onClick = { showNotesDialog = true; showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                }
                if (onUpdateLinks != null) {
                    DropdownMenuItem(
                        text = { Text("Attach Links") },
                        onClick = { showLinkDialog = true; showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
                    )
                }
                if (onDelete != null) {
                    DropdownMenuItem(
                        text = { Text("Delete Topic") },
                        onClick = { onDelete(); showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
    
    if (showLinkDialog && onUpdateLinks != null) {
        var links by remember { mutableStateOf(topic.studyLinks ?: "") }
        AlertDialog(
            onDismissRequest = { showLinkDialog = false },
            title = { Text("Attach Study Links") },
            text = {
                OutlinedTextField(
                    value = links,
                    onValueChange = { links = it },
                    label = { Text("URLs (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateLinks(topic, links)
                    showLinkDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLinkDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (showNotesDialog && onUpdateNotes != null) {
        var notes by remember { mutableStateOf(topic.notes ?: "") }
        AlertDialog(
            onDismissRequest = { showNotesDialog = false },
            title = { Text("Add Notes") },
            text = {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes for this topic") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateNotes(topic, notes)
                    showNotesDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExamCountdownCard(exam: String?, onClick: () -> Unit) {
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
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
                    text = "Tap to view full countdown",
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
fun CountdownUnit(value: String, label: String, progress: Float, color: androidx.compose.ui.graphics.Color) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "progress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = value,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun ExamCountdownScreen(exam: String?, onBack: () -> Unit) {
    if (exam == null) return
    
    val examDate = when (exam.uppercase()) {
        "JEE" -> java.time.LocalDateTime.of(2027, 1, 24, 9, 0)
        "NEET" -> java.time.LocalDateTime.of(2027, 5, 2, 14, 0)
        "CUET" -> java.time.LocalDateTime.of(2027, 5, 15, 9, 0)
        else -> return
    }
    
    var timeNow by remember { mutableStateOf(java.time.LocalDateTime.now()) }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            timeNow = java.time.LocalDateTime.now()
        }
    }
    
    val duration = java.time.Duration.between(timeNow, examDate)
    val totalSeconds = maxOf(0L, duration.seconds)
    
    val days = totalSeconds / (24 * 3600)
    val hours = (totalSeconds % (24 * 3600)) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    val maxDays = 365f
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
        ) {
            Text(
                text = "The exam begins in",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.primary
            )
            
            CountdownUnit(
                value = String.format("%02d", days),
                label = "Days",
                progress = minOf(1f, days / maxDays),
                color = colors[0]
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CountdownUnit(
                value = String.format("%02d", hours),
                label = "Hours",
                progress = hours / 24f,
                color = colors[1]
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CountdownUnit(
                value = String.format("%02d", minutes),
                label = "Min",
                progress = minutes / 60f,
                color = colors[2]
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CountdownUnit(
                value = String.format("%02d", seconds),
                label = "Sec",
                progress = seconds / 60f,
                color = colors[3]
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomEnd).clickable { /* Register action if applicable */ }
        ) {
            Text(
                text = "Prepare\nNow",
                style = MaterialTheme.typography.labelLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
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
    
    val themes = listOf(
        "Theme 1 (Lavender)",
        "Theme 2 (Mint)",
        "Theme 3 (Peach)",
        "Theme 4 (Ocean)",
        "Theme 5 (Rose)"
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
            Text("Theme Palette", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            themes.forEachIndexed { index, name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateThemeColor(index) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(name, style = MaterialTheme.typography.bodyLarge)
                    if (themeColor == index || (themeColor == null && index == 0)) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
@Composable
fun OverviewScreen(
    viewModel: SyllabusViewModel,
    onExamClick: (String) -> Unit
) {
    val selectedExams by viewModel.selectedExams.collectAsStateWithLifecycle()
    val selectedSubjects by viewModel.selectedSubjects.collectAsStateWithLifecycle()
    val allTopics by viewModel.allTopics.collectAsStateWithLifecycle()
    val studyStreak by viewModel.studyStreak.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf<String?>(null) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = if (selectedFilter == null) 0 else selectedExams.toList().indexOf(selectedFilter) + 1,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                val index = if (selectedFilter == null) 0 else selectedExams.toList().indexOf(selectedFilter) + 1
                if (index >= 0 && index < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            Tab(
                selected = selectedFilter == null,
                onClick = { selectedFilter = null },
                text = { Text("All") }
            )
            selectedExams.toList().forEach { exam ->
                Tab(
                    selected = selectedFilter == exam,
                    onClick = { selectedFilter = exam },
                    text = { Text(exam) }
                )
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(androidx.compose.material.icons.Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Study Streak", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("$studyStreak Days", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
            
            item {
                val dailyGoals = allTopics.filter { it.isDailyGoal && !it.isCompleted }
                if (dailyGoals.isNotEmpty()) {
                    Text("Today's Goals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        dailyGoals.forEach { goal ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(goal.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    IconButton(onClick = { viewModel.toggleTopicCompletion(goal) }) {
                                        Icon(androidx.compose.material.icons.Icons.Outlined.Circle, contentDescription = "Complete")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    if (selectedFilter == null) "All Exams" else "$selectedFilter Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            val examsToShow = if (selectedFilter == null) selectedExams.toList() else listOf(selectedFilter!!)
            
            items(examsToShow, key = { it }) { exam ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .clickable { onExamClick(exam) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = exam,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val examTopics = allTopics.filter { it.exam == exam }
                    
                    // Filter subjects that belong to this exam, or just show all selected subjects for this exam
                    // Let's deduce what subjects belong to the exam based on Topics or a hardcoded list.
                    val getSubjectsForExam = { e: String ->
                        when (e) {
                            "JEE" -> listOf("Physics", "Chemistry", "Maths")
                            "NEET" -> listOf("Physics", "Chemistry", "Biology")
                            "CUET" -> listOf("General Test", "English")
                            else -> emptyList()
                        }
                    }
                    val examSubjects = selectedSubjects.intersect(getSubjectsForExam(exam).toSet())
                    
                    if (examSubjects.isEmpty()) {
                        Text("No subjects selected", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        examSubjects.forEach { subject ->
                            val subjectTopics = examTopics.filter { it.subject == subject }
                            val total = subjectTopics.size
                            val completed = subjectTopics.count { it.isCompleted }
                            val progress = if (total == 0) 0f else completed.toFloat() / total
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = subject,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(animationSpec = tween(1000))
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        androidx.compose.material.icons.Icons.Default.Insights,
                        contentDescription = "Logo",
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000, delayMillis = 400))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Master Your prep",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Track your syllabus effortlessly, analyze weak areas, and ace your upcoming exams with focus.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))
            
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(animationSpec = tween(1000, delayMillis = 800))
            ) {
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Get Started", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun FlashcardsScreen(viewModel: SyllabusViewModel) {
    val flashcards by viewModel.flashcards.collectAsStateWithLifecycle()
    var showAddCardDialog by remember { mutableStateOf(false) }
    var reviewMode by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Flashcards", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (flashcards.isNotEmpty()) {
                    Button(onClick = { reviewMode = true }) {
                        Text("Review")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (flashcards.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No flashcards yet. Add one to start Active Recall!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(flashcards) { card ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(card.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("A: ${card.answer}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showAddCardDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Flashcard")
        }
    }
    
    if (showAddCardDialog) {
        var question by remember { mutableStateOf("") }
        var answer by remember { mutableStateOf("") }
        val currentExam by viewModel.currentExam.collectAsStateWithLifecycle()
        
        AlertDialog(
            onDismissRequest = { showAddCardDialog = false },
            title = { Text("Add Flashcard") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = question,
                        onValueChange = { question = it },
                        label = { Text("Question") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { answer = it },
                        label = { Text("Answer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (question.isNotBlank() && answer.isNotBlank() && currentExam != null) {
                        viewModel.addFlashcard(com.example.data.Flashcard(exam = currentExam!!, subject = "General", question = question, answer = answer))
                        showAddCardDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCardDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    if (reviewMode && flashcards.isNotEmpty()) {
        // Simple review mode dialog
        var currentIndex by remember { mutableStateOf(0) }
        var showAnswer by remember { mutableStateOf(false) }
        val card = flashcards[currentIndex]
        
        AlertDialog(
            onDismissRequest = { reviewMode = false },
            title = { Text("Review") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(card.question, style = MaterialTheme.typography.headlineSmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    if (showAnswer) {
                        Text(card.answer, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else {
                        Button(onClick = { showAnswer = true }) {
                            Text("Show Answer")
                        }
                    }
                }
            },
            confirmButton = {
                if (showAnswer) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            viewModel.updateFlashcardProgress(card, 1)
                            if (currentIndex < flashcards.size - 1) { currentIndex++; showAnswer = false } else { reviewMode = false }
                        }) { Text("Hard") }
                        TextButton(onClick = {
                            viewModel.updateFlashcardProgress(card, 3)
                            if (currentIndex < flashcards.size - 1) { currentIndex++; showAnswer = false } else { reviewMode = false }
                        }) { Text("Good") }
                        TextButton(onClick = {
                            viewModel.updateFlashcardProgress(card, 5)
                            if (currentIndex < flashcards.size - 1) { currentIndex++; showAnswer = false } else { reviewMode = false }
                        }) { Text("Easy") }
                    }
                }
            }
        )
    }
}

@Composable
fun VaultScreen(viewModel: SyllabusViewModel) {
    val topics by viewModel.allTopics.collectAsStateWithLifecycle()
    val vaultTopics = topics.filter { !it.notes.isNullOrBlank() || !it.studyLinks.isNullOrBlank() }
    
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Resource Vault", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (vaultTopics.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Your vault is empty. Add notes or links to topics to see them here.", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(vaultTopics) { topic ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(topic.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (!topic.notes.isNullOrBlank()) {
                                    Text("Notes:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                    Text(topic.notes!!, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                if (!topic.studyLinks.isNullOrBlank()) {
                                    Text("Links:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                    Text(topic.studyLinks!!, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimerScreen(viewModel: SyllabusViewModel) {
    var timeLeft by remember { mutableStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    
    androidx.compose.runtime.LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                kotlinx.coroutines.delay(1000)
                timeLeft--
            }
            if (timeLeft == 0) {
                isRunning = false
                viewModel.incrementStreak()
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Focus Timer", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
                CircularProgressIndicator(
                    progress = { timeLeft.toFloat() / (25 * 60) },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Button(
                    onClick = { 
                        isRunning = false
                        timeLeft = 25 * 60
                    },
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
