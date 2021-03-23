package com.voxeet.think360.interfaces

import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent

interface ConferenceStateEvent {
    fun videoCallConferenceStateEvent(event: ConferenceStatusUpdatedEvent)
}