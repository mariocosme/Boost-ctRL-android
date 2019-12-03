package pt.cosmik.boostctrl.ui.matches.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.UpcomingMatch
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.common.KeyValueActionListAdapter
import pt.cosmik.boostctrl.ui.common.KeyValueListAdapter
import pt.cosmik.boostctrl.ui.common.VerticalSpaceItemDecoration
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics


class MatchFragment : BaseFragment() {

    private val vm: MatchViewModel by viewModel()

    private var loadingBar: ProgressBar? = null
    private var homeTeamImg: ImageView? = null
    private var awayTeamImg: ImageView? = null
    private var homeTeamText: TextView? = null
    private var awayTeamText: TextView? = null
    private var liveText: TextView? = null
    private var rostersText: TextView? = null

    private var swipeRefresh: SwipeRefreshLayout? = null

    private var dividerItemDeco: DividerItemDecoration? = null
    private var verticalSpacerDeco = VerticalSpaceItemDecoration()

    private var matchDetailsRecyclerView: RecyclerView? = null
    private val matchDetailsListAdapter = KeyValueListAdapter()
    private var teamRostersRecyclerView: RecyclerView? = null
    private val teamRostersListAdapter = TeamRostersListAdapter()
    private var matchDetailActionsRecyclerView: RecyclerView? = null
    private val matchDetailActionsListAdapter = KeyValueActionListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_match_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)
        homeTeamImg = view.findViewById(R.id.image_view_home_team)
        awayTeamImg = view.findViewById(R.id.image_view_away_team)
        homeTeamText = view.findViewById(R.id.text_home_team)
        awayTeamText = view.findViewById(R.id.text_away_team)
        liveText = view.findViewById(R.id.text_live)
        rostersText = view.findViewById(R.id.match_roster_title)

        dividerItemDeco = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        context?.let { context ->
            ContextCompat.getDrawable(context, R.drawable.bg_list_news_item_separator)?.let { dividerItemDeco?.setDrawable(it) }
        }

        swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)?.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorCloudWhite)
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                isRefreshing = false
                vm.processEvent(MatchViewModel.MatchFragmentEvent.DidTriggerRefresh)
            }
        }

        matchDetailActionsListAdapter.context = context
        matchDetailActionsRecyclerView = view.findViewById<RecyclerView>(R.id.match_detail_actions_recycler_view)?.apply {
            setHasFixedSize(true)
            addItemDecoration(verticalSpacerDeco)
            layoutManager = LinearLayoutManager(context)
            adapter = matchDetailActionsListAdapter
        }

        disposables.add(matchDetailActionsListAdapter.onItemClickEvent().subscribe {
            vm.processEvent(MatchViewModel.MatchFragmentEvent.SelectedAction(it))
        })

        matchDetailsListAdapter.context = context
        matchDetailsRecyclerView = view.findViewById<RecyclerView>(R.id.match_details_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = matchDetailsListAdapter
        }

        teamRostersListAdapter.context = context
        teamRostersRecyclerView = view.findViewById<RecyclerView>(R.id.teams_roster_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = teamRostersListAdapter
        }

        disposables.add(teamRostersListAdapter.onItemClickEvent().subscribe {
            vm.processEvent(MatchViewModel.MatchFragmentEvent.SelectedPlayer(it))
        })

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            liveText?.visibility = if (it.isLive) View.VISIBLE else View.INVISIBLE
            it.barTitle?.let { barTitle -> (activity as MainActivity).setActionBarTitle(barTitle) }
            homeTeamText!!.text = it.homeTeamName
            awayTeamText!!.text = it.awayTeamName
            Glide.with(context!!).load(it.homeTeamImage).into(homeTeamImg!!)
            Glide.with(context!!).load(it.awayTeamImage).into(awayTeamImg!!)
            it.matchDetailsItems?.let { items -> matchDetailsListAdapter.setItems(items) }
            it.teamRostersItems?.let { items ->
                if (items.isEmpty()) rostersText?.visibility = View.GONE
                teamRostersListAdapter.setItems(items)
            }
            it.matchActions?.let { items -> matchDetailActionsListAdapter.setItems(items) }
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is MatchViewModel.MatchFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is MatchViewModel.MatchFragmentViewEffect.PresentPersonFragment -> findNavController().navigate(MatchFragmentDirections.actionGlobalPersonFragment(it.person))
                is MatchViewModel.MatchFragmentViewEffect.StartActivity -> startActivity(it.intent)
            }
        })

        vm.processEvent(MatchViewModel.MatchFragmentEvent.ViewCreated(arguments?.get("match") as? UpcomingMatch, context))
    }

    override fun onResume() {
        super.onResume()
        vm.viewState.value?.barTitle?.let { BoostCtrlAnalytics.instance.trackScreen("MatchFragment: $it") }
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.match_detail) ?: ""

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
        vm.viewState.removeObservers(this)
        vm.viewEffect.removeObservers(this)
    }
}

