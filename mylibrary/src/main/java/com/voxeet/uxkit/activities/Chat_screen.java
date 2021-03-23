package com.voxeet.uxkit.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.voxeet.R;
import com.voxeet.VoxeetSDK;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.events.sdk.MessageReceived;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.implementation.overlays.OverlayState;
import com.voxeet.uxkit.utils.MessageModel;
import com.voxeet.uxkit.views.internal.VoxeetOverlayContainerFrameLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Chat_screen extends VoxeetAppCompatActivity {
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
    public static Activity chatContext;
    public static boolean chatOpen=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(this);
        chatContext=Chat_screen.this;
        mProgress.setCancelable(false);
        mProgress.setMessage("Uploading...");
        setContentView(R.layout.activity_chat_screen);
        setupViews();
        setUpClicks();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        try {
            chatOpen=false;
            VoxeetToolkit.instance().getConferenceToolkit()
                    .setDefaultOverlayState(OverlayState.EXPANDED);

        } catch (Exception e) {

            e.printStackTrace();
//            super.onBackPressed();
        }
    }

    private void setupViews() {
        edtMessage = findViewById(R.id.txt_send);
        mSend = findViewById(R.id.btn_send);
        conferenceId = getIntent().getExtras().getString("ID");
        mAdapter = new MessageAdapter(mMessages, this);
        recycler_message = findViewById(R.id.recycler_message);
        recycler_message.setNestedScrollingEnabled(false);
        final LinearLayoutManager layoutManagerhorizental = new LinearLayoutManager(this);
        layoutManagerhorizental.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_message.setLayoutManager(layoutManagerhorizental);
        recycler_message.setAdapter(mAdapter);
        mBack = findViewById(R.id.back_img);
        mattach = findViewById(R.id.attachment);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onresume "," >>>>>> resume");
        chatOpen=true;
    }

    private void setUpClicks() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
//                try {
//                    VoxeetToolkit.instance().getConferenceToolkit()
//                            .setDefaultOverlayState(OverlayState.EXPANDED);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
       /*         try {
                    new Handler().postDelayed(new Runnable() {


                        @Override
                        public void run() {
                            // This method will be executed once the timer is over

                            finish();
                        }
                    }, 900);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }*/
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
                UploadPicDialog uploadPicDialog = new UploadPicDialog(Chat_screen.this);
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
                            if (ContextCompat.checkSelfPermission(Chat_screen.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(Chat_screen.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        21);
                            } else {
                                if (editText.equals("gallery")) {
                                    selectImageFromGallery();
                                } else if (editText.equals("document")) {
                                    selectDoc();
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


    private static final int SELECT_IMAGE = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 21) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (image) {
                    selectImageFromGallery();
                } else {
                    selectDoc();
                }
            } else {
                Toast.makeText(this, "Camera or storage permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void selectDoc() {
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        image = false;
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Doc"), SELECT_IMAGE);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
    }

    private Date getDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        return c;
    }


    int pos = 0;
    String attachName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    VoxeetToolkit.instance().getConferenceToolkit()
                            .setDefaultOverlayState(OverlayState.MINIMIZED);
                    this.imageUri = imageReturnedIntent.getData();
                    if (image) {

                    }
                    pos = imageReturnedIntent.getData().getPath().split("/").length - 1;
                    attachName = imageReturnedIntent.getData().getPath().split("/")[pos];

                    if (getfileExtension(imageUri) != null && !attachName.endsWith(getfileExtension(imageUri))) {
                        attachName = attachName + "." + getfileExtension(imageUri);
                    }

                    if (image) {

                        ViewPicDialog viewPicDialog = new ViewPicDialog(Chat_screen.this, "", imageUri, false);
                        viewPicDialog.show();
                        viewPicDialog.setCancelable(false);

                        viewPicDialog.getText(new FieldListener() {
                            @Override
                            public void onClick(String editText) {
                                if (editText.equals("send")) {
                                    uploadImage();
                                }
                            }
                        });
                    } else {
                        uploadImage();
                    }
//                    edtMessage.setText(attachName);
                }

                VoxeetToolkit.instance().getConferenceToolkit()
                        .setDefaultOverlayState(OverlayState.MINIMIZED);
        }
    }

    private String getfileExtension(Uri uri) {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    String aUrl;

    private void uploadImage() {
        try {
            if (mProgress != null && !mProgress.isShowing()) {
                mProgress.show();
            }
            final InputStream imageStream = getContentResolver().openInputStream(this.imageUri);
            final int imageLength = imageStream.available();

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {

                        final String imageName = ImageManager.UploadImage(attachName, imageStream, imageLength);

                        handler.post(new Runnable() {

                            public void run() {
                                mProgress.dismiss();
                                sendAttachMessage();
                            }
                        });
                    } catch (Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        Log.e("exeption", "  run time ex" + ex);
                        handler.post(new Runnable() {
                            public void run() {
                                mProgress.dismiss();
                                Toast.makeText(Chat_screen.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            th.start();
        } catch (Exception ex) {
            mProgress.dismiss();
            Log.e("exeption", " ex" + ex);
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAttachMessage() {
        aUrl = ImageManager.bLobUrl + edtMessage.getText() + ImageManager.blobToken;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", "" + attachName);
            jsonObject.put("time", "" + System.currentTimeMillis() / 1000);
            jsonObject.put("type", "attachment");
            jsonObject.put("name", "" + VoxeetSDK.session().getParticipantInfo().getName());
            if (VoxeetSDK.session().getParticipantInfo().getAvatarUrl() != null) {
                jsonObject.put("avatarUrl", "" + VoxeetSDK.session().getParticipantInfo().getAvatarUrl());
            }
            jsonObject.put("title", "Chat_Message");
            jsonObject.put("attachmentUrl", aUrl);
            jsonObject.put("ownerId", "" + VoxeetSDK.session().getParticipantInfo().getExternalId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("DATA", " Message data " + jsonObject);
        VoxeetSDK.command().send(conferenceId, jsonObject.toString()).then((result, solver) -> {
            Log.e("result ", "" + result.booleanValue());
        }).error(error -> {
        });
        upload = false;
        mMessages.add(new MessageModel("" + System.currentTimeMillis() / 1000, attachName, true, "attachment", VoxeetSDK.session().getParticipantInfo().getAvatarUrl(), VoxeetSDK.session().getParticipantInfo().getExternalId(), "", VoxeetSDK.session().getParticipantInfo().getName()));
        recyclerListHandling(true);
        VoxeetToolkit.instance().getConferenceToolkit()
                .setDefaultOverlayState(OverlayState.MINIMIZED);
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


  /*  @Override
    protected void onConferenceState(@NonNull ConferenceStatusUpdatedEvent event) {
        super.onConferenceState(event);

        switch (event.state) {
            case JOINED:
                Log.e("on   ", " joined   " + " join");
                break;
            case LEFT:
                Log.e("on   ", " Left   " + " left");
                Intent a=new Intent(this,SelectAc)
                finish();
                break;

            case ENDED:

                break;
        }
    }*/
}
