package com.ashish.videoconferencingtool.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ashish.videoconferencingtool.api.ChatAPI
import com.ashish.videoconferencingtool.models.Message
import com.ashish.videoconferencingtool.utils.Constants.MSG
import com.ashish.videoconferencingtool.utils.Constants.NO_INTERNET_CONNECTION
import com.ashish.videoconferencingtool.utils.Constants.SOMETHING_WENT_WRONG
import com.ashish.videoconferencingtool.utils.Constants.TAG
import com.ashish.videoconferencingtool.utils.NetworkManager
import com.ashish.videoconferencingtool.utils.NetworkResult
import com.ashish.videoconferencingtool.utils.SharedPref
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ChatRepo @Inject constructor(
    private val chatAPI: ChatAPI,
    private val networkManager: NetworkManager,
    private val tokenManager: SharedPref
) {
    private val chatUserMutableLiveData = MutableLiveData<NetworkResult<List<Message>>>()
    val chatUserLiveData: LiveData<NetworkResult<List<Message>>> get() = chatUserMutableLiveData

    suspend fun getChatUsers(participants: List<String>) {
        try {
            chatUserMutableLiveData.postValue(NetworkResult.Loading())
            if (networkManager.internetConnected) {
                val response = chatAPI.getUserChat(participants)
                if (response.isSuccessful && response.body() != null) {
                    chatUserMutableLiveData.postValue(NetworkResult.Success(response.body()!!))
                } else {
                    val errorObj = JSONObject(response.errorBody()?.charStream()!!.readText())
                    chatUserMutableLiveData.postValue(NetworkResult.Error(errorObj.getString(MSG)))
                }
            } else {
                chatUserMutableLiveData.postValue(NetworkResult.Error(NO_INTERNET_CONNECTION))
            }
        } catch (e: Exception) {
            Log.d(TAG,"${e.message}")
            chatUserMutableLiveData.postValue(NetworkResult.Error(SOMETHING_WENT_WRONG))
        }
    }

//    private val _fileLiveData = MutableLiveData<NetworkResult<Message>>()
//    val fileLiveData : LiveData<NetworkResult<Message>> get() = _fileLiveData
//
//    suspend fun uploadFile(file : MultipartBody.Part,receiverId : String){
//        try {
//            _fileLiveData.postValue(NetworkResult.Loading())
//            if (networkManager.internetConnected){
//                Log.d("ChatRepo", "Uploading")
//
//                val receiverIdBody = MultipartBody.Part.createFormData("receiverId",receiverId)
//                val response = chatAPI.uploadFile(file, receiverIdBody)
//
//                if (response.isSuccessful && response.body() != null){
//                    _fileLiveData.postValue(NetworkResult.Success(response.body()!!))
//                }else{
//                    val errorObj = JSONObject(response.errorBody()?.charStream()!!.readText())
//                    _fileLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
//                }
//            }else{
//                _fileLiveData.postValue(NetworkResult.Error("No Internet connection"))
//            }
//
//        }catch (e : Exception){
//            Log.d("ChatRepo", e.localizedMessage)
//            _fileLiveData.postValue(NetworkResult.Error("Something went wrong"))
//        }
//    }

    private val _uploadFileLiveData = MutableLiveData<NetworkResult<Uri>>()
    val uploadFileLiveData: LiveData<NetworkResult<Uri>> get() = _uploadFileLiveData

    suspend fun uploadFile(fileUri: String, type: String) {

        val uri = Uri.parse(fileUri)
        try {
            _uploadFileLiveData.postValue(NetworkResult.Loading())
            val format = SimpleDateFormat("yyyy_MM_dd_HHH_mm:ss", Locale.ENGLISH)
            val date = Date()
            val fileName = format.format(date) + tokenManager.getUserId() + "." + type

            val storageRef = FirebaseStorage.getInstance().reference.child("files")
            val url = storageRef.child(fileName).putFile(uri)
                .await()
                .storage
                .downloadUrl
                .await()

            _uploadFileLiveData.postValue(NetworkResult.Success(url))

//            .addOnCompleteListener {
//                if (it.isSuccessful){
//                _uploadFileLiveData.postValue(NetworkResult.Success(it.result.storage.downloadUrl.result))
//                }else{
//                    _uploadFileLiveData.postValue(NetworkResult.Error("file not uploaded"))
//                }
//            }
//            .addOnFailureListener {
//                _uploadFileLiveData.postValue(NetworkResult.Error(it.message))
//            }
        } catch (e: Exception) {
            _uploadFileLiveData.postValue(NetworkResult.Error(e.message))

        }
    }

}