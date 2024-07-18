package ru.hihit.cobuy.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.hihit.cobuy.R
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
    var passwordVisible by remember { mutableStateOf(false) }
    var login by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    LaunchedEffect(vm.apiResponse) {
        if (vm.apiResponse == "OK") {
            if (isRegistering) {
                isRegistering = false
                Toast.makeText(
                    context,
                    context.getString(R.string.account_create_success),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                navHostController.navigate(Route.Groups)
                Toast.makeText(
                    context,
                    context.getString(R.string.login_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (vm.apiResponse == "FAIL") {
            Toast.makeText(
                context,
                context.getString(R.string.authorization_error),
                Toast.LENGTH_SHORT
            ).show()
        }
        vm.apiResponse = ""
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (isRegistering) {
                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = {
                        Text(
                            stringResource(R.string.login),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (vm.loginError != "") {
                    Text(vm.loginError, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                label = {
                    Text(
                        stringResource(R.string.email), color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (vm.emailError != "") {
                Text(vm.emailError, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            OutlinedTextField(
                value = password,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password = it },
                label = {
                    Text(
                        stringResource(R.string.password),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.visibility_off_24px)
                    else painterResource(id = R.drawable.visibility_24px)

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, description)
                    }
                }

            )
            Spacer(modifier = Modifier.height(16.dp))
            if (vm.passwordError != "") {
                Text(
                    vm.passwordError,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(onClick = {
                if (isRegistering) {
                    vm.register(login, email, password)

                } else {
                    vm.login(email, password)
                }
            }) {
                if (vm.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Text(
                        if (isRegistering) stringResource(R.string.register_word)
                        else stringResource(R.string.login_word)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                isRegistering = !isRegistering
                vm.resetErrors()
                email = ""
                password = ""
                login = ""
            }) {
                Text(
                    if (isRegistering) stringResource(R.string.already_registered_question)
                    else stringResource(R.string.register_word)
                )
            }
        }
    }
}