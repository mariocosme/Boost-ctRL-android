package pt.cosmik.boostctrl.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.utils.Constants
import pt.cosmik.boostctrl.utils.DateUtils
import java.util.concurrent.TimeUnit

class NewsListAdapter: RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {

    private var items: List<NewsItem> = listOf()
    private val itemClickSubject = PublishSubject.create<NewsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_news_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        holder.titleText.text = item.title
        holder.sourceText.text = "Octane.gg" // TODO: there is only one news source so far

        item.date?.let {
            holder.dateText.text = DateUtils.getDateFormatter(DateUtils.patternCommon).format(it)
        }
    }

    override fun getItemCount() = items.size

    fun onItemClickEvent(): Observable<NewsItem> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    fun setNewsItems(items: List<NewsItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.title_text)
        val sourceText: TextView = itemView.findViewById(R.id.source_text)
        val dateText: TextView = itemView.findViewById(R.id.date_text)
    }
}
