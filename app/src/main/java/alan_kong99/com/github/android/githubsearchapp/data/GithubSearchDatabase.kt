package alan_kong99.com.github.android.githubsearchapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Repo::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class GithubSearchDatabase : RoomDatabase() {

    abstract fun reposDao() : RepoDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: GithubSearchDatabase? = null

        fun getInstance(context: Context): GithubSearchDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context)
            }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                GithubSearchDatabase::class.java,
                 "GithubSearch.db")
                    .build()
    }
}