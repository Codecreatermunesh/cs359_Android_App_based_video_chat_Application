package com.ashish.videoconferencingtool.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashish.videoconferencingtool.adapter.UserRvAdapter
import com.ashish.videoconferencingtool.databinding.FragmentSelectNewUserBinding
import com.ashish.videoconferencingtool.models.Contact
import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.utils.Constants.TAG
import com.ashish.videoconferencingtool.utils.Extensions.toast
import com.ashish.videoconferencingtool.utils.LoadingDialog
import com.ashish.videoconferencingtool.utils.NetworkResult
import com.ashish.videoconferencingtool.viewmodels.UserVewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectNewUserFragment : Fragment() {

    private var _binding : FragmentSelectNewUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRvAdapter: UserRvAdapter

    private val userViewModel: UserVewModel by viewModels()

    @Inject
    lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectNewUserBinding.inflate(inflater, container, false)
        userRvAdapter = UserRvAdapter(::onItemClick)
        return binding.root
    }
    private fun onItemClick(user: User) {
        val action = SelectNewUserFragmentDirections.actionSelectNewUserFragmentToChatFragment(user)
        findNavController().navigate(action)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactPermission()

        binding.userRv.layoutManager = LinearLayoutManager(requireContext())
        binding.userRv.setHasFixedSize(false)
        binding.userRv.adapter = userRvAdapter


        // Observers
        userViewModel.userLiveData.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            when (it) {
                is NetworkResult.Error -> {
                    Log.d(TAG, it.message!!)
                }
                is NetworkResult.Loading -> loadingDialog.startLoading()
                is NetworkResult.Success -> {
                    it.data?.let { users ->
                        userRvAdapter.submitList(users)
                        binding.contactCountTxt.text = "${users.size} Contacts"
                    }
                }
            }
        }
    }

    // get Permissions to access Contacts
    private fun contactPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            userViewModel.getAllUsers(stringManipulation(getNamePhoneDetails().distinctBy { it.number }.map { it.number }))
        }else{
            requestPermissions.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it){
                userViewModel.getAllUsers(
                    stringManipulation(getNamePhoneDetails().distinctBy {contact ->contact.number }.map { contact ->contact.number }))
            }else{
                toast("Read Contacts Permission denied")
            }
        }

    //Get Contacts
    @SuppressLint("Range")
    fun getNamePhoneDetails(): MutableList<Contact> {
        val names: MutableList<Contact> = mutableListOf()
        val cr = requireContext().contentResolver
        val cur = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            null, null, null
        )
        if (cur!!.count > 0) {
            while (cur.moveToNext()) {
                val id =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID))
                val name =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                names.add(Contact(id, name, number))
            }
        }
        return names
    }
    private fun stringManipulation(contacts : List<String>):List<Long>{
        val list =  contacts.map {
            val number = it.replace(" ","").replace("-","").replace("+","")
               if (number.length>10)
                    number.slice(2..<number.length).toLong()
            else
                number.toLong()

        }
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding
    }
}