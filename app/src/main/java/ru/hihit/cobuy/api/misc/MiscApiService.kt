package ru.hihit.cobuy.api.misc

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MiscApiService {
    @GET("api/invite/{group_id}")
    suspend fun getInviteToken(@Path("group_id") groupId: Int): Response<InviteTokenResponse>

    @GET("api/invite")
    suspend fun acceptInvitation(@Query("token") token: String): Response<InvitationStatusResponse>
}