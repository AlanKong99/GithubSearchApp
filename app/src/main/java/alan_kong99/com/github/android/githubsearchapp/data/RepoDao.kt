package alan_kong99.com.github.android.githubsearchapp.data

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Repo>)

    @Query("SELECT * FROM repos WHERE repos.name LIKE :query OR repos.description LIKE :query ORDER BY stars DESC, name ASC")
    fun reposByName(query: String): PagingSource<Int, Repo>

    @Query("DELETE FROM repos")
    suspend fun deleteAllRepos()
}