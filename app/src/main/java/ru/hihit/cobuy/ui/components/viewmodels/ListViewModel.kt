package ru.hihit.cobuy.ui.components.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ru.hihit.cobuy.api.ListData
import ru.hihit.cobuy.api.ListRequester
import ru.hihit.cobuy.api.ProductData
import ru.hihit.cobuy.api.ProductRequester
import ru.hihit.cobuy.api.lists.UpdateListRequest
import ru.hihit.cobuy.api.products.CreateProductRequest
import ru.hihit.cobuy.api.products.UpdateProductRequest
import ru.hihit.cobuy.models.Product
import ru.hihit.cobuy.models.ProductList

class ListViewModel(private val listId: Int) : ViewModel() {
    var products: MutableStateFlow<List<ProductData>> = MutableStateFlow(emptyList())
    var isListLoading = mutableStateOf(true)
    var list: MutableStateFlow<ListData> = MutableStateFlow(ListData(0, "", 0, 0, 0))
    var isRefreshing = mutableStateOf(false)

    init {
        updateAll()
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

    fun onProductAdded(product: ProductData) {
        ProductRequester.createProduct(listId,
            CreateProductRequest(product.name, product.description),
            callback = { response ->
                response?.data?.let {
                    val curProducts = products.value.toMutableList()
                    curProducts.add(it)
                    products.value = curProducts
                }
                Log.d("ListViewModel", "onProductAdded: $response")
            },
            onError = { code, body ->
                Log.e("ListViewModel", "onProductAdded: $code $body")
            }
        )
        Log.d("ListViewModel", "onProductAdded: $product")

    }

    fun onProductStatusChanged(product: ProductData) {
        products.value.find { it.id == product.id }?.let {
            it.status = product.status
        }

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
            UpdateProductRequest(product.name, product.description, product.status),
            callback = { response ->
                Log.d("ListViewModel", "onProductDeleted: $response")
            },
            onError = { code, body ->
                Log.e("ListViewModel", "onProductDeleted: $code $body")
            }
        )
        Log.d("ListViewModel", "onProductEdited: $product")
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
            },
            onError = { code, body ->
                Log.e("ListViewModel", "getList: $code $body")
                isListLoading.value = false
            }
        )
    }
}