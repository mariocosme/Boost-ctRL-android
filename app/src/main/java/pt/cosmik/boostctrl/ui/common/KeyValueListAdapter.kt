package pt.cosmik.boostctrl.ui.common

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
import pt.cosmik.boostctrl.utils.Constants
import java.util.concurrent.TimeUnit

class KeyValueListAdapter(var context: Context? = null): RecyclerView.Adapter<KeyValueListAdapter.ViewHolder>() {

    private var items: List<KeyValueListItemDescriptor> = listOf()
    private val itemClickSubject = PublishSubject.create<KeyValueListItemDescriptor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_title_value_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.image.visibility = View.GONE // Not showing the image if there isn't one available
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        holder.titleText.text = item.title
        holder.valueText.text = item.value
        context?.let { context ->
            item.image?.let {
                holder.image.visibility = View.VISIBLE
                Glide.with(context).load(it).into(holder.image)
            }
        }
    }

    override fun getItemCount() = items.size

    fun onItemClickEvent(): Observable<KeyValueListItemDescriptor> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    fun setItems(items: List<KeyValueListItemDescriptor>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.text_title)
        val valueText: TextView = itemView.findViewById(R.id.text_value)
        val image: ImageView = itemView.findViewById(R.id.image)
    }
}

class KeyValueListItemDescriptor(
    val title: String?,
    val value: String?,
    val image: String? = null
)