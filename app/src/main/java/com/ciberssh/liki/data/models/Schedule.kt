package com.ciberssh.liki.data.models

data class Schedule(
    val dayOfWeek: String,
    val lessons: List<Lesson>
)

data class Lesson(
    val number: Int,
    val subject: String,
    val startTime: String,
    val endTime: String,
    val room: String = "",
    val teacher: String = ""
)

data class BellSchedule(
    val number: Int,
    val startTime: String,
    val endTime: String
)
