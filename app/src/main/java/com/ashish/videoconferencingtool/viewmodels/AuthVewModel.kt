package com.ashish.videoconferencingtool.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.models.request.UserSignInReq
import com.ashish.videoconferencingtool.models.request.UserSignUpReq
import com.ashish.videoconferencingtool.repository.AuthRepo
import com.ashish.videoconferencingtool.utils.NetworkResult
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