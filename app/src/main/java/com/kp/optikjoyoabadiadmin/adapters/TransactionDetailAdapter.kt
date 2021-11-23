package com.kp.optikjoyoabadiadmin.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import com.kp.optikjoyoabadiadmin.databinding.ItemTransactionDetailsBinding
import com.kp.optikjoyoabadiadmin.model.TransactionDetail

open class TransactionDetailAdapter(query: Query, private val reference: StorageReference): FirestoreAdapter<TransactionDetailAdapter.CardViewHolder>(query) {
    //revised

    inner class CardViewHolder(private val items: ItemTransactionDetailsBinding) : RecyclerView.ViewHolder(items.root) {
        fun bind(data: DocumentSnapshot) {
            val transactionDetail = data.toObject<TransactionDetail>()
            Log.d("bind:", "$transactionDetail")
            //revised
            items.txtNote.text = transactionDetail?.notes
            items.txtQuantity.text = transactionDetail?.quantity.toString()
            items.txtItemname.text = transactionDetail?.productName
            items.txtPrice.text = transactionDetail?.price.toString()
            val image = transactionDetail?.let { reference.child("products/${it.image_url}") }
            Glide.with(items.root)
                .load(image)
                .override(128,128)
                .into(items.productPictureThumb)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(ItemTransactionDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }
}