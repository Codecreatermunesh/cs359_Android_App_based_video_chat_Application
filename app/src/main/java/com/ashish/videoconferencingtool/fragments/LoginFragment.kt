package com.ashish.videoconferencingtool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.ashish.videoconferencingtool.viewmodels.AuthVewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel : AuthVewModel by viewModels()

    @Inject
    lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val mobile = binding.mobileItl.getText("Please enter mobile Number")
        val password = binding.mobileItl.getMobile()
        return  if (mobile != null  && password != null ){
            UserSignInReq(mobile = mobile, password = password)
        }else null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}