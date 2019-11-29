package pt.cosmik.boostctrl.ui.teams

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SectionIndexer
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.utils.Constants
import java.util.concurrent.TimeUnit


class TeamsListAdapter(var context: Context?): RecyclerView.Adapter<TeamsListAdapter.ViewHolder>(), SectionIndexer {

    private var items: List<Team> = listOf()
    private var sectionPositions: List<Int> = listOf()

    val itemClickSubject = PublishSubject.create<Team>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_team_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener { itemClickSubject.onNext(item) }
        holder.teamText.text = item.name
        context?.let { Glide.with(it).load(item.mainImage).into(holder.teamImg) }
    }

    override fun getItemCount() = items.size

    override fun getSectionForPosition(p0: Int): Int = 0

    override fun getPositionForSection(p0: Int): Int = sectionPositions[p0]

    fun onItemClickEvent(): Observable<Team> = itemClickSubject.throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS)

    override fun getSections(): Array<String>? {
        val sections = mutableListOf<Char>()
        val tmpSectionPositions = mutableListOf<Int>()
        var i = 0
        val size = items.size
        while (i < size) {
            val section = items[i].name[0].toUpperCase()
            if (!sections.contains(section)) {
                sections.add(section)
                tmpSectionPositions.add(i)
            }
            i++
        }
        sectionPositions = tmpSectionPositions


        return sections.map { it.toString() }.toTypedArray()
    }

    fun setTeamItems(items: List<Team>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val teamImg: ImageView = itemView.findViewById(R.id.image_view_logo)
        val teamText: TextView = itemView.findViewById(R.id.text_team)
    }

}