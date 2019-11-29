package pt.cosmik.boostctrl.ui.competitions

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.utils.Constants
import java.util.concurrent.TimeUnit


class CompetitionsListAdapter(var context: Context? = null): RecyclerView.Adapter<CompetitionsListAdapter.ViewHolder>() {

    private var items: List<CompetitionDescriptorItem> = listOf()
    private val itemClickSubject = PublishSubject.create<CompetitionDescriptorItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_competition_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }

        holder.itemView.visibility = View.VISIBLE
        holder.textView.text = item.competitionText
        context?.let {
            when (item.competitionDescriptorType) {
                CompetitionDescriptorItemType.COMPETITION_GROUP -> {
                    holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    holder.textView.setTextColor(ContextCompat.getColor(it, R.color.colorRadicalRed))
                }
                CompetitionDescriptorItemType.COMPETITION -> {
                    holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    holder.textView.setTextColor(ContextCompat.getColor(it, R.color.colorIceberg))
                }
                CompetitionDescriptorItemType.SPACER -> holder.itemView.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemCount() = items.size

    fun onItemClickEvent(): Observable<CompetitionDescriptorItem> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    fun setCompetitionItems(items: List<CompetitionDescriptorItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text)
    }

}

data class CompetitionDescriptorItem(
    val competitionDescriptorType: CompetitionDescriptorItemType,
    val competitionText: String? = null,
    val competitionId: String? = null
)

enum class CompetitionDescriptorItemType { COMPETITION, SPACER, COMPETITION_GROUP }