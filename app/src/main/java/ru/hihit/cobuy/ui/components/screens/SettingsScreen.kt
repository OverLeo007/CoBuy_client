package ru.hihit.cobuy.ui.components.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.twoTargetSwitchPreference
import ru.hihit.cobuy.R
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.ui.components.viewmodels.SettingKeys
import ru.hihit.cobuy.ui.components.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    navHostController: NavHostController,
    vm: SettingsViewModel
) {

    var isEditing by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(vm.user.name) }


    val onRes = stringResource(R.string.on)
    val offRes = stringResource(R.string.off)

    val showCompletedListsStr = stringResource(R.string.show_completed_lists)

    val themeStr = stringResource(R.string.theme)

    val themeStrMap = mapOf(
        SettingKeys.THEME_LIGHT to stringResource(R.string.theme_light),
        SettingKeys.THEME_DARK to stringResource(R.string.theme_dark),
        SettingKeys.THEME_SYSTEM to stringResource(R.string.theme_system)
    )

    val productCardTypeStr = stringResource(R.string.product_card_type)
    val productCartTypeDescStr = stringResource(R.string.product_card_type_description)

    val productCartStrMap = mapOf(
        SettingKeys.PRODUCT_CARD_TYPE_STANDARD to stringResource(R.string.product_card_type_standard),
        SettingKeys.PRODUCT_CARD_TYPE_SHOPPING_LIST to stringResource(R.string.product_card_type_shopping_list),
//        SettingKeys.PRODUCT_CARD_TYPE_PICTURE to stringResource(R.string.product_card_type_picture)
    )

    Scaffold(
        topBar = {
            TopAppBarImpl(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.settings_word))
                    }
                },
                navHostController = navHostController,
                actions = {
                    IconButton(
                        onClick = {
                            vm.onLogout()
                            navHostController.navigate(Route.Authorization)
                        }

                    ) {
                        Icon(
                            painterResource(id = R.drawable.logout_24px),
                            contentDescription = "Logout icon",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
//        Column(
//            modifier = Modifier.fillMaxWidth().padding(PaddingValues(start = 16.dp))
//        ) {
//            Text(
//                text = "Login as:\nId:${vm.user.id}\nLogin: ${vm.user.name}\nEmail: ${vm.user.email}\n",
//            )
//        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)),
            contentPadding = paddingValues
        ) {
            twoTargetSwitchPreference(
                key = SettingKeys.SHOW_COMPLETED_LISTS,
                defaultValue = true,
                title = { Text(text = showCompletedListsStr) },
                summary = { Text(text = if (it) onRes else offRes) },
            ) {}
            listPreference(
                key = SettingKeys.THEME,
                defaultValue = SettingKeys.THEME_SYSTEM,
                values = themeStrMap.keys.toList(),
                valueToText = { AnnotatedString(themeStrMap[it] ?: "") },
                title = { Text(text = themeStr) },
                summary = { Text(text = themeStrMap[it] ?: "") },
                type = ListPreferenceType.ALERT_DIALOG,
            )
            listPreference(
                key = SettingKeys.PRODUCT_CARD_TYPE,
                defaultValue = SettingKeys.PRODUCT_CARD_TYPE_STANDARD,
                values = productCartStrMap.keys.toList(),
                valueToText = { AnnotatedString(productCartStrMap[it] ?: "") },
                title = { Text(text = productCardTypeStr) },
                summary = { Text(text = productCartTypeDescStr) },
                type = ListPreferenceType.ALERT_DIALOG,
            )
        }
    }

//    Box(modifier = Modifier.fillMaxSize()) {
//        Column {
//
//
//            Spacer(modifier = Modifier.size(20.dp))

//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                AsyncImage(
//                    model = ImageRequest.Builder(LocalContext.current)
//                        .data(vm.imageUri)
//                        .crossfade(true)
//                        .build(),
//                    contentDescription = "Avatar",
//                    contentScale = ContentScale.Fit,
//                    modifier = Modifier
//                        .size(127.dp)
//                        .clip(CircleShape)
//                        .clickable {
//                            launcher.launch("image/*")
//                        },
//                    placeholder = ColorPainter(MaterialTheme.colorScheme.primary)
//
//                )
//                Spacer(modifier = Modifier.size(10.dp))
//                Row(
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        painterResource(id = R.drawable.edit_square_24px),
//                        contentDescription = "List Icon",
//                        modifier = Modifier
//                            .sizeIn(maxHeight = MaterialTheme.typography.titleLarge.fontSize.value.dp),
//                        tint = Color.Transparent
//                    )
//                    if (isEditing) {
//                        OutlinedTextField(
////                            modifier = Modifier.weight(1F),
//                            value = text,
//                            onValueChange = { newText -> text = newText },
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = KeyboardType.Text,
//                                imeAction = ImeAction.Done
//                            ),
//                            keyboardActions = KeyboardActions(
//                                onDone = {
//                                    if (text.length in 3..100) {
//                                        isEditing = false
//                                        isError = false
//                                        vm.onNameChanged(text)
//                                    }
//                                    isError = true
//                                }
//                            ),
//                            singleLine = true
//                        )
//
//                    } else {
//                        Text(
////                            modifier = Modifier.weight(1F),
//                            text = text,
//                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    }
//                    IconButton(
//                        onClick = {
//                            isEditing = true
//                        },
//                        enabled = !isEditing
//                    ) {
//                        Icon(
//                            painterResource(id = R.drawable.edit_square_24px),
//                            contentDescription = "List Icon",
//                            modifier = Modifier
//                                .sizeIn(maxHeight = MaterialTheme.typography.titleLarge.fontSize.value.dp),
//                            tint = MaterialTheme.colorScheme.onTertiary
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.size(20.dp))
//            LazyColumn {
//                item {
//                    Text(
//                        text = "Notification settings",
//                        modifier = Modifier
//                            .padding(
//                                PaddingValues(
//                                    start = 16.dp,
//                                    end = 8.dp,
//                                    top = 8.dp,
//                                    bottom = 8.dp
//                                )
//                            ),
//                        color = MaterialTheme.colorScheme.onTertiary,
//                    )
//                }
//                items(10) {
//                    var checked by remember {
//                        mutableStateOf(true)
//                    }
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(MaterialTheme.colorScheme.primaryContainer)
//                            .padding(
//                                PaddingValues(
//                                    start = 16.dp,
//                                    end = 8.dp,
//                                    top = 8.dp,
//                                    bottom = 8.dp
//                                )
//                            ),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "Notification $it",
//                        )
//                        val num = it
//                        Switch(
//                            checked = checked,
//                            onCheckedChange = {
//                                checked = it
//                                Toast.makeText(
//                                    context,
//                                    "Notification $num is ${if (it) "enabled" else "disabled"}",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        )
//
//                    }
//                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
//                }
//                item {
//                    Text(
//                        text = "Other settings",
//                        modifier = Modifier
//                            .padding(
//                                PaddingValues(
//                                    start = 16.dp,
//                                    end = 8.dp,
//                                    top = 8.dp,
//                                    bottom = 8.dp
//                                )
//                            ),
//                        color = MaterialTheme.colorScheme.onTertiary,
//                    )
//                }
//                items(5) {
//                    var checked by remember {
//                        mutableStateOf(true)
//                    }
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(MaterialTheme.colorScheme.primaryContainer)
//                            .padding(
//                                PaddingValues(
//                                    start = 16.dp,
//                                    end = 8.dp,
//                                    top = 8.dp,
//                                    bottom = 8.dp
//                                )
//                            ),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "Setting $it",
//                        )
//                        val num = it
//                        Switch(
//                            checked = checked,
//                            onCheckedChange = {
//                                checked = it
//                                Toast.makeText(
//                                    context,
//                                    "Setting $num is ${if (it) "enabled" else "disabled"}",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        )
//
//                    }
//                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
//                }
//            }
//        }
//    }


}