package ru.hihit.cobuy.pusher

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener

class ChannelsPool(
    private val pusher: Pusher,
    private val channels: MutableMap<String, ChannelImpl> = mutableMapOf(),
    private val listenerPaths: MutableMap<String, ListenerPath> = mutableMapOf()
) {
    fun listen(
        channelName: String,
        eventName: String,
        listenerName: String = "Anonymous listener",
        onEvent: (PusherEvent) -> Unit,
        onError: (String?, java.lang.Exception?) -> Unit
    ) {
        val path = ListenerPath(channelName, eventName, listenerName)
        Log.d("PusherService", "Add listener $path")

        val channel = channels[channelName] ?: ChannelImpl(pusher.subscribe(channelName)).also {
            channels[channelName] = it
        }

        val event = channel.events[eventName] ?: EventImpl().also {
            channel.value.bind(eventName, it.pusherListener)
            channel.events[eventName] = it
        }

        event.listeners[listenerName] ?: EventListener(onEvent, onError).also {
            event.listeners[listenerName] = it
            listenerPaths[listenerName] = path
        }

    }

    fun unlisten(
        listenerName: String = "Anonymous listener"
    ) {
        val path = listenerPaths[listenerName] ?: return

        Log.d("PusherService", "Removing listener: $path")

        val channel = channels[path.channelName] ?: return
        val event = channel.events[path.eventName] ?: return


        event.listeners[listenerName]?.let {
            event.listeners.remove(listenerName)
        }
        if (event.listeners.isEmpty()) {
            channel.value.unbind(path.eventName, event.pusherListener)
            channel.events.remove(path.eventName)
        }
        if (channel.events.isEmpty()) {
            pusher.unsubscribe(path.channelName)
            channels.remove(path.channelName)
        }
    }

    fun unlistenAll() {
        Log.d("PusherService", "Unsubscribing from all channels")
        channels.forEach { (channelName, channel) ->
            channel.events.forEach { (eventName, event) ->
                event.listeners.clear()
                channel.value.unbind(eventName, event.pusherListener)
            }
            pusher.unsubscribe(channelName)
        }
        channels.clear()
    }
}

data class ChannelImpl(
    val value: Channel,
    val events: MutableMap<String, EventImpl> = mutableMapOf()
)

data class EventImpl(
    val listeners: MutableMap<String, EventListener> = mutableMapOf(),
    val pusherListener: SubscriptionEventListener = object : SubscriptionEventListener {
        override fun onEvent(event: PusherEvent) {
            Log.d("ChannelsPool", "Received event: $event")
            listeners.forEach { it.value.onEvent(event) }
        }

        override fun onError(message: String?, e: java.lang.Exception?) {
            Log.d("ChannelsPool", "Error on event: $message")
            listeners.forEach { it.value.onError(message, e) }
        }
    }
)

data class EventListener(
    val onEvent: (PusherEvent) -> Unit,
    val onError: (String?, Exception?) -> Unit
)

data class ListenerPath(
    val channelName: String,
    val eventName: String,
    val listenerName: String
) {
    override fun toString(): String {
        return "$channelName->$eventName->$listenerName"
    }
}