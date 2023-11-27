package com.ashish.videoconferencingtool

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

    }

//    fun  showNotification(){
//        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "channel_id")
//            .setSmallIcon(R.drawable.logo)
//            .setContentTitle("New Message")
//            .setContentText("You have a new message on WhatsApp")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//        val intent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        builder.setContentIntent(pendingIntent)
//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//
//            val notificationManager = NotificationManagerCompat.from(this)
//            notificationManager.notify(1, builder.build())
//        }
//    }
}