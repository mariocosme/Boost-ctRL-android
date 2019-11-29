package pt.cosmik.boostctrl.ui.competitions.detail

import android.content.Context
import android.graphics.Color
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

class CompetitionStandingsListAdapter(var context: Context? = null): RecyclerView.Adapter<CompetitionStandingsListAdapter.ViewHolder>() {

    private var items: List<StandingItemDescriptor> = listOf()
    val itemClickSubject = PublishSubject.create<StandingItemDescriptor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_standing_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        holder.indexText.text = item.index
        holder.teamNameText.text = item.teamName
        context?.let { Glide.with(it).load(item.teamImage).into(holder.teamImage) }
        holder.seriesText.text = item.seriesWL
        holder.gamesText.text = item.gamesWL
        holder.gamesDifferenceText.text = item.gamesDifference
        holder.viewColor.setBackgroundColor(Color.parseColor(item.teamColor))
    }

    override fun getItemCount() = items.size

    fun setStandingItemDescriptors(items: List<StandingItemDescriptor>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun onItemClickEvent(): Observable<StandingItemDescriptor> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val indexText: TextView = itemView.findViewById(R.id.index_text)
        val teamNameText: TextView = itemView.findViewById(R.id.team_name_text)
        val teamImage: ImageView = itemView.findViewById(R.id.team_image)
        val seriesText: TextView = itemView.findViewById(R.id.series_text)
        val gamesText: TextView = itemView.findViewById(R.id.games_text)
        val gamesDifferenceText: TextView = itemView.findViewById(R.id.games_difference_text)
        val viewColor: View = itemView.findViewById(R.id.ranking_color)
    }
}

class StandingItemDescriptor {
    var index: String? = null
    var teamName: String? = null
    var teamImage: String? = null
    var seriesWL: String? = null
    var gamesWL: String? = null
    var gamesDifference: String? = null
    var teamColor: String? = null
}