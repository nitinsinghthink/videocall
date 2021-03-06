package com.voxeet

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.voxeet.promise.solve.ErrorPromise
import com.voxeet.promise.solve.PromiseExec
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent
import com.voxeet.sdk.json.ParticipantInfo
import com.voxeet.sdk.push.center.NotificationCenter
import com.voxeet.sdk.push.center.management.EnforcedNotificationMode
import com.voxeet.sdk.push.center.management.NotificationMode
import com.voxeet.sdk.push.center.management.VersionFilter
import com.voxeet.sdk.services.conference.information.ConferenceStatus
import com.voxeet.think360.interfaces.ConferenceStateEvent
import com.voxeet.think360.interfaces.CreateCallListener
import com.voxeet.uxkit.activities.VoxeetAppCompatActivity
import com.voxeet.uxkit.activities.notification.DefaultIncomingCallActivity
import com.voxeet.uxkit.controllers.ConferenceToolkitController
import com.voxeet.uxkit.controllers.VoxeetToolkit
import com.voxeet.uxkit.implementation.overlays.OverlayState
import com.voxeet.uxkit.incoming.IncomingFullScreen
import com.voxeet.uxkit.incoming.IncomingNotification
import org.greenrobot.eventbus.EventBus
import kotlin.system.exitProcess

class CreateCall : VoxeetAppCompatActivity() {

    private lateinit var events : ConferenceStatusUpdatedEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_call)
    }


    /**
     * Register SDK
     */
    fun registerSDK(context: Context, activity : Activity) {
        try {
            if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                    ||
                    ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.CAMERA
                        ),
                        10
                )
            }
            VoxeetSDK.instance().register(context)
        } catch (e: java.lang.NullPointerException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun initSDK(ConsumerKey: String, ConsumerSecret: String, context: Application) {
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
                        29
                )
        )
    }

    //Selected User to Connect
    fun logSelectedUser(
            name: String,
            externalId: String,
            avatarUrl: String,
            action : CreateCallListener
    ) {
        VoxeetSDK.session().open(ParticipantInfo(name, externalId, avatarUrl))
                .then<Any> { _, solver ->
                    action.call(solver)
                }
                .error { error ->
                    error.printStackTrace()
                    action.failure(error.message!!)
                }
    }


    //Join  conference Call
    fun joinCall(context: Context, activity: Activity, @NonNull conferenceAliasName : String) {
        if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    10
            )
        } else {
            VoxeetToolkit.getInstance().enable(
                    VoxeetToolkit.getInstance().conferenceToolkit
            )
            val service = VoxeetSDK.conference()
            val create =
                    service.create(conferenceAliasName)
                            .then<Any> { result, solver ->
                                try {
                                    val conferenceId =
                                            result?.conferenceId
                                                    ?: throw NullPointerException("ConferenceId null")
                                    solver.resolve(service.join(conferenceId))
                                } catch (e: Exception) {
                                    solver.reject(e)
                                }
                            }
            if (VoxeetSDK.conference().isLive) {
                VoxeetSDK.conference()
                        .leave()
                        .then(create) //.then(VoxeetSdk.conference().startVideo())
                        .then(defaultConsume())
                        .error(createErrorDump())
            } else {
                create
                        //.then(VoxeetSdk.conference().startVideo())
                        .then<Any>(defaultConsume<Any>())
                        .error(createErrorDump())
            }
        }
        Log.d("789979987", "JoinCall")
    }


    private fun <TYPE> defaultConsume(): PromiseExec<TYPE, Any?>? {
        return PromiseExec<TYPE, Any?> { result, solver ->
            //dismissLoader()
            //mName.setText("");
            //mRoomName.setText("")
            Log.e("onCall", "Result $result and Solver $solver")
        }
    }

    private fun createErrorDump(): ErrorPromise? {
        return ErrorPromise { error -> error.printStackTrace() }
    }





    override fun onConferenceState(event: ConferenceStatusUpdatedEvent) {
        super.onConferenceState(event)
        events = event
    }

    fun onConferenceState(action : ConferenceStateEvent) {
        action.videoCallConferenceStateEvent(events)
    }


    override fun onBackPressed() {
        if (VoxeetSDK.conference().isLive) {
            VoxeetSDK.conference().leave()
                    .then(defaultConsume())
                    .error(createErrorDump())
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val userService = VoxeetSDK.session()
        userService.close().then(defaultConsume())?.error(createErrorDump())
    }
}