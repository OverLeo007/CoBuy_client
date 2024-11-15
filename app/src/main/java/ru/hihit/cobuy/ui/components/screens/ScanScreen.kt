package ru.hihit.cobuy.ui.components.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.hihit.cobuy.R
import ru.hihit.cobuy.ui.components.composableElems.CameraScanner
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.composableElems.imageScanner
import ru.hihit.cobuy.ui.components.viewmodels.GroupsViewModel


@Composable
fun ScanScreen(
    navHostController: NavHostController,
    vm: GroupsViewModel
) {

    val context = LocalContext.current

    val imagePickLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageScanner(context, it,
                    onScanned = { token ->
                        vm.joinGroup(token, navHostController)
                    },
                    onFailed = { msg ->
                        vm.scanError = msg
                    }
                )
            }
        }

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
                        Text(text = stringResource(R.string.scan_qr_code))
                    }
                },
                navHostController = navHostController,
                isBackArrow = true,
                isSettings = false
            )
            Spacer(modifier = Modifier.height(20.dp))
            CameraScanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                onScanned = {
                    vm.joinGroup(it, navHostController)

                }
            )
            if (vm.scanError != "") {
                Text(
                    text = vm.scanError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
//            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically

            ) {
                IconButton(
                    onClick = {
                        imagePickLauncher.launch("image/*")
                    }
                ) {
                    Icon(painter = painterResource(R.drawable.photo_library_24dp), "Load from photos")
                }
                Text(
                    text = stringResource(R.string.point_camera_qr),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}