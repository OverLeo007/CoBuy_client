package ru.hihit.cobuy.pusher

import com.pusher.client.channel.Channel
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import ru.hihit.cobuy.utils.MutablePair
import java.lang.Exception

class PusherManager {

    private val pusherService = PusherService()

    /**
     * ☠️
     * jsonLike structure:
     *  {
     * 	"channel_name": [
     * 		Channel,
     * 		{
     * 			"event_name" : (
     * 			    SubscriptionEventListener,
     * 		        {
     * 				    "handler_name": ("onEventLambda", "onErrorLambda")
     * 			    }
     * 		    )
     * 	    }
     *  ]
     * }
     */
    private val channelsState =
        mutableMapOf<
                String, // channel_name
                MutablePair< // channel and listeners
                        Channel, // channel
                        MutableMap< // event_name and listeners
                                String, // event_name
                                MutablePair< // pusherListener and listeners
                                        SubscriptionEventListener, // pusherListener
                                        MutableMap< // listeners
                                                String, // handler_name
                                                Pair< // onEvent and onError
                                                            (PusherEvent) -> Unit, // onEvent
                                                            (String?, Exception?) -> Unit // onError
                                                        >
                                                >
                                        >
                                >
                        >
                >()


    fun subscribeToChannel(
        channelName: String
    ) {
        if (channelsState.containsKey(channelName)) {
            return
        }
        val channel = pusherService.subscribeToChannel(channelName)
        channelsState[channelName] = MutablePair(channel, mutableMapOf())
    }

    fun unsubscribeFromChannel(channelName: String) {
        pusherService.unsubscribeFromChannel(channelName)
        channelsState.remove(channelName)
    }

    private fun getCurListeners(
        channelName: String,
        eventName: String
    ): MutableMap<String, Pair<(PusherEvent) -> Unit, (String?, Exception?) -> Unit>> {
        val channel: Channel = channelsState.getOrElse(channelName) {
            subscribeToChannel(channelName)
            channelsState[channelName]!!
        }.first

        val listenersMap = channelsState[channelName]!!.second

        val curListeners = listenersMap.getOrElse(eventName) {
            listenersMap[eventName]!!.second = mutableMapOf()
            listenersMap[eventName]!!
        }
        if (curListeners.second.isEmpty()) {
            val listener = object : SubscriptionEventListener {
                override fun onEvent(event: PusherEvent) {
                    curListeners.second.forEach { it.value.first(event) }
                }

                override fun onError(message: String?, e: Exception?) {
                    curListeners.second.forEach { it.value.second(message, e) }
                }
            }
            curListeners.first = listener
            channel.bind(eventName, listener)
        }

        return curListeners.second
    }

    fun subscribeToEvent(
        channelName: String,
        eventName: String,
        handlerName: String,
        onEvent: (PusherEvent) -> Unit,
        onError: (String?, Exception?) -> Unit
    ) {
        getCurListeners(channelName, eventName)[handlerName] = Pair(onEvent, onError)
    }

    fun unsubscribeFromEvent(
        channelName: String,
        eventName: String,
        handlerName: String
    ) {
        channelsState[channelName]?.let {
            val channel = it.first
            it.second[eventName]?.let { events ->
                val listener = events.first
                events.second[handlerName]?.let {handlers ->
                    events.second.remove(handlerName)
                    if (events.second.isEmpty()) {
                        channel.unbind(eventName, listener)
                        it.second.remove(eventName)
                    }
                }
            }
            if (it.second.isEmpty()) {
                unsubscribeFromChannel(channelName)
            }
        }
    }


}