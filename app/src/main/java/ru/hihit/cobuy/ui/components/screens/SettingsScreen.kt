package ru.hihit.cobuy.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.hihit.cobuy.R
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    vm: SettingsViewModel
) {
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBarImpl(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Settings")
                    }
                },
                navHostController = navHostController,
                isSettings = false
            )

            Spacer(modifier = Modifier.size(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://sun125-1.userapi.com/s/v1/ig2/bsKSb_3JolWpjJ5mez44ii5lzdgwXsl4fOtV685zcybEWn7h1TUhPaGwOvyCz-tZveB4XzU1tNT_SDnzxZzrAC07.jpg?quality=95&crop=212,824,1052,1052&as=32x32,48x48,72x72,108x108,160x160,240x240,360x360,480x480,540x540,640x640,720x720&ava=1&u=5_m3lbtS8y6Kw-IhyX0ct7f_g-PoWL8G1p9eSoFoBHM&cs=200x200")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(127.dp)
                        .clip(CircleShape)
                        .clickable {
                            Toast
                                .makeText(context, "Edit avatar", Toast.LENGTH_SHORT)
                                .show()
                        }
                )
                Spacer(modifier = Modifier.size(10.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Леф", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = {
                        Toast.makeText(context, "Edit name", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            painterResource(id = R.drawable.edit_square_24px),
                            contentDescription = "List Icon",
                            modifier = Modifier
                                .sizeIn(maxHeight = MaterialTheme.typography.titleLarge.fontSize.value.dp),
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            LazyColumn {
                item {
                    Text(
                        text = "Notification settings",
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            ),
                        color = MaterialTheme.colorScheme.onTertiary,
                    )
                }
                items(10) {
                    var checked by remember {
                        mutableStateOf(true)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notification $it",
                        )
                        val num = it
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                Toast.makeText(
                                    context,
                                    "Notification $num is ${if (it) "enabled" else "disabled"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                }
                item {
                    Text(
                        text = "Other settings",
                        modifier = Modifier
                            .padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            ),
                        color = MaterialTheme.colorScheme.onTertiary,
                    )
                }
                items(5) {
                    var checked by remember {
                        mutableStateOf(true)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(
                                PaddingValues(
                                    start = 16.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Setting $it",
                        )
                        val num = it
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                Toast.makeText(
                                    context,
                                    "Setting $num is ${if (it) "enabled" else "disabled"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                }
            }
        }
    }


}