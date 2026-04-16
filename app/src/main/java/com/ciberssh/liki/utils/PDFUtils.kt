package com.ciberssh.liki.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object PDFUtils {

    fun init(context: Context) {
        PDFBoxResourceLoader.init(context)
    }

    suspend fun extractTextFromPage(file: File, pageNumber: Int): String = withContext(Dispatchers.IO) {
        try {
            val document = PDDocument.load(file)
            val stripper = PDFTextStripper()
            stripper.startPage = pageNumber
            stripper.endPage = pageNumber
            val text = stripper.getText(document)
            document.close()
            text
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    suspend fun extractTextFromPages(file: File, startPage: Int, endPage: Int): String = withContext(Dispatchers.IO) {
        try {
            val document = PDDocument.load(file)
            val stripper = PDFTextStripper()
            stripper.startPage = startPage
            stripper.endPage = endPage
            val text = stripper.getText(document)
            document.close()
            text
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    suspend fun getCoverImage(file: File): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)

            if (pdfRenderer.pageCount > 0) {
                val page = pdfRenderer.openPage(0)
                val bitmap = Bitmap.createBitmap(
                    page.width,
                    page.height,
                    Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                pdfRenderer.close()
                fileDescriptor.close()
                bitmap
            } else {
                pdfRenderer.close()
                fileDescriptor.close()
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getPageCount(file: File): Int = withContext(Dispatchers.IO) {
        try {
            val document = PDDocument.load(file)
            val count = document.numberOfPages
            document.close()
            count
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    suspend fun searchInPDF(file: File, query: String): List<PageSearchResult> = withContext(Dispatchers.IO) {
        try {
            val results = mutableListOf<PageSearchResult>()
            val document = PDDocument.load(file)
            val stripper = PDFTextStripper()

            for (pageNum in 1..document.numberOfPages) {
                stripper.startPage = pageNum
                stripper.endPage = pageNum
                val pageText = stripper.getText(document)

                if (pageText.contains(query, ignoreCase = true)) {
                    val lines = pageText.lines()
                    lines.forEachIndexed { index, line ->
                        if (line.contains(query, ignoreCase = true)) {
                            results.add(
                                PageSearchResult(
                                    pageNumber = pageNum,
                                    lineNumber = index + 1,
                                    text = line.trim()
                                )
                            )
                        }
                    }
                }
            }

            document.close()
            results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

data class PageSearchResult(
    val pageNumber: Int,
    val lineNumber: Int,
    val text: String
)
