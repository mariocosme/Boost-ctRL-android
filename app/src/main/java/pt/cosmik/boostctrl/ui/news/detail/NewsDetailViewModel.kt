package pt.cosmik.boostctrl.ui.news.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import pt.cosmik.boostctrl.models.NewsItem
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.repositories.BoostCtrlRepository
import pt.cosmik.boostctrl.utils.SingleLiveEvent
import java.text.DateFormat


class NewsDetailViewModel(private val boostCtrlRepository: BoostCtrlRepository): ViewModel() {

    val viewState = MutableLiveData(NewsDetailFragmentViewState())
    val viewEffect = SingleLiveEvent<NewsDetailFragmentViewEffect>()
    private val disposables = CompositeDisposable()

    private var newsItem: NewsItem? = null

    fun processEvent(event: NewsDetailFragmentEvent) {
        when (event) {
            is NewsDetailFragmentEvent.DidCreateWithNewsItem -> {
                if (event.newsItem.id != newsItem?.id) {
                    newsItem = event.newsItem
                    initNewsItem()
                }
            }
            is NewsDetailFragmentEvent.DidCreateWithNewsItemId -> {
                disposables.add(boostCtrlRepository.getNewsItem(event.newsItemId).subscribe ({
                    viewState.value = viewState.value?.copy(isLoading = false)
                    newsItem = it
                    initNewsItem()
                }, {
                    Crashlytics.logException(it)
                    viewState.value = viewState.value?.copy(isLoading = false)
                    viewEffect.value = NewsDetailFragmentViewEffect.ShowError("Something went wrong trying to obtain the selected news item.")
                }))
            }
            is NewsDetailFragmentEvent.DidTapArticleLink -> getDataToPresentDetailFragment(event.link)
            // TODO: change when we have more sources
            NewsDetailFragmentEvent.DitPressShareMenuItem -> viewEffect.value = NewsDetailFragmentViewEffect.PresentSharesheet("https://octane.gg/news/${newsItem?.hyphenated}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    private fun initNewsItem() {
        newsItem?.let {
            viewState.value = viewState.value?.copy(
                actionBarTitle = it.source,
                articleTitle = it.description,
                articleContent = it.article,
                articleImage = it.image,
                articleAuthorDate = "${it.author} @ ${it.date?.let { date -> DateFormat.getDateInstance(DateFormat.SHORT).format(date) }}"
            )
        }
    }

    private fun getDataToPresentDetailFragment(articleLink: String) {
        when (getArticleLinkType(articleLink)) {
            ArticleLinkType.PERSON -> {
                viewState.value = viewState.value?.copy(isLoading = true)

                val person = articleLink.replace("player/", "")
                disposables.add(boostCtrlRepository.getPerson(person).subscribe ({
                    viewState.value = viewState.value?.copy(isLoading = false)
                    it?.let { person -> viewEffect.value = NewsDetailFragmentViewEffect.PresentPersonFragment(person) }
                }, {
                    Crashlytics.logException(it)
                    viewState.value = viewState.value?.copy(isLoading = false)
                    viewEffect.value = NewsDetailFragmentViewEffect.ShowError("Something went wrong trying to obtain the selected subject.")
                }))
            }
            ArticleLinkType.TEAM -> {
                viewState.value = viewState.value?.copy(isLoading = true)
                val teamName = articleLink.replace("team/", "").replace("team_", "")
                disposables.add(boostCtrlRepository.getTeam(teamName).subscribe ({
                    viewState.value = viewState.value?.copy(isLoading = false)
                    if (it == null) {
                        viewEffect.value = NewsDetailFragmentViewEffect.ShowError("Cannot obtain the selected team.")
                    }
                    else {
                        viewEffect.value = NewsDetailFragmentViewEffect.PresentTeamFragment(it)
                    }
                }, {
                    Crashlytics.logException(it)
                    viewState.value = viewState.value?.copy(isLoading = false)
                    viewEffect.value = NewsDetailFragmentViewEffect.ShowError("Something went wrong trying to obtain the selected team.")
                }))
            }
            ArticleLinkType.UNKNOWN -> {}
        }
    }

    private fun getArticleLinkType(link: String): ArticleLinkType {
        if (link.contains("player/")) return ArticleLinkType.PERSON
        if (link.contains("team/")) return ArticleLinkType.TEAM
        return ArticleLinkType.UNKNOWN
    }

    private enum class ArticleLinkType { PERSON, TEAM, UNKNOWN }

}

data class NewsDetailFragmentViewState(
    val actionBarTitle: String? = null,
    val articleTitle: String? = null,
    val articleContent: String? = null,
    val articleImage: String? = null,
    val articleAuthorDate: String? = null,
    val isLoading: Boolean = true
)

sealed class NewsDetailFragmentViewEffect {
    data class ShowError(val message: String): NewsDetailFragmentViewEffect()
    data class PresentSharesheet(val extra: String): NewsDetailFragmentViewEffect()
    data class PresentPersonFragment(val person: Person): NewsDetailFragmentViewEffect()
    data class PresentTeamFragment(val team: Team): NewsDetailFragmentViewEffect()
}

sealed class NewsDetailFragmentEvent {
    data class DidCreateWithNewsItem(val newsItem: NewsItem): NewsDetailFragmentEvent()
    data class DidCreateWithNewsItemId(val newsItemId: String): NewsDetailFragmentEvent()
    data class DidTapArticleLink(val link: String): NewsDetailFragmentEvent()
    object DitPressShareMenuItem: NewsDetailFragmentEvent()
}