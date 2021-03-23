package com.voxeet.uxkit.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.voxeet.R;
import com.voxeet.uxkit.utils.MessageModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private List<MessageModel> mMessages;
    Context ctx;
    String otherUserPhoto;


    public MessageAdapter(List<MessageModel> messages, Context context) {
        mMessages = messages;
        ctx = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_messages_right, parent, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_messages_left, parent, false);
            return new ViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final MessageModel message = mMessages.get(position);

        viewHolder.mMessageView.setText(mMessages.get(position).getContent());
        if (!mMessages.get(position).getType().equalsIgnoreCase("text")) {
            viewHolder.mFile.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mFile.setVisibility(View.GONE);
        }

        if (mMessages.get(position).getOwnerPic() != null && !mMessages.get(position).getOwnerPic().isEmpty()) {
            Picasso.get().load(mMessages.get(position).getOwnerPic()).error(R.drawable.default_profile_pic_md).into(viewHolder.userPic);
        }

        if (mMessages.get(position).getDate() != null) {
            viewHolder.mMessageTime.setText(getTime("" + mMessages.get(position).getDate()));
        }
        if (mMessages.get(position).getName() != null && !mMessages.get(position).getName().isEmpty() && !mMessages.get(position).getName().equalsIgnoreCase("null")) {
            viewHolder.mName.setText(mMessages.get(position).getName());
        }

        viewHolder.mFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clikWork(position);
            }
        });
        viewHolder.mMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clikWork(position);
            }


        });
    }

    private void clikWork(int position) {
        if (!mMessages.get(position).getType().equalsIgnoreCase("text")) {
            if (mMessages.get(position).getContent().endsWith("jpg") || mMessages.get(position).getContent().endsWith("jpeg") || mMessages.get(position).getContent().endsWith("png")) {
                Log.e("url", "image click else " + mMessages.get(position).getContent());
                ViewPicDialog viewPicDialog = new ViewPicDialog(ctx, "" + mMessages.get(position).getContent(), null, true);
                viewPicDialog.show();
                viewPicDialog.setCancelable(false);
            } else {
                String url = ImageManager.bLobUrl + mMessages.get(position).getContent() + ImageManager.blobToken;
                Log.e("url", "image click else " + url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                ctx.startActivity(browserIntent);
            }
        }
    }

    public static String getTime(String timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        long times = Long.parseLong(timestamp) * 1000L;
        String dateString = formatter.format(new Date(times));
        formatter.setTimeZone(TimeZone.getDefault());
        return dateString;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessages.get(position).isMe()) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView, mFile, userPic;
        private LinearLayout mImageViewLayout;
        private TextView mMessageView, mMessageTime, mName;
        private TextView txt_activity_change_message;


        public ViewHolder(View itemView) {
            super(itemView);
            mFile = itemView.findViewById(R.id.fileicon);
            mMessageView = itemView.findViewById(R.id.txt_message);
            mName = itemView.findViewById(R.id.usernametxt);
            mMessageTime = itemView.findViewById(R.id.txt_message_time);
            mImageView = itemView.findViewById(R.id.img_show_image);
            mImageViewLayout = itemView.findViewById(R.id.img_show_image_layout);
            txt_activity_change_message = itemView.findViewById(R.id.txt_activity_change_message);
            userPic = itemView.findViewById(R.id.profile_user);
        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            if (null == message) return;
            mMessageView.setText(message);
        }
    }
}
