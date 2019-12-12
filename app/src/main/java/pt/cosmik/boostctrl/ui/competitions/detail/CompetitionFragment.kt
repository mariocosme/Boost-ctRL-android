package pt.cosmik.boostctrl.ui.competitions.detail

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Competition
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.common.KeyValueListAdapter
import pt.cosmik.boostctrl.ui.teams.detail.TeamFragmentDirections
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics


class CompetitionFragment : BaseFragment() {

    private val vm: CompetitionViewModel by viewModel()

    private var loadingBar: ProgressBar? = null
    private var imageView: ImageView? = null
    private var competitionDescription: TextView? = null
    private var standingsText: TextView? = null

    private var dividerItemDeco: DividerItemDecoration? = null
    private var competitionGeneralDetailsRecyclerView: RecyclerView? = null
    private val competitionGeneralDetailsListAdapter = KeyValueListAdapter()
    private var competitionStandingsRecyclerView: RecyclerView? = null
    private val competitionStandingsListAdapter = CompetitionStandingsListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.competition_detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.brackets_item) vm.processEvent(CompetitionViewModel.CompetitionFragmentEvent.SelectedBracketsMenuItem)
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_competition_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)
        imageView = view.findViewById(R.id.image_view)
        competitionDescription = view.findViewById(R.id.text_competition_desc)
        standingsText = view.findViewById(R.id.text_competition_standings_title)

        dividerItemDeco = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        context?.let { context ->
            ContextCompat.getDrawable(context, R.drawable.bg_list_news_item_separator)?.let { dividerItemDeco?.setDrawable(it) }
        }

        competitionGeneralDetailsListAdapter.context = context
        competitionGeneralDetailsRecyclerView = view.findViewById<RecyclerView>(R.id.competition_general_details_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = competitionGeneralDetailsListAdapter
        }

        competitionStandingsListAdapter.context = context
        competitionStandingsRecyclerView = view.findViewById<RecyclerView>(R.id.competition_standings_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = competitionStandingsListAdapter
        }

        disposables.add(competitionStandingsListAdapter.onItemClickEvent().subscribe {
            vm.processEvent(CompetitionViewModel.CompetitionFragmentEvent.SelectedTeamItem(it))
        })

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE

            it.barTitle?.let { barTitle -> (activity as MainActivity).setActionBarTitle(barTitle) }
            it.competitionGeneralDetailItems?.let { items -> competitionGeneralDetailsListAdapter.setItems(items) }
            it.competitionStandingItems?.let { items ->
                if (items.isNotEmpty()) {
                    standingsText!!.visibility = View.VISIBLE
                    competitionStandingsRecyclerView?.visibility = View.VISIBLE
                    competitionStandingsListAdapter.setStandingItemDescriptors(items)
                }
            }
            if (it.competitionImage != null && imageView != null) {
                imageView!!.visibility = View.VISIBLE
                Glide.with(context!!).load(it.competitionImage).into(imageView!!)
            }
            competitionDescription?.text = it.competitionDescription
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is CompetitionViewModel.CompetitionFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is CompetitionViewModel.CompetitionFragmentViewEffect.PresentTeamFragment -> findNavController().navigate(TeamFragmentDirections.actionGlobalTeamDetailFragment(it.team))
                is CompetitionViewModel.CompetitionFragmentViewEffect.PresentBracketsFragment -> findNavController().navigate(CompetitionFragmentDirections.actionCompetitionDetailFragmentToBracketsFragment2(it.competitionId))
            }
        })

        vm.processEvent(CompetitionViewModel.CompetitionFragmentEvent.ViewCreated(arguments?.get("competition") as? Competition, context))
    }

    override fun onResume() {
        super.onResume()
        vm.viewState.value?.barTitle?.let { BoostCtrlAnalytics.instance.trackScreen("TeamFragment: $it") }
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.competition_detail) ?: ""

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
        vm.viewState.removeObservers(this)
        vm.viewEffect.removeObservers(this)
    }
}

