package pt.cosmik.boostctrl.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment

class NewsFragment : BaseFragment() {

    private val vm: NewsViewModel by viewModel()

    private var recyclerView: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingBar: ProgressBar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setActionBarTitle("News")

        recyclerView = view.findViewById(R.id.recycler_view)
        loadingBar = view.findViewById(R.id.loading_bar)
        swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)?.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorCloudWhite)
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                isRefreshing = false
                vm.processEvent(NewsViewModel.NewsFragmentEvent.DidTriggerRefresh)
            }
        }

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is NewsViewModel.NewsFragmentViewEffect.ShowError -> showErrorMessage(it.message)
            }
        })
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

}