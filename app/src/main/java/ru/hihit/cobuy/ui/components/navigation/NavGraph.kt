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
//        composable(Route.Group) {
//            GroupScreen(navHostController = navHostController, vm = vms[Route.Group] as GroupViewModel)
//        }
        // TODO: Сделать метод апдейта vm с подтягиванием данных при изменении id
        composable(Route.Group + "/{groupId}") {
            val vm = vms[Route.Group] as GroupViewModel
            vm.groupId = it.arguments?.getString("groupId")?.toInt() ?: 0
            GroupScreen(navHostController = navHostController, vm = vm)
        }
        composable(Route.List+ "/{listId}") {
            val vm = vms[Route.List] as ListViewModel
            // TODO: Пофиксить способ получения вм, она не должна меняться а создаваться новая
            vm.productList.id = it.arguments?.getString("listId")?.toInt() ?: 0
            ListScreen(navHostController = navHostController, vm = vm)
        }
        composable(Route.Settings) {
            SettingsScreen(navHostController = navHostController, vm = vms[Route.Settings] as SettingsViewModel)
        }
    }
}