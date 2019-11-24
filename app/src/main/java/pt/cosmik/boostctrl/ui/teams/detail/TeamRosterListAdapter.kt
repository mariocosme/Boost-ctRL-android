package pt.cosmik.boostctrl.ui.teams.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.utils.Constants
import java.util.concurrent.TimeUnit

class TeamRosterListAdapter(var context: Context? = null): RecyclerView.Adapter<TeamRosterListAdapter.ViewHolder>() {

    private var items: List<TeamRosterPlayerListItemDescriptor> = listOf()
    val itemClickSubject = PublishSubject.create<TeamRosterPlayerListItemDescriptor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_team_roster_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        holder.playerNameText.text = item.name
        holder.joinDateText.text = item.joinDate
        context?.let { context -> Glide.with(context).load(item.countryFlag).into(holder.countryImage) }

    }

    override fun getItemCount() = items.size

    fun onItemClickEvent(): Observable<TeamRosterPlayerListItemDescriptor> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    fun setItems(items: List<TeamRosterPlayerListItemDescriptor>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val playerNameText: TextView = itemView.findViewById(R.id.text_player_nickname)
        val joinDateText: TextView = itemView.findViewById(R.id.text_join_date)
        val countryImage: ImageView = itemView.findViewById(R.id.country_image)
    }
}

class TeamRosterPlayerListItemDescriptor(
    val countryFlag: String?,
    val name: String?,
    val playerNickname: String?,
    val joinDate: String?
)