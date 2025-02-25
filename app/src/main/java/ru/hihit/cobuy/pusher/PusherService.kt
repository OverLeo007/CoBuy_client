package ru.hihit.cobuy.pusher

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionState
import ru.hihit.cobuy.BuildConfig
import java.lang.Exception

class PusherService {
    private val pusher: Pusher
    private val channelsPool: ChannelsPool

    init {
        val options = PusherOptions().apply {
            setCluster(BuildConfig.PUSHER_CLUSTER)
        }
        pusher = Pusher(BuildConfig.PUSHER_KEY, options)
        pusher.connect()
        channelsPool = ChannelsPool(pusher)
    }

    fun addListener(
        channelName: String,
        eventName: String,
        listenerName: String = "Anonymous listener",
        onEvent: (PusherEvent) -> Unit,
        onError: (String?, Exception?) -> Unit
    ) {


        channelsPool.listen(channelName, eventName, listenerName, onEvent, onError)
    }



    fun isPusherConnected() {
        Log.d(
            "PusherService",
            "Checking if Pusher is connected: ${pusher.connection.state == ConnectionState.CONNECTED}"
        )
    }

    fun removeListener(listenerName: String) {
        channelsPool.unlisten(listenerName)
    }

    fun removeListeners(vararg listenerNames: String) {
        listenerNames.forEach { listenerName ->
            channelsPool.unlisten(listenerName)
        }
    }

    fun close() {
        channelsPool.unlistenAll()
        pusher.disconnect()
    }
}