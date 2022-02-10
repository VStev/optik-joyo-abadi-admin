package com.kp.optikjoyoabadiadmin

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val NOTIFICATION_CHANNEL_NAME = "OJA_ADMIN"
const val NOTIFICATION_CHANNEL_ID = "notify-status-admin"

fun getFirebaseFirestoreInstance(): FirebaseFirestore {
    return Firebase.firestore
}