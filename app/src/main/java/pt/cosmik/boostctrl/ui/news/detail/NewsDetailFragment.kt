package pt.cosmik.boostctrl.ui.news.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.common.BoostCtrlWebView

class NewsDetailFragment : BaseFragment() {

    private val vm: NewsDetailViewModel by viewModel()
    private var disposables = CompositeDisposable()

    private var titleText: TextView? = null
    private var webview: BoostCtrlWebView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_detail, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.get("newsItem")?.let { vm.processEvent(NewsDetailViewModel.NewsDetailFragmentEvent.DidCreateWithNewsItem(it as NewsItem)) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleText = view.findViewById(R.id.title_text)
        progressBar = view.findViewById(R.id.loading_bar)
        webview = view.findViewById<BoostCtrlWebView>(R.id.webview)?.apply {
            setProgressBar(progressBar)
            setParentActivity(activity)
            disposables.add(onErrorSubject.subscribe {
                (activity as MainActivity).showMessageInSnackBar("Something went wrong loading the content of this article.")
            })
        }

        vm.viewState.observe(this, Observer {
            titleText?.text = it.articleTitle
            webview?.loadTwitterContent(it.articleContent)

            // TODO: add image below the title, the article writer and the date
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
//                is NewsViewModel.NewsFragmentViewEffect.ShowError -> showErrorMessage(it.message)
            }
        })
    }

    override fun getActionBarTitle(): String = "News"

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