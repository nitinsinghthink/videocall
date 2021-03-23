package com.voxeet.uxkit.implementation.overlays.abs;

import android.content.Context;
import android.util.AttributeSet;

import com.voxeet.uxkit.implementation.VoxeetView;

/**
 * Created by kevinleperf on 18/01/2018.
 */

public abstract class AbstractVoxeetExpandableView extends VoxeetView implements IExpandableViewListener {


    public AbstractVoxeetExpandableView(Context context) {
        super(context);
    }

    public AbstractVoxeetExpandableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractVoxeetExpandableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
