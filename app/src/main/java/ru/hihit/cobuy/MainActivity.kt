package ru.hihit.cobuy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import ru.hihit.cobuy.ui.components.screens.MainScreen
import ru.hihit.cobuy.ui.theme.CoBuyTheme


@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoBuyTheme {
                MainScreen()
            }
        }
    }
}





