package ru.hihit.cobuy.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import ru.hihit.cobuy.R
import ru.hihit.cobuy.models.Product
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.ui.components.composableElems.AddButton
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.composableElems.UniversalModal
import ru.hihit.cobuy.ui.components.viewmodels.ListViewModel
import ru.hihit.cobuy.ui.theme.getColorByHash

@Composable
fun ListScreen(
    navHostController: NavHostController,
    vm: ListViewModel
) {
    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    val openAddProductModal = remember { mutableStateOf(false) }

    when {
        openAddProductModal.value -> AddProductModal(
            onDismiss = { openAddProductModal.value = false },
            onAdd = {
                vm.onProductAdded(it)
                openAddProductModal.value = false
            }
        )
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000L)
            isRefreshing = false
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column {
            TopAppBarImpl(
                title = {
                    ListEditableTitle(vm.productList.name) { vm.onNameChanged(it) }
                },
                navHostController = navHostController,
            )

            SwipeRefreshImpl(
                swipeState = swipeRefreshState,
                onRefresh = {
                    Toast.makeText(context, "Обновляем товары", Toast.LENGTH_SHORT).show()
                    isRefreshing = true
                }) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
                ) {
                    items(10) {
                        ProductItem(
                            product = Product(id = it),
                            onStatusChanged = { product -> vm.onProductStatusChanged(product) },
                            onDelete = { product ->
                                vm.onProductDeleted(product)
                            },
                            onEdited = { product ->
                                vm.onProductEdited(product)
                            }
                        )
                    }
                }
            }
        }
        AddButton(
            onClick = { openAddProductModal.value = true },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductItem(
    product: Product = Product(),
    onStatusChanged: (Product) -> Unit = {},
    onEdited: (Product) -> Unit = {},
    onDelete: (Product) -> Unit = {}
) {
    var isBought by remember { mutableStateOf(false) }
    var isPlanned by remember { mutableStateOf(false) }
    val statusChanged = remember { mutableStateOf(false) }
    when {
        statusChanged.value -> {
            when (product.status) {
                ProductStatus.BOUGHT -> {
                    isBought = true
                    isPlanned = false
                }

                ProductStatus.PLANNED -> {
                    isBought = false
                    isPlanned = true
                }

                ProductStatus.NONE -> {
                    isBought = false
                    isPlanned = false
                }
            }
            statusChanged.value = false
        }
    }

    val openModal = remember { mutableStateOf(false) }
    val openEditModal = remember {
        mutableStateOf(false)
    }
    val modalButtons = mapOf<String, (Product) -> Unit>(
        stringResource(id = R.string.delete_word) to {
            onDelete(product)
            openModal.value = false
        },
        stringResource(R.string.edit_word) to {
            openEditModal.value = true
            openModal.value = false
        },
    )

    when {
        openModal.value ->
            UniversalModal(
                subject = product,
                buttons = modalButtons,
                onDismiss = { openModal.value = false }
            )

        openEditModal.value ->
            EditProductModal(
                product = product,
                onEdit = { editedProduct ->
                    onEdited(editedProduct)
                    openEditModal.value = false
                },
                onDismiss = { openEditModal.value = false }
            )
    }

    Card(
        Modifier
            .height(200.dp)
            .padding(PaddingValues(bottom = 8.dp)),
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            Modifier.combinedClickable(onClick = { }, onLongClick = { openModal.value = true })
        ) {
            Box(
                modifier = Modifier
                    .weight(0.1f)
                    .background(getColorByHash(product.name))
                    .fillMaxHeight()
            ) {
                Text("")
            }
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .padding(PaddingValues(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp))
            ) {
                Column(
                    modifier = Modifier

                ) {
                    Text(product.name, style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.3F),
                        color = MaterialTheme.colorScheme.surfaceTint
                    )
                    Text(
                        product.description,
                        maxLines = 3,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.End
                ) {
                    val buyButtonColor =
                        if (isBought) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background
                    val planButtonColor =
                        if (isPlanned) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background

                    Button(
                        modifier = Modifier.padding(PaddingValues(end = 8.dp)),
                        onClick = {
                            product.status =
                                if (product.status == ProductStatus.PLANNED) ProductStatus.NONE else ProductStatus.PLANNED
                            onStatusChanged(product)
                            statusChanged.value = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = planButtonColor),
                        border = if (isPlanned) null else BorderStroke(
                            1.dp,
                            color = MaterialTheme.colorScheme.surfaceTint
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            top = 1.dp,
                            bottom = 1.dp
                        )
                    ) {
                        Text(
                            if (isPlanned) stringResource(id = R.string.planned_word)
                            else stringResource(
                                id = R.string.plan_word
                            )
                        )
                    }
                    Button(
                        onClick = {
                            product.status =
                                if (product.status == ProductStatus.BOUGHT) ProductStatus.NONE else ProductStatus.BOUGHT
                            onStatusChanged(product)
                            statusChanged.value = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buyButtonColor),
                        border = if (isBought) null else BorderStroke(
                            1.dp,
                            color = MaterialTheme.colorScheme.surfaceTint
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            top = 1.dp,
                            bottom = 1.dp
                        )
                    ) {
                        Text(
                            if (isBought) stringResource(id = R.string.bought_word)
                            else stringResource(
                                id = R.string.buy_word
                            )
                        )
                    }
                }
            }
        }
    }

}


@Composable
fun ListEditableTitle(
    listName: String = "List",
    onNameChanged: (String) -> Unit = {},
) {

    var isNameEditing by remember {
        mutableStateOf(false)
    }
    var isNameCorrect by remember {
        mutableStateOf(true)
    }

    var text by remember { mutableStateOf(listName) }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                isNameEditing = true
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (!isNameEditing) {
                Text(text = text)
            } else {
                OutlinedTextField(
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    value = text,
                    onValueChange = { newText -> text = newText },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            isNameCorrect = text.isNotEmpty()
                            if (isNameCorrect) {
                                onNameChanged(text)
                                isNameEditing = false
                            }
                        }
                    ),
                    singleLine = true,
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
@Preview
fun AddProductModal(
    onAdd: (Product) -> Unit = {},
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
                            onAdd(Product(name = nameText, description = descriptionText))
                    }) {
                        Text(text = stringResource(id = R.string.submit_word))
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditProductModal(
    product: Product,
    onEdit: (Product) -> Unit = {},
    onDismiss: () -> Unit = {},
    title: String = stringResource(R.string.edit_product),
) {
    var isNameCorrect by remember { mutableStateOf(true) }

    var nameText by remember { mutableStateOf(product.name) }
    var descriptionText by remember { mutableStateOf(product.description) }


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
                                text = stringResource(id = R.string.product_description),
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
                            text = stringResource(id = R.string.enter_product_name),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                    Button(onClick = {
                        isNameCorrect = nameText.isNotEmpty()
                        if (isNameCorrect)
                            onEdit(product.copy(name = nameText, description = descriptionText))
                    }) {
                        Text(text = stringResource(id = R.string.submit_word))
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}