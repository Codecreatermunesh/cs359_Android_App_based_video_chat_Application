package com.ashish.videoconferencingtool.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ashish.videoconferencingtool.databinding.FragmentCallBinding
import com.ashish.videoconferencingtool.utils.Extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas


@AndroidEntryPoint
class CallFragment : Fragment() {

    private var _binding : FragmentCallBinding? = null
    private val binding get() = _binding!!

    // Fill the App ID of your project generated on Agora Console.
    private val appId = "29af2dd4be72459980bc046050ddc0bc"

    // Fill the channel name.
    private val channelName = "ashish"

    // Fill the temp token generated on Agora Console.
    private val token = "007eJxTYHg5Paox6FZxR3vMped31/xUvHucWTz91QSdl2WFAWKfpxcpMBhZJqYZpaSYJKWaG5mYWlpaGCQlG5iYGZgapKQkA9l/LPJTGwIZGSIWNzEyMkAgiM/GkFickVmcwcAAAFgYIpo="

    // An integer that identifies the local user.
    private val uid = 0
    private var isJoined = false

    private var agoraEngine: RtcEngine? = null

    //SurfaceView to render local video in a Container.
    private var localSurfaceView: SurfaceView? = null

    //SurfaceView to render Remote video in a Container.
    private var remoteSurfaceView: SurfaceView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        setupVideoSDKEngine()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!checkSelfPermission()){
            requestPermission.launch(arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO))
        }else{
            setupVideoSDKEngine()
        }



        binding.LeaveButton.setOnClickListener {
            leaveCall()
        }
    }

    private fun checkSelfPermission():Boolean {
        return !(ContextCompat.checkSelfPermission(
            requireContext(),android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(
            requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        )
    }


    private fun joinCall() {
        if (checkSelfPermission()) {
            val options = ChannelMediaOptions()

            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            // Display LocalSurfaceView.
            setupLocalVideo()
            localSurfaceView!!.visibility = View.VISIBLE
            // Start local preview.
            agoraEngine!!.startPreview()
            // Join the channel with a temp token.
            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
            agoraEngine!!.joinChannel(token, channelName, uid, options)
        } else {
            Toast.makeText(
                requireContext().applicationContext,
                "Permissions was not granted",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun leaveCall() {
        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine!!.leaveChannel()
            showMessage("You left the channel")
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
        }
        findNavController().popBackStack()
    }
    private fun setupVideoSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = requireActivity().baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine!!.enableVideo()
            joinCall()
        } catch (e: Exception) {
            showMessage(e.toString())
        }
    }
    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote host joining the channel to get the uid of the host.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined $uid")

            // Set the remote video view
            activity!!.runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            isJoined = true
            showMessage("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("Remote user offline $uid $reason")
            activity!!.runOnUiThread {
                remoteSurfaceView!!.visibility = View.GONE
                findNavController().popBackStack()
            }
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(requireActivity().baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(remoteSurfaceView)
        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        // Display RemoteSurfaceView.
        remoteSurfaceView!!.visibility = View.VISIBLE
    }

    private fun setupLocalVideo() {
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = SurfaceView(requireActivity().baseContext)
        binding.localVideoViewContainer.addView(localSurfaceView)
        // Call setupLocalVideo with a VideoCanvas having uid set to 0.
        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    private fun showMessage(message: String){
        requireActivity().runOnUiThread {
            toast(message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread{
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }
}