package ru.hihit.cobuy.api.requesters

import okhttp3.ResponseBody
import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.misc.InvitationStatusResponse
import ru.hihit.cobuy.api.misc.InviteTokenResponse
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

object MiscRequester {
    suspend fun getInviteToken(
        groupId: Int
    ): Result<InviteTokenResponse> {
        return launchRequest(
            request = { Api.misc.getInviteToken(groupId) },
            errorMessage = "Get Invite Token Error",
        )
    }

    suspend fun acceptInvitation(
        token: String,
    ): Result<InvitationStatusResponse> {
        return launchRequest(
            request = { Api.misc.acceptInvitation(token) },
            errorMessage = "Accept Invitation Error",
        )
    }
}