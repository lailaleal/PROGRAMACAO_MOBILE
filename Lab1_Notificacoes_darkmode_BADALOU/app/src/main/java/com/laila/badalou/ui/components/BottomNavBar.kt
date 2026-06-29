package com.laila.badalou.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
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
    object Futuras : BottomNavItem("futuras", "Futuras")
    object Pendentes : BottomNavItem("pendentes", "Pendentes")
    object Concluidas : BottomNavItem("concluidas", "Concluídas")
}

@Composable
fun BottomNavBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
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
            selected = selectedRoute == BottomNavItem.Futuras.route,
            onClick = { onItemSelected(BottomNavItem.Futuras.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Futuras"
                )
            },
            label = { Text("Futuras") },
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
            selected = selectedRoute == BottomNavItem.Concluidas.route,
            onClick = { onItemSelected(BottomNavItem.Concluidas.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Concluídas"
                )
            },
            label = { Text("Feitas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmbarPrimary,
                selectedTextColor = AmbarPrimary,
                indicatorColor = AmbarPrimary.copy(alpha = 0.1f)
            )
        )
    }
}