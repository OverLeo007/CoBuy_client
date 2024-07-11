package ru.hihit.cobuy.api.products

import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String
)

@Serializable
data class UpdateProductRequest(
    val name: String,
    val description: String?,
    val status: Int?
)