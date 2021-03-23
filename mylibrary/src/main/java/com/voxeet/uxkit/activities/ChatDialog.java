package com.voxeet.uxkit.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.voxeet.VoxeetSDK;
import com.voxeet.sdk.events.sdk.MessageReceived;
import com.voxeet.R;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.implementation.overlays.OverlayState;
import com.voxeet.uxkit.utils.MessageModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatDialog extends Dialog {
    Context context;

    EditText edtMessage;
    String conferenceId;
    ImageView mSend;
    private RecyclerView recycler_message;
    public static List<MessageModel> mMessages = new ArrayList<>();
    MessageAdapter mAdapter;
    ImageView mattach;
    boolean image = true;
    private Uri imageUri;
    boolean upload = false;
    private ProgressDialog mProgress;
    ImageView mBack;


    public ChatDialog(Context context, String conferenceId) {
        super(context);
        this.context = context;
        this.conferenceId = conferenceId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat_screen);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.FILL_PARENT;
        params.height = WindowManager.LayoutParams.FILL_PARENT;

        mProgress = new ProgressDialog(context);
        mProgress.setCancelable(false);
        mProgress.setMessage("Uploading...");


        edtMessage = findViewById(R.id.txt_send);
        mSend = findViewById(R.id.btn_send);

        mAdapter = new MessageAdapter(mMessages, context);
        recycler_message = findViewById(R.id.recycler_message);
        recycler_message.setNestedScrollingEnabled(false);
        final LinearLayoutManager layoutManagerhorizental = new LinearLayoutManager(context);
        layoutManagerhorizental.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_message.setLayoutManager(layoutManagerhorizental);
        recycler_message.setAdapter(mAdapter);
        mBack = findViewById(R.id.back_img);
        mattach = findViewById(R.id.attachment);


        setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    VoxeetToolkit.instance().getConferenceToolkit()
                            .setDefaultOverlayState(OverlayState.EXPANDED);

                }
                return true;
            }
        });


        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMessage.getText().toString() != null && !edtMessage.getText().toString().trim().isEmpty() && conferenceId != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("content", "" + edtMessage.getText().toString().trim());
                        jsonObject.put("time", "" + System.currentTimeMillis() / 1000);
                        jsonObject.put("type", "text");
                        jsonObject.put("attachmentUrl", "");
                        jsonObject.put("title", "Chat_Message");

                        jsonObject.put("name", "" + VoxeetSDK.session().getParticipantInfo().getName());
                        if (VoxeetSDK.session().getParticipantInfo().getAvatarUrl() != null) {
                            jsonObject.put("avatarUrl", "" + VoxeetSDK.session().getParticipantInfo().getAvatarUrl());
                        }
                        jsonObject.put("ownerId", "" + VoxeetSDK.session().getParticipantInfo().getExternalId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("DATA", " Message data " + jsonObject);
                    VoxeetSDK.command().send(conferenceId, jsonObject.toString()).then((result, solver) -> {
                        Log.e("result ", "" + result.booleanValue());
                    }).error(error -> {
                    });
                    mMessages.add(new MessageModel("" + System.currentTimeMillis() / 1000, edtMessage.getText().toString(), true, "text", VoxeetSDK.session().getParticipantInfo().getAvatarUrl(), VoxeetSDK.session().getParticipantInfo().getExternalId(), "", VoxeetSDK.session().getParticipantInfo().getName()));
                    recyclerListHandling(true);
                }
            }
        });
        mattach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadPicDialog uploadPicDialog = new UploadPicDialog(context);
                uploadPicDialog.show();

                uploadPicDialog.getText(new FieldListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(String editText) {
                        upload = true;
                        if (editText.equals("gallery")) {
                            image = true;
                        } else {
                            image = false;
                        }
                        if (!editText.equals("cancel")) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ((Activity) context).requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        21);
                            } else {
                                if (editText.equals("gallery")) {
//                                    selectImageFromGallery();
                                } else if (editText.equals("document")) {
//                                    selectDoc();
                                }

                            }
                        } else {
                            upload = false;
                        }

                    }
                });
            }
        });

    }

    private void recyclerListHandling(boolean editmessage) {
        mAdapter.notifyDataSetChanged();

        if (editmessage) {
            edtMessage.setText("");
        }

        if (mMessages != null && mMessages.size() > 1)
            recycler_message.scrollToPosition(mMessages.size() - 1);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageReceived event) {
        if (event.message != null && !event.message.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(event.message);
                if (jsonObject != null && !jsonObject.optString("content").isEmpty()) {
                    mMessages.add(new MessageModel("" + System.currentTimeMillis() / 1000, jsonObject.optString("content"), false, jsonObject.optString("type"), jsonObject.optString("avatarUrl"), jsonObject.optString("ownerId"), jsonObject.optString("attachmentUrl"), jsonObject.optString("name")));

                }
            } catch (JSONException e) {
                e.printStackTrace();
                mMessages.add(new MessageModel("" + System.currentTimeMillis() / 1000, event.message, false, "", "", "", "", ""));
            }
//            mMessages.add(new MessageModel(getDate(), event.message, false));
            recyclerListHandling(false);
        }
    }




}
