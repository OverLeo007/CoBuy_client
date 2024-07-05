package ru.hihit.cobuy.models

data class Group(
    val id: Int = 0,
    val name: String,
    val avaUrl: String,
    // TODO: Add users
) {
    companion object {
        fun default(): Group {
            return Group(
                id = 0,
                name = "name",
                avaUrl = "https://sun125-1.userapi.com/s/v1/ig2/AIxZdnOPgs7aVJZn24luWz84Fg1aa2iyzU6GbG-qp1065HTamsIBsBnINypL_PRcXVNEKZP6yZc_9oWq5UciHnW-.jpg?size=50x0&quality=96&crop=0,0,984,984&ava=1",
            )
        }
    }
}

