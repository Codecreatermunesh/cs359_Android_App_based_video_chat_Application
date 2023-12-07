package com.munesh.videoconferencingtool.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.munesh.videoconferencingtool.api.AuthAPI
import com.munesh.videoconferencingtool.models.User
import com.munesh.videoconferencingtool.models.request.UserSignInReq
import com.munesh.videoconferencingtool.models.request.UserSignUpReq
import com.munesh.videoconferencingtool.models.response.UserResponse
import com.munesh.videoconferencingtool.utils.Constants.MSG
import com.munesh.videoconferencingtool.utils.Constants.NO_INTERNET_CONNECTION
import com.munesh.videoconferencingtool.utils.Constants.SOMETHING_WENT_WRONG
import com.munesh.videoconferencingtool.utils.NetworkManager
import com.munesh.videoconferencingtool.utils.NetworkResult
import com.munesh.videoconferencingtool.utils.SharedPref
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

    private fun handleResponse(response: Response<UserResponse>) {
        if (response.isSuccessful && response.body() != null) {
            val user = response.body()!!
            userMutableLiveData.postValue(NetworkResult.Success(response.body()!!.user))
            tokenManager.saveToken(user.token)
            tokenManager.saveUserDetails(user.user)
        }else{
            val errorJson = JSONObject(response.errorBody()?.charStream()!!.readText())
            userMutableLiveData.postValue(NetworkResult.Error(errorJson.getString(MSG)))
        }
    }

}