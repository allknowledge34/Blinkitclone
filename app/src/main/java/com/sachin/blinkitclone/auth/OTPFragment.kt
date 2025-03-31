package com.sachin.blinkitclone.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sachin.blinkitclone.R
import com.sachin.blinkitclone.Utils
import com.sachin.blinkitclone.activity.UsersMainActivity
import com.sachin.blinkitclone.databinding.FragmentOTPBinding
import com.sachin.blinkitclone.models.Users
import com.sachin.blinkitclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {
    private  val  viewModel : AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private  lateinit var userNumber : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)

        getUserNumber()
        customizingEnteringOTP()
        sendOTP()
        onLoginButtonClick()
        onBackButtonClick()
        return binding.root
    }

    private fun onLoginButtonClick() {
        binding.btnLogin.setOnClickListener {
            Utils.showDialog(requireContext(), message = "Sigin You...")
            val editTexts = arrayOf(binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5,binding.etOtp6)
            val otp = editTexts.joinToString ("") { it.text.toString()
            }

            if (otp.length < editTexts.size){
                Utils.showToast(requireContext(), message = "Please enter right otp")
            }else{
                editTexts.forEach { it.text?.clear(); it.clearFocus() }
                verifyOtp(otp)
            }
        }
    }

    private fun verifyOtp(otp: String) {
        val user = Users(uid = null, userPhoneNumber = userNumber, userAddress = " ")
        viewModel.signInWithPhoneAuthCredential(otp,userNumber, user)
        startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
        requireActivity().finish()

        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect{
                if (it){
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), message = "Logged In")
                }
            }
        }
    }

    private fun sendOTP() {
        Utils.showDialog(requireContext(), message = "Sending OTP...")
        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                otpSent.collect{
                    if (it == true){
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), message = "OTP Send..")
                    }
                }
            }

        }


    }

    private fun onBackButtonClick() {
        binding.tbOtpFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_singInFragment)
        }
    }

    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5,binding.etOtp6)
        for (i in editTexts.indices){
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1){
                        if (i < editTexts.size - 1){
                            editTexts[i +1].requestFocus()
                        }
                    }else if (s?.length == 0) {
                        if (i > 0){
                            editTexts[i - 1].requestFocus()
                        }
                    }
                }

            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text = userNumber
    }


}