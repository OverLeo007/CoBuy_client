package ru.hihit.cobuy.pusher

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionState
import ru.hihit.cobuy.BuildConfig
import java.lang.Exception

class PusherService {
    private val pusher: Pusher

    init {
        val options = PusherOptions().apply {
            setCluster(BuildConfig.PUSHER_CLUSTER)
//            setChannelAuthorizer()
        }
        pusher = Pusher(BuildConfig.PUSHER_KEY, options)
        pusher.connect()
    }

    fun subscribeToChannel(
        channelName: String,
        eventName: String,
        onEvent: (PusherEvent) -> Unit,
        onError: (String?, Exception?) -> Unit
        ) {
        Log.d("PusherService", "Subscribing to channel $channelName")
        val channel: Channel = pusher.subscribe(channelName)
        val listener: SubscriptionEventListener = object : SubscriptionEventListener {
            override fun onEvent(event: PusherEvent) {
                Log.d("PusherService", "Received event: $event")
                onEvent(event)
            }

            override fun onError(message: String?, e: Exception?) {
                Log.d("PusherService", "Error: $message")
                onError(message, e)
            }
        }
        channel.bind(eventName, listener)
        Log.d("PusherService", "Subscribed to channel? ${channel.isSubscribed}")
    }

    fun isPusherConnected(): Unit {
        Log.d("PusherService", "Checking if Pusher is connected: ${pusher.connection.state == ConnectionState.CONNECTED}")
    }

    fun unsubscribeFromChannel(channelName: String) {
        Log.d("PusherService", "Unsubscribing from channel $channelName")
        pusher.unsubscribe(channelName)
    }
}