package ru.hihit.cobuy.models

data class Group(
    val id: Int = 0,
    var name: String,
    var avaUrl: String,
    var inviteLink: String = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
    var owner: User = User.default(),
    var members: List<User> = emptyList(),
    var membersCount: Int = 0,
    var listsCount: Int = 0
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

