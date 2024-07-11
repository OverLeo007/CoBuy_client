package ru.hihit.cobuy.api.products

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ProductsApiService {

    @POST("api/list/{list_id}/product")
    suspend fun createProduct(@Path("list_id") listId: Int, @Body request: CreateProductRequest): Response<CreateProductResponse>

    @GET("api/list/{list_id}/product")
    suspend fun getProducts(@Path("list_id") listId: Int): Response<GetProductsResponse>

    @GET("api/list/{list_id}/product/{id}")
    suspend fun getProductById(@Path("list_id") listId: Int, @Path("id") id: Int): Response<GetUpdateProductResponse>

    @PUT("api/list/{list_id}/product/{id}")
    suspend fun updateProduct(@Path("list_id") listId: Int, @Path("id") id: Int, @Body request: UpdateProductRequest): Response<GetUpdateProductResponse>

    @DELETE("api/list/{list_id}/product/{id}")
    suspend fun deleteProduct(@Path("list_id") listId: Int, @Path("id") id: Int): Response<Unit>

}