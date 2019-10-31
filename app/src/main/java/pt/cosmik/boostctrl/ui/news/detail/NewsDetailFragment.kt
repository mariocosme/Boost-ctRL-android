package pt.cosmik.boostctrl.ui.news.detail

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.common.views.BoostCtrlWebView

class NewsDetailFragment : BaseFragment() {

    private val vm: NewsDetailViewModel by viewModel()
    private var disposables = CompositeDisposable()

    private var titleText: TextView? = null
    private var authorDateText: TextView? = null
    private var webview: BoostCtrlWebView? = null
    private var progressBar: ProgressBar? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.get("newsItem")?.let { vm.processEvent(NewsDetailFragmentEvent.DidCreateWithNewsItem(it as NewsItem)) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.news_detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_item) {
            vm.processEvent(NewsDetailFragmentEvent.DitPressShareMenuItem)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.image_view)
        titleText = view.findViewById(R.id.title_text)
        authorDateText = view.findViewById(R.id.author_date_text)
        progressBar = view.findViewById(R.id.loading_bar)
        webview = view.findViewById<BoostCtrlWebView>(R.id.webview)?.apply {
            setProgressBar(progressBar)
            setParentActivity(activity)
            disposables.add(onErrorSubject.subscribe {
                (activity as MainActivity).showMessageInSnackBar("Something went wrong loading the content of this article.")
            })
            onArticleLinkTappedSubject.subscribe { vm.processEvent(NewsDetailFragmentEvent.DidTapArticleLink(it)) }
        }

        vm.viewState.observe(this, Observer {
            titleText?.text = it.articleTitle
            authorDateText?.text = it.articleAuthorDate
            progressBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            webview?.loadTwitterContent(it.articleContent)
            it.articleImage?.let { imageLink -> Glide.with(this).load(imageLink).into(imageView!!) }
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is NewsDetailFragmentViewEffect.ShowError -> showErrorMessage(it.message)
                is NewsDetailFragmentViewEffect.PresentSharesheet -> presentSharesheet(it.extra)
                is NewsDetailFragmentViewEffect.PresentPersonFragment -> {} // TODO: navigate to global person detail fragment
            }
        })
    }

    private fun presentSharesheet(extra: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, extra)
            type = "text/plain"
        }
        activity?.startActivity(intent)
    }

    override fun getActionBarTitle(): String = "Octane.gg"

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