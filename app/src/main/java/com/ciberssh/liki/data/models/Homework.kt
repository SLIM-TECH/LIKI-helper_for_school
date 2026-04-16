package com.ciberssh.liki.data.models

data class Homework(
    val id: String,
    val subject: String,
    val description: String,
    val dueDate: String,
    val dayOfWeek: String,
    val isCompleted: Boolean = false,
    val createdAt: String
)
