package com.ashish.videoconferencingtool.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ashish.videoconferencingtool.databinding.MessageRvItemBinding
import com.ashish.videoconferencingtool.models.Message
import com.ashish.videoconferencingtool.utils.Extensions.gone
import com.ashish.videoconferencingtool.utils.Extensions.visible
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageRvAdapter(
    private val userId: String,
    private val context: Context
) : ListAdapter<Message, MessageRvAdapter.UserViewHolder>(diffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = MessageRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        if (item.senderId == userId) {
            holder.bindMyMsg(item, context)
        } else {
            holder.bindReceiverMsg(item,context)
        }
    }

    class UserViewHolder(private val binding: MessageRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindMyMsg(item: Message, context: Context) {
            binding.myLayout.visible()
            binding.receiverLayout.gone()
            if (item.message.isNotEmpty()) {
                binding.messageTxt.text = item.message
            } else {
                binding.messageTxt.gone()
            }
            binding.timeTxt.text = getTime(item.timestamp)

            if (item.file != null) {
                if (item.file.mimeType.contains("image/")) {
                    Glide.with(context)
                        .load(item.file.url)
                        .into(binding.mySentIv)
                    binding.mySentIv.visible()
                    binding.mySentVv.gone()
                    binding.mySentFileLayout.gone()

                } else if (item.file.mimeType.contains("video/")) {
                    binding.mySentIv.gone()
                    binding.mySentVv.visible()
                    binding.mySentFileLayout.gone()
                } else {
                    binding.topFileTypeTxt.text = item.file.fileType
                    val size = if (item.file.size/1024 < 1024){
                        String.format("%.2f", (item.file.size/1024).toDouble())+" KB"
                    }else{

                        String.format("%.2f", (item.file.size/(1024*1024)).toDouble())+" MB"
                    }
                    binding.fileSizeTxt.text = size
                    binding.sideFileTypeTxt.text = item.file.fileType
                    binding.fileNameTxt.text = item.file.originalName
                    binding.topFileTypeTxt.text = item.file.fileType
                    binding.mySentIv.gone()
                    binding.mySentVv.gone()
                    binding.mySentFileLayout.visible()
                }
            }else{
                binding.mySentIv.gone()
                binding.mySentVv.gone()
                binding.mySentFileLayout.gone()
            }
        }

        fun bindReceiverMsg(item: Message,context: Context) {
            binding.receiverLayout.visible()
            binding.myLayout.gone()
            if (item.message.isNotEmpty()) {
                binding.receiverMsgTxt.text = item.message
            } else {
                binding.receiverMsgTxt.gone()
            }
            binding.receiverTimeTxt.text = getTime(item.timestamp)
            if (item.file != null) {
                if (item.file.mimeType.contains("image/")) {
                    Glide.with(context)
                        .load(item.file.url)
                        .into(binding.receiverSentIv)
                    binding.receiverSentIv.visible()
                    binding.receiverSentVv.gone()
                    binding.receiverSentFileLayout.gone()

                } else if (item.file.mimeType.contains("video/")) {
                    binding.receiverSentIv.visible()
                    binding.receiverSentVv.visible()
                    binding.receiverSentFileLayout.gone()
                } else {
                    binding.receiverFileNameTxt.text = item.file.originalName
                    binding.receiverTopFileTypeTxt.text = item.file.fileType
                    val size = if (item.file.size/1024 < 1024){
                        String.format("%.2f", (item.file.size/1024).toDouble())+" KB"
                    }else{

                        String.format("%.2f", (item.file.size/(1024*1024)).toDouble())+" MB"
                    }
                    binding.receiverFileSizeTxt.text = size
                    binding.receiverSideFileTypeTxt.text = item.file.fileType
                    binding.receiverTopFileTypeTxt.text = item.file.fileType
                    binding.receiverSentIv.gone()
                    binding.receiverSentVv.gone()
                    binding.receiverSentFileLayout.visible()
                }
            }else{
                binding.receiverSentIv.gone()
                binding.receiverSentVv.gone()
                binding.receiverSentFileLayout.gone()
            }
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