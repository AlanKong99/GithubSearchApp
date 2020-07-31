package alan_kong99.com.github.android.githubsearchapp.repository

import alan_kong99.com.github.android.githubsearchapp.api.GithubService
import alan_kong99.com.github.android.githubsearchapp.data.GithubSearchDatabase
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.widget.Toast
import java.util.concurrent.ConcurrentHashMap

private const val THREAD_NAME = "THUMBNAIL_LOADER"
private const val MSG_DOWNLOAD = 1
class ThumbnailLoader<in T>(
    private val mainThreadHandler: Handler,
    val context: Context,
    val bindThumbnail: (T, Bitmap) -> Unit
) : HandlerThread(THREAD_NAME) {

    private var requestThumbnailHandler: Handler? = null
    private var requestMap = ConcurrentHashMap<T, String>()
    private val searchRepository = SearchRepository(GithubService.create(), GithubSearchDatabase.getInstance(context))
    private var hasQuit = false

    fun setup() {
        start()
        looper
    }

    fun clearQueue() {
        requestThumbnailHandler?.removeMessages(MSG_DOWNLOAD)
        requestMap.clear()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        super.onLooperPrepared()
        requestThumbnailHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MSG_DOWNLOAD) {
                    val target = msg.obj as T
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target]?:return

        try {
            val bitmap = searchRepository.getBitmap(url)?: return
            mainThreadHandler.post(kotlinx.coroutines.Runnable {
                if (requestMap[target] != url || hasQuit) {
                    return@Runnable
                }
                requestMap.remove(target)
                bindThumbnail(target, bitmap)
            })
        } catch (exception: Exception){
            Toast.makeText(context,"Network Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadBitmap(target: T, url: String) {
        requestMap[target] = url
        requestThumbnailHandler?.obtainMessage(MSG_DOWNLOAD, target)?.sendToTarget()
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }
}