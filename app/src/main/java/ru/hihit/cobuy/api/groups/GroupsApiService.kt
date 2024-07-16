package ru.hihit.cobuy.api.groups

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupsApiService {
    @POST("api/group")
    suspend fun createGroup(@Body request: CreateUpdateGroupRequest): Response<CreateGroupResponse>

    @GET("api/group")
    suspend fun getGroups(): Response<GetGroupsResponse>

    @GET("api/group/{id}")
    suspend fun getGroupById(@Path("id") id: Int): Response<GetUpdateGroupResponse>

    @PUT("api/group/{id}")
    suspend fun updateGroup(@Path("id") id: Int, @Body request: CreateUpdateGroupRequest): Response<GetUpdateGroupResponse>

    @DELETE("api/group/{id}")
    suspend fun deleteGroup(@Path("id") id: Int): Response<Unit>

    @POST("api/group/{groupId}/leave")
    suspend fun leaveGroup(@Path("groupId") groupId: Int): Response<Unit>

    @POST("api/group/kick")
    suspend fun kickUser(@Body request: KickUserRequest): Response<Unit>

}