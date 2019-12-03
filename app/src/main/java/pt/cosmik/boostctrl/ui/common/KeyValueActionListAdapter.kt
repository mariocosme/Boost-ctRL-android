package pt.cosmik.boostctrl.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.utils.Constants
import java.util.concurrent.TimeUnit

class KeyValueActionListAdapter(var context: Context? = null): RecyclerView.Adapter<KeyValueActionListAdapter.ViewHolder>() {

    private var items: List<KeyValueActionListItemDescriptor> = listOf()
    private val itemClickSubject = PublishSubject.create<KeyValueActionListItemDescriptor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_title_value_action_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        holder.titleText.text = item.title
        context?.let { context ->
            item.image?.let {
                holder.image.setImageDrawable(ContextCompat.getDrawable(context, it))
            }
        }
    }

    override fun getItemCount() = items.size

    fun onItemClickEvent(): Observable<KeyValueActionListItemDescriptor> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    fun setItems(items: List<KeyValueActionListItemDescriptor>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.text_title)
        val image: ImageView = itemView.findViewById(R.id.image)
    }

}

class KeyValueActionListItemDescriptor(
    val title: String?,
    val image: Int? = null
)