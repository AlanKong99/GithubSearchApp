package alan_kong99.com.github.android.githubsearchapp.repository

import androidx.paging.PagingSource
import alan_kong99.com.github.android.githubsearchapp.api.GithubService
import alan_kong99.com.github.android.githubsearchapp.data.Repo
import retrofit2.HttpException
import java.io.IOException

private const val PAGE_DEFAULT_NUM = 1

class RepoPagingSource(
    private val githubService: GithubService,
    private val query: String
) : PagingSource<Int, Repo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        return try {
            val page: Int = params.key ?: PAGE_DEFAULT_NUM

            val response = githubService.
                                                getRepositories(query, page, params.loadSize)

            val repos = response.items
            val prevKey = if (page > 0) page - 1 else null

            val nextKey = if (response.items.isNotEmpty()) page + 1 else null
            return LoadResult.Page(
                repos,
                prevKey,
                nextKey
            )
        } catch (e: IOException){
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}