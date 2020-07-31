package alan_kong99.com.github.android.githubsearchapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.bignerdranch.android.githubsearchapp.R
import com.bignerdranch.android.githubsearchapp.databinding.ActivityWebViewBinding

const val SEARCH_HTML_URL = "SEARCH_HTML_URL"

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_web_view
        )
        val url = intent.getStringExtra(SEARCH_HTML_URL) ?: "https:www.github.com"
        binding.webView.isForceDarkAllowed = false
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        fun newIntent(context: Context, url: String?): Intent {
            return Intent(
                    context,
                    WebViewActivity::class.java).apply{
                        putExtra(SEARCH_HTML_URL, url)
            }
        }
    }
}