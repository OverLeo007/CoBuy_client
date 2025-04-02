package ru.hihit.cobuy.api.requesters

import okhttp3.ResponseBody
import ru.hihit.cobuy.api.Api
import ru.hihit.cobuy.api.misc.InvitationStatusResponse
import ru.hihit.cobuy.api.misc.InviteTokenResponse
import ru.hihit.cobuy.api.requesters.RequestLauncher.launchRequest

object MiscRequester {
    fun getInviteToken(
        groupId: Int,
        callback: (InviteTokenResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.misc.getInviteToken(groupId) },
            callback = callback,
            errorMessage = "Get Invite Token Error",
            onError = onError
        )
    }

    fun acceptInvitation(
        token: String,
        callback: (InvitationStatusResponse?) -> Unit,
        onError: (Int, ResponseBody?) -> Unit
    ) {
        launchRequest(
            request = { Api.misc.acceptInvitation(token) },
            callback = callback,
            errorMessage = "Accept Invitation Error",
            onError = onError
        )
    }
}