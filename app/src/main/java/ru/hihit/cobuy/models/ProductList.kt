package ru.hihit.cobuy.models

data class ProductList(
    var id: Int = 0,
    var name: String  = "default list name",
    val parent: Group = Group.default(),
    var products: List<Product> = emptyList(),
)