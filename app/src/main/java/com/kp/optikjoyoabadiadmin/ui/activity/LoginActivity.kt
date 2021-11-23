package com.kp.optikjoyoabadiadmin.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.kp.optikjoyoabadiadmin.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var fireDB: FirebaseFirestore
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        setListeners()
    }

    private fun updateUI(user: FirebaseUser?){
        if (user != null) {
            var account: String
            //reference to document header, header of User always uses UID
            val reference = fireDB.collection("Users").document(user.uid)
            //when complete do this
            reference.get().addOnCompleteListener {
                //if success
                if (it.isSuccessful){
                    //gets the account type see if its Admin or not
                    account = it.result?.get("Type") as String
                    if (account != "Admin"){
                        //not admin then no login
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }else{
                        //admin then admit
                       // maybe assign the FCMTOKEN here
                        Log.d("TAG", "signIn:success")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    //when fail to get data
                    Log.w("TAG", "signIn:failure", it.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setListeners(){
        binding.buttonLogin.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        //if authentication is not banned then admit else nope
        auth.signInWithEmailAndPassword(binding.inputLoginId.text.toString(), binding.inputPassword.text.toString())
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signIn:failure", it.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}