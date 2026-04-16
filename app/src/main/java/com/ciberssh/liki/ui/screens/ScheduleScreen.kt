package com.ciberssh.liki.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ciberssh.liki.data.models.Lesson
import com.ciberssh.liki.ui.theme.*
import com.ciberssh.liki.ui.viewmodel.ScheduleViewModel
import com.ciberssh.liki.utils.PreferencesManager
import java.text.SimpleDateFormat
import java.util.*

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

    // Get current date
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
    val currentDate = dateFormat.format(Date())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5B4E9D),
                        Color(0xFF3D2F6B)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header - exact copy from screenshot
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Расписание на сегодня",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentDate,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Lessons list - exact card style from screenshot
            val selectedSchedule = schedule.find { it.dayOfWeek == selectedDay }

            if (selectedSchedule != null) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(selectedSchedule.lessons) { index, lesson ->
                        LessonCardExact(
                            lesson = lesson,
                            isAdmin = isAdmin,
                            onClick = if (isAdmin) {
                                { editingLesson = lesson }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Add button - exact style from screenshot
            if (isAdmin) {
                Button(
                    onClick = { /* TODO: Add new lesson */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5B86E5)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Добавить урок",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }

    // Edit dialog
    if (editingLesson != null && isAdmin) {
        LessonEditDialog(
            lesson = editingLesson!!,
            onDismiss = { editingLesson = null },
            onSave = { updatedLesson ->
                editingLesson = null
            }
        )
    }
}

@Composable
fun LessonCardExact(
    lesson: Lesson,
    isAdmin: Boolean,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle - exact style from screenshot
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = getSubjectColor(lesson.subject),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getSubjectIcon(lesson.subject),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Subject name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.subject,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D3142)
                )
                if (lesson.teacher.isNotEmpty()) {
                    Text(
                        text = lesson.teacher,
                        fontSize = 13.sp,
                        color = Color(0xFF9A9A9A)
                    )
                }
            }

            // Time - exact style from screenshot
            Text(
                text = "${lesson.startTime} - ${lesson.endTime}",
                fontSize = 13.sp,
                color = Color(0xFF9A9A9A)
            )
        }
    }
}

fun getSubjectIcon(subject: String): ImageVector {
    return when {
        subject.contains("Математика", ignoreCase = true) ||
        subject.contains("Алгебра", ignoreCase = true) ||
        subject.contains("Геометрия", ignoreCase = true) -> Icons.Default.Calculate
        subject.contains("Русский", ignoreCase = true) ||
        subject.contains("Литература", ignoreCase = true) -> Icons.Default.MenuBook
        subject.contains("Английский", ignoreCase = true) -> Icons.Default.Language
        subject.contains("История", ignoreCase = true) ||
        subject.contains("Обществознание", ignoreCase = true) ||
        subject.contains("ЧиО", ignoreCase = true) -> Icons.Default.HistoryEdu
        subject.contains("Физика", ignoreCase = true) -> Icons.Default.Science
        subject.contains("Химия", ignoreCase = true) -> Icons.Default.Biotech
        subject.contains("Биология", ignoreCase = true) -> Icons.Default.Eco
        subject.contains("География", ignoreCase = true) -> Icons.Default.Public
        subject.contains("Информатика", ignoreCase = true) -> Icons.Default.Computer
        subject.contains("Физкультура", ignoreCase = true) ||
        subject.contains("Физ-ра", ignoreCase = true) -> Icons.Default.FitnessCenter
        subject.contains("Музыка", ignoreCase = true) -> Icons.Default.MusicNote
        subject.contains("ИЗО", ignoreCase = true) ||
        subject.contains("Рисование", ignoreCase = true) -> Icons.Default.Palette
        else -> Icons.Default.School
    }
}

fun getSubjectColor(subject: String): Color {
    return when {
        subject.contains("Математика", ignoreCase = true) ||
        subject.contains("Алгебра", ignoreCase = true) ||
        subject.contains("Геометрия", ignoreCase = true) -> Color(0xFF5B86E5)
        subject.contains("Русский", ignoreCase = true) ||
        subject.contains("Литература", ignoreCase = true) -> Color(0xFFFF6B6B)
        subject.contains("Английский", ignoreCase = true) -> Color(0xFF4ECDC4)
        subject.contains("История", ignoreCase = true) ||
        subject.contains("Обществознание", ignoreCase = true) ||
        subject.contains("ЧиО", ignoreCase = true) -> Color(0xFFFFBE0B)
        subject.contains("Физика", ignoreCase = true) -> Color(0xFF8B5CF6)
        subject.contains("Химия", ignoreCase = true) -> Color(0xFF06FFA5)
        subject.contains("Биология", ignoreCase = true) -> Color(0xFF4ADE80)
        subject.contains("География", ignoreCase = true) -> Color(0xFF3B82F6)
        subject.contains("Информатика", ignoreCase = true) -> Color(0xFF6366F1)
        subject.contains("Физкультура", ignoreCase = true) ||
        subject.contains("Физ-ра", ignoreCase = true) -> Color(0xFFEF4444)
        subject.contains("Музыка", ignoreCase = true) -> Color(0xFFEC4899)
        subject.contains("ИЗО", ignoreCase = true) ||
        subject.contains("Рисование", ignoreCase = true) -> Color(0xFFF59E0B)
        else -> Color(0xFF64748B)
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
    var teacher by remember { mutableStateOf(lesson.teacher) }
    var room by remember { mutableStateOf(lesson.room) }
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
                    containerColor = Color(0xFF5B86E5)
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
