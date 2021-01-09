package ipvc.estg.chatmenseger.Notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceID: FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val refreshToken=FirebaseInstanceId.getInstance().token


    }
}