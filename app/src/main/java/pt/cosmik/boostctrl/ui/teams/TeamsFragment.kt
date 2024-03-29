package pt.cosmik.boostctrl.ui.teams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.koin.android.viewmodel.ext.android.sharedViewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.teams.detail.TeamFragmentDirections
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics

class TeamsFragment : BaseFragment() {

    private val vm: TeamsViewModel by sharedViewModel()

    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingBar: ProgressBar? = null

    private var recyclerView: RecyclerView? = null
    private val listAdapter = TeamsListAdapter(null)
    private var dividerItemDeco: DividerItemDecoration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)

        swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)?.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorCloudWhite)
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                isRefreshing = false
                vm.processEvent(TeamsViewModel.TeamsFragmentEvent.DidTriggerRefresh)
            }
        }

        dividerItemDeco = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        context?.let { context ->
            ContextCompat.getDrawable(context, R.drawable.bg_list_news_item_separator)?.let { dividerItemDeco?.setDrawable(it) }
        }

        listAdapter.context = context
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        disposables.add(listAdapter.onItemClickEvent().subscribe {
            vm.processEvent(TeamsViewModel.TeamsFragmentEvent.DidSelectTeam(it))
        })

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            listAdapter.setTeamItems(it.teams)
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is TeamsViewModel.TeamsFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is TeamsViewModel.TeamsFragmentViewEffect.PresentTeamFragment -> findNavController().navigate(TeamFragmentDirections.actionGlobalTeamDetailFragment(it.team))
            }
        })

        vm.processEvent(TeamsViewModel.TeamsFragmentEvent.ViewCreated)
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.teams) ?: ""

    override fun onResume() {
        super.onResume()
        BoostCtrlAnalytics.instance.trackScreen("TeamsFragment")
        (activity as MainActivity).setActionBarTitle(getActionBarTitle())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
        vm.viewState.removeObservers(this)
        vm.viewEffect.removeObservers(this)
    }

}