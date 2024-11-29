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
//            setChannelAuthorizer()
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
//        Log.d("PusherService", "Subscribing to channel $channelName")
//        var channel: Channel
//        try {
//            channel = pusher.subscribe(channelName)
//        } catch (e: IllegalArgumentException) {
//            Log.e("PusherService", "Error subscribing to channel $channelName: $e")
//            unsubscribeFromChannel(channelName)
//            Log.d("PusherService", "Trying to subscribe to channel $channelName again")
//            channel = pusher.subscribe(channelName)
//        }
//        val listener: SubscriptionEventListener = object : SubscriptionEventListener {
//            override fun onEvent(event: PusherEvent) {
//                Log.d("PusherService", "Received event: $event")
//                onEvent(event)
//            }
//
//            override fun onError(message: String?, e: Exception?) {
//                Log.d("PusherService", "Error: $message")
//                onError(message, e)
//            }
//        }
//        channel.bind(eventName, listener)
//        Log.d("PusherService", "Subscribed to channel? ${channel.isSubscribed}")

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

    fun close() {
        channelsPool.unlistenAll()
        pusher.disconnect()
    }
}