package alan_kong99.com.github.android.githubsearchapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import alan_kong99.com.github.android.githubsearchapp.data.Repo
import com.bignerdranch.android.githubsearchapp.databinding.ReposItemBinding

class RepoAdapter : PagingDataAdapter<Repo, RepoViewHolder>(
    REPO_COMPARATOR
) {
    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.bindRepo(repoItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = ReposItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
           false
        )
        return RepoViewHolder(
            binding.root,
            binding
        )
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return  oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}