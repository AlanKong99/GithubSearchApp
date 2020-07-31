package alan_kong99.com.github.android.githubsearchapp

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import alan_kong99.com.github.android.githubsearchapp.data.User
import alan_kong99.com.github.android.githubsearchapp.repository.ThumbnailLoader
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.bignerdranch.android.githubsearchapp.R
import com.bignerdranch.android.githubsearchapp.databinding.UserItemBinding

class UserAdapter(
    private val context: Context
) : PagingDataAdapter<User, UserViewHolder>(
    USER_COMPARATOR
) {
    var thumbnailLoaders = mutableListOf<ThumbnailLoader<UserViewHolder>>()

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val placeholder = ContextCompat.getDrawable(
            context,
            R.drawable.place_holder
        )?: ColorDrawable()

        holder.bindAvatar(placeholder)

        val usr = getItem(position)
        if (usr != null) {
            holder.bindUser(usr)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
            false)

        val mainThreadHandler = Handler()
        val thumbnailLoader: ThumbnailLoader<UserViewHolder> = ThumbnailLoader(
            mainThreadHandler,
            context
        ) { userViewHolder, bitmap ->
            val drawable = BitmapDrawable(context.resources, bitmap)
            userViewHolder.bindAvatar(drawable)
        }

        thumbnailLoaders.add(thumbnailLoader)
        thumbnailLoader.setup()

        return UserViewHolder(binding.root, binding, thumbnailLoader)
    }
    
    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.userId == newItem.userId
            }
        }
    }
}