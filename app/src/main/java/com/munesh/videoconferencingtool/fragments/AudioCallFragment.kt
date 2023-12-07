package com.munesh.videoconferencingtool.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.munesh.videoconferencingtool.R
import com.munesh.videoconferencingtool.databinding.FragmentAudioCallBinding
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class AudioCallFragment : Fragment() {

    private val args : AudioCallFragmentArgs by navArgs()

    private var _binding: FragmentAudioCallBinding? = null
    private val binding get() = _binding!!

    private val appId = "29af2dd4be72459980bc046050ddc0bc"

    // Fill the channel name.

    private val channelName = "ashish"

    // Fill the temp token generated on Agora Console.
    private val token =
        "007eJxTYHg5Paox6FZxR3vMped31/xUvHucWTz91QSdl2WFAWKfpxcpMBhZJqYZpaSYJKWaG5mYWlpaGCQlG5iYGZgapKQkA9l/LPJTGwIZGSIWNzEyMkAgiM/GkFickVmcwcAAAFgYIpo="

    // An integer that identifies the local user.
    private val uid = 0

    // Track the status of your connection
    private var isJoined = false

    // Agora engine instance
    private var agoraEngine: RtcEngine? = null

    private lateinit var audioManager : AudioManager

    private lateinit var timer : Timer

    // UI elements

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioCallBinding.inflate(inflater, container, false)
        binding.userName.text = args.username
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = false


        setupToggleAudioDevice()

        if (checkSelfPermission()) {
            setupVoiceSDKEngine()
        } else {
            requestPermission.launch(android.Manifest.permission.RECORD_AUDIO)
        }

        binding.leaveButton.setOnClickListener {
            joinLeaveChannel()
        }
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                setupVoiceSDKEngine()
            } else {
                showMessage("Please Grant Permission to use voice calling")
            }
        }


    private fun setupVoiceSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = requireActivity().baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            joinChannel()
        } catch (e: Exception) {
            throw RuntimeException("Check the error.")
        }
    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel.
        override fun onUserJoined(uid: Int, elapsed: Int) {

//                infoText!!.text = "Remote user joined: $uid"

                    requireActivity().runOnUiThread {
                        stopwatch()
                    }
                }


        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            // Successfully joined a channel
            isJoined = true
            showMessage("Joined Channel $channel")
//            requireActivity().runOnUiThread {
//                infoText!!.text = "Waiting for a remote user to join"
//            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            // Listen for remote users leaving the channel
            showMessage("Remote user offline $uid $reason")
            if (isJoined) requireActivity().runOnUiThread {
//                infoText!!.text = "Waiting for a remote user to join"
                findNavController().popBackStack()
            }
        }

        override fun onLeaveChannel(stats: RtcStats) {
            // Listen for the local user leaving the channel
//            requireActivity().runOnUiThread { infoText!!.text = "Press the button to join a channel" }
            isJoined = false
        }
    }

    private fun joinChannel() {
        val options = ChannelMediaOptions()
        options.autoSubscribeAudio = true
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING

        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine!!.joinChannel(token, channelName, uid, options)
    }

    private fun joinLeaveChannel() {
        if (isJoined) {
            agoraEngine!!.leaveChannel()
            findNavController().popBackStack()
//            joinLeaveButton!!.text = "Join"
        } else {
            joinChannel()
//            joinLeaveButton!!.text = "Leave"
        }
    }


    fun showMessage(message: String?) {
        requireActivity().runOnUiThread {
            Toast.makeText(
                requireContext().applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkSelfPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        super.onDestroy()
        agoraEngine!!.leaveChannel()
        // Destroy the engine in a sub-thread to avoid congestion
        // Destroy the engine in a sub-thread to avoid congestion
        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun stopwatch() {
        var num = 0L
        timer = Timer()
        val tt : TimerTask = object : TimerTask(){
            @SuppressLint("SetTextI18n")
            override fun run() {
                num+=1000L
                val minutes = TimeUnit.MILLISECONDS.toMinutes(num)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(num) - TimeUnit
                    .MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(num))

                requireActivity().runOnUiThread {
                    binding.calling.text = " %02d:%02d".format(minutes, seconds)
                }
            }
        }
        timer.schedule(tt, 0L, 1000L)

    }

    private fun setupToggleAudioDevice() {

        binding.toggleAudioDevice.setOnClickListener {
            val isSpeakerOn = audioManager.isSpeakerphoneOn
            audioManager.isSpeakerphoneOn = !isSpeakerOn

            if (isSpeakerOn) {
                //we should set it to earpiece mode
                binding.toggleAudioDevice.setImageResource(R.drawable.ic_speaker)
                //we should send a command to our service to switch between devices
            } else {
                //we should set it to speaker mode
                binding.toggleAudioDevice.setImageResource(R.drawable.ic_ear)
            }
        }
    }

}