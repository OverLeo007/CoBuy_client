package ru.hihit.cobuy.api.models

import kotlinx.serialization.Serializable

@Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
data class UserData(
    val id: Int = 0,
    val name: String = "",
    val email: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserData) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }
}