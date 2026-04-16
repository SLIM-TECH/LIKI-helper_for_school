package com.ciberssh.liki.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.ciberssh.liki.BuildConfig
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ciberssh.liki.ui.theme.*
import com.ciberssh.liki.utils.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAdminStatusChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isAdmin by PreferencesManager.isAdmin(context).collectAsState(initial = false)
    var showLoginDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PrimaryBlue,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Настройки",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (isAdmin) {
                    Text(
                        text = "Режим администратора",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Admin section
            SettingsCard {
                Column {
                    Text(
                        text = "Администрирование",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (isAdmin) {
                        SettingsItem(
                            icon = Icons.Default.Logout,
                            title = "Выйти из аккаунта",
                            subtitle = "Войдено как Умар",
                            onClick = { showLogoutDialog = true }
                        )
                    } else {
                        SettingsItem(
                            icon = Icons.Default.Login,
                            title = "Войти как администратор",
                            subtitle = "Для публикации ДЗ",
                            onClick = { showLoginDialog = true }
                        )
                    }
                }
            }

            // App info
            SettingsCard {
                Column {
                    Text(
                        text = "О приложении",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Версия",
                        subtitle = "1.0.0"
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsItem(
                        icon = Icons.Default.Code,
                        title = "GitHub",
                        subtitle = "Исходный код",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            // Features
            SettingsCard {
                Column {
                    Text(
                        text = "Возможности",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    SettingsItem(
                        icon = Icons.Default.Schedule,
                        title = "Расписание уроков",
                        subtitle = "Просмотр расписания на неделю"
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsItem(
                        icon = Icons.Default.Assignment,
                        title = "Домашние задания",
                        subtitle = "Отслеживание ДЗ"
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsItem(
                        icon = Icons.Default.SmartToy,
                        title = "AI Помощник",
                        subtitle = "Помощь с домашкой"
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsItem(
                        icon = Icons.Default.MenuBook,
                        title = "Электронные книги",
                        subtitle = "Учебники в PDF"
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingsItem(
                        icon = Icons.Default.Timer,
                        title = "Таймер звонков",
                        subtitle = "Время до звонка"
                    )
                }
            }
        }
    }

    if (showLoginDialog) {
        AdminLoginDialog(
            onDismiss = { showLoginDialog = false },
            onLogin = { username, password ->
                if (username == BuildConfig.ADMIN_LOGIN && password == BuildConfig.ADMIN_PASSWORD) {
                    scope.launch {
                        PreferencesManager.setAdmin(context, true)
                        onAdminStatusChanged(true)
                        showLoginDialog = false
                    }
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Выйти?") },
            text = { Text("Вы уверены, что хотите выйти из аккаунта администратора?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            PreferencesManager.setAdmin(context, false)
                            onAdminStatusChanged(false)
                            showLogoutDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Выйти")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginDialog(
    onDismiss: () -> Unit,
    onLogin: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Вход администратора") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        showError = false
                    },
                    label = { Text("Логин") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        showError = false
                    },
                    label = { Text("Пароль") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError
                )

                if (showError) {
                    Text(
                        text = "Неверный логин или пароль",
                        color = ErrorRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username == BuildConfig.ADMIN_LOGIN && password == BuildConfig.ADMIN_PASSWORD) {
                        onLogin(username, password)
                    } else {
                        showError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Войти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
