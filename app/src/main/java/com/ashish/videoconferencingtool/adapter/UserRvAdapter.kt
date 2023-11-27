package com.ashish.videoconferencingtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ashish.videoconferencingtool.databinding.UserRvItemBinding
import com.ashish.videoconferencingtool.models.User

class UserRvAdapter(val onUserClick : (User)-> Unit) : ListAdapter<User,UserViewHolder>(diffUtils) {
       
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = UserRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)

        holder.bindContactItem(item)
        holder.binding.root.setOnClickListener {
            onUserClick(item)
        }

    }

}

//    fun setData(newUserList: List<User>) {
//        Log.d(TAG, "Old List $oldUserList")
//        Log.d(TAG, "new List $newUserList")
//
//        val diffUtil = UserListDiffUtil(oldUserList, newUserList)
//        val diffResult = DiffUtil.calculateDiff(diffUtil)
//        this.oldUserList = newUserList
//        diffResult.dispatchUpdatesTo(this)
//    }


class UserViewHolder(val binding: UserRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindContactItem(user: User) {
        binding.nameTxt.text = user.name
    }
//    fun bindChatItem(user: User) {
//        binding.nameTxt.text = user.name
//        binding.lastMsgTimeTxt.text = if (user.isOnline) "Online" else "Offline"
//    }
}


private val diffUtils = object : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return when {
            oldItem.id != newItem.id -> false
            oldItem.name != newItem.name -> false
            oldItem.isOnline != newItem.isOnline -> false
            oldItem.lastSeen != newItem.lastSeen -> false
            oldItem.mobile != newItem.mobile -> false
            else -> true
        }
    }

}