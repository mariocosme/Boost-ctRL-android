package pt.cosmik.boostctrl.ui.common.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.webkit.WebView
import android.widget.ProgressBar
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import pt.cosmik.boostctrl.utils.BoostCtrlWebviewClient

@SuppressLint("SetJavaScriptEnabled")
class BoostCtrlWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    val onErrorSubject = PublishSubject.create<Any>()
    val onArticleLinkTappedSubject = PublishSubject.create<String>()

    private var disposables = CompositeDisposable()
    private var webviewClient = BoostCtrlWebviewClient()
    private var progressBar: ProgressBar? = null
    private var parentActivity: Activity? = null

    init {
        alpha = 0.0f
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        settings.setAppCacheEnabled(true)
        settings.defaultTextEncodingName = "utf-8"
        webViewClient = webviewClient

        disposables.addAll(
            webviewClient.didFinishedLoadingSubject.subscribe { withError ->
                progressBar?.visibility = View.GONE

                if (withError) {
                    onErrorSubject.onNext(withError)
                }
                else {
                    animate().apply {
                        interpolator = LinearInterpolator()
                        duration = 500
                        alpha(1f)
                        startDelay = 500
                        start()
                    }
                }
            },
            webviewClient.didTapTwitterLink.subscribe {
                parentActivity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
            },
            webviewClient.didTapArticleLink.subscribe {
                onArticleLinkTappedSubject.onNext(it)
            }
        )
    }

    private fun addCSS(content: String?): String? {
        var html = content?.replace("<blockquote class=\"twitter-tweet\">", "<blockquote class=\"twitter-tweet\" data-theme=\"dark\" data-dnt\"true\">")
        html = html?.replace("/flags/", "https://octane.gg/flags/")
        html = html?.replace("/team-icons/", "https://octane.gg/team-icons/")
        return "<head><style>body { background-color: #283149; } *:not(a) { color: #DBEDF3; } a:link, a:visited, a:hover { color: #F73859 } a:active { color: #670415 }</style></head><body>$html</body>"
    }

    fun setProgressBar(progressBar: ProgressBar?) {
        this.progressBar = progressBar
    }

    fun setParentActivity(activity: Activity?) {
        this.parentActivity = activity
    }

    fun loadTwitterContent(html: String?) {
        loadDataWithBaseURL("https://twitter.com", addCSS(html), "text/html; charset=utf-8", "utf-8", null)
    }

}