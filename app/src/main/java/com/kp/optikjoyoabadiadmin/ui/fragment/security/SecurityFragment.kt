package com.kp.optikjoyoabadiadmin.ui.fragment.security

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadiadmin.R
import com.kp.optikjoyoabadiadmin.databinding.FragmentProductListBinding
import com.kp.optikjoyoabadiadmin.databinding.FragmentSecurityBinding
import com.kp.optikjoyoabadiadmin.ui.activity.LoginActivity

class SecurityFragment : Fragment() {

    private var _binding: FragmentSecurityBinding? = null
    private val binding get() = _binding!!
    private var paramPwd: String = "view"
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        return
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecurityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLayout()
        setListeners()
    }

    private fun setListeners(){
        binding.buttonSaveEditPassword.setOnClickListener {
            when(paramPwd){
                "view" -> {
                    paramPwd = "edit"
                    binding.inputOldPassword.isEnabled = true
                    binding.inputNewPasswordA.isEnabled = true
                    binding.inputNewPasswordB.isEnabled = true
                    binding.buttonCancelEditPassword.visibility = View.VISIBLE
                    binding.buttonSaveEditPassword.setText(R.string.simpan)
                }
                "edit" -> {
                    paramPwd = "view"
                    val user = auth.currentUser
                    val email = user?.email.toString()
                    val oldPass = binding.inputOldPassword.text.toString()
                    val newPassA = binding.inputNewPasswordA.text.toString()
                    val newPassB = binding.inputNewPasswordB.text.toString()
                    val credential = EmailAuthProvider
                        .getCredential(email, oldPass)
                    if (newPassA != newPassB) {
                        Toast.makeText(context, "Password Tidak Sama!", Toast.LENGTH_SHORT).show()
                    }else{
                        auth.currentUser!!.reauthenticate(credential)
                            .addOnCompleteListener {
                                if (it.isSuccessful){
                                    auth.currentUser!!.updatePassword(newPassA)
                                        .addOnCompleteListener {
                                            //either relog or reauth
                                            Toast.makeText(context, "Password berashil dirubah, silahkan login kembali", Toast.LENGTH_SHORT).show()
                                            auth.signOut()
                                            startActivity(Intent(context, LoginActivity::class.java))
                                        }
                                }else{
                                    Toast.makeText(context, "Password lama anda salah!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
        }

        binding.buttonCancelEditPassword.setOnClickListener {
            binding.inputOldPassword.setText("")
            binding.inputNewPasswordA.setText("")
            binding.inputNewPasswordB.setText("")
            binding.inputOldPassword.isEnabled = false
            binding.inputNewPasswordA.isEnabled = false
            binding.inputNewPasswordB.isEnabled = false
            binding.buttonCancelEditPassword.visibility = View.GONE
            binding.buttonSaveEditPassword.setText(R.string.ubah)
        }
    }

    private fun showLayout() {
        binding.inputOldPassword.isEnabled = false
        binding.inputNewPasswordA.isEnabled = false
        binding.inputNewPasswordB.isEnabled = false
    }
}