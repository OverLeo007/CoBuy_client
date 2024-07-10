package ru.hihit.cobuy.ui.components.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ru.hihit.cobuy.models.Product
import ru.hihit.cobuy.models.ProductList

class ListViewModel() : ViewModel() {
    var productList: ProductList by mutableStateOf(ProductList())

    fun onNameChanged(newName: String) {
        productList.name = newName
        Log.d("ListViewModel", "onNameChanged: $newName")
    }

    fun onProductAdded(product: Product) {
        productList.products += product
        Log.d("ListViewModel", "onProductAdded: $product")

    }

    fun onProductStatusChanged(product: Product) {
        Log.d("ListViewModel", "onProductStatusChanged: $product")
    }

    fun onProductDeleted(product: Product) {
        Log.d("ListViewModel", "onProductDeleted: $product")
    }

    fun onProductEdited(product: Product) {
        Log.d("ListViewModel", "onProductEdited: $product")
    }
}