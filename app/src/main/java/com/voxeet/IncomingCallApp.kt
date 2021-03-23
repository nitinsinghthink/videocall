package com.voxeet
import androidx.multidex.MultiDexApplication

class IncomingCallApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        CreateCall().initSDK("NWhvYTR2ZDlzbXRyOA==", "N2VxcWV0aWphMDMzMGdyNzV1dWl1ZzR1ag==", this)
    }
}