package com.ciberssh.liki.ui.screens

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.ciberssh.liki.data.models.Book
import com.ciberssh.liki.ui.theme.*
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFViewerScreen(
    book: Book,
    onBack: () -> Unit,
    onExtractText: (Int) -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    var totalPages by remember { mutableStateOf(0) }
    var showPageDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PrimaryBlue,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = book.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = "Страница ${currentPage + 1} из $totalPages",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                IconButton(onClick = { showPageDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Перейти к странице",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { onExtractText(currentPage + 1) }) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI помощь",
                        tint = Color.White
                    )
                }
            }
        }

        // PDF Viewer
        AndroidView(
            factory = { context ->
                PDFView(context, null).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { pdfView ->
                val file = File(book.filePath)
                if (file.exists()) {
                    pdfView.fromFile(file)
                        .defaultPage(0)
                        .onPageChange { page, pageCount ->
                            currentPage = page
                            totalPages = pageCount
                        }
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .scrollHandle(DefaultScrollHandle(pdfView.context))
                        .spacing(10)
                        .load()
                }
            }
        )
    }

    if (showPageDialog) {
        GoToPageDialog(
            currentPage = currentPage + 1,
            totalPages = totalPages,
            onDismiss = { showPageDialog = false },
            onGoToPage = { page ->
                // TODO: Implement page navigation
                showPageDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoToPageDialog(
    currentPage: Int,
    totalPages: Int,
    onDismiss: () -> Unit,
    onGoToPage: (Int) -> Unit
) {
    var pageInput by remember { mutableStateOf(currentPage.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Перейти к странице") },
        text = {
            Column {
                Text("Введите номер страницы (1-$totalPages)")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pageInput,
                    onValueChange = { pageInput = it },
                    label = { Text("Страница") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val page = pageInput.toIntOrNull()
                    if (page != null && page in 1..totalPages) {
                        onGoToPage(page)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Перейти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
