package ru.hihit.cobuy.ui.components.composableElems.modals.listScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.ProductData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddProductModal(
    onAdd: (ProductData) -> Unit = {},
    onDismiss: () -> Unit = {},
    title: String = stringResource(R.string.new_product),
    namePlaceholder: String = stringResource(R.string.product_name),
) {
    var isNameCorrect by remember { mutableStateOf(true) }

    var nameText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }


    val focusManager = LocalFocusManager.current
    val (a, b) = FocusRequester.createRefs()

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
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = MaterialTheme.colorScheme.onTertiary
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
                            onClick = {},
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
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                Spacer(modifier = Modifier.size(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        modifier = Modifier
                            .focusRequester(a)
                            .focusProperties { next = b },
                        value = nameText,
                        onValueChange = { newText -> nameText = newText },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                isNameCorrect = nameText.isNotEmpty()
                                if (isNameCorrect) focusManager.moveFocus(FocusDirection.Next)
                            }
                        ),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = namePlaceholder,
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    TextField(
                        modifier = Modifier
                            .focusRequester(b)
                            .focusProperties { previous = a },
                        value = descriptionText,
                        onValueChange = { newText -> descriptionText = newText },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                        ),
                        minLines = 3,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.product_description),
                                color = MaterialTheme.colorScheme.onTertiary
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceTint)
                    Spacer(modifier = Modifier.size(20.dp))
                    if (!isNameCorrect) {
                        Text(
                            text = stringResource(R.string.enter_product_name),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                    Button(onClick = {
                        isNameCorrect = nameText.isNotEmpty()
                        if (isNameCorrect)
                            onAdd(ProductData(name = nameText, description = descriptionText))
                    }) {
                        Text(text = stringResource(id = R.string.submit_word))
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}


