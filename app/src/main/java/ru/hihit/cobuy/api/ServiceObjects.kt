package ru.hihit.cobuy.api

import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.auth.AuthApiService
import ru.hihit.cobuy.api.groups.GroupsApiService
import ru.hihit.cobuy.api.lists.ListsApiService
import ru.hihit.cobuy.api.misc.MiscApiService
import ru.hihit.cobuy.api.products.ProductsApiService


object Auth {
    val api: AuthApiService by lazy {
        App.getRetrofit().create(AuthApiService::class.java)
    }
}

object Groups {
    val api: GroupsApiService by lazy {
        App.getRetrofit().create(GroupsApiService::class.java)
    }
}

object Lists {
    val api: ListsApiService by lazy {
        App.getRetrofit().create(ListsApiService::class.java)
    }
}

object Products {
    val api: ProductsApiService by lazy {
        App.getRetrofit().create(ProductsApiService::class.java)
    }
}

object Misc {
    val api: MiscApiService by lazy {
        App.getRetrofit().create(MiscApiService::class.java)
    }
}
