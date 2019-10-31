package pt.cosmik.boostctrl.ui.standings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment

class StandingsFragment : BaseFragment() {

    private val vm: StandingsViewModel by viewModel()
    private var disposables = CompositeDisposable()

    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingBar: ProgressBar? = null
    private var lastUpdatedText: TextView? = null

    private var recyclerView: RecyclerView? = null
    private val listAdapter = StandingsListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_standings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)
        lastUpdatedText = view.findViewById(R.id.last_updated_text)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
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
            lastUpdatedText?.text = it.lastUpdatedAt
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is StandingsViewModel.StandingsFragmentViewEffect.ShowError -> showErrorMessage(it.message)
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