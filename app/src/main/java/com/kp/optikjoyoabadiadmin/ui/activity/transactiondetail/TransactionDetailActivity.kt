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
        val reference = fireDB.collection("Transaction").document(transactionId)

        binding.buttonCancelOrder.setOnClickListener {
            reference
                .update("status", "CANCELLED")
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "Pesanan telah dibatalkan.",
                        Toast.LENGTH_SHORT).show()
                    showLayout()
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
                    commitStock()
                    showLayout()
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext, "Gagal mengubah status pesanan! $it",
                        Toast.LENGTH_SHORT).show()
                }
        }

        binding.buttonResi.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(baseContext)
            builder.setTitle("Masukkan Nomor Resi")
            val input = EditText(baseContext)
            builder.setView(input)
            builder.setPositiveButton("Ok") { dialog, _ ->
                reference
                    .update(mapOf(
                        "status" to "SHIPPED",
                        "shippingNumber" to input.text.toString()
                    ))
                    .addOnSuccessListener {
                        Toast.makeText(baseContext, "Pesanan telah dikirimkan.",
                            Toast.LENGTH_SHORT).show()
                        showLayout()
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
        val query = fireDB.collection("Transaction").document(transactionId)
        val rvQuery = fireDB.collection("TransactionDetail")
            .whereEqualTo("transactionId", transactionId)
        val reference = Firebase.storage.reference
        query.get().addOnCompleteListener {
            if (it.isSuccessful){
                val data = it.result?.toObject<Transaction>()
                val region = data?.city + data?.region
                binding.transactionInvoices.text = data?.transactionId
                binding.tanggalBeli.text = data?.dateTime.toString()
                binding.statusTransaksi.text = data?.status
                binding.namaPenerima.text = data?.recipientName
                binding.alamatPenerima.text = data?.street
                binding.regionPenerima.text = region
                binding.postalCodePenerima.text = data?.postalCode.toString()
                binding.subtotalPrice.text = data?.subtotal.toString()
                binding.shippingPrice.text = data?.shippingFee.toString()
                binding.totalPrice.text = data?.total.toString()
                if (data?.shippingNumber != ""){
                    binding.noResiPenerima.text = data?.shippingNumber
                    binding.noResiPenerima.visibility = View.VISIBLE
                    binding.buttonResi.visibility = View.GONE
                }
                if (data?.status != "UNCONFIRMED" || data.status != "WAITING PAYMENT"){
                    binding.buttonProcessOrder.visibility = View.GONE
                    binding.buttonCancelOrder.visibility = View.GONE
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

    private fun commitStock(){
        val query = fireDB.collection("TransactionDetail")
            .whereEqualTo("transactionId", transactionId)
        query.get()
            .addOnSuccessListener {
                for (i in 0 until it.size()-1){
                    val item = it.documents[i].toObject<TransactionDetail>()
                    val reference = item?.let { it1 -> fireDB.collection("Products").document(it1.productId) }
                    reference?.get()?.addOnSuccessListener { product ->
                        val stock = product["stock"] as Int
                        val newStock = if (stock - item.quantity < 0) 0 else stock - item.quantity
                        reference.update(
                            mapOf(
                                "stock" to newStock
                            )
                        )
                    }
                }
            }
    }
}