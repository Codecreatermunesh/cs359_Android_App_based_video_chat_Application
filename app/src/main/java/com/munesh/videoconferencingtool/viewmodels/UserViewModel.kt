package com.munesh.videoconferencingtool.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.munesh.videoconferencingtool.models.User
import com.munesh.videoconferencingtool.repository.UserRepo
import com.munesh.videoconferencingtool.utils.NetworkResult
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