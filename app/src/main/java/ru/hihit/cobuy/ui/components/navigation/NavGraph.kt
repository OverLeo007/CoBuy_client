package ru.hihit.cobuy.ui.components.navigation

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.hihit.cobuy.App
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
import ru.hihit.cobuy.utils.extractAllByRegex
import ru.hihit.cobuy.utils.saveToPreferences


@Composable
fun NavGraph(
    navHostController: NavHostController,
    vms: HashMap<String, ViewModel>,
    startDestination: String,
    navigateTo: String
) {
    var lastNavigation: String = ""

    navHostController.addOnDestinationChangedListener { _, dest, arguments ->
        var resRoute: String? = null
        dest.route?.let { route ->
            var tempRoute = route
            val argsInDest = route.extractAllByRegex( "\\{([^}]*)\\}")
            if (argsInDest.isNotEmpty()) {
                arguments?.let {
                    argsInDest.forEach { arg ->
                        arguments.getString(arg)?.let {
                            tempRoute = route.replace("{$arg}", it)
                        }
                    }
                }
            }
            resRoute = tempRoute
        }
        if (resRoute == lastNavigation) {
            return@addOnDestinationChangedListener
        }
        Log.d("NavGraph", "destination changed: $resRoute")
        val context = App.getContext()
        resRoute?.let {
            if (resRoute != Route.Dummy) {
                context.saveToPreferences("last_route", it)
            } else {
                context.saveToPreferences("last_route", Route.Groups)

            }
        }
//        val route = dest.route ?: Route.Groups
//        if (route == Route.Dummy) {
//            context.saveToPreferences("last_route", Route.Groups)
//        }
//        val fullRoute = if (arguments != null) {
//            var tempRoute = route
//            arguments.keySet().forEach { key ->
//                tempRoute = tempRoute.replace("{$key}", arguments.getString(key) ?: "")
//            }
//            tempRoute
//        } else {
//            route
//        }
//        context.saveToPreferences("last_route", fullRoute)
//        Log.d("NavGraph", "new last_route saved: $fullRoute")
    }

    var isLoaded by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(isLoaded) {
        if (isLoaded && !hasNavigated) {
            Log.d("NavGraph", "NavigateToAfterLoad dummy: $navigateTo")
            navHostController.popBackStack()
            navHostController.navigate(Route.Groups) {
                launchSingleTop = false
                restoreState = true
            }
            if (navigateTo != Route.Groups) {
                navHostController.navigate(route = navigateTo)
            } else {
                navHostController.navigate(route = startDestination)
            }
            hasNavigated = true
        }
    }


    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current
    val screenWidthPx = with(density) { screenWidthDp.toPx().toInt() }
    val duration = 300
    NavHost(
        navController = navHostController,
        startDestination = Route.Dummy,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { screenWidthPx }, animationSpec = tween(duration))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -screenWidthPx }, animationSpec = tween(duration))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -screenWidthPx }, animationSpec = tween(duration))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { screenWidthPx }, animationSpec = tween(duration))
        },
    ) {

        composable(Route.Dummy) {
            Box(
                modifier =  Modifier.onGloballyPositioned {
                    isLoaded = true
                }
            ) {}
        }

        composable(Route.Groups) {
            val vm: GroupsViewModel = viewModel(key = Route.Groups)
            GroupsScreen(navHostController = navHostController, vm = vm)
        }

        composable(Route.Group + "/{groupId}") {
            val vm: GroupViewModel = viewModel(key = Route.Group) {
                GroupViewModel(
                    it.arguments?.getString("groupId")?.toInt() ?: 0,
                    navHostController = navHostController
                )
            }
            GroupScreen(navHostController = navHostController, vm = vm)
        }
        composable(Route.List + "/{listId}") {
            val vm: ListViewModel = viewModel(key = Route.List) {
                ListViewModel(
                    it.arguments?.getString("listId")?.toInt() ?: 0
                )
            }
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