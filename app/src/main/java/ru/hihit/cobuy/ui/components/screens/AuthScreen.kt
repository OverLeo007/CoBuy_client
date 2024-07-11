package ru.hihit.cobuy.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.AuthViewModel

@Composable
fun AuthScreen(
    navHostController: NavHostController,
    vm: AuthViewModel
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp).fillMaxSize()
        ) {
            if (isRegistering) {
                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = { Text("Логин", color = MaterialTheme.colorScheme.onSurface) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Почта", color = MaterialTheme.colorScheme.onSurface) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль", color = MaterialTheme.colorScheme.onSurface) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (isRegistering) {
                    val success = vm.register(login, email, password)
                    if (success == "OK") {
                        isRegistering = false
                    } else {
                        Toast.makeText(context, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val result = vm.login(email, password)
                    if (result == "OK") {
                        navHostController.navigate(Route.Groups)
                    } else {
                        Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(if (isRegistering) "Зарегистрироваться" else "Войти")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(if (isRegistering) "Уже зарегистрированы? Войти" else "Зарегистрироваться")
            }
        }
    }
}