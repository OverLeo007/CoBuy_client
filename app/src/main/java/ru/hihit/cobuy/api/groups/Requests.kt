package ru.hihit.cobuy.api.groups

import kotlinx.serialization.Serializable

@Serializable
data class CreateUpdateGroupRequest(
    val name: String,
)
