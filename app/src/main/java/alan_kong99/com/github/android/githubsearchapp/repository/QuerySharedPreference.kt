package alan_kong99.com.github.android.githubsearchapp.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

const val QUERY = "Query"
const val CHOICE = "CHOICE"

class QuerySharedPreference(app: Application) {

    private val sharedPreference: SharedPreferences = app.getSharedPreferences(
                    "QuerySharedPreference",
                    Context.MODE_PRIVATE
                    )

    fun getLastQuery() : String {
        return sharedPreference.getString(QUERY, "") ?: ""
    }

    fun storeQuery(query: String) {
        sharedPreference.edit().putString(QUERY, query).apply()
    }

    fun getLastSearchChoice() : Long {
        return sharedPreference.getLong(CHOICE, 0)
    }

    fun storeChoice(choice: Long) {
        sharedPreference.edit().putLong(CHOICE, choice).apply()
    }

}