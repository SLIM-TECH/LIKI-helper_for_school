package com.ciberssh.liki.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciberssh.liki.data.models.Lesson
import com.ciberssh.liki.data.models.Schedule
import com.ciberssh.liki.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScheduleScreen(
    onBack: () -> Unit
) {
    var selectedDay by remember { mutableStateOf("Понедельник") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingLesson by remember { mutableStateOf<Lesson?>(null) }

    val days = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PrimaryBlue,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Редактор расписания",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Админ панель",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CardBackgroundDark,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить урок",
                        tint = PrimaryBlue
                    )
                }
            }
        }

        // Day selector
        ScrollableTabRow(
            selectedTabIndex = days.indexOf(selectedDay),
            modifier = Modifier.fillMaxWidth(),
            containerColor = CardBackgroundDark,
            edgePadding = 8.dp
        ) {
            days.forEach { day ->
                Tab(
                    selected = day == selectedDay,
                    onClick = { selectedDay = day }
                ) {
                    Text(
                        text = day,
                        modifier = Modifier.padding(vertical = 12.dp),
                        fontWeight = if (day == selectedDay) FontWeight.Bold else FontWeight.Normal,
                        color = if (day == selectedDay) PrimaryBlue else TextSecondary
                    )
                }
            }
        }

        // Lessons list
        Text(
            text = "Уроки на $selectedDay",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // TODO: Load lessons from repository
            items(emptyList<Lesson>()) { lesson ->
                AdminLessonCard(
                    lesson = lesson,
                    onEdit = { editingLesson = lesson },
                    onDelete = { /* TODO */ }
                )
            }
        }
    }

    if (showAddDialog) {
        AddLessonDialog(
            day = selectedDay,
            onDismiss = { showAddDialog = false },
            onAdd = { lesson ->
                // TODO: Save to repository
                showAddDialog = false
            }
        )
    }

    if (editingLesson != null) {
        EditLessonDialog(
            lesson = editingLesson!!,
            onDismiss = { editingLesson = null },
            onSave = { lesson ->
                // TODO: Update in repository
                editingLesson = null
            }
        )
    }
}

@Composable
fun AdminLessonCard(
    lesson: Lesson,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lesson.number.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.subject,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "${lesson.startTime} - ${lesson.endTime}",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = PrimaryBlue
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = ErrorRed
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonDialog(
    day: String,
    onDismiss: () -> Unit,
    onAdd: (Lesson) -> Unit
) {
    var lessonNumber by remember { mutableStateOf("1") }
    var subject by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("13:30") }
    var endTime by remember { mutableStateOf("14:15") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить урок") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = lessonNumber,
                    onValueChange = { lessonNumber = it },
                    label = { Text("Номер урока") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Предмет") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Начало (ЧЧ:ММ)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Конец (ЧЧ:ММ)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val number = lessonNumber.toIntOrNull()
                    if (number != null && subject.isNotBlank()) {
                        onAdd(Lesson(number, subject, startTime, endTime))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLessonDialog(
    lesson: Lesson,
    onDismiss: () -> Unit,
    onSave: (Lesson) -> Unit
) {
    var subject by remember { mutableStateOf(lesson.subject) }
    var startTime by remember { mutableStateOf(lesson.startTime) }
    var endTime by remember { mutableStateOf(lesson.endTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать урок") },
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
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Начало (ЧЧ:ММ)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Конец (ЧЧ:ММ)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(lesson.copy(subject = subject, startTime = startTime, endTime = endTime))
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
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
