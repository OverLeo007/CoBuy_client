package ru.hihit.cobuy.ui.components.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.pusher.client.channel.PusherEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import okhttp3.MultipartBody
import ru.hihit.cobuy.api.lists.UpdateListRequest
import ru.hihit.cobuy.api.models.ListData
import ru.hihit.cobuy.api.models.ProductData
import ru.hihit.cobuy.api.products.CreateProductRequest
import ru.hihit.cobuy.api.products.UpdateProductRequest
import ru.hihit.cobuy.api.requesters.ImageRequester
import ru.hihit.cobuy.api.requesters.ListRequester
import ru.hihit.cobuy.api.requesters.ProductRequester
import ru.hihit.cobuy.api.requesters.handle
import ru.hihit.cobuy.models.EventType
import ru.hihit.cobuy.pusher.events.ListChangedEvent
import ru.hihit.cobuy.pusher.events.ProductChangedEvent
import ru.hihit.cobuy.ui.components.navigation.Route
import ru.hihit.cobuy.utils.getMultipartImageFromUri

class ListViewModel(
    private val listId: Int,
    private val navHostController: NavHostController
) : PusherViewModel() {
    var products: MutableStateFlow<List<ProductData>> = MutableStateFlow(emptyList())
    var isListLoading = mutableStateOf(true)
    var list: MutableStateFlow<ListData> = MutableStateFlow(ListData(0, "", 0, 0, 0))
    var isRefreshing = mutableStateOf(false)



    init {
        updateAll()
        subscribeToProducts()
    }

    private fun subscribeToList() {
        pusherService.addListener(
            channelName = "list-changed.${list.value.groupId}",
            eventName = "list-changed",
            listenerName = className + "ListChanged",
            onEvent = { onWsListChanged(it) },
            onError = { message, e -> onWsError(message, e) }
        )
    }

    private fun onWsListChanged(event: PusherEvent) {
        Log.d(className, "onWsEvent: $event")
        val eventData: ListChangedEvent
        try {
            eventData = ListChangedEvent.fromJson(event.data)
            Log.d(className, "Got list from ws: $eventData")
            when (eventData.type) {
                EventType.Update -> {
                    if (eventData.data.id == listId) {
                        list.value = eventData.data
                    }
                }

                EventType.Delete -> {
                    viewModelScope.launch {
                        Log.d(className, "List was deleted")
                        try {
                            navHostController.navigateUp()
                        } catch (e: Exception) {
                            Log.e(className, "Can't navigate up: $e")
                            navHostController.navigate(Route.Groups)
                        }
                    }
                }

                EventType.Create -> {
                    Log.w(className, "Its wrong event type for already created list")
                }
            }
        } catch (_: SerializationException) {
            Log.e(className, "json from ws event is not serializable to ListChangedEvent: $event")
            return
        }
    }

    private fun subscribeToProducts() {
        pusherService.addListener(
            "product-changed.${listId}",
            eventName = "product-changed",
            listenerName = className + "ProductChanged",
            onEvent = { onWsProductsChanged(it) },
            onError = { message, e -> onWsError(message, e) }
        )
    }

    private fun onWsProductsChanged(event: PusherEvent) {
        Log.d(className, "onWsProductListChanged$event")
        val eventData: ProductChangedEvent
        try {
            eventData = ProductChangedEvent.fromJson(event.data)
            Log.d(className, "Got product from ws: $eventData")
            when (eventData.type) {
                EventType.Update -> {
                    val curProducts = products.value.toMutableList()
                    val prodIdx = curProducts.indexOfFirst { it.id == eventData.data.id }
                    if (prodIdx != -1) {
                        curProducts[prodIdx] = eventData.data
                        products.value = curProducts
                    }
                }
                EventType.Delete -> {
                    products.value = products.value.filter { it.id != eventData.data.id }
                }
                EventType.Create -> {
                    val curProducts = products.value.toMutableList()
                    if (curProducts.find { it.id == eventData.data.id } == null) {
                        curProducts.add(eventData.data)
                        products.value = curProducts
                    }
                }
            }
        } catch (_: SerializationException) {
            Log.e("GroupViewModel", "json from ws event is not serializable to GroupChangedEvent: $event")
            return
        }
    }

    private fun onWsError(message: String?, e: Exception?) {
        Log.e(className, "onWsError: $message", e)
    }

    fun onNameChanged(newName: String) {
        list.value.name = newName

        viewModelScope.launch {
            ListRequester.updateList(listId, UpdateListRequest(newName))
                .handle(
                    onSuccess = { response ->
                        Log.d("ListViewModel", "onNameChanged: $response")
                    },
                    onServerError = { parsedError ->
                        Log.w("ListViewModel", "onNameChanged: $parsedError")
                    },
                    onOtherError = {
                        Log.w("ListViewModel", "onNameChanged: $it")
                    }
                )
        }
    }

    fun onProductAdded(context: Context, product: ProductData) {

        viewModelScope.launch {
            val result = ProductRequester.createProduct(
                listId,
                CreateProductRequest(
                    product.name,
                    product.description,
                    product.price,
                    product.quantity
                )
            )

            result.handle(
                onSuccess = { response ->
                    response.data.let {
                        if (products.value.find { prod -> prod.id == it.id } != null) {
                            return@let
                        }
                        val curProducts = products.value.toMutableList()
                        it.productImgUrl = product.productImgUrl
                        curProducts.add(it)
                        products.value = curProducts

                        onUploadImage(context, it)
                    }
                    Log.d("ListViewModel", "onProductAdded: $response")
                },
                onServerError = { parsedError ->
                    Log.w("ListViewModel", "onProductAdded: $parsedError")
                },
                onOtherError = {
                    Log.w("ListViewModel", "onProductAdded: $it")
                }
            )
        }
    }

    fun onProductStatusChanged(product: ProductData) {
        products.value.find { it.id == product.id }?.let {
            it.status = product.status
        }

        viewModelScope.launch {
            val result = ProductRequester.updateProduct(
                listId,
                product.id,
                UpdateProductRequest(status = product.status)
            )

            result.handle(
                onSuccess = { response ->
                    Log.d("ListViewModel", "onProductStatusChanged: $response")
                },
                onServerError = { parsedError ->
                    Log.w("ListViewModel", "onProductStatusChanged: $parsedError")
                },
                onOtherError = {
                    Log.w("ListViewModel", "onProductStatusChanged: $it")
                }
            )
        }
    }

    fun onProductDeleted(product: ProductData) {
        products.value = products.value.filter { it.id != product.id }

        viewModelScope.launch {
            val result = ProductRequester.deleteProduct(listId, product.id)

            result.handle(
                onSuccess = { response ->
                    Log.d("ListViewModel", "onProductDeleted: $response")
                },
                onServerError = { parsedError ->
                    Log.w("ListViewModel", "onProductDeleted: $parsedError")
                },
                onOtherError = {
                    Log.w("ListViewModel", "onProductDeleted: $it")
                }
            )
        }
    }

    fun onProductEdited(product: ProductData) {
        products.value.find { it.id == product.id }?.let {
            it.name = product.name
            it.description = product.description
        }

        viewModelScope.launch {
            val result = ProductRequester.updateProduct(
                listId,
                product.id,
                UpdateProductRequest(
                    product.name,
                    product.description,
                    product.status,
                    price = product.price,
                    count = product.quantity
                )
            )

            result.handle(
                onSuccess = { response ->
                    Log.d("ListViewModel", "onProductEdited: $response")
//                updateAll() // FIXME: Надо или нет хммм

                },
                onServerError = { parsedError ->
                    Log.w("ListViewModel", "onProductEdited: $parsedError")
                },
                onOtherError = {
                    Log.w("ListViewModel", "onProductEdited: $it")
                }
            )
        }
    }

    fun onUploadImage(context: Context, product: ProductData) {
        Log.d("ListViewModel", "Uploading image: $product")
        var image: MultipartBody.Part? = null
        product.productImgUrl?.let {
            image = context.getMultipartImageFromUri(it)
        }

        image?.let {
            viewModelScope.launch {
                ImageRequester.uploadProductImage(product.listId, product.id, it)
                    .handle(
                        onSuccess = { response ->
                            Log.d("ListViewModel", "onUploadImage: $response")
                        },
                        onServerError = { parsedError ->
                            Log.w("ListViewModel", "onUploadImage: $parsedError")
                        },
                        onOtherError = {
                            Log.w("ListViewModel", "onUploadImage: $it")
                        }
                    )
            }
        }
    }


    private fun updateAll() {
        viewModelScope.launch {
            isRefreshing.value = true
            val jobs = listOf(
                launch { getList() },
                launch { getProducts() }
            )
            jobs.joinAll()
            isRefreshing.value = false
        }

    }

    fun onRefresh() {
        updateAll()
    }

    private fun getProducts() {

        viewModelScope.launch {
            val result = ProductRequester.getProducts(listId)

            result.handle(
                onSuccess = { response ->
                    Log.d("ListViewModel", "getProducts: $response")
                    products.value = response.data
                },
                onServerError = { parsedError ->
                    Log.w("ListViewModel", "getProducts: $parsedError")
                },
                onOtherError = {
                    Log.w("ListViewModel", "getProducts: $it")
                }
            )
        }
    }

    private fun getList() {
        isListLoading.value = true

        viewModelScope.launch {
            val result = ListRequester.getListById(listId)

            result.handle(
                onSuccess = { response ->
                    Log.d("ListViewModel", "getList: $response")
                    list.value = response.data
                    subscribeToList()
                },
                onServerError = { parsedError ->
                    Log.w("ListViewModel", "getList: $parsedError")
                },
                onOtherError = {
                    Log.w("ListViewModel", "getList: $it")
                },
                finally = {
                    isListLoading.value = false
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        pusherService.removeListeners(
            className + "ListChanged",
            className + "ProductChanged"
        )
    }
}