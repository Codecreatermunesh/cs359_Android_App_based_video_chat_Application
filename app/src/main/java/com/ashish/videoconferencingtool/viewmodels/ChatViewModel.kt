package com.ashish.videoconferencingtool.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashish.videoconferencingtool.models.Message
import com.ashish.videoconferencingtool.repository.ChatRepo
import com.ashish.videoconferencingtool.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo
) :ViewModel(){

    val chatUserLiveData : LiveData<NetworkResult<List<Message>>> get() = chatRepo.chatUserLiveData

    fun getChatUsers(participants : List<String>){
        viewModelScope.launch {
            chatRepo.getChatUsers(participants)
        }
    }

    private val _userChatListLiveData = MutableLiveData<List<Message>>()
    val userChatListLiveData : LiveData<List<Message>> get() = _userChatListLiveData

    fun setUserChatList(chatList : List<Message>){
        _userChatListLiveData.postValue(chatList)
    }

    fun setNewMessage(message: Message){
        val newList = userChatListLiveData.value as MutableList<Message>
        newList.add(message)
        _userChatListLiveData.postValue(newList)
    }

    private val _msgSentLiveData = MutableLiveData<Boolean>()
    val msgSentLiveData : LiveData<Boolean> get() = _msgSentLiveData

    fun msgSentStatusLiveData(msgStatus : Boolean){
        _msgSentLiveData.postValue(msgStatus)
    }

    // Upload File
    val fileLiveData : LiveData<NetworkResult<Message>> get() = chatRepo.fileLiveData

    fun uploadFile(file : MultipartBody.Part, receiverId : String){
        viewModelScope.launch {
            Log.d("ChatApp","All is Fine")
            chatRepo.uploadFile(file,receiverId)
        }
    }


}