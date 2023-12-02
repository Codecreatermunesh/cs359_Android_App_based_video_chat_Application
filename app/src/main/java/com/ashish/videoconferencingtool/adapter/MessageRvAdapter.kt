package com.ashish.videoconferencingtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ashish.videoconferencingtool.databinding.MessageRvItemBinding
import com.ashish.videoconferencingtool.models.Message
import com.ashish.videoconferencingtool.utils.Extensions.gone
import com.ashish.videoconferencingtool.utils.Extensions.visible
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageRvAdapter(
    private val userId: String
) : ListAdapter<Message, MessageRvAdapter.UserViewHolder>(diffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = MessageRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        if (item.senderId == userId) {
            holder.bindMyMsg(item)
        } else {
            holder.bindReceiverMsg(item)
        }
    }

    class UserViewHolder(private val binding: MessageRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindMyMsg(item: Message) {
            binding.myLayout.visible()
            binding.receiverLayout.gone()
            binding.messageTxt.text = item.message
            binding.timeTxt.text = getTime(item.timestamp)
            if (item.file != null){

            }
        }

        fun bindReceiverMsg(item: Message) {
            binding.receiverLayout.visible()
            binding.myLayout.gone()
            binding.receiverMsgTxt.text = item.message
            binding.receiverTimeTxt.text = getTime(item.timestamp)
        }

        private fun getTime(timestamp: Long): String {
            return SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(timestamp))
        }
    }
}

private val diffUtils = object : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}