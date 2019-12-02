package pt.cosmik.boostctrl.ui.matches.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.utils.Constants
import java.util.concurrent.TimeUnit

class TeamRostersListAdapter(var context: Context? = null): RecyclerView.Adapter<TeamRostersListAdapter.ViewHolder>() {

    private var items: List<MatchTeamRosterItemDescriptor> = listOf()
    private val itemClickSubject = PublishSubject.create<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_match_roster_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.homePlayerContainer.setOnClickListener { itemClickSubject.onNext(item.homePlayerNickname!!) }
        holder.awayPlayerContainer.setOnClickListener { itemClickSubject.onNext(item.awayPlayerNickname!!) }
        holder.homePlayerNickname.text = item.homePlayerNickname
        holder.awayPlayerNickname.text = item.awayPlayerNickname
        context?.let {
            Glide.with(it).load(item.homePlayerFlag).into(holder.homePlayerCountry)
            Glide.with(it).load(item.awayPlayerFlag).into(holder.awayPlayerCountry)
        }
    }

    override fun getItemCount() = items.size

    fun onItemClickEvent(): Observable<String> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    fun setItems(items: List<MatchTeamRosterItemDescriptor>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val homePlayerCountry: ImageView = itemView.findViewById(R.id.image_view_home_player_country)
        val awayPlayerCountry: ImageView = itemView.findViewById(R.id.image_view_away_player_country)
        val homePlayerNickname: TextView = itemView.findViewById(R.id.text_home_player_nickname)
        val awayPlayerNickname: TextView = itemView.findViewById(R.id.text_away_player_nickname)
        val homePlayerContainer: LinearLayout = itemView.findViewById(R.id.home_player_container)
        val awayPlayerContainer: LinearLayout = itemView.findViewById(R.id.away_player_container)
    }

}

data class MatchTeamRosterItemDescriptor(
    var homePlayerNickname: String? = null,
    var homePlayerFlag: String? = null,
    var awayPlayerNickname: String? = null,
    var awayPlayerFlag: String? = null
)