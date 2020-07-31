package alan_kong99.com.github.android.githubsearchapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import alan_kong99.com.github.android.githubsearchapp.data.Repo
import com.bignerdranch.android.githubsearchapp.databinding.ReposItemBinding

class RepoViewHolder(itemView: View, private val binding: ReposItemBinding) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            val intent =
                WebViewActivity.newIntent(
                    it.context,
                    binding.repo?.url
                )
            it.context.startActivity(intent)
        }
    }

    fun bindRepo(repo: Repo){
        binding.repo = repo
        binding.notifyChange()
    }
}