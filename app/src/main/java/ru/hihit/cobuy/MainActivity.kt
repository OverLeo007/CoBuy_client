package ru.hihit.cobuy

import android.os.Bundle
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
import ru.hihit.cobuy.api.AuthRequester
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.screens.MainScreen
import ru.hihit.cobuy.ui.theme.CoBuyTheme
import ru.hihit.cobuy.utils.getFromPreferences


@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContent {
            CoBuyTheme {
                WhileAuthScreen()
            }
        }
        super.onCreate(savedInstanceState)
        val context = App.getContext()
        var startDestination: String = Route.Authorization
        if (context.getFromPreferences("auth_token", "") == "") {
            setContent {
                CoBuyTheme {
                    MainScreen(startDestination)
                }
            }
        } else {

            AuthRequester.checkLogin(
                callback = { response ->
                    if (response != null) {
                        if (context.getFromPreferences(
                                "user_id",
                                -1
                            ) == response.userData.id
                            && context.getFromPreferences(
                                "user_name",
                                ""
                            ) == response.userData.name
                            && context.getFromPreferences(
                                "user_email",
                                ""
                            ) == response.userData.email
                        ) startDestination = Route.Groups
                    }
                    setContent {
                        CoBuyTheme {
                            MainScreen(startDestination)
                        }
                    }
                },
                onError = { _, _ ->
                    startDestination = Route.Authorization
                    setContent {
                        CoBuyTheme {
                            MainScreen(startDestination)
                        }
                    }
                }
            )
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