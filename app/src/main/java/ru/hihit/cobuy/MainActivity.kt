package ru.hihit.cobuy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.defaultPreferenceFlow
import ru.hihit.cobuy.api.requesters.AuthRequester
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.screens.MainScreen
import ru.hihit.cobuy.ui.theme.CoBuyTheme
import ru.hihit.cobuy.utils.getFromPreferences
import ru.hihit.cobuy.utils.getUserDataFromPreferences
import ru.hihit.cobuy.utils.removeFromPreferences

@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        setContent {
//            CoBuyTheme {
//                WhileAuthScreen()
//            }
//        }
        setContentImpl {
            WhileAuthScreen()
        }
        super.onCreate(savedInstanceState)
        val context = App.getContext()
        var startDestination: String = Route.Authorization
        var navigateTo: String = Route.Groups
        logSharedPreferences(context)
        if (context.getFromPreferences("auth_token", "") == "") {
            setContentImpl {
                MainScreen(startDestination)
            }
        } else {

            AuthRequester.checkLogin(
                callback = { response ->
                    response?.let {
                        (context.getUserDataFromPreferences() == response.userData).let {
                            val lastRoute = context.getFromPreferences("last_route", Route.Groups)
                            navigateTo =
                                if (lastRoute.contains(Regex("\\{.*?\\}")) || lastRoute.contains(
                                        Route.Dummy
                                    )
                                ) {
                                    context.removeFromPreferences("last_route")
                                    Route.Groups
                                } else lastRoute
                            Log.d("MainActivity", "start_from: $navigateTo")
                            startDestination = Route.Groups
                        }
                    } ?: run {
                        Toast.makeText(
                            context,
                            "Ошибка авторизации, возможно проблема с сервером",
                            Toast.LENGTH_SHORT
                        ).show()
                        startDestination = Route.Authorization
                    }
                    setContentImpl {
                        MainScreen(startDestination, navigateTo)
                    }
                },
                onError = { _, _ ->
                    startDestination = Route.Authorization
                    setContentImpl {
                        MainScreen(startDestination)
                    }
//                    setContent {
//                        CoBuyTheme {
//                            ProvidePreferenceLocals {
//                                MainScreen(startDestination)
//                            }
//                        }
//                    }
                }
            )
        }
    }


}


fun ComponentActivity.setContentImpl(screen: @Composable () -> Unit) {
    setContent {
        val preferenceFlow = defaultPreferenceFlow()
        CoBuyTheme(
            preferenceFlow = preferenceFlow
        ) {
            ProvidePreferenceLocals(
                flow = preferenceFlow
            ) {
                screen()
            }
        }
    }
}


@Composable
fun WhileAuthScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")
            Text(text = "Пытаемся авторизоваться...")

        }
    }
}

fun logSharedPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("CoBuyApp", Context.MODE_PRIVATE)
    Log.d("SharedPreferences", "All entries:")
    val allEntries = sharedPreferences.all
    for ((key, value) in allEntries) {
        Log.d("SharedPreferences", "$key: $value")
    }
}