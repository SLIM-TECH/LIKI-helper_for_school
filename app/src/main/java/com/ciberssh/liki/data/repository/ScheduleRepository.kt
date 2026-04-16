package com.ciberssh.liki.data.repository

import com.ciberssh.liki.data.models.BellSchedule
import com.ciberssh.liki.data.models.Lesson
import com.ciberssh.liki.data.models.Schedule

class ScheduleRepository {

    fun getBellSchedule(): List<BellSchedule> {
        return listOf(
            BellSchedule(1, "13:30", "14:15"),
            BellSchedule(2, "14:20", "15:05"),
            BellSchedule(3, "15:10", "15:55"),
            BellSchedule(4, "16:05", "16:50"),
            BellSchedule(5, "16:55", "17:40"),
            BellSchedule(6, "17:45", "18:30")
        )
    }

    fun getBellScheduleWednesday(): List<BellSchedule> {
        return listOf(
            BellSchedule(1, "13:30", "14:05"),
            BellSchedule(2, "14:10", "14:45"),
            BellSchedule(3, "14:50", "15:25"),
            BellSchedule(4, "15:35", "16:10"),
            BellSchedule(5, "16:15", "16:50"),
            BellSchedule(6, "16:55", "17:30"),
            BellSchedule(7, "17:35", "18:00") // Классный час
        )
    }

    fun getSchedule(): List<Schedule> {
        return listOf(
            Schedule("Понедельник", listOf(
                Lesson(1, "Русский язык", "13:30", "14:15"),
                Lesson(2, "Кыргыз адабият", "14:20", "15:05"),
                Lesson(3, "Английский язык", "15:10", "15:55"),
                Lesson(4, "Биология", "16:05", "16:50"),
                Lesson(5, "Алгебра", "16:55", "17:40"),
                Lesson(6, "История", "17:45", "18:30")
            )),
            Schedule("Вторник", listOf(
                Lesson(1, "Кыргызский язык", "13:30", "14:15"),
                Lesson(2, "Физика", "14:20", "15:05"),
                Lesson(3, "Русская литература", "15:10", "15:55"),
                Lesson(4, "Физкультура", "16:05", "16:50"),
                Lesson(5, "Алгебра", "16:55", "17:40"),
                Lesson(6, "География", "17:45", "18:30")
            )),
            Schedule("Среда", listOf(
                Lesson(1, "Геометрия", "13:30", "14:05"),
                Lesson(2, "Человек и Общество", "14:10", "14:45"),
                Lesson(3, "Английский язык", "14:50", "15:25"),
                Lesson(4, "Религия", "15:35", "16:10"),
                Lesson(5, "Химия", "16:15", "16:50"),
                Lesson(6, "Русская литература", "16:55", "17:30"),
                Lesson(7, "Классный час", "17:35", "18:00")
            )),
            Schedule("Четверг", listOf(
                Lesson(1, "Технология", "13:30", "14:15"),
                Lesson(2, "Физика", "14:20", "15:05"),
                Lesson(3, "Русский язык", "15:10", "15:55"),
                Lesson(4, "Геометрия", "16:05", "16:50"),
                Lesson(5, "Биология", "16:55", "17:40"),
                Lesson(6, "Кыргызский язык", "17:45", "18:30")
            )),
            Schedule("Пятница", listOf(
                Lesson(1, "История", "13:30", "14:15"),
                Lesson(2, "Физкультура", "14:20", "15:05"),
                Lesson(3, "Химия", "15:10", "15:55"),
                Lesson(4, "Информатика", "16:05", "16:50"),
                Lesson(5, "География", "16:55", "17:40")
            ))
        )
    }
}
