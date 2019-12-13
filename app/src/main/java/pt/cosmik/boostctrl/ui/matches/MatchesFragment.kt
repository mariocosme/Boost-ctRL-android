package pt.cosmik.boostctrl.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.koin.android.viewmodel.ext.android.sharedViewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics

class MatchesFragment : BaseFragment() {

    private val vm: MatchesViewModel by sharedViewModel()

    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingBar: ProgressBar? = null

    private var recyclerView: RecyclerView? = null
    private val listAdapter = UpcomingMatchesListAdapter()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)

        listAdapter.context = context
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        disposables.add(listAdapter.onItemClickEvent().subscribe {
            vm.processEvent(MatchesViewModel.MatchesFragmentEvent.DidSelectMatch(it))
        })

        swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)?.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorCloudWhite)
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                isRefreshing = false
                vm.processEvent(MatchesViewModel.MatchesFragmentEvent.DidTriggerRefresh)
            }
        }

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            listAdapter.setMatchesItems(it.matches)
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is MatchesViewModel.MatchesFragmentViewEffect.ShowError -> showErrorMessage(it.message)
                is MatchesViewModel.MatchesFragmentViewEffect.PresentUpcomingMatchFragment -> findNavController().navigate(MatchesFragmentDirections.actionGlobalMatchDetailFragment(it.match))
            }
        })

        vm.processEvent(MatchesViewModel.MatchesFragmentEvent.ViewCreated)
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.matches) ?: ""

    override fun onResume() {
        super.onResume()
        BoostCtrlAnalytics.instance.trackScreen("MatchesFragment")
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