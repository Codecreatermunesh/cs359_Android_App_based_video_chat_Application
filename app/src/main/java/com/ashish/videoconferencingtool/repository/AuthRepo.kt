package com.ashish.videoconferencingtool.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ashish.videoconferencingtool.api.AuthAPI
import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.models.request.UserSignInReq
import com.ashish.videoconferencingtool.models.request.UserSignUpReq
import com.ashish.videoconferencingtool.utils.Constants.MSG
import com.ashish.videoconferencingtool.utils.Constants.NO_INTERNET_CONNECTION
import com.ashish.videoconferencingtool.utils.Constants.SOMETHING_WENT_WRONG
import com.ashish.videoconferencingtool.utils.NetworkManager
import com.ashish.videoconferencingtool.utils.NetworkResult
import com.ashish.videoconferencingtool.utils.SharedPref
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val authAPI: AuthAPI,
    private val networkManager: NetworkManager,
    private val tokenManager: SharedPref
    ) {

    private val userMutableLiveData = MutableLiveData<NetworkResult<User>>()
    val userLiveData : LiveData<NetworkResult<User>> get() = userMutableLiveData

    suspend fun signUp(userSignUpReq: UserSignUpReq){
        userMutableLiveData.postValue(NetworkResult.Loading())
        try {
            if (networkManager.internetConnected){
                val response = authAPI.signUp(userSignUpReq)
                handleResponse(response)
            }else{
                userMutableLiveData.postValue(NetworkResult.Error(NO_INTERNET_CONNECTION))
            }
        }catch (e : Exception){
            userMutableLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
        }
    }

    suspend fun signIn(userSignInReq: UserSignInReq){
        userMutableLiveData.postValue(NetworkResult.Loading())
        try {
            if (networkManager.internetConnected){
                val response = authAPI.signIn(userSignInReq)
                handleResponse(response)
            }else{
                userMutableLiveData.postValue(NetworkResult.Error(NO_INTERNET_CONNECTION))
            }
        }catch (e : Exception){
            userMutableLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
        }
    }

    private fun handleResponse(response: Response<User>) {
        if (response.isSuccessful && response.body() != null) {
            val user = response.body()!!
            userMutableLiveData.postValue(NetworkResult.Success(response.body()!!))
            tokenManager.saveUser(user)
        }else{
            val errorJson = JSONObject(response.errorBody()?.charStream()!!.readText())
            userMutableLiveData.postValue(NetworkResult.Error(errorJson.getString(MSG)))
        }
    }

}