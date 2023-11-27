package com.ashish.videoconferencingtool.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashish.videoconferencingtool.adapter.UserRvAdapter
import com.ashish.videoconferencingtool.databinding.FragmentMainBinding
import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.utils.Extensions.gone
import com.ashish.videoconferencingtool.utils.LoadingDialog
import com.ashish.videoconferencingtool.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRvAdapter: UserRvAdapter

    private lateinit var userList: List<User>

    @Inject
    lateinit var loadingDialog: LoadingDialog

    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    lateinit var socket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userList = listOf()
//        userViewModel.getAllUsers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        userRvAdapter = UserRvAdapter(::onItemClick)
        return binding.root
    }

    private fun onItemClick(user: User) {
        val action = MainFragmentDirections.actionMainFragmentToChatFragment(user)
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (activity is MainActivity) {
//            (activity as MainActivity?)?.showNotification()
//        }


        socket.connect()

        binding.chatRv.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRv.setHasFixedSize(false)
        binding.chatRv.adapter = userRvAdapter

        binding.newContactBtn.setOnClickListener {
            findNavController().navigate(com.ashish.videoconferencingtool.R.id.action_mainFragment_to_selectNewUserFragment)
        }
//        binding.newContactBtn.isExtended = false
        binding.startChattingMsgTxt.gone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission(),
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            generateFcmToken()
//        } else {
//            askNotificationPermission()
//        }
//    }

//    private fun askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                generateFcmToken()
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                //  display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }

//    private fun generateFcmToken(){
//        if (sharedPref.getFCMToken() == null) {
//            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                    return@OnCompleteListener
//                }
//                // Get new FCM registration token
//                val token = task.result
//                sharedPref.saveFCMToken(token)
//                // Log and toast
//                val msg = "Token Generated $token"
//                Log.d(TAG, msg)
//                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
//            })
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::socket.isInitialized && socket.connected()) {
            socket.disconnect()
        }
    }
}