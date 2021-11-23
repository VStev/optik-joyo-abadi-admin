package com.kp.optikjoyoabadiadmin.ui.activity.paymentdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.kp.optikjoyoabadiadmin.databinding.ActivityPaymentDetailBinding

class PaymentDetailActivity : AppCompatActivity() {

    private var _binding: ActivityPaymentDetailBinding? = null
    private lateinit var fireDB: FirebaseFirestore
    private lateinit var paymentId: String
    private val binding get() = _binding!!

    companion object {
        const val  EXTRA_PAYID = "payment1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPaymentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        paymentId = intent.getStringExtra(EXTRA_PAYID).toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView()
    }

    private fun setContentView() {
        //remove the line of code below after done developing
        FirebaseFirestore.setLoggingEnabled(true)
        //remove the line of code above after done developing
        val query = fireDB.collection("Payment").document(paymentId)
        query.get()
            .addOnSuccessListener {

            }
            .addOnSuccessListener {

            }
    }
}