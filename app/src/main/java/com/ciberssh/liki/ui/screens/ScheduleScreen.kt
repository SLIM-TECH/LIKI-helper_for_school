package com.ciberssh.liki.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    val schedule by viewModel.schedule.collectAsState()
    val currentDay by viewModel.currentDay.collectAsState()
    var selectedDay by remember { mutableStateOf(currentDay) }

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
                        BackgroundDark,
                        Color(0xFF0D1226)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Modern header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryBlue, AccentPurple)
                        )
                    )
                    .shadow(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📅 Расписание",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Сегодня: $currentDay",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Day selector
            ScrollableTabRow(
                selectedTabIndex = schedule.indexOfFirst { it.dayOfWeek == selectedDay }.coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                containerColor = SurfaceDark,
                edgePadding = 8.dp
            ) {
                schedule.forEach { day ->
                    val isSelected = day.dayOfWeek == selectedDay
                    val isToday = day.dayOfWeek == currentDay

                    Tab(
                        selected = isSelected,
                        onClick = { selectedDay = day.dayOfWeek },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = day.dayOfWeek,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isSelected -> PrimaryBlue
                                    isToday -> AccentOrange
                                    else -> TextSecondary
                                }
                            )
                            if (isToday && !isSelected) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(AccentOrange)
                                )
                            }
                        }
                    }
                }
            }

            // Lessons list
            val selectedSchedule = schedule.find { it.dayOfWeek == selectedDay }

            if (selectedSchedule != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(selectedSchedule.lessons) { index, lesson ->
                        AnimatedLessonCard(lesson, index)
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
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedLessonCard(lesson: Lesson, index: Int) {
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
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = CardBackgroundDark
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                CardBackgroundDark,
                                SurfaceDark
                            )
                        )
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Modern lesson number badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryBlue, AccentPurple)
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lesson.number.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Lesson info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lesson.subject,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${lesson.startTime} - ${lesson.endTime}",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
