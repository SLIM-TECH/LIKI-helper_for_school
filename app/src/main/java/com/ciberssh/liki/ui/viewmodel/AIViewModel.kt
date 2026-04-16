package com.ciberssh.liki.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciberssh.liki.data.remote.GroqAIClient
import com.ciberssh.liki.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class AIViewModel(application: Application) : AndroidViewModel(application) {
    private val aiClient = GroqAIClient()
    private val bookRepository = BookRepository(application)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _availableBooks = MutableStateFlow<List<String>>(emptyList())
    val availableBooks: StateFlow<List<String>> = _availableBooks

    init {
        loadAvailableBooks()
    }

    private fun loadAvailableBooks() {
        viewModelScope.launch {
            val books = bookRepository.getBooks(getApplication<Application>().filesDir.parent ?: "")
            _availableBooks.value = books.map { "${it.title} (${it.fileName})" }
        }
    }

    fun sendMessage(text: String, image: Bitmap? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            val userMessage = ChatMessage(text, isUser = true, image = image)
            _messages.value = _messages.value + userMessage

            // Проверяем, упоминает ли пользователь книгу и страницу
            val enhancedText = enhanceMessageWithBookContext(text)

            val imageBase64 = image?.let { bitmapToBase64(it) }
            val response = aiClient.sendMessage(enhancedText, imageBase64)

            val aiMessage = ChatMessage(response, isUser = false)
            _messages.value = _messages.value + aiMessage

            _isLoading.value = false
        }
    }

    private suspend fun enhanceMessageWithBookContext(text: String): String {
        // Ищем упоминания книг и страниц
        val bookPattern = Regex("(алгебр|геометр|русск|чио|человек)", RegexOption.IGNORE_CASE)
        val pagePattern = Regex("страниц[аеу]?\\s+(\\d+)", RegexOption.IGNORE_CASE)
        val exercisePattern = Regex("упражнени[еяй]\\s+(\\d+)", RegexOption.IGNORE_CASE)

        val bookMatch = bookPattern.find(text)
        val pageMatch = pagePattern.find(text)

        if (bookMatch != null && pageMatch != null) {
            val bookName = when {
                bookMatch.value.contains("алгебр", ignoreCase = true) -> "Алгебра 8 класс.pdf"
                bookMatch.value.contains("геометр", ignoreCase = true) -> "Геометрия с 7 по 9 класс.pdf"
                bookMatch.value.contains("русск", ignoreCase = true) -> "Руский язык 8 класс.pdf"
                bookMatch.value.contains("чио", ignoreCase = true) ||
                bookMatch.value.contains("человек", ignoreCase = true) -> "ЧиО(Человек и Общество) 8 класс.pdf"
                else -> null
            }

            val pageNumber = pageMatch.groupValues[1].toIntOrNull()

            if (bookName != null && pageNumber != null) {
                val rootPath = getApplication<Application>().filesDir.parent ?: ""
                val filePath = "$rootPath/$bookName"

                // Извлекаем текст со страницы
                val pageText = bookRepository.extractTextFromBook(filePath, pageNumber)

                if (pageText.isNotEmpty()) {
                    return """
                        Пользователь спрашивает: $text

                        Контекст из книги "$bookName", страница $pageNumber:

                        $pageText

                        Пожалуйста, ответь на вопрос пользователя, используя этот контекст из учебника.
                    """.trimIndent()
                }
            }
        }

        return text
    }

    fun sendMessageWithBookPage(bookPath: String, pageNumber: Int, question: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val userMessage = ChatMessage(
                "Вопрос по странице $pageNumber: $question",
                isUser = true
            )
            _messages.value = _messages.value + userMessage

            // Извлекаем текст со страницы
            val pageText = bookRepository.extractTextFromBook(bookPath, pageNumber)

            val enhancedQuestion = """
                Контекст из учебника (страница $pageNumber):

                $pageText

                Вопрос: $question

                Пожалуйста, ответь на вопрос, используя информацию из учебника.
            """.trimIndent()

            val response = aiClient.sendMessage(enhancedQuestion, null)

            val aiMessage = ChatMessage(response, isUser = false)
            _messages.value = _messages.value + aiMessage

            _isLoading.value = false
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val image: Bitmap? = null,
    val timestamp: Long = System.currentTimeMillis()
)
