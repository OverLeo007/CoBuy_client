package ru.hihit.cobuy.api.lists

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ListsApiService {

    @POST("api/list")
    suspend fun createList(@Body request: CreateListRequest): Response<CreateListResponse>

    @GET("api/list")
    suspend fun getLists(@Query("group_id") groupId: Int?): Response<GetListsResponse>

    @GET("api/list/{id}")
    suspend fun getListById(@Path("id") id: Int): Response<GetUpdateListResponse>

    @PUT("api/list/{id}")
    suspend fun updateList(@Path("id") id: Int, @Body request: UpdateListRequest): Response<GetUpdateListResponse>

    @DELETE("api/list/{id}")
    suspend fun deleteList(@Path("id") id: Int): Response<Unit>

}