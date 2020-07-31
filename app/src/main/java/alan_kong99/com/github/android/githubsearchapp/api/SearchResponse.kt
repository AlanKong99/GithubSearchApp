package alan_kong99.com.github.android.githubsearchapp.api

import com.google.gson.annotations.SerializedName

class SearchResponse<T> (
    @SerializedName("total_count") val total: Int = 0,
    @SerializedName("items") val items: List<T> = emptyList()
)