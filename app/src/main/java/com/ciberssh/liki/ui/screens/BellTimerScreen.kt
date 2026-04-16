package com.ciberssh.liki.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ciberssh.liki.data.models.BellSchedule
import com.ciberssh.liki.ui.theme.*
import com.ciberssh.liki.ui.viewmodel.ScheduleViewModel
import com.ciberssh.liki.utils.TimeUtils
import java.time.DayOfWeek
import java.util.concurrent.TimeUnit

@Composable
fun BellTimerScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    val timeUntilBell by viewModel.timeUntilBell.collectAsState()
    val bellSchedule by viewModel.bellSchedule.collectAsState()
    val currentDay by viewModel.currentDay.collectAsState()

    val now = TimeUtils.getCurrentTimeInBishkek()
    val isWeekend = now.dayOfWeek == DayOfWeek.SUNDAY

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
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "До звонка",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = currentDay,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        when {
            isWeekend -> {
                // Выходной - показываем "Отдых"
                RestCard()
            }
            timeUntilBell != null -> {
                // Идет урок или перемена
                AnimatedTimerCard(timeUntilBell!!)
            }
            else -> {
                // Уроки закончились
                NoLessonsCard()
            }
        }

        // Bell schedule
        Text(
            text = "Расписание звонков",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bellSchedule) { bell ->
                BellScheduleCard(
                    bell = bell,
                    isCurrent = timeUntilBell?.lessonNumber == bell.number
                )
            }
        }
    }
}

@Composable
fun RestCard() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(animationSpec = tween(500)) { -it }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                Icon(
                    imageVector = Icons.Default.Weekend,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scale),
                    tint = AccentOrange
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Отдых",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Сегодня выходной",
                    fontSize = 16.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun NoLessonsCard() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(animationSpec = tween(500)) { -it }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "rotate")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = SuccessGreen.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Уроков нет",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Все уроки закончились",
                    fontSize = 16.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun AnimatedTimerCard(timeUntilBell: com.ciberssh.liki.utils.TimeUntilBell) {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeUntilBell.milliseconds)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeUntilBell.milliseconds) % 60
    val millis = (timeUntilBell.milliseconds % 1000) / 10

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) +
                slideInVertically(animationSpec = tween(500)) { -it }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (timeUntilBell.isLesson)
                    SuccessGreen.copy(alpha = 0.1f)
                else
                    AccentOrange.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
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
                    imageVector = if (timeUntilBell.isLesson) Icons.Default.School else Icons.Default.FreeBreakfast,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .scale(scale),
                    tint = if (timeUntilBell.isLesson) SuccessGreen else AccentOrange
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (timeUntilBell.isLesson) "До конца урока" else "До начала урока",
                    fontSize = 16.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${timeUntilBell.lessonNumber} урок",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (timeUntilBell.isLesson) SuccessGreen else AccentOrange
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedTimeDigit(
                        value = minutes,
                        label = "минут",
                        color = if (timeUntilBell.isLesson) SuccessGreen else AccentOrange
                    )

                    Text(
                        text = ":",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timeUntilBell.isLesson) SuccessGreen else AccentOrange,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    AnimatedTimeDigit(
                        value = seconds,
                        label = "секунд",
                        color = if (timeUntilBell.isLesson) SuccessGreen else AccentOrange
                    )

                    Text(
                        text = ":",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timeUntilBell.isLesson) SuccessGreen else AccentOrange,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    AnimatedTimeDigit(
                        value = millis,
                        label = "мс",
                        color = if (timeUntilBell.isLesson) SuccessGreen else AccentOrange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val progress = remember(timeUntilBell.milliseconds) {
                    val totalTime = if (timeUntilBell.isLesson) 40 * 60 * 1000L else 10 * 60 * 1000L
                    1f - (timeUntilBell.milliseconds.toFloat() / totalTime).coerceIn(0f, 1f)
                }

                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(300),
                    label = "progress"
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (timeUntilBell.isLesson) SuccessGreen else AccentOrange,
                    trackColor = (if (timeUntilBell.isLesson) SuccessGreen else AccentOrange).copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
fun AnimatedTimeDigit(value: Long, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { -it } + fadeIn() togetherWith
                            slideOutVertically { it } + fadeOut()
                } else {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            label = "digit"
        ) { targetValue ->
            Text(
                text = String.format("%02d", targetValue),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun BellScheduleCard(bell: BellSchedule, isCurrent: Boolean) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(bell.number * 50L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) +
                expandVertically(animationSpec = tween(300))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrent) PrimaryBlue.copy(alpha = 0.1f) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 4.dp else 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCurrent) PrimaryBlue else PrimaryBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bell.number.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color.White else PrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "${bell.number} урок",
                        fontSize = 14.sp,
                        fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isCurrent) PrimaryBlue else TextPrimary
                    )
                    Text(
                        text = "${bell.startTime} - ${bell.endTime}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                if (isCurrent) {
                    Spacer(modifier = Modifier.weight(1f))

                    val infiniteTransition = rememberInfiniteTransition(label = "blink")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha"
                    )

                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = PrimaryBlue.copy(alpha = alpha),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
