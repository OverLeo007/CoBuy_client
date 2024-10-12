package ru.hihit.cobuy.api

import ru.hihit.cobuy.App
import ru.hihit.cobuy.api.auth.AuthApiService
import ru.hihit.cobuy.api.groups.GroupsApiService
import ru.hihit.cobuy.api.images.ImagesApiService
import ru.hihit.cobuy.api.lists.ListsApiService
import ru.hihit.cobuy.api.misc.MiscApiService
import ru.hihit.cobuy.api.products.ProductsApiService


object Api {
    val auth: AuthApiService by lazy {
        App.getRetrofit().create(AuthApiService::class.java)
    }

    val groups: GroupsApiService by lazy {
        App.getRetrofit().create(GroupsApiService::class.java)
    }

    val lists: ListsApiService by lazy {
        App.getRetrofit().create(ListsApiService::class.java)
    }

    val products: ProductsApiService by lazy {
        App.getRetrofit().create(ProductsApiService::class.java)
    }

    val misc: MiscApiService by lazy {
        App.getRetrofit().create(MiscApiService::class.java)
    }

    val images: ImagesApiService by lazy {
        App.getRetrofit().create(ImagesApiService::class.java)
    }

}