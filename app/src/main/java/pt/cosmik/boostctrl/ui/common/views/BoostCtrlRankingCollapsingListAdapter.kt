package pt.cosmik.boostctrl.ui.common.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R

class BoostCtrlRankingCollapsingListAdapter(var context: Context?): RecyclerView.Adapter<BoostCtrlRankingCollapsingListAdapter.ViewHolder>() {

    private var items: List<RankingItemDescriptor> = listOf()
    val itemClickSubject = PublishSubject.create<RankingItemDescriptor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_ranking_item, parent, false))
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
    }

    override fun getItemCount() = items.size

    fun setRankingItemDescriptors(items: List<RankingItemDescriptor>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val indexText: TextView = itemView.findViewById(R.id.index_text)
        val teamNameText: TextView = itemView.findViewById(R.id.team_name_text)
        val teamImage: ImageView = itemView.findViewById(R.id.team_image)
        val seriesText: TextView = itemView.findViewById(R.id.series_text)
        val gamesText: TextView = itemView.findViewById(R.id.games_text)
        val gamesDifferenceText: TextView = itemView.findViewById(R.id.games_difference_text)
    }
}

class RankingItemDescriptor {
    var index: String? = null
    var teamName: String? = null
    var teamImage: String? = null
    var seriesWL: String? = null
    var gamesWL: String? = null
    var gamesDifference: String? = null
}