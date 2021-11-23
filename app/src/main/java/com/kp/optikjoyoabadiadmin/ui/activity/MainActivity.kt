package com.kp.optikjoyoabadiadmin.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadiadmin.R
import com.kp.optikjoyoabadiadmin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val logged = auth.currentUser
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (logged == null){
            Toast.makeText(this, "AUTHENTICATION FAILED", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }else{
            //update FCM Token
        }
        val navController = findNavController(R.id.fragmentContainer)
        binding.navbarMain.setupWithNavController(navController)
    }
}