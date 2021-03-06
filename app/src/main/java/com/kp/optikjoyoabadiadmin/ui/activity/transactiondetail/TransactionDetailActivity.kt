package com.kp.optikjoyoabadiadmin.ui.activity.transactiondetail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadiadmin.R
import com.kp.optikjoyoabadiadmin.adapters.TransactionDetailAdapter
import com.kp.optikjoyoabadiadmin.databinding.ActivityTransactionDetailBinding
import com.kp.optikjoyoabadiadmin.model.Transaction
import com.kp.optikjoyoabadiadmin.model.TransactionDetail
import com.kp.optikjoyoabadiadmin.ui.activity.paymentdetail.PaymentDetailActivity

class TransactionDetailActivity : AppCompatActivity() {
    private lateinit var detailAdapter: TransactionDetailAdapter
    private var _binding: ActivityTransactionDetailBinding? = null
    private val fireDB = Firebase.firestore
    private lateinit var transactionId: String
    private val binding get() = _binding!!

    companion object {
        const val EXTRA_ID = "DEMOTRANS"
    }

    override fun onStart() {
        super.onStart()
        detailAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        detailAdapter.stopListening()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactionId = intent.getStringExtra(EXTRA_ID).toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""
        showLayout()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        val reference = fireDB.collection("Transactions").document(transactionId)

        binding.buttonCancelOrder.setOnClickListener {
            reference
                .update("status", "CANCELLED")
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "Pesanan telah dibatalkan.",
                        Toast.LENGTH_SHORT).show()
                    binding.statusTransaksi.text = "CANCELLED"
                    binding.buttonProcessOrder.visibility = View.GONE
                    binding.buttonCancelOrder.visibility = View.GONE
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext, "Gagal mengubah status pesanan! $it",
                        Toast.LENGTH_SHORT).show()
                }
        }

        binding.buttonCheckReceipt.setOnClickListener {
            val intent = Intent(it.context, PaymentDetailActivity::class.java)
            intent.putExtra(PaymentDetailActivity.EXTRA_PAYID, transactionId)
            it.context.startActivity(intent)
        }

        binding.buttonProcessOrder.setOnClickListener {
            reference
                .update("status", "PROCESSED")
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "Berhasil mengubah status pesanan.",
                        Toast.LENGTH_SHORT).show()
                    binding.buttonProcessOrder.visibility = View.GONE
                    binding.buttonResi.visibility = View.VISIBLE
                    binding.statusTransaksi.text = "PROCESSED"
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext, "Gagal mengubah status pesanan! $it",
                        Toast.LENGTH_SHORT).show()
                }
        }

        binding.buttonResi.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Masukkan Nomor Resi")
            val view = layoutInflater.inflate(R.layout.set_shipping_alert_dialog_box, null)
            builder.setView(view)
            builder.setPositiveButton("Ok") { dialog, _ ->
                val resi = view.findViewById<EditText>(R.id.addResi).text.toString()
                reference
                    .update(mapOf(
                        "status" to "SHIPPED",
                        "shippingNumber" to resi
                    ))
                    .addOnSuccessListener {
                        Toast.makeText(baseContext, "Pesanan telah dikirimkan.",
                            Toast.LENGTH_SHORT).show()
                        binding.buttonResi.visibility = View.GONE
                        binding.buttonCancelOrder.visibility = View.GONE
                        binding.statusTransaksi.text = "SHIPPED"
                        binding.noResiPenerima.visibility = View.VISIBLE
                        binding.noResiPenerima.text = resi
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(baseContext, "Gagal mengubah status pesanan! $e",
                            Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            builder.show()
        }
    }

    private fun showLayout() {
        //remove the line of code below after done developing
        FirebaseFirestore.setLoggingEnabled(true)
        //remove the line of code above after done developing
        val rv: RecyclerView = binding.recyclerTransactionItem
        val query = fireDB.collection("Transactions").document(transactionId)
        val rvQuery = fireDB.collection("TransactionDetail")
            .whereEqualTo("transactionId", transactionId)
        val reference = Firebase.storage.reference
        query.get().addOnCompleteListener {
            if (it.isSuccessful){
                val data = it.result?.toObject<Transaction>()
                val region = data?.city + data?.region
                binding.transactionInvoices.text = data?.transactionId
                binding.tanggalBeli.text = data?.dateTime?.toDate().toString()
                binding.statusTransaksi.text = data?.status
                binding.namaPenerima.text = data?.recipientName
                binding.nomorTeleponPenerima.text = data?.phoneNumber
                binding.alamatPenerima.text = data?.street
                binding.regionPenerima.text = region
                binding.postalCodePenerima.text = data?.postalCode.toString()
                binding.subtotalPrice.text = "Rp. ${data?.subTotal.toString()}"
                binding.shippingPrice.text = "Rp. ${data?.shippingFee.toString()}"
                binding.totalPrice.text = "Rp. ${data?.total.toString()}"
                if (data?.shippingNumber != ""){
                    binding.noResiPenerima.text = data?.shippingNumber
                    binding.noResiPenerima.visibility = View.VISIBLE
                    binding.buttonResi.visibility = View.GONE
                }
                if (data != null) {
                    if (data.status == "FINISHED" || data.status == "WAITING FOR PAYMENT" || data.status == "CANCELLED" || data.status == "SHIPPED"){
                        binding.buttonProcessOrder.visibility = View.GONE
                        binding.buttonCancelOrder.visibility = View.GONE
                        binding.buttonResi.visibility = View.GONE
                    }else if (data.status == "UNCONFIRMED"){
                        binding.buttonResi.visibility = View.GONE
                        binding.buttonProcessOrder.visibility = View.VISIBLE
                        binding.buttonCancelOrder.visibility = View.VISIBLE
                    }else if (data.status == "PROCESSED"){
                        binding.buttonProcessOrder.visibility = View.GONE
                        binding.noResiPenerima.visibility = View.VISIBLE
                        binding.buttonCancelOrder.visibility = View.VISIBLE
                    }
                }
            }else{
                Log.w("TAG", "loadDetail:failure", it.exception)
                Toast.makeText(baseContext, "Failed to load transaction detail",
                    Toast.LENGTH_SHORT).show()
            }
        }
        detailAdapter = object : TransactionDetailAdapter(rvQuery, reference){
            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(binding.root, "Error connecting to database", Snackbar.LENGTH_LONG).show()
            }
        }
        with (rv){
            layoutManager = LinearLayoutManager(context)
            adapter = detailAdapter
        }
    }
}