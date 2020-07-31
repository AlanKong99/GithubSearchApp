package alan_kong99.com.github.android.githubsearchapp

import alan_kong99.com.github.android.githubsearchapp.data.User
import alan_kong99.com.github.android.githubsearchapp.repository.ThumbnailLoader
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.githubsearchapp.databinding.UserItemBinding

class UserViewHolder(itemView: View,
                     private val binding:UserItemBinding,
                     private val thumbnailLoader: ThumbnailLoader<UserViewHolder>
) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            val intent = WebViewActivity.newIntent(
                itemView.context,
                binding.user?.homeUrl
                )
            itemView.context.startActivity(intent)
        }
    }

    fun bindAvatar(avatar: Drawable) {
        binding.avatar.setImageDrawable(avatar)
    }

    fun bindUser(usr: User) {
        thumbnailLoader.loadBitmap(this, usr.avatarUrl)
        binding.user = usr
        binding.notifyChange()
    }
}