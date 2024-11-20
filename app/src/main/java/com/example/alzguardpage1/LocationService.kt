package com.example.alzguardpage1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach



class LocationService: Service() {

    private lateinit var wakeLock: PowerManager.WakeLock
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
                applicationContext,
                LocationServices.getFusedLocationProviderClient(applicationContext)
        )



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val userId = intent?.getStringExtra("userId")
        when(intent?.action) {
            ACTION_START -> userId?.let { start(it) }
            ACTION_STOP -> stop()
        }
        if (userId != null){
            Log.e("userid", userId)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(userId: String) {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag")
        wakeLock?.acquire()



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Location"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("location", name, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "location")
                .setContentTitle("Tracking location...")
                .setContentText("Location: null")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager





        Log.e("id",userId)
        locationClient
                .getLocationUpdates(1000L)
                .catch { e -> e.printStackTrace() }
                .onEach { location ->
                    val lat = location.latitude.toString()
                    val long = location.longitude.toString()
                    val updatedNotification = notification.setContentText(
                            "Location: ($lat, $long)"
                    )

                    notificationManager.notify(1, updatedNotification.build())

                    val databaseRef = FirebaseDatabase.getInstance("https://alzguardpage1-default-rtdb.europe-west1.firebasedatabase.app")
                            .getReference("patient")
                            .orderByChild("userid")
                            .equalTo(userId)

                    databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (ds in dataSnapshot.children) {
                                    val patientId = ds.key.toString()
                                    val data = hashMapOf(
                                            "latitude" to location.latitude,
                                            "longitude" to location.longitude
                                    )
                                    FirebaseDatabase.getInstance("https://alzguardpage1-default-rtdb.europe-west1.firebasedatabase.app")
                                            .getReference("patient")
                                            .child(patientId)
                                            .child("location")
                                            .setValue(data)
                                            .addOnSuccessListener {
                                                Log.d("Firebase", "Location updated successfully")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("Firebase", "Location update failed", e)
                                            }
                                }
                            } else {
                                Log.e("Firebase", "No user with the given ID found")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Database query cancelled", error.toException())
                        }
                    })
                }
                .launchIn(serviceScope)


        startForeground(1, notification.build())

    }






    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        wakeLock?.release()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}