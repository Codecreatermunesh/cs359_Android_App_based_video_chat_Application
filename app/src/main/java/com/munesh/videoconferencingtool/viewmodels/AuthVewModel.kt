package com.munesh.videoconferencingtool.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.munesh.videoconferencingtool.models.User
import com.munesh.videoconferencingtool.models.request.UserSignInReq
import com.munesh.videoconferencingtool.models.request.UserSignUpReq
import com.munesh.videoconferencingtool.repository.AuthRepo
import com.munesh.videoconferencingtool.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthVewModel @Inject constructor(private val authRepo: AuthRepo) : ViewModel(){

    val userLiveData : LiveData<NetworkResult<User>> get() = authRepo.userLiveData

    fun signUp(userSignUpReq: UserSignUpReq){
        viewModelScope.launch {
            authRepo.signUp(userSignUpReq)
        }
    }
    fun signIn(userSignInReq: UserSignInReq){
        viewModelScope.launch {
            authRepo.signIn(userSignInReq)
        }
    }
}