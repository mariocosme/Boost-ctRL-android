package pt.cosmik.boostctrl.ui.news.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.utils.SingleLiveEvent


class NewsDetailViewModel: ViewModel() {

    val viewState = MutableLiveData(NewsDetailFragmentViewState())
    val viewEffect = SingleLiveEvent<NewsDetailFragmentViewEffect>()

    private var newsItem: NewsItem? = null

    fun processEvent(event: NewsDetailFragmentEvent) {
        when (event) {
            is NewsDetailFragmentEvent.DidCreateWithNewsItem -> {
                newsItem = event.newsItem
                initNewsItem()
            }
            NewsDetailFragmentEvent.DitPressShareMenuItem -> viewEffect.value = NewsDetailFragmentViewEffect.PresentSharesheet("https://octane.gg/news/${newsItem?.hyphenated}")
        }
    }

    private fun initNewsItem() {
        newsItem?.let {
            viewState.value = viewState.value?.copy(
                actionBarTitle = it.title,
                articleTitle = it.description,
                articleContent = it.secondArticle ?: ""
            )
        }
    }

}

data class NewsDetailFragmentViewState(
    val actionBarTitle: String? = null,
    val articleTitle: String? = null,
    val articleContent: String? = null
)

sealed class NewsDetailFragmentViewEffect {
    data class ShowError(val message: String): NewsDetailFragmentViewEffect()
    data class PresentSharesheet(val extra: String): NewsDetailFragmentViewEffect()
}

sealed class NewsDetailFragmentEvent {
    data class DidCreateWithNewsItem(val newsItem: NewsItem): NewsDetailFragmentEvent()
    object DitPressShareMenuItem: NewsDetailFragmentEvent()
}