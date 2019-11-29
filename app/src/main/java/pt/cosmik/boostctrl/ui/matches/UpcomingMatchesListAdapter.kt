package pt.cosmik.boostctrl.ui.matches

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.UpcomingMatch
import pt.cosmik.boostctrl.utils.Constants
import pt.cosmik.boostctrl.utils.DateUtils
import java.util.concurrent.TimeUnit

class UpcomingMatchesListAdapter(var context: Context?): RecyclerView.Adapter<UpcomingMatchesListAdapter.ViewHolder>() {

    private var items: List<UpcomingMatch> = listOf()
    val itemClickSubject = PublishSubject.create<UpcomingMatch>()
    private var disposables = CompositeDisposable()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_ongoing_or_upcoming_match_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        holder.homeTeamText.text = item.homeTeam?.name
        holder.awayTeamText.text = item.awayTeam?.name
        holder.dateTimeText.text = DateUtils.getDateFormatter(DateUtils.patternWithHourMinuteSeconds).format(item.dateTime)
        holder.tournamentText.text = item.tournamentName
        context?.let {
            Glide.with(it).load(item.homeTeam?.mainImage).into(holder.homeTeamImg)
            Glide.with(it).load(item.awayTeam?.mainImage).into(holder.awayTeamImg)
            Glide.with(it).load(item.tournamentImage).into(holder.tournamentImg)
        }
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
        val tournamentImg: ImageView = itemView.findViewById(R.id.image_view_tournament)
        val tournamentText: TextView = itemView.findViewById(R.id.text_tournament_name)
    }

}