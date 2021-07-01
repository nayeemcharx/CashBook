package com.example.cashbook.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cashbook.AppConstants.AppConstants
import com.example.cashbook.HomeActivity
import com.example.cashbook.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseNotificationService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val user=FirebaseAuth.getInstance().currentUser
        if(user!=null)
            updateToken(token,user.email!!)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {

            val map: Map<String, String> = remoteMessage.data

            val title = map["title"]
            val message = map["message"]
            createOreoNotification(title!!, message!!)

        }
    }
    private fun updateToken(token: String, email: String) {

        val data = hashMapOf(
                "token" to token)

        FirebaseFirestore.getInstance()
                .collection("Tokens")
                .document(email).set(data).addOnSuccessListener{ Log.d("logictext", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("logictext", "Error writing document", e) }

    }


//    private fun createNormalNotification(
//            title: String,
//            message: String,
//            hisId: String,
//            hisImage: String,
//            chatId: String
//    ) {
//
//        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//        val builder = NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
//        builder.setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setSmallIcon(R.drawable.ic_baseline_account_box_24)
//                .setAutoCancel(true)
//                .setColor(ResourcesCompat.getColor(resources, R.color.blue, null))
//                .setSound(uri)
//
//        val intent = Intent(this, LoginActivity::class.java)
//
//        intent.putExtra("hisId", hisId)
//        intent.putExtra("hisImage", hisImage)
//        intent.putExtra("chatId", chatId)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        builder.setContentIntent(pendingIntent)
//        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        manager.notify(Random().nextInt(85 - 65), builder.build())
//
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
            title: String,
            message: String
    ) {

        val channel = NotificationChannel(
                AppConstants.CHANNEL_ID,
                "Message",
                NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("history?",true)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this, AppConstants.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notif_icon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        manager.notify(100, notification)
    }


}