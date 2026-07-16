package mads.group3.stagecheck.common.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMessaging : FirebaseMessagingService() {
    @Deprecated("Deprecated in Java")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendTokenToFirestore(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.body?.let { Log.d("FCM", it) }
    }

    private fun sendTokenToFirestore(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.d("FCM", "No user logged in")
            return
        }

        val uid = currentUser.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid)
            .update("fcmTokens", FieldValue.arrayUnion(token))
            .addOnSuccessListener {
                Log.d("FCM", "Token saved successfully for user $uid")
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "Failed to save token, ${e.message}")
            }
    }
}