package pt.cosmik.boostctrl.utils

import android.net.http.SslError
import android.webkit.*
import io.reactivex.subjects.PublishSubject

class BoostCtrlWebviewClient: WebViewClient() {

    val didFinishedLoadingSubject = PublishSubject.create<Boolean>()
    val didTapTwitterLink = PublishSubject.create<String>()
    val didTapArticleLink = PublishSubject.create<String>()

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (isTwitterLink(url)) {
            didTapTwitterLink.onNext(url)
        }
        else {
            didTapArticleLink.onNext(url.replace("https://twitter.com/", ""))
        }

        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        didFinishedLoadingSubject.onNext(false)
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        didFinishedLoadingSubject.onNext(true)
    }

    private fun isTwitterLink(url: String): Boolean {
        return !url.contains("/team/").or(url.contains("/player"))
    }

}