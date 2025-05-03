package com.puffless.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text("🗒️ Меню", style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(16.dp))

                DrawerItem("📈 Статистика") {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("stats") { popUpTo(0) }
                    }
                }

                DrawerItem("📆 Планировщик") {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("planner") { popUpTo(0) }
                    }
                }

                DrawerItem("⚙️ Настройки") {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("settings") { popUpTo(0) }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Puffless") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    }
                )
            }
        ) {
            Box(modifier = Modifier.padding(it)) {
                content()
            }
        }
    }
}

@Composable
fun DrawerItem(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick)
    )
}
