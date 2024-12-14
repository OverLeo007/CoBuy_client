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
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import okhttp3.MultipartBody
import ru.hihit.cobuy.api.ImageRequester
import ru.hihit.cobuy.api.ListChangedEvent
import ru.hihit.cobuy.api.ListData
import ru.hihit.cobuy.api.ListRequester
import ru.hihit.cobuy.api.ProductChangedEvent
import ru.hihit.cobuy.api.ProductData
import ru.hihit.cobuy.api.ProductRequester
import ru.hihit.cobuy.api.lists.UpdateListRequest
import ru.hihit.cobuy.api.products.CreateProductRequest
import ru.hihit.cobuy.api.products.UpdateProductRequest
import ru.hihit.cobuy.models.EventType
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
                    list.value = eventData.data
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
        } catch (e: SerializationException) {
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
        } catch (e: SerializationException) {
            Log.e("GroupViewModel", "json from ws event is not serializable to GroupChangedEvent: $event")
            return
        }
    }

    private fun onWsError(message: String?, e: Exception?) {
        Log.e(className, "onWsError: $message", e)
    }

    fun onNameChanged(newName: String) {
        list.value.name = newName
        ListRequester.updateList(listId,
            UpdateListRequest(newName),
            callback = { response ->
                Log.d("ListViewModel", "onNameChanged: $response")
            },
            onError = { code, body ->
                Log.e("ListViewModel", "onNameChanged: $code $body")
            }
        )
        Log.d("ListViewModel", "onNameChanged: $newName")
    }

    fun onProductAdded(context: Context, product: ProductData) {
        ProductRequester.createProduct(listId,
            CreateProductRequest(
                product.name,
                product.description,
                product.price,
                product.quantity
            ),
            callback = { response ->
                response?.data?.let {
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
            onError = { code, body ->
                Log.e("ListViewModel", "onProductAdded: $code $body")
            }
        )
//        Log.d("ListViewModel", "onProductAdded: $product")

    }

    fun onProductStatusChanged(product: ProductData) {
        products.value.find { it.id == product.id }?.let {
            it.status = product.status
        }
        ProductRequester.updateProduct(listId, product.id,
            UpdateProductRequest(status = product.status),
            callback = { response ->
                Log.d("ListViewModel", "onProductStatusChanged: $response")
            },
            onError = { code, body ->
                Log.e("ListViewModel", "onProductStatusChanged: $code $body")
            }
        )

        Log.d("ListViewModel", "onProductStatusChanged: $product")
    }

    fun onProductDeleted(product: ProductData) {
        products.value = products.value.filter { it.id != product.id }
        ProductRequester.deleteProduct(listId, product.id,
            callback = { response ->
                Log.d("ListViewModel", "onProductDeleted: $response")
            },
            onError = { code, body ->
                Log.e("ListViewModel", "onProductDeleted: $code $body")
            }
        )
        Log.d("ListViewModel", "onProductDeleted: $product")
    }

    fun onProductEdited(product: ProductData) {
        products.value.find { it.id == product.id }?.let {
            it.name = product.name
            it.description = product.description
        }
        ProductRequester.updateProduct(listId, product.id,
            UpdateProductRequest(
                product.name,
                product.description,
                product.status,
                price = product.price,
                count = product.quantity
            ),
            callback = { response ->
                Log.d("ListViewModel", "onProductEdited: $response")
                updateAll()
            },
            onError = { code, body ->
                Log.e("ListViewModel", "onProductEdited: $code $body")
            }
        )
        Log.d("ListViewModel", "onProductEdited: $product")
    }

    fun onUploadImage(context: Context, product: ProductData) {
        Log.d("ListViewModel", "Uploading image: $product")
        var image: MultipartBody.Part? = null
        product.productImgUrl?.let {
            image = context.getMultipartImageFromUri(it)
        }

        image?.let {
            ImageRequester.uploadProductImage(product.listId, product.id, it,
                callback = { response ->
                    Log.d("ListViewModel", "onUploadImage: $response")
                },
                onError = { code, body ->
                    Log.e("ListViewModel", "onUploadImage: $code $body")
                }
            )
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
        ProductRequester.getProducts(
            listId,
            callback = { response ->
                response?.data?.let {
                    products.value = it
                }
                Log.d("ListViewModel", "getProducts: $response")
            },
            onError = { code, body ->
                Log.e("ListViewModel", "getProducts: $code $body")
            }
        )
    }

    private fun getList() {
        isListLoading.value = true
        ListRequester.getListById(
            listId,
            callback = { response ->
                response?.data?.let {
                    list.value = it
                }
                Log.d("ListViewModel", "getList: $response")
                isListLoading.value = false
                subscribeToList()

            },
            onError = { code, body ->
                Log.e("ListViewModel", "getList: $code $body")
                isListLoading.value = false
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        pusherService.removeListeners(
            className + "ListChanged",
            className + "ProductChanged"
        )
    }
}