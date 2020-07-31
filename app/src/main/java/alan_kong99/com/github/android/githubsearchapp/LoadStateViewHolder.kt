package alan_kong99.com.github.android.githubsearchapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.githubsearchapp.databinding.LoadStateViewBinding

class LoadStateViewHolder(val binding: LoadStateViewBinding, val retry: ()-> Unit) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errMsg.text = loadState.error.localizedMessage
        }

        when(loadState) {
            LoadState.Loading -> {
                binding.loadProgressBar.visibility = View.VISIBLE
                binding.errMsg.visibility = View.GONE
                binding.retryButton.visibility = View.GONE
            }
            else -> {
                binding.loadProgressBar.visibility = View.GONE
                binding.errMsg.visibility = View.VISIBLE
                binding.retryButton.visibility = View.VISIBLE
            }
        }

    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val binding = LoadStateViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return LoadStateViewHolder(binding, retry)
        }
    }
}