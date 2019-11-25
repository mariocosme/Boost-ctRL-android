package pt.cosmik.boostctrl.ui.news

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

class NewsFragment : BaseFragment() {

    private val vm: NewsViewModel by sharedViewModel()

    private var recyclerView: RecyclerView? = null
    private var swipeRefresh: SwipeRefreshLayout? = null
    private var loadingBar: ProgressBar? = null

    private var dividerItemDeco: DividerItemDecoration? = null
    private val listAdapter = NewsListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)

        dividerItemDeco = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        context?.let { context ->
            ContextCompat.getDrawable(context, R.drawable.bg_list_news_item_separator)?.let { dividerItemDeco?.setDrawable(it) }
        }

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)?.apply {
            setProgressBackgroundColorSchemeResource(R.color.colorCloudWhite)
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                isRefreshing = false
                vm.processEvent(NewsViewModel.NewsFragmentEvent.DidTriggerRefresh)
            }
        }

        disposables.add(listAdapter.onItemClickEvent().subscribe {
            findNavController().navigate(NewsFragmentDirections.actionNavigationNewsToNewsItemDetailFragment(it))
        })

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            listAdapter.setNewsItems(it.newsItems)
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is NewsViewModel.NewsFragmentViewEffect.ShowError -> showErrorMessage(it.message)
            }
        })

        vm.processEvent(NewsViewModel.NewsFragmentEvent.ViewCreated)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.news) ?: ""

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(getActionBarTitle())
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

}