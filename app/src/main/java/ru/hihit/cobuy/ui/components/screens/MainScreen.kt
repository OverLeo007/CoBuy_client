package ru.hihit.cobuy.ui.components.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.hihit.cobuy.ui.components.navigation.NavGraph
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.GroupViewModel
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel
import ru.hihit.cobuy.ui.components.viewmodels.ListViewModel
import ru.hihit.cobuy.ui.components.viewmodels.SettingsViewModel


@Composable
fun MainScreen(

) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val vms: HashMap<String, ViewModel> = HashMap()
//    TODO: Добавить фабрики
    vms[Route.Groups] = viewModel(key = Route.Groups) { GroupsViewModel() }
    vms[Route.Group] = viewModel(key = Route.Group) { GroupViewModel() }
    vms[Route.List] = viewModel(key = Route.List) { ListViewModel() }
    vms[Route.Settings] = viewModel(key = Route.Settings) { SettingsViewModel() }

    Surface(color = MaterialTheme.colorScheme.background) {
        NavGraph(navHostController = navController, vms = vms)
    }
}