package com.voxeet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent
import com.voxeet.sdk.services.conference.information.ConferenceStatus
import com.voxeet.think360.interfaces.ConferenceStateEvent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}