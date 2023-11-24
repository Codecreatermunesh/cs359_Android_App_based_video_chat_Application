package com.ashish.videoconferencingtool.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ashish.videoconferencingtool.R
import com.ashish.videoconferencingtool.databinding.FragmentRegistartionBinding
import com.ashish.videoconferencingtool.models.request.UserSignUpReq
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
class RegistrationFragment : Fragment() {

    private var _binding : FragmentRegistartionBinding? = null
    private val binding get() = _binding!!
    private val authViewModel : AuthVewModel by viewModels()

    @Inject
    lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentRegistartionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInTxt.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.signUpBtn.setOnClickListener {
            val userSignUpReq = validateInput()
            if (userSignUpReq != null){
                authViewModel.signUp(userSignUpReq)
            }
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
                    findNavController().navigate(R.id.action_registrationFragment_to_mainFragment)
                }
            }
        }
    }

    private fun validateInput(): UserSignUpReq?{
        binding.msgTxt.gone()
        val name = binding.nameItl.getText("Please enter your name")
        val mobile = binding.mobileItl.getText("Please enter mobile Number")
        val password = binding.mobileItl.getMobile()
        return  if (name != null  && mobile != null  && password != null ){
            UserSignUpReq(mobile = mobile, password = password, name = name)
        }else null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}