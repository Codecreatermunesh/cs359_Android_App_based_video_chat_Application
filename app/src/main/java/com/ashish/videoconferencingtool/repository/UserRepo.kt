package com.ashish.videoconferencingtool.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ashish.videoconferencingtool.api.UserAPI
import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.utils.Constants.MSG
import com.ashish.videoconferencingtool.utils.Constants.NO_INTERNET_CONNECTION
import com.ashish.videoconferencingtool.utils.NetworkManager
import com.ashish.videoconferencingtool.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val userAPI: UserAPI,
    private val networkManager: NetworkManager
) {

    private val userListMutableLiveData = MutableLiveData<NetworkResult<List<User>>>()
    val userListLiveData : LiveData<NetworkResult<List<User>>> get() = userListMutableLiveData

    suspend fun getAllUsers(){
        userListMutableLiveData.postValue(NetworkResult.Loading())
        try {
            if (networkManager.internetConnected){
                val response = userAPI.getAllUser()
                if (response.isSuccessful && response.body() != null){
                    userListMutableLiveData.postValue(NetworkResult.Success(response.body()!!))
                }else{
                    val errorObject = JSONObject(response.errorBody()?.charStream()!!.readText())
                    userListMutableLiveData.postValue(NetworkResult.Error(errorObject.getString(MSG)))
                }
            }else
                userListMutableLiveData.postValue(NetworkResult.Error(NO_INTERNET_CONNECTION))

        }catch(e : Exception){
            userListMutableLiveData.postValue(NetworkResult.Error(e.localizedMessage))
        }

    }
}