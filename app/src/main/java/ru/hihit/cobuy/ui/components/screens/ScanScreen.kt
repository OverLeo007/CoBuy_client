package ru.hihit.cobuy.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.hihit.cobuy.ui.components.composableElems.CameraScanner
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel

@Composable
fun ScanScreen(
    navHostController: NavHostController,
    vm: GroupsViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBarImpl(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Сканировать QR-код")
                    }
                },
                navHostController = navHostController,
                isBackArrow = true,
                isSettings = false
            )
            Spacer(modifier = Modifier.height(50.dp))
            CameraScanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                onScanned = {
                    if (vm.joinGroup(it))
                        navHostController.navigateUp()
                    else
                        Toast.makeText(
                            navHostController.context,
                            "Неверный QR-код",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            )
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Наведите камеру на QR-код, чтобы присоединиться к группе",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}