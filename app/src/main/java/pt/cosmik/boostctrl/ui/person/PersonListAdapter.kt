package pt.cosmik.boostctrl.ui.person

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.utils.Constants
import java.util.concurrent.TimeUnit

class PersonListAdapter: RecyclerView.Adapter<PersonListAdapter.ViewHolder>() {

    private var items: List<PersonDetailListItemDescriptor> = listOf()
    private val itemClickSubject = PublishSubject.create<PersonDetailListItemDescriptor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_title_value_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        holder.titleText.text = item.title
        holder.valueText.text = item.value
    }

    override fun getItemCount() = items.size

    fun onItemClickEvent(): Observable<PersonDetailListItemDescriptor> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    fun setItems(items: List<PersonDetailListItemDescriptor>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.text_title)
        val valueText: TextView = itemView.findViewById(R.id.text_value)
    }
}

class PersonDetailListItemDescriptor(
    val title: String?,
    val value: String?
)