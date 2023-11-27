package com.ashish.videoconferencingtool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ashish.videoconferencingtool.R
import com.ashish.videoconferencingtool.databinding.FragmentChatBinding
import com.ashish.videoconferencingtool.utils.Constants.DAY
import com.ashish.videoconferencingtool.utils.Constants.IS_ONLINE
import com.ashish.videoconferencingtool.utils.Constants.LAST_SEEN
import com.ashish.videoconferencingtool.utils.Constants.USER_ONLINE_STATUS
import com.ashish.videoconferencingtool.utils.Extensions.gone
import com.ashish.videoconferencingtool.utils.Extensions.invisible
import com.ashish.videoconferencingtool.utils.Extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val args : ChatFragmentArgs by navArgs()

    private var _binding : FragmentChatBinding? = null

    @Inject
    lateinit var socket : Socket
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.message.requestFocus()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = args.user
        //Set Values
        binding.titleTxt.text = args.user.name
        binding.onlineStatusTxt.text = if (user.isOnline){
            "Online"
        }else{
           getLastSeenStatus(user.lastSeen)
        }
        //Listen Io
        socket.on(USER_ONLINE_STATUS,userOnlineStatusMessage)

        // Text Change Listener
        binding.message.doOnTextChanged { text, start, before, count ->
            if (!text.isNullOrEmpty()){
                binding.sendMsgIv.visible()
                binding.micIv.invisible()
            }else{
                binding.sendMsgIv.gone()
                binding.micIv.visible()
            }
        }

        // Click Listeners
        binding.icBack.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_mainFragment)
        }

        binding.messageItl.setEndIconOnClickListener {

        }
        binding.messageItl.setStartIconOnClickListener {

        }

    }


    private val userOnlineStatusMessage = Emitter.Listener { args ->
        CoroutineScope(Dispatchers.Main).launch {
            val data = args[0] as JSONObject
            val isOnline = data.getBoolean(IS_ONLINE)
            if (isVisible){
                binding.onlineStatusTxt.text = if (isOnline) {
                    "Online"
                }else {
                    getLastSeenStatus(data.getLong(LAST_SEEN))
                }
            }
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
                "last seen today at ${SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(lastSeenTimestamp))}"
            }
            timeDifference < 2 * DAY -> "last seen yesterday at ${SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(lastSeenTimestamp))}"
            else -> "last seen ${SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Date(lastSeenTimestamp))}"
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}