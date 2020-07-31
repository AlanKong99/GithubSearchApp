package alan_kong99.com.github.android.githubsearchapp.repository

import alan_kong99.com.github.android.githubsearchapp.api.GithubService
import alan_kong99.com.github.android.githubsearchapp.data.GithubSearchDatabase
import alan_kong99.com.github.android.githubsearchapp.data.RemoteKeys
import alan_kong99.com.github.android.githubsearchapp.data.Repo
import alan_kong99.com.github.android.githubsearchapp.viewmodel.GithubSearchViewModel
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

private const val PAGE_DEFAULT_NUM = 1
@OptIn(ExperimentalPagingApi::class)
class GithubRepoRemoteMediator(
    private val query: String,
    private val service: GithubService,
    private val githubSearchDatabase: GithubSearchDatabase
) : RemoteMediator<Int, Repo>(){

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Repo>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.prevKey?.plus(1)?: PAGE_DEFAULT_NUM
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                    ?: return MediatorResult.Error(Throwable("The result is empty"))
                val prevKey = remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                if (remoteKeys?.nextKey == null) {
                    return MediatorResult.Error(Throwable("The end of list"))
                }
                remoteKeys.nextKey
            }
        }

        try {
            val apiResponse = service.getRepositories(
                                    query+GithubSearchViewModel.REPO_QUALIFIER,
                                    page,
                                    state.config.pageSize)

            val repos = apiResponse.items

            val endOfPaginationReached = repos.isEmpty()
            githubSearchDatabase.withTransaction {
                if (loadType ==  LoadType.REFRESH) {
                    githubSearchDatabase.remoteKeysDao().deleteAllRemoteKeys()
                    githubSearchDatabase.reposDao().deleteAllRepos()
                }
                val prevKey = if (page == PAGE_DEFAULT_NUM) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = repos.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                githubSearchDatabase.remoteKeysDao().insertAll(keys)
                githubSearchDatabase.reposDao().insertAll(repos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Repo>): RemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let {repo ->
                githubSearchDatabase.remoteKeysDao().remoteKeysId(repo.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Repo>): RemoteKeys? {
        return state.pages.firstOrNull(){ it.data.isNotEmpty()}?.data?.firstOrNull()
            ?.let {repo ->
                githubSearchDatabase.remoteKeysDao().remoteKeysId(repo.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Repo>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                githubSearchDatabase.remoteKeysDao().remoteKeysId(repoId)
            }
        }
    }
}