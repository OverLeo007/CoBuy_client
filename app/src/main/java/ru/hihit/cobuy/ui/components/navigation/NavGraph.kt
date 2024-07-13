package ru.hihit.cobuy.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.hihit.cobuy.api.GroupRequester
import ru.hihit.cobuy.ui.components.screens.AuthScreen
import ru.hihit.cobuy.ui.components.screens.GroupScreen
import ru.hihit.cobuy.ui.components.screens.GroupsScreen
import ru.hihit.cobuy.ui.components.screens.ListScreen
import ru.hihit.cobuy.ui.components.screens.ScanScreen
import ru.hihit.cobuy.ui.components.screens.SettingsScreen
import ru.hihit.cobuy.ui.components.viewmodels.AuthViewModel
import ru.hihit.cobuy.ui.components.viewmodels.GroupViewModel
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel
import ru.hihit.cobuy.ui.components.viewmodels.ListViewModel
import ru.hihit.cobuy.ui.components.viewmodels.SettingsViewModel


@Composable
fun NavGraph(
    navHostController: NavHostController,
    vms: HashMap<String, ViewModel>,
    startDestination: String
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
    ) {


        composable(Route.Groups) {
            val vm: GroupsViewModel = viewModel(key = Route.Groups)
            GroupsScreen(navHostController = navHostController, vm = vm)
        }

        composable(Route.Group + "/{groupId}") {
            val vm: GroupViewModel = viewModel(key = Route.Group) {
                GroupViewModel(
                    it.arguments?.getString("groupId")?.toInt() ?: 0
                )
            }
            GroupScreen(navHostController = navHostController, vm = vm)
        }
        composable(Route.List + "/{listId}") {
            val vm = vms[Route.List] as ListViewModel
            // TODO: Пофиксить способ получения вм, она не должна меняться а создаваться новая
            vm.productList.id = it.arguments?.getString("listId")?.toInt() ?: 0
            ListScreen(navHostController = navHostController, vm = vm)
        }
        composable(Route.Settings) {
            SettingsScreen(
                navHostController = navHostController,
                vm = vms[Route.Settings] as SettingsViewModel
            )
        }

        composable(Route.Scanner) {
            val vm: GroupsViewModel = viewModel(key = Route.Groups)
            ScanScreen(
                navHostController = navHostController,
                vm = vm
            )
        }

        composable(Route.Authorization) {
            AuthScreen(
                navHostController = navHostController,
                vm = vms[Route.Authorization] as AuthViewModel
            )
        }
    }
}