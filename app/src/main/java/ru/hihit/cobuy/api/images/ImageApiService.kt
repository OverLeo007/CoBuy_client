package ru.hihit.cobuy.api.images

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImagesApiService {
    @GET("api/group/{groupId}/getImage")
    suspend fun getGroupImage(@Path("groupId") groupId: Int): Response<GetGroupImageResponse>

    @Multipart
    @POST("api/group/{groupId}/setImage")
    suspend fun setGroupImage(
        @Path("groupId") groupId: Int,
        @Part image: MultipartBody.Part
    ): Response<GetGroupImageResponse>


    @GET("api/list/{listId}/product/{productId}/getImage")
    suspend fun getProductImage(
        @Path("listId") listId: Int,
        @Path("productId") productId: Int
    ): Response<GetProductImageResponse>

    @Multipart
    @POST("api/list/{listId}/product/{productId}/setImage")
    suspend fun setProductImage(
        @Path("listId") listId: Int,
        @Path("productId") productId: Int,
        @Part image: MultipartBody.Part
    ): Response<GetProductImageResponse>
}