package alan_kong99.com.github.android.githubsearchapp.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import alan_kong99.com.github.android.githubsearchapp.api.GithubService
import alan_kong99.com.github.android.githubsearchapp.data.GithubSearchDatabase
import alan_kong99.com.github.android.githubsearchapp.data.Repo
import alan_kong99.com.github.android.githubsearchapp.data.User
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

class SearchRepository(
    private val service: GithubService,
    private val database: GithubSearchDatabase) {

    fun getRepositories(query: String): Flow<PagingData<Repo>> {
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = {database.reposDao().reposByName(dbQuery)}
        return Pager(
            config = PagingConfig(
                NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = GithubRepoRemoteMediator(
                query,
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getUsers(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UserPagingSource(
                    service,
                    query
                )
            }
        ).flow
    }

    private fun fetchThumbnail(url: String) = service.fetchThumbnail(url)

    fun getBitmap(url: String): Bitmap? {
        val response: Response<ResponseBody> = fetchThumbnail(url).execute()
        val responseBody = response.body()
        val byteStream = responseBody?.byteStream()
        return byteStream?.use(BitmapFactory::decodeStream)
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 15
    }

}