package com.laila.badalou.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.laila.badalou.ui.theme.AmbarPrimary

sealed class BottomNavItem(
    val route: String,
    val title: String
) {
    object Hoje : BottomNavItem("hoje", "Hoje")
    object Pendentes : BottomNavItem("pendentes", "Pendentes")
    object NovaTarefa : BottomNavItem("nova_tarefa", "Adicionar")
}

@Composable
fun BottomNavBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedRoute == BottomNavItem.Hoje.route,
            onClick = { onItemSelected(BottomNavItem.Hoje.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Hoje"
                )
            },
            label = { Text("Hoje") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmbarPrimary,
                selectedTextColor = AmbarPrimary,
                indicatorColor = AmbarPrimary.copy(alpha = 0.1f)
            )
        )

        NavigationBarItem(
            selected = selectedRoute == BottomNavItem.Pendentes.route,
            onClick = { onItemSelected(BottomNavItem.Pendentes.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Pendentes"
                )
            },
            label = { Text("Pendentes") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmbarPrimary,
                selectedTextColor = AmbarPrimary,
                indicatorColor = AmbarPrimary.copy(alpha = 0.1f)
            )
        )

        NavigationBarItem(
            selected = selectedRoute == BottomNavItem.NovaTarefa.route,
            onClick = { onItemSelected(BottomNavItem.NovaTarefa.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nova Tarefa"
                )
            },
            label = { Text("Adicionar") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmbarPrimary,
                selectedTextColor = AmbarPrimary,
                indicatorColor = AmbarPrimary.copy(alpha = 0.1f)
            )
        )
    }
}