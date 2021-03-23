package com.voxeet.uxkit.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.voxeet.R;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.implementation.overlays.OverlayState;

import java.io.ByteArrayOutputStream;


/**
 * Created by think360user on 07-Mar-19.
 */

public class ViewPicDialog extends Dialog implements View.OnClickListener {

    Context context;
    Uri imageUrl;
    ImageView mImage;
    ImageView mBack;
    TextView mSend;
    ProgressBar progressBar;
    FieldListener fieldListener;
    String imagetext;
    boolean adapter = false;

    public ViewPicDialog(Context context, String Image, Uri imgeUri, boolean adapter) {
        super(context);
        this.context = context;
        this.imageUrl = imgeUri;
        this.adapter = adapter;
        this.imagetext = Image;
    }

    public void getText(FieldListener fieldListener) {
        this.fieldListener = fieldListener;
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_pic_layout);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.FILL_PARENT;
        params.height = WindowManager.LayoutParams.FILL_PARENT;


        mImage = findViewById(R.id.imageid);
        mImage.setMinimumHeight(params.height);
        mImage.setMinimumWidth(params.width);
        mBack = findViewById(R.id.backbtn);
        mBack.setOnClickListener(this);

        progressBar = findViewById(R.id.pb);
        mSend = findViewById(R.id.sendimage);
        VoxeetToolkit.instance().getConferenceToolkit()
                .setDefaultOverlayState(OverlayState.MINIMIZED);
        if (!adapter) {
            mImage.setImageURI(imageUrl);
        } else {
            mSend.setVisibility(View.GONE);
            setImage();
        }

        // title.setText(strTitle);

//        if (imageUrl != null && !imageUrl.equalsIgnoreCase("") && !imageUrl.equalsIgnoreCase("null") && mImage != null) {
//            Glide.with(context).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
//                @Override
//                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                    return false;
//                }
//
//                @Override
//                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                    progressBar.setVisibility(View.GONE);
//                    return false;
//                }
//            }).into(mImage);


//        }
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldListener.onClick("send");
                dismiss();
            }
        });


       setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                 dismiss();
                }
                return true;
            }
        });
    }

    private void setImage() {


        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();

        final Handler handler = new Handler();

        Thread th = new Thread(new Runnable() {
            public void run() {

                try {

                    long imageLength = 0;

                    ImageManager.GetImage(imagetext, imageStream, imageLength);

                    handler.post(new Runnable() {

                        public void run() {
                            byte[] buffer = imageStream.toByteArray();

                            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                            mImage.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception ex) {
                    final String exceptionMessage = ex.getMessage();
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(context, exceptionMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        th.start();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backbtn) {
            dismiss();
        } else if (id == R.id.sendimage) {
            fieldListener.onClick("send");
            dismiss();
        }
    }
}
