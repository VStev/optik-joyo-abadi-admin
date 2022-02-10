package com.kp.optikjoyoabadiadmin.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.kp.optikjoyoabadiadmin.databinding.TransactionCardLayoutBinding
import com.kp.optikjoyoabadiadmin.getFirebaseFirestoreInstance
import com.kp.optikjoyoabadiadmin.model.Transaction
import com.kp.optikjoyoabadiadmin.ui.activity.transactiondetail.TransactionDetailActivity

open class TransactionAdapter(query: Query): FirestoreAdapter<TransactionAdapter.CardViewHolder>(query) {

    inner class CardViewHolder(private val items: TransactionCardLayoutBinding) : RecyclerView.ViewHolder(items.root) {
        fun bind(data: DocumentSnapshot) {
            val transaction = data.toObject<Transaction>()
            Log.d("bind:", "$transaction")
            items.txtTransactionNumber.text = transaction?.headerTitle
            items.txtTransactionStatus.text = transaction?.status
            items.txtTransactionTotal.text = "Rp. ${transaction?.total.toString()}"
            items.root.setOnClickListener {
                val intent = Intent(items.root.context, TransactionDetailActivity::class.java)
                intent.putExtra(TransactionDetailActivity.EXTRA_ID, transaction?.transactionId)
                items.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(TransactionCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }

    fun updateQuery(argument: String){
        val query = when {
            (argument == "all") -> {
                getFirebaseFirestoreInstance().collection("Transactions")
                    .orderBy("dateTime", Query.Direction.DESCENDING)
            }
            else -> {
                getFirebaseFirestoreInstance().collection("Transactions")
                    .orderBy("dateTime", Query.Direction.DESCENDING)
                    .whereEqualTo("status", argument)
            }
        }
        this.setQuery(query)
    }
}