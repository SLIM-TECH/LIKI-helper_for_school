package com.ciberssh.liki.data.repository

import android.content.Context
import com.ciberssh.liki.data.models.Book
import com.ciberssh.liki.utils.PDFUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class BookRepository(private val context: Context) {

    suspend fun getBooks(rootPath: String): List<Book> = withContext(Dispatchers.IO) {
        val books = mutableListOf<Book>()
        val rootDir = File(rootPath)

        if (rootDir.exists() && rootDir.isDirectory) {
            rootDir.listFiles()?.forEach { file ->
                if (file.extension.lowercase() == "pdf") {
                    val subject = when {
                        file.name.contains("Алгебра", ignoreCase = true) -> "Алгебра"
                        file.name.contains("Геометрия", ignoreCase = true) -> "Геометрия"
                        file.name.contains("Русский", ignoreCase = true) -> "Русский язык"
                        file.name.contains("ЧиО", ignoreCase = true) ||
                        file.name.contains("Человек", ignoreCase = true) -> "Человек и Общество"
                        else -> "Другое"
                    }

                    val pageCount = PDFUtils.getPageCount(file)
                    val coverImage = PDFUtils.getCoverImage(file)

                    books.add(
                        Book(
                            title = file.nameWithoutExtension,
                            fileName = file.name,
                            filePath = file.absolutePath,
                            subject = subject,
                            pageCount = pageCount,
                            coverImage = coverImage,
                            isDownloaded = true
                        )
                    )
                }
            }
        }

        books.sortedBy { it.title }
    }

    suspend fun extractTextFromBook(filePath: String, pageNumber: Int): String {
        val file = File(filePath)
        return if (file.exists()) {
            PDFUtils.extractTextFromPage(file, pageNumber)
        } else {
            ""
        }
    }

    suspend fun extractTextFromPages(filePath: String, startPage: Int, endPage: Int): String {
        val file = File(filePath)
        return if (file.exists()) {
            PDFUtils.extractTextFromPages(file, startPage, endPage)
        } else {
            ""
        }
    }

    suspend fun searchInBook(filePath: String, query: String): List<com.ciberssh.liki.utils.PageSearchResult> {
        val file = File(filePath)
        return if (file.exists()) {
            PDFUtils.searchInPDF(file, query)
        } else {
            emptyList()
        }
    }
}
