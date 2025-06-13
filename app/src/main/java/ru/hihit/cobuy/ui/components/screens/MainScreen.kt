package ru.hihit.cobuy.ui.components.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ru.hihit.cobuy.currency.CurrencyConverter
import ru.hihit.cobuy.currency.CurrencyPreferenceManager
import ru.hihit.cobuy.currency.CurrencyViewModel
import ru.hihit.cobuy.currency.data.CurrencyRepository
import ru.hihit.cobuy.currency.data.implementation.DefaultCbrXmlService
import ru.hihit.cobuy.ui.components.navigation.NavGraph
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.AuthViewModel
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel
import ru.hihit.cobuy.ui.components.viewmodels.SettingsViewModel


@Composable
fun MainScreen(
    startDestination: String = Route.Authorization,
    navigateTo: String = Route.Groups
) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val cbrService = remember { DefaultCbrXmlService() }
    val repository = remember { CurrencyRepository(cbrService) }
    val prefManager = remember { CurrencyPreferenceManager(context) }

    val vms: HashMap<String, ViewModel> = hashMapOf(
        Route.Groups to viewModel(key = Route.Groups) { GroupsViewModel() },
        Route.Settings to viewModel(key = Route.Settings) { SettingsViewModel() },
        Route.Authorization to viewModel(key = Route.Authorization) { AuthViewModel() },
        Route.Currency to viewModel(key = Route.Currency) {
            CurrencyViewModel(repository, prefManager)
        }
    )

    Surface(color = MaterialTheme.colorScheme.background) {
        NavGraph(navHostController = navController, vms = vms, startDestination = startDestination, navigateTo = navigateTo)
    }
}