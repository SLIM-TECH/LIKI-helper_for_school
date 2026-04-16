package com.ciberssh.liki.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciberssh.liki.data.models.BellSchedule
import com.ciberssh.liki.data.models.Schedule
import com.ciberssh.liki.data.repository.ScheduleRepository
import com.ciberssh.liki.utils.TimeUntilBell
import com.ciberssh.liki.utils.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ScheduleRepository()

    private val _schedule = MutableStateFlow<List<Schedule>>(emptyList())
    val schedule: StateFlow<List<Schedule>> = _schedule

    private val _bellSchedule = MutableStateFlow<List<BellSchedule>>(emptyList())
    val bellSchedule: StateFlow<List<BellSchedule>> = _bellSchedule

    private val _timeUntilBell = MutableStateFlow<TimeUntilBell?>(null)
    val timeUntilBell: StateFlow<TimeUntilBell?> = _timeUntilBell

    private val _currentDay = MutableStateFlow("")
    val currentDay: StateFlow<String> = _currentDay

    init {
        loadSchedule()
        startBellTimer()
    }

    private fun loadSchedule() {
        _schedule.value = repository.getSchedule()
        updateBellSchedule()
        _currentDay.value = TimeUtils.getCurrentDayOfWeek()
    }

    private fun updateBellSchedule() {
        val currentDay = TimeUtils.getCurrentDayOfWeek()
        _bellSchedule.value = if (currentDay == "Среда") {
            repository.getBellScheduleWednesday()
        } else {
            repository.getBellSchedule()
        }
    }

    private fun startBellTimer() {
        viewModelScope.launch {
            while (true) {
                val newDay = TimeUtils.getCurrentDayOfWeek()
                if (newDay != _currentDay.value) {
                    _currentDay.value = newDay
                    updateBellSchedule()
                }
                _timeUntilBell.value = TimeUtils.getTimeUntilNextBell(_bellSchedule.value)
                delay(100)
            }
        }
    }
}
