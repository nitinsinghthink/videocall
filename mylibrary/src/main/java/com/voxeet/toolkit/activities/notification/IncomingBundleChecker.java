package com.voxeet.toolkit.activities.notification;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


@Deprecated
public class IncomingBundleChecker extends com.voxeet.uxkit.activities.notification.IncomingBundleChecker {
    public IncomingBundleChecker(@NonNull Intent intent, @Nullable IExtraBundleFillerListener filler_listener) {
        super(intent, filler_listener);
    }
}
