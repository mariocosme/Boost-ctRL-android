package pt.cosmik.boostctrl.ui.matches

import android.annotation.SuppressLint
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
import pt.cosmik.boostctrl.models.UpcomingMatch
import pt.cosmik.boostctrl.utils.Constants
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UpcomingMatchesListAdapter(var context: Context? = null): RecyclerView.Adapter<UpcomingMatchesListAdapter.ViewHolder>() {

    private var items: List<UpcomingMatch> = listOf()
    private val itemClickSubject = PublishSubject.create<UpcomingMatch>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_ongoing_or_upcoming_match_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        holder.homeTeamText.text = item.homeTeam?.name
        holder.awayTeamText.text = item.awayTeam?.name
        holder.tournamentText.text = item.tournamentName
        context?.let {
            Glide.with(it).load(item.homeTeam?.mainImage).into(holder.homeTeamImg)
            Glide.with(it).load(item.awayTeam?.mainImage).into(holder.awayTeamImg)
            Glide.with(it).load(item.tournamentImage).into(holder.tournamentImg)
        }

        if (item.dateTime.before(Date())) {
            holder.liveText.visibility = View.VISIBLE
            holder.dateTimeText.visibility = View.INVISIBLE
        }
        else {
            holder.liveText.visibility = View.INVISIBLE
            holder.dateTimeText.visibility = View.VISIBLE
        }
        holder.dateTimeText.text = "${DateFormat.getDateInstance(DateFormat.SHORT).format(item.dateTime)} ${DateFormat.getTimeInstance(DateFormat.SHORT).format(item.dateTime)}" // 12/01/2019 17:45
    }

    override fun getItemCount() = items.size

    fun onItemClickEvent(): Observable<UpcomingMatch> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    fun setMatchesItems(items: List<UpcomingMatch>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val homeTeamImg: ImageView = itemView.findViewById(R.id.image_view_home_team)
        val homeTeamText: TextView = itemView.findViewById(R.id.text_home_team)
        val awayTeamText: TextView = itemView.findViewById(R.id.text_away_team)
        val awayTeamImg: ImageView = itemView.findViewById(R.id.image_view_away_team)
        val dateTimeText: TextView = itemView.findViewById(R.id.text_date_time)
        val liveText: TextView = itemView.findViewById(R.id.text_live)
        val tournamentImg: ImageView = itemView.findViewById(R.id.image_view_tournament)
        val tournamentText: TextView = itemView.findViewById(R.id.text_tournament_name)
    }

}