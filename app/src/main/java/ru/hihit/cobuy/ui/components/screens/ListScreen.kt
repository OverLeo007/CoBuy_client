package ru.hihit.cobuy.ui.components.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import ru.hihit.cobuy.R
import ru.hihit.cobuy.api.ProductData
import ru.hihit.cobuy.models.ProductStatus
import ru.hihit.cobuy.ui.components.composableElems.FloatingActionButtonImpl
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.composableElems.modals.listScreen.NewAddProductModal
import ru.hihit.cobuy.ui.components.composableElems.modals.listScreen.NewProductModal
import ru.hihit.cobuy.ui.components.viewmodels.ListViewModel
import ru.hihit.cobuy.ui.theme.getColorByHash
import ru.hihit.cobuy.utils.getFromPreferences

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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

    val snackBarScope = rememberCoroutineScope()
    val dismissStateScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    when {
        openAddProductModal.value -> NewAddProductModal(
            onDismiss = { openAddProductModal.value = false },
            onSubmit = {
                vm.onProductAdded(context, it)
                openAddProductModal.value = false
            }
        )
    }

    Scaffold(
        topBar = {
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
        },
        floatingActionButton = {
            FloatingActionButtonImpl(
                onClick = { openAddProductModal.value = true },
                Modifier
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }

    ) { paddingValues ->
        SwipeRefreshImpl(
            swipeState = swipeRefreshState,
            onRefresh = {
                vm.onRefresh()
            }) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)),
//                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
                contentPadding = paddingValues
            ) {
                items(items = products, key = { it.id } ) { curProduct ->

                    val isDeleted = remember { mutableStateOf(false) }

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            when (value) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    if (!isDeleted.value) {
                                        Log.d("ProductItem", "onDelete")
                                        isDeleted.value = true
                                        snackBarScope.launch {
                                            val result = snackbarHostState
                                                .showSnackbar(
                                                    message = "Продукт будет удален",
                                                    actionLabel = "Отмена",
                                                    // Defaults to SnackbarDuration.Short
                                                    duration = SnackbarDuration.Long,
                                                    withDismissAction = true
                                                )
                                            when (result) {
                                                SnackbarResult.ActionPerformed -> {
                                                    isDeleted.value = false

                                                }

                                                SnackbarResult.Dismissed -> {
                                                    Toast.makeText(context, "Продукт удален", Toast.LENGTH_SHORT).show()
                                                    vm.onProductDeleted(curProduct)
                                                }
                                            }
                                        }
                                    }

                                }

                                SwipeToDismissBoxValue.Settled -> return@rememberSwipeToDismissBoxState true
                                SwipeToDismissBoxValue.StartToEnd -> return@rememberSwipeToDismissBoxState false
                            }
                            return@rememberSwipeToDismissBoxState true
                        },
                        positionalThreshold = { it * 0.25f }
                    )

                    LaunchedEffect(isDeleted.value) {
                        if (!isDeleted.value) {
                            dismissStateScope.launch {
                                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                            }
                        }
                    }

                    ProductItem(
                        product = curProduct,
                        onStatusChanged = { product -> vm.onProductStatusChanged(product) },
                        onEdited = { product ->
                            vm.onProductEdited(product)
                        },
                        onImageSelected = { product ->
                            vm.onUploadImage(context, product)
                        },
                        placementModifier = Modifier.animateItemPlacement(),
                        isDeleted = isDeleted,
                        dismissState = dismissState
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

    }

//    Box(
//        modifier = Modifier
//            .fillMaxSize(),
//    ) {
//        Column {
//            TopAppBarImpl(
//                title = {
//                    if (isListLoading) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator(
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//                        }
//                    } else {
//                        ListEditableTitle(list.name) { vm.onNameChanged(it) }
//                    }
//                },
//                navHostController = navHostController,
//            )
//
//            SwipeRefreshImpl(
//                swipeState = swipeRefreshState,
//                onRefresh = {
//                    vm.onRefresh()
//                }) {
//                LazyColumn(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
//                ) {
//                    items(products) { curProduct ->
//                        ProductItem(
//                            product = curProduct,
//                            onStatusChanged = { product -> vm.onProductStatusChanged(product) },
//                            onDelete = { product ->
//                                vm.onProductDeleted(product)
//                            },
//                            onEdited = { product ->
//                                vm.onProductEdited(product)
//                            },
//                            onImageSelected = { product ->
//                                vm.onUploadImage(context, product)
//                            }
//                        )
//                    }
//                    item {
//                        Spacer(modifier = Modifier.height(100.dp))
//                    }
//
//                }
//            }
//        }
//        AddButton(
//            onClick = { openAddProductModal.value = true },
//            modifier = Modifier.align(Alignment.BottomEnd)
//        )
//    }

}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ProductItem(
    product: ProductData = ProductData(),
    onStatusChanged: (ProductData) -> Unit = {},
    onImageSelected: (ProductData) -> Unit = {},
    onEdited: (ProductData) -> Unit = {},
    @SuppressLint("ModifierParameter") placementModifier: Modifier,
    isDeleted: MutableState<Boolean>,
    dismissState: SwipeToDismissBoxState
) {
    var isBought by remember { mutableStateOf(product.status == ProductStatus.BOUGHT) }
    var isPlanned by remember { mutableStateOf(product.status == ProductStatus.PLANNED) }

    fun updateStatus(newStatus: ProductStatus) {
        product.status = newStatus
        isBought = newStatus == ProductStatus.BOUGHT
        isPlanned = newStatus == ProductStatus.PLANNED
        onStatusChanged(product)
    }

    LaunchedEffect(product.status) {
        isBought = product.status == ProductStatus.BOUGHT
        isPlanned = product.status == ProductStatus.PLANNED
    }
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

    AnimatedVisibility(
        modifier = placementModifier,
        visible = !isDeleted.value,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 150)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 150)
        )
    ) {

        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,
            backgroundContent = {
                val color = when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> Color.Transparent
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                    else -> Color.Transparent
                }

                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(PaddingValues(bottom = 8.dp)),
                    colors = CardDefaults.cardColors().copy(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),

                    ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }) {
            Card(
                Modifier
                    .height(200.dp)
                    .padding(PaddingValues(bottom = 8.dp)),
                colors = CardDefaults.cardColors()
                    .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp),
            ) {
                Row(
                    Modifier.combinedClickable(
                        onClick = { openModal.value = true },
                        onLongClick = { })
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
                            .padding(
                                PaddingValues(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = 4.dp,
                                    bottom = 4.dp
                                )
                            )
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
                            if (product.buyer == null || context.getFromPreferences("user_id", -1) == product.buyer.id) {
                                Column {
                                    val buyButtonColor = if (isBought) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background
                                    val planButtonColor = if (isPlanned) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.background

                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            val newStatus = if (isBought) ProductStatus.NONE else ProductStatus.BOUGHT
                                            updateStatus(newStatus)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = buyButtonColor),
                                        border = if (isBought) null else BorderStroke(1.dp, color = MaterialTheme.colorScheme.surfaceTint),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(12.dp)
                                    ) {
                                        Text(if (isBought) stringResource(id = R.string.bought_word) else stringResource(id = R.string.buy_word))
                                    }

                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            val newStatus = if (isPlanned) ProductStatus.NONE else ProductStatus.PLANNED
                                            updateStatus(newStatus)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = planButtonColor),
                                        border = if (isPlanned) null else BorderStroke(1.dp, color = MaterialTheme.colorScheme.surfaceTint),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(12.dp)
                                    ) {
                                        Text(if (isPlanned) stringResource(id = R.string.planned_word) else stringResource(id = R.string.plan_word))
                                    }
                                }
                            } else {
                                val statusWord = if (product.status == ProductStatus.BOUGHT) stringResource(id = R.string.bought_by_word) else stringResource(id = R.string.planned_by_word)
                                Text(
                                    text = statusWord + ' ' + product.buyer.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                                )
                            }
                        }
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

    val currentListName by rememberUpdatedState(listName)
    var text by remember { mutableStateOf(listName) }

    LaunchedEffect(currentListName) {
        if (!isNameEditing) {
            text = currentListName
        }
    }

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

