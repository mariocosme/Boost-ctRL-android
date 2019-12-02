package pt.cosmik.boostctrl.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.utils.Constants
import java.util.concurrent.TimeUnit

class SocialsListAdapter(var context: Context? = null): RecyclerView.Adapter<SocialsListAdapter.ViewHolder>() {

    private var items: List<SocialType> = listOf()
    private val itemClickSubject = PublishSubject.create<SocialType>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_social_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        val image = when(item) {
            SocialType.INSTAGRAM -> R.drawable.ic_instagram_logo
            SocialType.FACEBOOK -> R.drawable.ic_facebook_logo
            SocialType.TWITTER -> R.drawable.ic_twitter_logo
            SocialType.STEAM -> R.drawable.ic_steam_logo
            SocialType.DISCORD -> R.drawable.ic_discord_logo
            SocialType.SNAPCHAT -> R.drawable.ic_snapchat_logo
            SocialType.TWITCH -> R.drawable.ic_twitch_logo
            SocialType.YOUTUBE -> R.drawable.ic_youtube_logo
        }
        holder.image.setImageDrawable(ContextCompat.getDrawable(context!!, image))
    }

    override fun getItemCount() = items.size

    fun onItemClickEvent(): Observable<SocialType> = itemClickSubject.throttleFirst(
        Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    fun setItems(items: List<SocialType>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image_view)
    }
}

enum class SocialType {
    INSTAGRAM, FACEBOOK, TWITTER, STEAM, DISCORD, SNAPCHAT, TWITCH, YOUTUBE
}