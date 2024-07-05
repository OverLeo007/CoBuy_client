package ru.hihit.cobuy.ui.components.composableElems

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import ru.hihit.cobuy.ui.components.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarImpl(
    title: @Composable () -> Unit,
    isSettings: Boolean = true,
    isBackArrow: Boolean = true,
    navHostController: NavHostController
) {
    TopAppBar(
        title = title,
        navigationIcon = {
            IconButton(
                onClick = {
                    navHostController.navigateUp()
                },
                enabled = isBackArrow
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (isBackArrow) MaterialTheme.colorScheme.onSurface else Color.Transparent
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    navHostController.navigate(Route.Settings)
                },
                enabled = isSettings
            ) {
                if (isSettings) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                    )
                } else {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = Color.Transparent
                    )
                }
            }
        }
    )
    HorizontalDivider()
}