package ru.hihit.cobuy.models

data class User(
    val id: Int,
    val name: String
) {
    companion object {
        fun default(): User {
            return User(
                id = 0,
                name = "default name"
            )

        }
    }
}

