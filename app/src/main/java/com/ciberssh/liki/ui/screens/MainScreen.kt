package com.ciberssh.liki.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ciberssh.liki.ui.navigation.Screen
import com.ciberssh.liki.ui.theme.*
import com.ciberssh.liki.ui.viewmodel.ScheduleViewModel
import com.ciberssh.liki.utils.PreferencesManager
import com.ciberssh.liki.utils.UpdateChecker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isAdmin by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<com.ciberssh.liki.utils.UpdateInfo?>(null) }

    LaunchedEffect(Unit) {
        PreferencesManager.isAdmin(context).collect { isAdmin = it }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            val update = UpdateChecker.checkForUpdates("1.0.0")
            if (update != null) {
                updateInfo = update
                showUpdateDialog = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    label = { Text("Расписание") },
                    selected = currentRoute == Screen.Schedule.route,
                    onClick = {
                        navController.navigate(Screen.Schedule.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                    )
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                    label = { Text("ДЗ") },
                    selected = currentRoute == Screen.Homework.route,
                    onClick = {
                        navController.navigate(Screen.Homework.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                    )
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Timer, contentDescription = null) },
                    label = { Text("Звонок") },
                    selected = currentRoute == Screen.BellTimer.route,
                    onClick = {
                        navController.navigate(Screen.BellTimer.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                    )
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    label = { Text("Книги") },
                    selected = currentRoute == Screen.Books.route,
                    onClick = {
                        navController.navigate(Screen.Books.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                    )
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Еще") },
                    selected = currentRoute == Screen.Settings.route,
                    onClick = {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                    )
                )
            }
        },
        floatingActionButton = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != Screen.AI.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AI.route) },
                    containerColor = AccentOrange
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI Помощник",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Schedule.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Schedule.route) {
                ScheduleScreen()
            }
            composable(Screen.Homework.route) {
                HomeworkScreen(isAdmin = isAdmin)
            }
            composable(Screen.BellTimer.route) {
                BellTimerScreen()
            }
            composable(Screen.Books.route) {
                BooksScreen()
            }
            composable(Screen.AI.route) {
                AIScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(onAdminStatusChanged = { isAdmin = it })
            }
        }
    }

    if (showUpdateDialog && updateInfo != null) {
        UpdateDialog(
            updateInfo = updateInfo!!,
            onDismiss = { /* Не позволяем закрыть */ },
            onUpdate = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo!!.downloadUrl))
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun UpdateDialog(
    updateInfo: com.ciberssh.liki.utils.UpdateInfo,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SystemUpdate,
                    contentDescription = null,
                    tint = AccentOrange,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Доступно обновление!")
            }
        },
        text = {
            Column {
                Text(
                    text = "Версия ${updateInfo.version}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Для продолжения работы необходимо обновить приложение.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onUpdate,
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Скачать обновление")
            }
        },
        dismissButton = null
    )
}
