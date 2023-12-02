package com.ashish.videoconferencingtool.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.repository.UserRepo
import com.ashish.videoconferencingtool.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepo: UserRepo) : ViewModel(){

    val userLiveData : LiveData<NetworkResult<List<User>>> get() = userRepo.userListLiveData

    fun getAllUsers(){
        viewModelScope.launch {
            userRepo.getAllUsers()
        }
    }
}