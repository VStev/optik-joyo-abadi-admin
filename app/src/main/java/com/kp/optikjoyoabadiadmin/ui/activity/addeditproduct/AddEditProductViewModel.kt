package com.kp.optikjoyoabadiadmin.ui.activity.addeditproduct

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.kp.optikjoyoabadiadmin.model.Product

class AddEditProductViewModel: ViewModel() {

    private val fireDB = Firebase.firestore
    private val storage = Firebase.storage
    private val reference = storage.reference

    fun submitData(data: Product, image: Uri): LiveData<String> {
        //remove after development
        FirebaseFirestore.setLoggingEnabled(true)
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }
        val uploadTask = reference.child("products/${data.image_url}").putFile(image, metadata)
        val value = MutableLiveData<String>().apply{
            value = "onProcess"
        }
        val product = hashMapOf(
            "productId" to data.productId,
            "productName" to data.productName,
            "category" to data.category,
            "shape" to data.shape,
            "price" to data.price,
            "stock" to data.stock,
            "details" to data.details,
            "image_url" to data.image_url
        )
        uploadTask
            .addOnSuccessListener {
                fireDB.collection("Products").document(data.productId)
                    .set(product)
                    .addOnSuccessListener {
                        value.value = "Success!"
                    }
                Log.d("TAG", "submitData: IMAGE UPLOADED")
            }
            .addOnFailureListener {
                value.value = "Failed!"
            }
        return value
    }
}