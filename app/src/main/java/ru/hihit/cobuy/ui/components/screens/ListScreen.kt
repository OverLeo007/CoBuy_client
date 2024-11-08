package ru.hihit.cobuy.ui.components.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.ProductData
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.ui.components.composableElems.AddButton
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.composableElems.UniversalModal
import ru.hihit.cobuy.ui.components.composableElems.modals.listScreen.AddProductModal
import ru.hihit.cobuy.ui.components.composableElems.modals.listScreen.EditProductModal
import ru.hihit.cobuy.ui.components.composableElems.modals.listScreen.NewAddProductModal
import ru.hihit.cobuy.ui.components.composableElems.modals.listScreen.NewProductModal
import ru.hihit.cobuy.ui.components.viewmodels.ListViewModel
import ru.hihit.cobuy.ui.theme.getColorByHash
import ru.hihit.cobuy.utils.getFromPreferences

@Composable
fun ListScreen(
    navHostController: NavHostController,
    vm: ListViewModel
) {
    val context = LocalContext.current

    val isListLoading by remember {
        vm.isListLoading
    }
    val isRefreshing by remember { vm.isRefreshing }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)


    val list by vm.list.collectAsState()
    val products by vm.products.collectAsState()

    val openAddProductModal = remember { mutableStateOf(false) }

    when {
        openAddProductModal.value -> NewAddProductModal(
            onDismiss = { openAddProductModal.value = false },
            onSubmit = {
                vm.onProductAdded(context, it)
                openAddProductModal.value = false
            }
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column {
            TopAppBarImpl(
                title = {
                    if (isListLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        ListEditableTitle(list.name) { vm.onNameChanged(it) }
                    }
                },
                navHostController = navHostController,
            )

            SwipeRefreshImpl(
                swipeState = swipeRefreshState,
                onRefresh = {
                    vm.onRefresh()
                }) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
                ) {
                    items(products) { curProduct ->
                        ProductItem(
                            product = curProduct,
                            onStatusChanged = { product -> vm.onProductStatusChanged(product) },
                            onDelete = { product ->
                                vm.onProductDeleted(product)
                            },
                            onEdited = { product ->
                                vm.onProductEdited(product)
                            },
                            onImageSelected = { product ->
                                vm.onUploadImage(context, product)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Preview
@Composable
fun ProductItem(
    product: ProductData = ProductData(),
    onStatusChanged: (ProductData) -> Unit = {},
    onImageSelected: (ProductData) -> Unit = {},
    onEdited: (ProductData) -> Unit = {},
    onDelete: (ProductData) -> Unit = {}
) {
    var isBought by remember { mutableStateOf(product.status == ProductStatus.BOUGHT) }
    var isPlanned by remember { mutableStateOf(product.status == ProductStatus.PLANNED) }
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


    when {
        openModal.value ->
            NewProductModal(
                product = product,
                onSubmit = {
                    onEdited(it)
                    openModal.value = false
                },
                onImageSelected = onImageSelected,
                onDismiss = { openModal.value = false }
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
            Modifier.combinedClickable(onClick = { openModal.value = true }, onLongClick = { })
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5F),
                        text = product.price.toString() + "₽", //FIXME Валюту надо выбирать в настройках
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp)
                    )

                    val context = LocalContext.current
                    if (product.buyer == null || context.getFromPreferences(
                            "user_id",
                            -1
                        ) == product.buyer.id
                    ) {
                        Column {
                            val buyButtonColor =
                                if (isBought) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background
                            val planButtonColor =
                                if (isPlanned) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background
                            Button(
                                modifier = Modifier.fillMaxWidth(),
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
                            Button(
//                            modifier = Modifier.padding(PaddingValues(end = 8.dp)),
                                modifier = Modifier.fillMaxWidth(),
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
                        }
                    } else {
                        val statusWord = if (product.status == ProductStatus.BOUGHT) stringResource(id = R.string.bought_by_word)
                        else stringResource(id = R.string.planned_by_word)
                        Text(
                            text = statusWord + product.buyer.name,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                        )
                    }
                }
            }
        }
    }

}


//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun ProductItemOld(
//    product: ProductData,
//    onStatusChanged: (ProductData) -> Unit = {},
//    onEdited: (ProductData) -> Unit = {},
//    onDelete: (ProductData) -> Unit = {}
//) {
//    var isBought by remember { mutableStateOf(product.status == ProductStatus.BOUGHT) }
//    var isPlanned by remember { mutableStateOf(product.status == ProductStatus.PLANNED) }
//    val statusChanged = remember { mutableStateOf(false) }
//    when {
//        statusChanged.value -> {
//            when (product.status) {
//                ProductStatus.BOUGHT -> {
//                    isBought = true
//                    isPlanned = false
//                }
//
//                ProductStatus.PLANNED -> {
//                    isBought = false
//                    isPlanned = true
//                }
//
//                ProductStatus.NONE -> {
//                    isBought = false
//                    isPlanned = false
//                }
//            }
//            statusChanged.value = false
//        }
//    }
//
//    val openModal = remember { mutableStateOf(false) }
//    val openEditModal = remember {
//        mutableStateOf(false)
//    }
//    val modalButtons = mapOf<String, (ProductData) -> Unit>(
//        stringResource(id = R.string.delete_word) to {
//            onDelete(product)
//            openModal.value = false
//        },
//        stringResource(R.string.edit_word) to {
//            openEditModal.value = true
//            openModal.value = false
//        },
//    )
//
//    when {
//        openModal.value ->
//            UniversalModal(
//                subject = product,
//                buttons = modalButtons,
//                onDismiss = { openModal.value = false }
//            )
//
//        openEditModal.value ->
//            EditProductModal(
//                product = product,
//                onEdit = { editedProduct ->
//                    onEdited(editedProduct)
//                    openEditModal.value = false
//                },
//                onDismiss = { openEditModal.value = false }
//            )
//    }
//
//    Card(
//        Modifier
//            .height(200.dp)
//            .padding(PaddingValues(bottom = 8.dp)),
//        colors = CardDefaults.cardColors()
//            .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
//        shape = RoundedCornerShape(24.dp),
//    ) {
//        Row(
//            Modifier.combinedClickable(onClick = { }, onLongClick = { openModal.value = true })
//        ) {
//            Box(
//                modifier = Modifier
//                    .weight(0.1f)
//                    .background(getColorByHash(product.name))
//                    .fillMaxHeight()
//            ) {
//                Text("")
//            }
//            Box(
//                modifier = Modifier
//                    .weight(0.9f)
//                    .fillMaxHeight()
//                    .padding(PaddingValues(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp))
//            ) {
//                Column(
//                    modifier = Modifier
//
//                ) {
//                    Text(product.name, style = MaterialTheme.typography.titleLarge)
//                    HorizontalDivider(
//                        modifier = Modifier.fillMaxWidth(0.3F),
//                        color = MaterialTheme.colorScheme.surfaceTint
//                    )
//                    Text(
//                        product.description,
//                        maxLines = 3,
//                        style = MaterialTheme.typography.bodyMedium,
//                        overflow = TextOverflow.Ellipsis
//                    )
//
//                }
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .align(Alignment.BottomEnd),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                    val buyButtonColor =
//                        if (isBought) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background
//                    val planButtonColor =
//                        if (isPlanned) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background
//
//                    Button(
//                        modifier = Modifier.padding(PaddingValues(end = 8.dp)),
//                        onClick = {
//                            product.status =
//                                if (product.status == ProductStatus.PLANNED) ProductStatus.NONE else ProductStatus.PLANNED
//                            onStatusChanged(product)
//                            statusChanged.value = true
//                        },
//                        colors = ButtonDefaults.buttonColors(containerColor = planButtonColor),
//                        border = if (isPlanned) null else BorderStroke(
//                            1.dp,
//                            color = MaterialTheme.colorScheme.surfaceTint
//                        ),
//                        shape = RoundedCornerShape(8.dp),
//                        contentPadding = PaddingValues(
//                            start = 12.dp,
//                            end = 12.dp,
//                            top = 1.dp,
//                            bottom = 1.dp
//                        )
//                    ) {
//                        Text(
//                            if (isPlanned) stringResource(id = R.string.planned_word)
//                            else stringResource(
//                                id = R.string.plan_word
//                            )
//                        )
//                    }
//                    Button(
//                        onClick = {
//                            product.status =
//                                if (product.status == ProductStatus.BOUGHT) ProductStatus.NONE else ProductStatus.BOUGHT
//                            onStatusChanged(product)
//                            statusChanged.value = true
//                        },
//                        colors = ButtonDefaults.buttonColors(containerColor = buyButtonColor),
//                        border = if (isBought) null else BorderStroke(
//                            1.dp,
//                            color = MaterialTheme.colorScheme.surfaceTint
//                        ),
//                        shape = RoundedCornerShape(8.dp),
//                        contentPadding = PaddingValues(
//                            start = 12.dp,
//                            end = 12.dp,
//                            top = 1.dp,
//                            bottom = 1.dp
//                        )
//                    ) {
//                        Text(
//                            if (isBought) stringResource(id = R.string.bought_word)
//                            else stringResource(
//                                id = R.string.buy_word
//                            )
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//}


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





