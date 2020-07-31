package alan_kong99.com.github.android.githubsearchapp.api

import alan_kong99.com.github.android.githubsearchapp.data.Repo
import alan_kong99.com.github.android.githubsearchapp.data.User
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface GithubService {
    @GET("search/repositories?sort=stars")
    suspend fun  getRepositories(
        @Query("q")query:String,
        @Query("page")Page: Int,
        @Query("per_page") itemsPerPage: Int
    ): SearchResponse<Repo>

    @GET("search/users?sort=follower")
    suspend fun  getUsers(
        @Query("q")query:String,
        @Query("page")Page: Int,
        @Query("per_page") itemsPerPage: Int
    ): SearchResponse<User>

    @GET
    fun fetchThumbnail(@Url url: String): Call<ResponseBody>

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GithubService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubService::class.java)
        }
    }
}