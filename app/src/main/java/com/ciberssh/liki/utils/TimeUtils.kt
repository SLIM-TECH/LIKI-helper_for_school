package com.ciberssh.liki.utils

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object TimeUtils {
    private val bishkekZone = ZoneId.of("Asia/Bishkek")

    fun getCurrentTimeInBishkek(): LocalDateTime {
        return LocalDateTime.now(bishkekZone)
    }

    fun getTimeUntilNextBell(bellSchedule: List<com.ciberssh.liki.data.models.BellSchedule>): TimeUntilBell? {
        val now = getCurrentTimeInBishkek()
        val currentTime = now.toLocalTime()
        val dayOfWeek = now.dayOfWeek

        if (dayOfWeek == DayOfWeek.SUNDAY) {
            return null
        }

        for (i in bellSchedule.indices) {
            val bell = bellSchedule[i]
            val startTime = LocalTime.parse(bell.startTime)
            val endTime = LocalTime.parse(bell.endTime)

            // Во время урока - показываем время до конца урока
            if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                val duration = ChronoUnit.MILLIS.between(currentTime, endTime)
                return TimeUntilBell(
                    lessonNumber = bell.number,
                    milliseconds = duration,
                    isBreak = false,
                    isLesson = true
                )
            }

            // На перемене - показываем время до начала следующего урока
            if (currentTime.isAfter(endTime) && i < bellSchedule.size - 1) {
                val nextBell = bellSchedule[i + 1]
                val nextStartTime = LocalTime.parse(nextBell.startTime)

                if (currentTime.isBefore(nextStartTime)) {
                    val duration = ChronoUnit.MILLIS.between(currentTime, nextStartTime)
                    return TimeUntilBell(
                        lessonNumber = nextBell.number,
                        milliseconds = duration,
                        isBreak = true,
                        isLesson = false
                    )
                }
            }

            // До начала первого урока
            if (currentTime.isBefore(startTime) && i == 0) {
                val duration = ChronoUnit.MILLIS.between(currentTime, startTime)
                return TimeUntilBell(
                    lessonNumber = bell.number,
                    milliseconds = duration,
                    isBreak = true,
                    isLesson = false
                )
            }
        }

        return null
    }

    fun getCurrentDayOfWeek(): String {
        val now = getCurrentTimeInBishkek()
        return when (now.dayOfWeek) {
            DayOfWeek.MONDAY -> "Понедельник"
            DayOfWeek.TUESDAY -> "Вторник"
            DayOfWeek.WEDNESDAY -> "Среда"
            DayOfWeek.THURSDAY -> "Четверг"
            DayOfWeek.FRIDAY -> "Пятница"
            DayOfWeek.SATURDAY -> "Суббота"
            DayOfWeek.SUNDAY -> "Воскресенье"
        }
    }
}

data class TimeUntilBell(
    val lessonNumber: Int,
    val milliseconds: Long,
    val isBreak: Boolean,
    val isLesson: Boolean
)
