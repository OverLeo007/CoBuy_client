package ru.hihit.cobuy.ui.components.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.LocalPreferenceFlow
import ru.hihit.cobuy.currency.CurrencyViewModel
import ru.hihit.cobuy.ui.components.composableElems.FloatingActionButtonImpl
import ru.hihit.cobuy.ui.components.composableElems.SwipeRefreshImpl
import ru.hihit.cobuy.ui.components.composableElems.TopAppBarImpl
import ru.hihit.cobuy.ui.components.composableElems.columnItems.procductItems.UniversalListItem
import ru.hihit.cobuy.ui.components.composableElems.modals.listScreen.AddProductModal
import ru.hihit.cobuy.ui.components.viewmodels.ListViewModel
import ru.hihit.cobuy.ui.components.viewmodels.SettingKeys

@Composable
fun ListScreen(
    navHostController: NavHostController,
    vm: ListViewModel,
    currencyVm: CurrencyViewModel
) {
    val context = LocalContext.current

    val preferences by LocalPreferenceFlow.current.collectAsStateWithLifecycle()
    val productCardTypePreference: String = preferences.asMap()
        .getOrDefault(
            SettingKeys.PRODUCT_CARD_TYPE,
            SettingKeys.PRODUCT_CARD_TYPE_STANDARD
        ).toString()

    preferences.let {
        Log.d("ListScreen", "using card type: $productCardTypePreference")
    }

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
        openAddProductModal.value -> AddProductModal(
            onDismiss = { openAddProductModal.value = false },
            onSubmit = {
                vm.onProductAdded(context, it)
                openAddProductModal.value = false
            },
            currencyVm = currencyVm
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
                modifier = getColumnModifierDependsOnCurProductCardType(productCardTypePreference)
                    .fillMaxSize(),
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

                    UniversalListItem(
                        currencyVM = currencyVm,
                        currentItemType = productCardTypePreference.toString(),
                        product = curProduct,
                        onStatusChanged = { product -> vm.onProductStatusChanged(product) },
                        onEdited = { product ->
                            vm.onProductEdited(product)
                        },
                        onImageSelected = { product ->
                            vm.onUploadImage(context, product)
                        },
                        placementModifier = Modifier.animateItem(),
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


fun getColumnModifierDependsOnCurProductCardType(curProductCartType: String): Modifier {
    return when (curProductCartType) {
        SettingKeys.PRODUCT_CARD_TYPE_STANDARD -> Modifier.padding(PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp))
        else -> Modifier
    }
}