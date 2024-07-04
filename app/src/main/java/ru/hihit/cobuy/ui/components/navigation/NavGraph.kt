package ru.hihit.cobuy.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.hihit.cobuy.ui.components.screens.GroupScreen
import ru.hihit.cobuy.ui.components.screens.GroupsScreen
import ru.hihit.cobuy.ui.components.screens.ListScreen
import ru.hihit.cobuy.ui.components.screens.SettingsScreen
import ru.hihit.cobuy.ui.components.viewmodels.GroupViewModel
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel
import ru.hihit.cobuy.ui.components.viewmodels.ListViewModel
import ru.hihit.cobuy.ui.components.viewmodels.SettingsViewModel


@Composable
fun NavGraph(
    navHostController: NavHostController,
    vms: HashMap<String, ViewModel>

) {
    NavHost(
        navController = navHostController,
        startDestination = Route.Groups,
    ) {
        composable(Route.Groups) {
            GroupsScreen(navHostController = navHostController, vm = vms[Route.Groups] as GroupsViewModel)
        }
        composable(Route.Group) {
            GroupScreen(navHostController = navHostController, vm = vms[Route.Group] as GroupViewModel)
        }
        composable(Route.List) {
            ListScreen(navHostController = navHostController, vm = vms[Route.List] as ListViewModel)
        }
        composable(Route.Settings) {
            SettingsScreen(navHostController = navHostController, vm = vms[Route.Settings] as SettingsViewModel)
        }
    }
}