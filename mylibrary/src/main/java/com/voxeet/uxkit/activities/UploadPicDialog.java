package com.voxeet.uxkit.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.voxeet.R;


public class UploadPicDialog extends Dialog implements View.OnClickListener {

    Context context;
    String firstText="",secondText="";
    FieldListener fieldListener;

    public UploadPicDialog(Context context) {
        super(context);
        this.context=context;
    }

    public UploadPicDialog(Context context, String firstText, String secondText) {
        super(context);
        this.context=context;
        this.firstText=firstText;
        this.secondText=secondText;
    }

    public void getText(FieldListener fieldListener){
        this.fieldListener=fieldListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 100);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        getWindow().setBackgroundDrawable(null);
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        window.setAttributes(wlp);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        init();
    }

    private void init() {
        TextView tv_cancel=(TextView)findViewById(R.id.tv_cancel);
        TextView camera=(TextView)findViewById(R.id.gallery);
        TextView gallery=(TextView)findViewById(R.id.document);

        if (firstText.length()>0) {
            camera.setText(firstText);
            gallery.setText(secondText);
        }
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fieldListener.onClick("gallery");
                dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldListener.onClick("document");
                dismiss();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldListener.onClick("cancel");
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}