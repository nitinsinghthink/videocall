package com.voxeet.think360.app
import android.app.Application
import androidx.multidex.MultiDexApplication
import com.voxeet.VoxeetSDK
import com.voxeet.sdk.push.center.NotificationCenter
import com.voxeet.sdk.push.center.management.EnforcedNotificationMode
import com.voxeet.sdk.push.center.management.NotificationMode
import com.voxeet.sdk.push.center.management.VersionFilter
import com.voxeet.uxkit.activities.notification.DefaultIncomingCallActivity
import com.voxeet.uxkit.controllers.ConferenceToolkitController
import com.voxeet.uxkit.controllers.VoxeetToolkit
import com.voxeet.uxkit.implementation.overlays.OverlayState
import com.voxeet.uxkit.incoming.IncomingFullScreen
import com.voxeet.uxkit.incoming.IncomingNotification
import org.greenrobot.eventbus.EventBus

class CallApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        initSDK("NWhvYTR2ZDlzbXRyOA==", "N2VxcWV0aWphMDMzMGdyNzV1dWl1ZzR1ag==", this)
    }

    private fun initSDK(ConsumerKey: String, ConsumerSecret: String, context: Application) {
        VoxeetSDK.initialize(
                ConsumerKey,
                ConsumerSecret
        )
        VoxeetToolkit.initialize(
                context,
                VoxeetSDK.instance().eventBus
        )
        VoxeetToolkit.initialize(context, EventBus.getDefault())
                .enableOverlay(true)

        //change the overlay used by default
        VoxeetToolkit.instance().conferenceToolkit
                .setScreenShareEnabled(false).defaultOverlayState = OverlayState.EXPANDED
        VoxeetToolkit.instance()
                .enable(
                        ConferenceToolkitController::class.java
                )
        //the default case of this SDK is to have the SDK with consumerKey and consumerSecret embedded
        onSdkInitialized()
    }

    private fun onSdkInitialized() {
        NotificationCenter.instance.register(
                NotificationMode.FULLSCREEN_INCOMING_CALL, IncomingFullScreen(
                DefaultIncomingCallActivity::class.java
        )
        )
        NotificationCenter.instance.register(
                NotificationMode.OVERHEAD_INCOMING_CALL,
                IncomingNotification()
        )
        NotificationCenter.instance.setEnforcedNotificationMode(EnforcedNotificationMode.MIXED_INCOMING_CALL)
        NotificationCenter.instance.register(
                NotificationMode.FULLSCREEN_INCOMING_CALL,
                VersionFilter(
                        VersionFilter.ALL,
                        21
                )
        )
    }

}