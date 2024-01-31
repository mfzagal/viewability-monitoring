package com.meli.viewability.monitoring.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.metrics.AddTrace
import com.iab.omid.library.mercadolibre.Omid
import com.meli.viewability.monitoring.utils.TAG
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.measureTimeMillis

@HiltAndroidApp
class MyViewabilityMonitoringApp : Application() {

    override fun onCreate() {
        super.onCreate()

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