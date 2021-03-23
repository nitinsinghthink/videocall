package com.voxeet.uxkit.service;

import android.os.Binder;
import androidx.annotation.NonNull;

import com.voxeet.sdk.utils.Annotate;

@Annotate
public abstract class SDKBinder<CLASS extends AbstractSDKService> extends Binder {

    @NonNull
    public abstract CLASS getService();
}
