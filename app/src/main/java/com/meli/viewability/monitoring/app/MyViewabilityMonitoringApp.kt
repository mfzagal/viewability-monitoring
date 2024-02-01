package com.meli.viewability.monitoring.app

import android.app.Application
import android.os.StrictMode
import android.util.Log
//import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.metrics.AddTrace
import com.iab.omid.library.mercadolibre.Omid
import com.meli.viewability.monitoring.BuildConfig
import com.meli.viewability.monitoring.utils.TAG
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.measureTimeMillis

@HiltAndroidApp
class MyViewabilityMonitoringApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // Habilitar el modo estricto solo en compilaciones de depuración (debug)
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    //.penaltyDeath()
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    //.penaltyDeath()
                    .build()
            )
        }

        //MobileAds.initialize(this) //POC
        initializeFirebase()

        val timeInMillis = measureTimeMillis {
            initializeOmsdk()
        }
        Log.d(TAG, "initializeOmsdk : ${timeInMillis}ms")
    }

    @AddTrace(name = "initializeOmsdk")
    private fun initializeOmsdk() {
        Log.d(TAG, "initializeOmsdk")
        Omid.activate(this)
        if (Omid.isActive()) {
            Log.d(TAG, "Omid ${Omid.getVersion()}")
        } else {
            Log.d(TAG, "fail initializeOmsdk")
        }
    }

    private fun initializeFirebase() {
        FirebaseApp.initializeApp(this)
    }
}