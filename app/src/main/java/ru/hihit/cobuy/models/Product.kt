package ru.hihit.cobuy.models

data class Product(
    val id: Int = 0,
    val name: String = "default item name",
    val description: String = "default item description",
    var status: ProductStatus = ProductStatus.NONE,
    val buyer: User? = null,
)
