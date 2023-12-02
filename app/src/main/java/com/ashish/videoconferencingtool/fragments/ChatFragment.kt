package com.ashish.videoconferencingtool.fragments

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashish.videoconferencingtool.R
import com.ashish.videoconferencingtool.adapter.MessageRvAdapter
import com.ashish.videoconferencingtool.databinding.FragmentChatBinding
import com.ashish.videoconferencingtool.models.Message
import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.utils.Constants.CHAT_SUCCESS
import com.ashish.videoconferencingtool.utils.Constants.DAY
import com.ashish.videoconferencingtool.utils.Constants.IS_ONLINE
import com.ashish.videoconferencingtool.utils.Constants.LAST_SEEN
import com.ashish.videoconferencingtool.utils.Constants.USER_ONLINE_STATUS
import com.ashish.videoconferencingtool.utils.Constants._CHAT
import com.ashish.videoconferencingtool.utils.Extensions.gone
import com.ashish.videoconferencingtool.utils.Extensions.invisible
import com.ashish.videoconferencingtool.utils.Extensions.toast
import com.ashish.videoconferencingtool.utils.Extensions.visible
import com.ashish.videoconferencingtool.utils.LoadingDialog
import com.ashish.videoconferencingtool.utils.NetworkResult
import com.ashish.videoconferencingtool.utils.SharedPref
import com.ashish.videoconferencingtool.viewmodels.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class ChatFragment : Fragment() {
    companion object {
        const val TAG = "ChatFragment"
    }

    private val args: ChatFragmentArgs by navArgs()

    private var _binding: FragmentChatBinding? = null

    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var messageRvAdapter: MessageRvAdapter

    private var selectedFile : File? = null

    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    lateinit var socket: Socket

    @Inject
    lateinit var loadingDialog: LoadingDialog

    lateinit var user: User
    private val binding get() = _binding!!

    private var file : MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val participants: List<String> = listOf(
            sharedPref.getUserId()!!, args.user.id
        )
        chatViewModel.getChatUsers(participants)
        messageRvAdapter = MessageRvAdapter(sharedPref.getUserId()!!)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observables
        observers()

        binding.msgRv.layoutManager = LinearLayoutManager(requireContext())
        binding.msgRv.setHasFixedSize(false)
        binding.msgRv.adapter = messageRvAdapter

        user = args.user
        //Set Values
        binding.titleTxt.text = args.user.name
        binding.onlineStatusTxt.text = if (user.isOnline) {
            "Online"
        } else {
            getLastSeenStatus(user.lastSeen)
        }
        //Listen Io
        socket.on(USER_ONLINE_STATUS, userOnlineStatusMessage)
        socket.on(CHAT_SUCCESS, msgSentStatus)
        socket.on("chat", receiveNewMsg)

        binding.sendMsgIv.setOnClickListener {
            val message = binding.message.text.toString()
            if (message.isNotEmpty()) {
                val msg = JSONObject()
                msg.put("message", message)
                msg.put("receiver", user.id)
                socket.emit(_CHAT, msg)
            }
        }

        // Text Change Listener
        binding.message.doOnTextChanged { text, start, before, count ->
            if (!text.isNullOrEmpty()) {
                binding.sendMsgIv.visible()
                binding.micIv.invisible()
                binding.attachmentIv.gone()
            } else {
                binding.sendMsgIv.gone()
                binding.micIv.visible()
                binding.attachmentIv.visible()
            }
        }

        binding.attachmentIv.setOnClickListener {
            selectFile.launch("*/*")
        }

        // Click Listeners
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_mainFragment)
        }
        binding.playVideoIv.setOnClickListener {
            if (binding.videoview.isPlaying){
                binding.videoview.pause()
                binding.playVideoIv.setImageResource(R.drawable.ic_play)
            }else{
                binding.videoview.start()
                binding.playVideoIv.setImageResource(R.drawable.baseline_pause)
            }
        }

        binding.cancelFileIv.setOnClickListener {
            binding.attachmentLayout.gone()
            selectedFile?.delete()
            file = null
            if (binding.videoview.isPlaying){
                binding.videoview.pause()
                binding.videoview.setVideoURI(null)
            }
        }

        binding.sendFileIv.setOnClickListener {
            if (file != null){
                toast("All is Right")
                chatViewModel.uploadFile(file!!,user.id)
            }else{
                toast("Something went wrong")
            }
        }

//        binding.fileIv.setOnClickListener {
//             selectFile.launch("*/*")
//        }
//        binding.photosIv.setOnClickListener {
//            selectImage.launch("image/*")
//        }
//        binding.videoIv.setOnClickListener {
//            selectVideo.launch("video/*")
//        }
    }

    private fun observers() {
        chatViewModel.msgSentLiveData.observe(viewLifecycleOwner) {
            if (it) {
                binding.message.setText("")
            } else {
                toast("Something went wrong")
            }
        }
        chatViewModel.userChatListLiveData.observe(viewLifecycleOwner) {
            messageRvAdapter.submitList(it.distinctBy { msg -> msg._id })
            binding.msgRv.scrollToPosition(it.size - 1)
        }
        chatViewModel.chatUserLiveData.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            when (it) {
                is NetworkResult.Error -> Log.e(TAG, "${it.message}")
                is NetworkResult.Loading -> loadingDialog.startLoading()
                is NetworkResult.Success -> {
                    it.data?.let { msgList -> chatViewModel.setUserChatList(msgList) }
                }
            }
        }

        chatViewModel.msgSentLiveData.observe(viewLifecycleOwner) {
            if (it) {
                binding.message.setText("")
            }
        }

        //Upload File Observable
        chatViewModel.fileLiveData.observe(viewLifecycleOwner){
            loadingDialog.dismiss()
            when(it) {
                is NetworkResult.Error -> {
                    Log.e(TAG, "${it.message}")
                }
                is NetworkResult.Loading -> loadingDialog.startLoading()
                is NetworkResult.Success -> {
                    chatViewModel.setNewMessage(it.data!!)
                    binding.attachmentLayout.gone()
                    selectedFile?.delete()
                    file = null
                    if (binding.videoLayout.isVisible){
                        binding.videoLayout.gone()
                    }
                    if (binding.fileLayout.isVisible){
                        binding.fileLayout.gone()
                    }
                    if (binding.selectedImage.isVisible){
                        binding.selectedImage.gone()
                    }
                    if (binding.videoview.isPlaying){
                        binding.videoview.pause()
                        binding.videoview.setVideoURI(null)
                    }
                }
            }
        }
    }

    private val userOnlineStatusMessage = Emitter.Listener { args ->
        CoroutineScope(Dispatchers.Main).launch {
            val data = args[0] as JSONObject
            val isOnline = data.getBoolean(IS_ONLINE)
            if (isVisible) {
                binding.onlineStatusTxt.text = if (isOnline) {
                    "Online"
                } else {
                    getLastSeenStatus(data.getLong(LAST_SEEN))
                }
            }
        }
    }

    // Listen New Message Received
    private val receiveNewMsg = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val id = data.getString("_id")
        val senderId = data.getString("senderId")
        val timestamp = data.getLong("timestamp")
        val msg = data.getString("message")
        chatViewModel.setNewMessage(Message(id, msg, senderId, timestamp))
    }

    // Listen Message Successfully send
    private val msgSentStatus = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val result = data.getBoolean("result")
        val id = data.getString("id")
        val timestamp = data.getLong("timestamp")
        val msg = data.getString("message")
        if (result) {
            val senderId = sharedPref.getUserId()!!
            val message =
                Message(_id = id, senderId = senderId, message = msg, timestamp = timestamp)
            chatViewModel.setNewMessage(message)
            chatViewModel.msgSentStatusLiveData(result)
        }
    }

    private fun getLastSeenStatus(lastSeenTimestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - lastSeenTimestamp

        return when {
//            timeDifference < 2 * MINUTE -> "Just now"
//            timeDifference < HOUR -> "${timeDifference / MINUTE} minutes ago"
//            timeDifference < 2 * HOUR -> "an hour ago"
            timeDifference < DAY -> {
                "last seen today at ${
                    SimpleDateFormat("HH:mm", Locale.ENGLISH).format(
                        Date(
                            lastSeenTimestamp
                        )
                    )
                }"
            }

            timeDifference < 2 * DAY -> "last seen yesterday at ${
                SimpleDateFormat(
                    "HH:mm",
                    Locale.ENGLISH
                ).format(Date(lastSeenTimestamp))
            }"

            else -> "last seen ${
                SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(
                    Date(
                        lastSeenTimestamp
                    )
                )
            }"
        }
    }

    private val selectFile = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try {
            it?.let { uri: Uri ->
                binding.attachmentLayout.visible()
                val cR = requireContext().contentResolver
                val mime = MimeTypeMap.getSingleton()
                val mimeType = cR.getType(uri)!!
                Log.d(TAG, mimeType)
                val fileName = getFileName(uri)
                val type = mime.getExtensionFromMimeType(mimeType)!!
                file = getFile(fileName,mimeType,uri)
                if (mimeType.split("/").first() == "image"){
                    binding.selectedImage.setImageURI(uri)
                    binding.videoLayout.gone()
                    binding.fileLayout.gone()
                    binding.selectedImage.visible()

                }else if (mimeType.split("/").first() == "video"){
                    binding.videoLayout.visible()
                    binding.selectedImage.gone()
                    binding.fileLayout.gone()
                    binding.videoview.setVideoURI(uri)
                }else{
                    binding.fileTypeTxt.text = type.uppercase()
                    binding.videoLayout.gone()
                    binding.selectedImage.gone()
                    binding.fileLayout.visible()
                }
            }
        }catch (e : NullPointerException){
            toast("Retry again")
        }
    }

    //
//    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
//        it?.let { uri: Uri ->
//            binding.attachmentIv.gone()
//            val cR = requireContext().contentResolver
//            val mime = MimeTypeMap.getSingleton()
//            val mimeType = cR.getType(uri)
//            Log.d(TAG, "IMage mimeType : $mimeType")
//            val type = mime.getExtensionFromMimeType(mimeType)
//            Log.d(TAG, "IMage Type : $type")
//        }
//
//    }
//    private val selectVideo = registerForActivityResult(ActivityResultContracts.GetContent()) {
//        it?.let { uri: Uri ->
//            binding.attachmentIv.gone()
//            val cR = requireContext().contentResolver
//            val mime = MimeTypeMap.getSingleton()
//            val mimeType = cR.getType(uri)
//            Log.d(TAG, "Video mimeType : $mimeType")
//            val type = mime.getExtensionFromMimeType(mimeType)
//            Log.d(TAG, "Video Type : $type")
//        }
//    }

    private fun getFile(fileName : String,mimeType: String, uri: Uri) : MultipartBody.Part{
        return run {
            val fileDir = requireContext().applicationContext.filesDir
            selectedFile = File(fileDir, fileName)
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(selectedFile)
            inputStream!!.copyTo(outputStream)
            inputStream.close()
            val requestBody = selectedFile!!.asRequestBody(mimeType.toMediaTypeOrNull())

            MultipartBody.Part.createFormData("file", selectedFile!!.name, requestBody)
        }
    }

    private fun getFileName(uri: Uri): String {
        var fileName: String? = null
        val contentResolver = requireContext().contentResolver

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                fileName =
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
//                    extension =
//                        cursor.getString(cursor.getColumnIndexOrThrow("mime_type")).split(("/"))
//                            .last()
            }
        }
        return fileName ?: "newFile"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectedFile?.delete()
        file = null
        _binding = null
    }

}