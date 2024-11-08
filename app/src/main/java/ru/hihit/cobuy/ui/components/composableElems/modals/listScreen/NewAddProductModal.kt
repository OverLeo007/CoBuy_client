package ru.hihit.cobuy.ui.components.composableElems.modals.listScreen

import android.content.Context
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.ProductData
import ru.hihit.cobuy.ui.components.composableElems.ImagePlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAddProductModal(
    onSubmit: (ProductData) -> Unit = {},
    onDismiss: () -> Unit = {},
) {

    val product = ProductData()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var nameText by remember { mutableStateOf("") }
    var isNameEditing by remember { mutableStateOf(false) }

    var descriptionText by remember { mutableStateOf("") }

    var quantity by remember { mutableIntStateOf(0) }

    var price by remember { mutableIntStateOf(product.price ?: 0) }


    var imageUri by remember { mutableStateOf(product.productImgUrl) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
            }
        }


    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.8F),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box {
                Column {
                    TopAppBar(
                        title = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                TextField(
                                    value = nameText,
                                    onValueChange = { nameText = it },
                                    singleLine = true,
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            isNameEditing = !isNameEditing

                                        }
                                    ),
                                    suffix = {
                                        IconButton(
                                            onClick = {
                                                isNameEditing = !isNameEditing
                                            }
                                        ) {
                                            Icon(
                                                painterResource(id = R.drawable.edit_square_24px),
                                                contentDescription = "Edit name"
                                            )
                                        }
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    placeholder = { Text(stringResource(id = R.string.product_name)) },
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    onDismiss()
                                },
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.arrow_back_ios_24px),
                                    contentDescription = "Back",
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    onDismiss()
                                },
                                enabled = false
                            ) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.Transparent
                                )
                            }
                        }
                    )
                    ImagePlaceholder(
                        uri = imageUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.3F)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        launcher.launch("image/*")
                                    },
                                )
                            },
                        name = if (nameText == "") "Click to put the image of product" else nameText, // TODO: Добавить возможность добавлять фото с камеры
                        contentScale = ContentScale.Crop,
                    )
                    TextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text(stringResource(id = R.string.product_description)) },
                        maxLines = 5,
                        minLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)

                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1F),
                            value = quantity.toString(),
                            onValueChange = {
                                quantity =
                                    if (it.isDigitsOnly() && it.isNotEmpty() && it.length <= 5) it.toInt() else 0
                            },
                            singleLine = true,
                            label = { Text("Количество") }, // FIXME: add quantity string
                            prefix = {
                                IconButton(
                                    onClick = { if (quantity > 1) quantity-- },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.remove_24dp),
                                        contentDescription = "Remove",
                                    )
                                }

                            },
                            suffix = {
                                IconButton(
                                    onClick = { quantity++ },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.add_24dp),
                                        contentDescription = "Add",
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(16.dp),
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                            )
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(0.5F),
                            singleLine = true,
                            value = price.toString(),
                            onValueChange = {
                                price =
                                    if (it.isDigitsOnly() && it.isNotEmpty() && it.length <= 5) it.toInt() else 0
                            },
                            label = { Text("Цена") }, // Fixme: add price string
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        )
                        Text(text = "₽", modifier = Modifier.weight(0.1F)) //FIXME: add currency
                    }
                    Button(
                        onClick = {
                            onSubmit(
                                product.copy(
                                    name = nameText,
                                    description = descriptionText,
                                    productImgUrl = imageUri,
                                    quantity = quantity,
                                    price = price,
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text("Добавить")
                    }
                }
            }

        }
    }
}


