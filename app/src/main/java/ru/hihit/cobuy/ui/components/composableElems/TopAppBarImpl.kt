package ru.hihit.cobuy.ui.components.composableElems

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import ru.hihit.cobuy.R
import ru.hihit.cobuy.ui.components.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarImpl(
    title: @Composable () -> Unit,
    isSettings: Boolean = true,
    isBackArrow: Boolean = true,
    navHostController: NavHostController,
    navigation: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = title,
        navigationIcon = {
            if (navigation != null) {
                navigation()
            } else {
                IconButton(
                    onClick = {
                        navHostController.navigateUp()
                    },
                    enabled = isBackArrow
                ) {
                    Icon(
                        painterResource(id = R.drawable.arrow_back_ios_24px),
                        contentDescription = "Back",
                        tint = if (isBackArrow) MaterialTheme.colorScheme.onSurface else Color.Transparent
                    )
                }
            }
        },
        actions = {
            if (actions != null) {
                actions()
            } else {
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
        }
    )
    HorizontalDivider()
}