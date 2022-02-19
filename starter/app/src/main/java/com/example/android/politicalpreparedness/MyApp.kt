package com.example.android.politicalpreparedness

import android.app.Application
import android.util.Log
import com.example.android.politicalpreparedness.database.ElectionDatabase

class MyApp: Application() {
    private val TAG = this::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: MyAPP ")
    }
}