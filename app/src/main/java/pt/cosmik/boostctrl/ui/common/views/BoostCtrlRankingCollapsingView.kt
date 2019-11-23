package pt.cosmik.boostctrl.ui.common.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.TournamentRanking

class BoostCtrlRankingCollapsingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var disposables = CompositeDisposable()

    private var titleHolder: ConstraintLayout? = null
    private var titleText: TextView? = null

    private var recyclerView: RecyclerView? = null
    private val listAdapter = BoostCtrlRankingCollapsingListAdapter(null)
    var didSelectItemDescriptor: ((descriptor: RankingItemDescriptor) -> Unit)? = null

    init {
        inflate(context, R.layout.view_ranking_collapsing, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        titleText = findViewById(R.id.title_text)
        titleHolder = findViewById(R.id.title_holder)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            listAdapter.context = context
            adapter = listAdapter
        }

        disposables.add(listAdapter.itemClickSubject.subscribe {
            didSelectItemDescriptor?.invoke(it)
        })
    }

    fun setTournamentRanking(ranking: TournamentRanking) {
        titleText?.text = "${ranking.tournamentName} (${ranking.region.getRegionAbbreviation()})"
        listAdapter.setRankingItemDescriptors(generateRankingItemDescriptors(ranking))
    }

    private fun generateRankingItemDescriptors(ranking: TournamentRanking): List<RankingItemDescriptor> {
        val items = mutableListOf<RankingItemDescriptor>()
        ranking.rankings?.teams?.forEachIndexed { index, team ->
            val item = RankingItemDescriptor()
            item.index = "${index+1}."
            item.teamName = team
            item.teamImage = ranking.rankings.teamImages?.get(index)
            item.seriesWL = ranking.rankings.winLoss?.get(index)
            item.gamesDifference = ranking.rankings.gamesDifference?.get(index)
            item.gamesWL = ranking.rankings.gamesWinLoss?.get(index)
            item.teamColor = ranking.rankings.colors?.get(index)
            items.add(item)
        }
        return items
    }

}