package com.ciberssh.liki.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciberssh.liki.data.models.Homework
import com.ciberssh.liki.data.repository.HomeworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeworkViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HomeworkRepository()

    private val _homework = MutableStateFlow<List<Homework>>(emptyList())
    val homework: StateFlow<List<Homework>> = _homework

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadHomework()
    }

    fun loadHomework() {
        viewModelScope.launch {
            _isLoading.value = true
            _homework.value = repository.getHomework()
            _isLoading.value = false
        }
    }

    fun addHomework(homework: Homework) {
        viewModelScope.launch {
            repository.addHomework(homework)
            loadHomework()
        }
    }

    fun updateHomework(homework: Homework) {
        viewModelScope.launch {
            repository.updateHomework(homework)
            loadHomework()
        }
    }

    fun deleteHomework(id: String) {
        viewModelScope.launch {
            repository.deleteHomework(id)
            loadHomework()
        }
    }

    fun toggleComplete(homework: Homework) {
        updateHomework(homework.copy(isCompleted = !homework.isCompleted))
    }
}
