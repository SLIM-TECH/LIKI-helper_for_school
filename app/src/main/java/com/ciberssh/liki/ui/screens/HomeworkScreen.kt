package com.ciberssh.liki.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ciberssh.liki.data.models.Homework
import com.ciberssh.liki.ui.theme.*
import com.ciberssh.liki.ui.viewmodel.HomeworkViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkScreen(
    viewModel: HomeworkViewModel = viewModel(),
    isAdmin: Boolean = false
) {
    val homework by viewModel.homework.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf("Все") }

    val days = listOf("Все", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Header with animation
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(500)) +
                    slideInVertically(animationSpec = tween(500)) { -it }
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryBlue,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Домашнее задание",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${homework.size} заданий",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    if (isAdmin) {
                        FloatingActionButton(
                            onClick = { showAddDialog = true },
                            containerColor = Color.White,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Добавить ДЗ",
                                tint = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }

        // Day filter
        ScrollableTabRow(
            selectedTabIndex = days.indexOf(selectedDay),
            modifier = Modifier.fillMaxWidth(),
            containerColor = SurfaceDark,
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

        // Homework list
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            val filteredHomework = if (selectedDay == "Все") {
                homework
            } else {
                homework.filter { it.dayOfWeek == selectedDay }
            }

            if (filteredHomework.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val infiniteTransition = rememberInfiniteTransition(label = "bounce")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        )

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .scale(scale),
                            tint = SuccessGreen.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Нет заданий",
                            fontSize = 18.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(filteredHomework) { index, hw ->
                        AnimatedHomeworkCard(
                            homework = hw,
                            index = index,
                            onToggleComplete = { viewModel.toggleComplete(hw) },
                            onDelete = if (isAdmin) {
                                { viewModel.deleteHomework(hw.id) }
                            } else null
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog && isAdmin) {
        AddHomeworkDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { hw ->
                viewModel.addHomework(hw)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AnimatedHomeworkCard(
    homework: Homework,
    index: Int,
    onToggleComplete: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(homework.id) {
        kotlinx.coroutines.delay(index * 80L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInHorizontally(animationSpec = tween(400)) { it } +
                expandVertically(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(300)) +
                shrinkVertically(animationSpec = tween(300))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (homework.isCompleted) SuccessGreen.copy(alpha = 0.1f) else CardBackgroundDark
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = homework.isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    colors = CheckboxDefaults.colors(checkedColor = SuccessGreen)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = homework.subject,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (homework.isCompleted) TextSecondary else TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = homework.description,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${homework.dayOfWeek} • ${homework.dueDate}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                if (onDelete != null) {
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHomeworkDialog(
    onDismiss: () -> Unit,
    onAdd: (Homework) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf("Понедельник") }
    var dueDate by remember { mutableStateOf("") }

    val days = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить ДЗ") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Предмет") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDay,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("День недели") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        days.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    selectedDay = day
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Срок сдачи") },
                    placeholder = { Text("дд.мм.гггг") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank() && description.isNotBlank()) {
                        onAdd(
                            Homework(
                                id = UUID.randomUUID().toString(),
                                subject = subject,
                                description = description,
                                dueDate = dueDate,
                                dayOfWeek = selectedDay,
                                createdAt = System.currentTimeMillis().toString()
                            )
                        )
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
