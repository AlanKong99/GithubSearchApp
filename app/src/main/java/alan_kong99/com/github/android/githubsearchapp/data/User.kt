package alan_kong99.com.github.android.githubsearchapp.data

import com.google.gson.annotations.SerializedName

data class User(
    @field:SerializedName("login") val userName: String,
    @field:SerializedName("id") val userId: Int,
    @field:SerializedName("avatar_url") val avatarUrl: String,
    @field:SerializedName("html_url") val homeUrl: String
)