package pt.cosmik.boostctrl.ui.news.detail

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.common.views.BoostCtrlWebView
import pt.cosmik.boostctrl.ui.person.PersonFragmentDirections
import pt.cosmik.boostctrl.ui.teams.detail.TeamFragmentDirections
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics

class NewsDetailFragment : BaseFragment() {

    private val vm: NewsDetailViewModel by viewModel()

    private var titleText: TextView? = null
    private var authorDateText: TextView? = null
    private var webview: BoostCtrlWebView? = null
    private var progressBar: ProgressBar? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
            it.actionBarTitle?.let { newsSource -> (activity as MainActivity).setActionBarTitle(getString(R.string.news_article, newsSource)) }
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is NewsDetailFragmentViewEffect.ShowError -> showErrorMessage(it.message)
                is NewsDetailFragmentViewEffect.PresentSharesheet -> presentSharesheet(it.extra)
                is NewsDetailFragmentViewEffect.PresentPersonFragment -> findNavController().navigate(PersonFragmentDirections.actionGlobalPersonFragment(it.person))
                is NewsDetailFragmentViewEffect.PresentTeamFragment -> findNavController().navigate(TeamFragmentDirections.actionGlobalTeamDetailFragment(it.team))
            }
        })

        arguments?.get("newsItem")?.let { vm.processEvent(NewsDetailFragmentEvent.DidCreateWithNewsItem(it as NewsItem)) }
        arguments?.get("newsItemId")?.let { vm.processEvent(NewsDetailFragmentEvent.DidCreateWithNewsItemId(it as String)) }
    }

    override fun onResume() {
        super.onResume()
        BoostCtrlAnalytics.instance.trackScreen("NewsDetailFragment")
    }

    private fun presentSharesheet(extra: String) {
        BoostCtrlAnalytics.instance.logEvent(extra, "Share news article", "article")
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, extra)
            type = "text/plain"
        }
        activity?.startActivity(intent)
    }

    override fun getActionBarTitle(): String = getString(R.string.news_article)

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
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