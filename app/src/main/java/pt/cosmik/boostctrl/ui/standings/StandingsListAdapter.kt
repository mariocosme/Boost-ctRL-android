package pt.cosmik.boostctrl.ui.standings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.TournamentRanking
import pt.cosmik.boostctrl.ui.common.views.BoostCtrlRankingCollapsingView
import pt.cosmik.boostctrl.ui.common.views.RankingItemDescriptor

class StandingsListAdapter: RecyclerView.Adapter<StandingsListAdapter.ViewHolder>() {

    private var items: List<TournamentRanking> = listOf()
    val itemClickSubject = PublishSubject.create<RankingItemDescriptor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_rankings_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.rankingCollapsingView.setTournamentRanking(item)
        holder.rankingCollapsingView.didSelectItemDescriptor = {
            itemClickSubject.onNext(it)
        }
    }

    override fun getItemCount() = items.size

    fun setRankingItems(items: List<TournamentRanking>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rankingCollapsingView: BoostCtrlRankingCollapsingView = itemView.findViewById(R.id.ranking_view)
    }

}
