package com.munesh.videoconferencingtool.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.munesh.videoconferencingtool.api.UserAPI
import com.munesh.videoconferencingtool.models.User
import com.munesh.videoconferencingtool.utils.Constants.MSG
import com.munesh.videoconferencingtool.utils.Constants.NO_INTERNET_CONNECTION
import com.munesh.videoconferencingtool.utils.NetworkManager
import com.munesh.videoconferencingtool.utils.NetworkResult
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