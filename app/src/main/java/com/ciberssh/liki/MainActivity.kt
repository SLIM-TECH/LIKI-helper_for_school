package com.ciberssh.liki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ciberssh.liki.ui.screens.MainScreen
import com.ciberssh.liki.ui.theme.LikiTheme
import com.ciberssh.liki.utils.PDFUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация PDFBox для работы с PDF
        PDFUtils.init(this)

        enableEdgeToEdge()
        setContent {
            LikiTheme {
                MainScreen()
            }
        }
    }
}