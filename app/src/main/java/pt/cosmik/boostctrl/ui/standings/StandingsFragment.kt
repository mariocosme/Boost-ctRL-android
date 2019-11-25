package pt.cosmik.boostctrl.ui.standings

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.koin.android.viewmodel.ext.android.sharedViewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.teams.detail.TeamFragmentDirections

class StandingsFragment : BaseFragment() {

    private val vm: StandingsViewModel by sharedViewModel()

    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingBar: ProgressBar? = null

    private var recyclerView: RecyclerView? = null
    private val listAdapter = StandingsListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.standings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.info_item) {
            context?.let {
                AlertDialog.Builder(it, R.style.AlertDialogTheme)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setTitle(R.string.rankings_info_dialog_title)
                    .setMessage(vm.viewState.value?.lastUpdatedAt)
                    .create()
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_standings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            if (adapter == null) adapter = listAdapter
        }

        disposables.add(listAdapter.itemClickSubject.subscribe {
            vm.processEvent(StandingsViewModel.StandingsFragmentEvent.DidSelectRankingDescriptor(it))
        })

        swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)?.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorCloudWhite)
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                isRefreshing = false
                vm.processEvent(StandingsViewModel.StandingsFragmentEvent.DidTriggerRefresh)
            }
        }

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            listAdapter.setRankingItems(it.rankingItems)
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is StandingsViewModel.StandingsFragmentViewEffect.ShowError -> showErrorMessage(it.message)
                is StandingsViewModel.StandingsFragmentViewEffect.PresentTeamFragment -> findNavController().navigate(TeamFragmentDirections.actionGlobalTeamDetailFragment(it.team))
            }
        })

        vm.processEvent(StandingsViewModel.StandingsFragmentEvent.ViewCreated(context))
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.standings) ?: ""

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(getActionBarTitle())
    }

}