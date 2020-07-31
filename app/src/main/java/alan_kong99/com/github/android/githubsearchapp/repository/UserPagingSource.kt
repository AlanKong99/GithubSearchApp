package alan_kong99.com.github.android.githubsearchapp.repository

import alan_kong99.com.github.android.githubsearchapp.api.GithubService
import alan_kong99.com.github.android.githubsearchapp.data.User
import androidx.paging.PagingSource
import retrofit2.HttpException
import java.io.IOException

private const val PAGE_DEFAULT_NUM = 1

class UserPagingSource(
    private val githubService: GithubService,
    private val query: String
) : PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val page = params.key ?: PAGE_DEFAULT_NUM
            val itemPage = params.loadSize
            val response = githubService.getUsers(query, page, itemPage)
            val users = response.items

            val prevKey = if (page > 0) page - 1 else null
            val nextKey = if (users.isNotEmpty()) page + 1 else null

            LoadResult.Page(
                users,
                prevKey,
                nextKey
            )
        } catch (e : IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}