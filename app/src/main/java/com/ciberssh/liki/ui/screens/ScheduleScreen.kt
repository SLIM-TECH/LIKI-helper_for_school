package com.ciberssh.liki.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ciberssh.liki.data.models.Lesson
import com.ciberssh.liki.ui.theme.*
import com.ciberssh.liki.ui.viewmodel.ScheduleViewModel
import com.ciberssh.liki.utils.PreferencesManager
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    val context = LocalContext.current
    val schedule by viewModel.schedule.collectAsState()
    val currentDay by viewModel.currentDay.collectAsState()
    var selectedDay by remember { mutableStateOf(currentDay) }
    val isAdmin by PreferencesManager.isAdmin(context).collectAsState(initial = false)
    var editingLesson by remember { mutableStateOf<Lesson?>(null) }

    LaunchedEffect(currentDay) {
        if (selectedDay.isEmpty()) {
            selectedDay = currentDay
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundGradientStart,
                        BackgroundGradientEnd
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Beautiful header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryBlue, AccentPurple)
                        )
                    )
                    .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Text(
                        text = "📚 Расписание",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentDay,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Day selector with pills
            ScrollableTabRow(
                selectedTabIndex = schedule.indexOfFirst { it.dayOfWeek == selectedDay }.coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                edgePadding = 16.dp,
                indicator = {},
                divider = {}
            ) {
                schedule.forEach { day ->
                    val isSelected = day.dayOfWeek == selectedDay
                    val isToday = day.dayOfWeek == currentDay

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(
                                        colors = listOf(PrimaryBlue, AccentPurple)
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(SurfaceDark, SurfaceDark)
                                    )
                                }
                            )
                            .clickable { selectedDay = day.dayOfWeek }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = day.dayOfWeek,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Lessons list
            val selectedSchedule = schedule.find { it.dayOfWeek == selectedDay }

            if (selectedSchedule != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(selectedSchedule.lessons) { index, lesson ->
                        BeautifulLessonCard(
                            lesson = lesson,
                            index = index,
                            isAdmin = isAdmin,
                            onClick = if (isAdmin) {
                                { editingLesson = lesson }
                            } else null
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет уроков",
                        color = TextSecondary,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

    // Edit dialog for admin
    if (editingLesson != null && isAdmin) {
        LessonEditDialog(
            lesson = editingLesson!!,
            onDismiss = { editingLesson = null },
            onSave = { updatedLesson ->
                // TODO: Save to Supabase
                editingLesson = null
            }
        )
    }
}

@Composable
fun BeautifulLessonCard(
    lesson: Lesson,
    index: Int,
    isAdmin: Boolean,
    onClick: (() -> Unit)? = null
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(lesson) {
        kotlinx.coroutines.delay(index * 20L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(150)) +
                slideInHorizontally(animationSpec = tween(150)) { it / 4 }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .then(
                    if (onClick != null) Modifier.clickable(onClick = onClick)
                    else Modifier
                ),
            colors = CardDefaults.cardColors(
                containerColor = CardBackgroundDark
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Beautiful number badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(6.dp, CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryBlue, AccentPurple)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lesson.number.toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Lesson info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lesson.subject,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${lesson.startTime} - ${lesson.endTime}",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                if (isAdmin) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = AccentPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonEditDialog(
    lesson: Lesson,
    onDismiss: () -> Unit,
    onSave: (Lesson) -> Unit
) {
    var subject by remember { mutableStateOf(lesson.subject) }
    var teacher by remember { mutableStateOf(lesson.teacher ?: "") }
    var room by remember { mutableStateOf(lesson.room ?: "") }
    var startTime by remember { mutableStateOf(lesson.startTime) }
    var endTime by remember { mutableStateOf(lesson.endTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Редактировать урок",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Предмет") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Учитель") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Кабинет") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Начало") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("Конец") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        lesson.copy(
                            subject = subject,
                            teacher = teacher,
                            room = room,
                            startTime = startTime,
                            endTime = endTime
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
