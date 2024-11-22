package ru.hihit.cobuy.ui.components.composableElems.modals.listScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.ProductData
import ru.hihit.cobuy.ui.components.composableElems.ImagePlaceholder
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Composable
@Preview
fun NewProductModal(
    product: ProductData = ProductData(),
    onSubmit: (ProductData) -> Unit = {},
    onDismiss: () -> Unit = {},
    onImageSelected: (ProductData) -> Unit = {},
    namePlaceholder: String = stringResource(R.string.product_name),
    descriptionPlaceholder: String = stringResource(R.string.product_description)
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    var isNameCorrect by remember { mutableStateOf(true) }

    var nameText by remember { mutableStateOf(if (product.name == "") namePlaceholder else product.name) }
    var isNameEditing by remember { mutableStateOf(false) }

    var descriptionText by remember { mutableStateOf(if (product.description == "") descriptionPlaceholder else product.description) }

    var quantity by remember { mutableIntStateOf(product.quantity ?: 0) } // TODO: product.quantity

    var price by remember { mutableIntStateOf(product.price ?: 0) }

    var isImageFullScreen by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf(product.productImgUrl) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
            }
        }

    val tempImageUri = remember {
        mutableStateOf(
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            )
        )
    }

    LaunchedEffect(Unit) {
        tempImageUri.value = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        )
    }

    val imageUpdatingKey = remember { mutableIntStateOf(0) }
    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {success ->
        if (success) {
            imageUri = tempImageUri.value
            Log.d("ProductModal", "Image Loaded success: $imageUri from ${tempImageUri.value}")
            imageUpdatingKey.intValue++
        }
    }


    when {
        !isNameEditing -> {
            HideKeyboardFrom()
        }
    }

    var isPhotoButtonsVisible by remember { mutableStateOf(false) }
    val animDuration = 150

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp



    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
//                .heightIn(min = LocalConfiguration.current.screenHeightDp.dp * 0.8F),
                .heightIn(max = screenHeight * 0.8F),
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
                                    )
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
                    key(imageUpdatingKey) {
                        ImagePlaceholder(
                            uri = imageUri,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(screenHeight * 0.8F * 0.25F)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
//                                        launcher.launch("image/*")
                                            isPhotoButtonsVisible = !isPhotoButtonsVisible
                                        },
                                        onLongPress = {
                                            isImageFullScreen = true
                                        }
                                    )
                                },
                            name = nameText,
                            contentScale = ContentScale.Crop,
                            isFullScreen = isImageFullScreen,
                            onFullScreenChange = { isImageFullScreen = it }
                        )
                    }
                    AnimatedVisibility(
                        visible = isPhotoButtonsVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(durationMillis = animDuration)
                        ) + expandVertically(
                            expandFrom = Alignment.Top
                        ) + fadeIn(
                            initialAlpha = 0.3f
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { -it },
                            animationSpec = tween(durationMillis = animDuration)
                        ) + shrinkVertically() + fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 50.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1F)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(bounded = true)
                                    ) {
                                        tempImageUri.value = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                                        )
                                        cameraLauncher.launch(tempImageUri.value)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.photo_camera_24dp),
                                        modifier = Modifier.padding(end = 8.dp),
                                        contentDescription = "camera icon"
                                    )
                                    Text(
                                        text = "Сделать фото",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = isPhotoButtonsVisible,
                                enter = scaleIn(animationSpec = tween(durationMillis = animDuration)),
                                exit = scaleOut(animationSpec = tween(durationMillis = animDuration))

                            ) {
                                VerticalDivider(
                                    color = MaterialTheme.colorScheme.surfaceTint
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .weight(1F)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(bounded = true)
                                    ) {
                                        launcher.launch("image/*")
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(end = 8.dp),
                                        painter = painterResource(id = R.drawable.photo_library_24dp),
                                        contentDescription = "photo library icon"
                                    )
                                    Text(
                                        text = "Выбрать из галереи",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = isPhotoButtonsVisible,
                        enter = scaleIn(animationSpec = tween(durationMillis = animDuration)),
                        exit = scaleOut(animationSpec = tween(durationMillis = animDuration))

                    ) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surfaceTint
                        )
                    }
                    TextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text("Описание") },
                        maxLines = 5,
                        minLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
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
                            if (imageUri != product.productImgUrl) {
                                onImageSelected(
                                    product.copy(productImgUrl = imageUri)
                                )
                            }
                            onSubmit(
                                product.copy(
                                    name = nameText,
                                    description = descriptionText,
                                    quantity = quantity,
                                    price = price,
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(stringResource(R.string.submit_word))
                    }
                }
            }

        }
    }
}


@Composable
fun HideKeyboardFrom() {
    val context = LocalContext.current
    val view = LocalView.current
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


