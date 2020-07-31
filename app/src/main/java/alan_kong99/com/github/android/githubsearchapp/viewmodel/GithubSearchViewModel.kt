package alan_kong99.com.github.android.githubsearchapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import alan_kong99.com.github.android.githubsearchapp.api.GithubService
import alan_kong99.com.github.android.githubsearchapp.data.GithubSearchDatabase
import alan_kong99.com.github.android.githubsearchapp.data.Repo
import alan_kong99.com.github.android.githubsearchapp.data.User
import alan_kong99.com.github.android.githubsearchapp.repository.QuerySharedPreference
import alan_kong99.com.github.android.githubsearchapp.repository.SearchRepository
import android.app.Application
import kotlinx.coroutines.flow.Flow


class GithubSearchViewModel(app: Application) : AndroidViewModel(app) {

    private var searchRepository: SearchRepository

    private var sharedPreference = QuerySharedPreference(app)

    private var repoQuery: String
    private var lastRepoPagingData: Flow<PagingData<Repo>>? = null
    private var lastRepoQuery: String = ""

    private var usrQuery: String
    private var lastUsrQuery: String = ""
    private var lastUserPagingData: Flow<PagingData<User>>? = null

    init {
        repoQuery = sharedPreference.getLastQuery()
        searchRepository=
            SearchRepository(
                GithubService.create(),
                GithubSearchDatabase.getInstance(app)
            )
        usrQuery = sharedPreference.getLastQuery()

    }

    fun setRepoQuery(query:String) {
        lastRepoQuery = repoQuery
        repoQuery = query
        sharedPreference.storeQuery(query)
    }

    fun getLastQuery() : String {
        return sharedPreference.getLastQuery()
    }

    fun getLastSearchChoice(): Int {
        return sharedPreference.getLastSearchChoice().toInt()
    }

    fun getRepos(): Flow<PagingData<Repo>> {
        val lastResult = lastRepoPagingData

        if (lastResult != null && lastRepoQuery == repoQuery) {
            return lastResult
        }

        val newResult = searchRepository.getRepositories(repoQuery)
                                                                 .cachedIn(viewModelScope)
        lastRepoPagingData = newResult
        return newResult
    }

    fun setUsrQuery(query: String) {
        lastUsrQuery = usrQuery
        usrQuery = query
        sharedPreference.storeQuery(query)
    }

    fun getUsers() : Flow<PagingData<User>> {
        val lasResult = lastUserPagingData
        if (lasResult != null && lastUsrQuery == usrQuery) {
            return lasResult
        }

        val newResult = searchRepository.getUsers(usrQuery)
                                                                .cachedIn(viewModelScope)
        lastUserPagingData = newResult

        return newResult
    }

    fun setLastSearchChoice(choice: Long) {
        sharedPreference.storeChoice(choice)
    }

    companion object {
        const val REPO_QUALIFIER:String = "in:name,description"
    }
}