package com.voxeet.uxkit.utils;

import com.voxeet.sdk.models.v2.ChatMessageType;

import java.util.Date;

public class MessageModel {

    public String date;
    public String content;
    public boolean me;
    String type;
    String ownerPic;
    String userId;
    String attachUrl;

    String name;

    public MessageModel(String date, String content, boolean me, String type, String ownerPic, String userId, String attacheUrl,String name) {
        this.date = date;
        this.content = content;
        this.me = me;
        this.type = type;
        this.ownerPic = ownerPic;
        this.userId = userId;
        this.attachUrl = attacheUrl;
        this.name = name;
    }

    public String getAttachUrl() {
        return attachUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttachUrl(String attachUrl) {
        this.attachUrl = attachUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwnerPic() {
        return ownerPic;
    }

    public void setOwnerPic(String ownerPic) {
        this.ownerPic = ownerPic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
