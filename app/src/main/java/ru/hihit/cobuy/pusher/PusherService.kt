import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.SubscriptionEventListener
import ru.hihit.cobuy.BuildConfig

class PusherService {
    private val pusher: Pusher

    init {
        val options = PusherOptions().apply {
            setCluster(BuildConfig.PUSHER_CLUSTER)
        }
        pusher = Pusher(BuildConfig.PUSHER_KEY, options)
        pusher.connect()
    }

    fun subscribeToChannel(channelName: String, eventName: String, listener: SubscriptionEventListener) {
        val channel: Channel = pusher.subscribe(channelName)
        channel.bind(eventName, listener)
    }

    fun unsubscribeFromChannel(channelName: String) {
        pusher.unsubscribe(channelName)
    }
}