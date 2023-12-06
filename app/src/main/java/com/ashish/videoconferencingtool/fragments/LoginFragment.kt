package com.ashish.videoconferencingtool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ashish.videoconferencingtool.R
import com.ashish.videoconferencingtool.databinding.FragmentLoginBinding
import com.ashish.videoconferencingtool.models.request.UserSignInReq
import com.ashish.videoconferencingtool.utils.Extensions.getMobile
import com.ashish.videoconferencingtool.utils.Extensions.getText
import com.ashish.videoconferencingtool.utils.Extensions.gone
import com.ashish.videoconferencingtool.utils.Extensions.visible
import com.ashish.videoconferencingtool.utils.LoadingDialog
import com.ashish.videoconferencingtool.utils.NetworkResult
import com.ashish.videoconferencingtool.utils.SharedPref
import com.ashish.videoconferencingtool.viewmodels.AuthVewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel : AuthVewModel by viewModels()

    @Inject
    lateinit var sharedPref : SharedPref
    @Inject
    lateinit var loadingDialog: LoadingDialog
    @Inject
    lateinit var tokenManager : SharedPref

//    private var fcmToken = ""

    override fun onStart() {
        super.onStart()
        if (tokenManager.getToken() != null) {
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
       }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        askNotificationPermission()

        binding.signInBtn.setOnClickListener {
            val userSignInReq = validateInput()
            if (userSignInReq != null){
                authViewModel.signIn(userSignInReq)
            }
        }
        binding.signUpTxt.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        authViewModel.userLiveData.observe(viewLifecycleOwner){
            loadingDialog.dismiss()
            when(it){
                is NetworkResult.Error -> {
                    binding.msgTxt.text = it.message
                    binding.msgTxt.visible()
                }
                is NetworkResult.Loading -> loadingDialog.startLoading()
                is NetworkResult.Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                }
            }
        }
    }

    private fun validateInput():UserSignInReq?{
        binding.msgTxt.gone()
        val mobile = binding.mobileItl.getMobile()
        val password = binding.passwordItl.getText("Please enter your password")
        return  if (mobile != null  && password != null ){
            UserSignInReq(mobile = mobile.toLong(), password = password)
        }else null

    }

//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission(),
//    ) { isGranted: Boolean ->
//        if (!isGranted) {
//            toast("Notification Permission denied")
//        }
//    }
//    private fun askNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) !=
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }

//    private fun generateFcmToken(){
//        if (sharedPref.getFCMToken() == null) {
//            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w(Constants.TAG, "Fetching FCM registration token failed", task.exception)
//                    return@OnCompleteListener
//                }
//                fcmToken = task.result
//                sharedPref.saveFCMToken(fcmToken)
//
//            })
//        }else{
//            fcmToken = sharedPref.getFCMToken()!!
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}