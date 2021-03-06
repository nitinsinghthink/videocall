package com.voxeet.uxkit.implementation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.voxeet.VoxeetSDK;
import com.voxeet.android.media.MediaStream;
import com.voxeet.sdk.models.Conference;
import com.voxeet.sdk.models.Participant;
import com.voxeet.sdk.models.v1.ConferenceParticipantStatus;
import com.voxeet.sdk.services.SessionService;
import com.voxeet.sdk.utils.Annotate;
import com.voxeet.sdk.utils.NoDocumentation;
import com.voxeet.R;
import com.voxeet.uxkit.configuration.Users;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.utils.IParticipantViewListener;
import com.voxeet.uxkit.utils.ParticipantViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple View to manage how Users are displayed "on top" of the screen (or wherever the default list should be positionned)
 */
@Annotate
public class VoxeetParticipantsView extends VoxeetView {

    private RecyclerView recyclerView;

    private ParticipantViewAdapter adapter;

    private LinearLayoutManager horizontalLayout;

    private boolean displaySelf = false;
    private boolean displayNonAir = true;

    private Handler mHandler;

    /**
     * Instantiates a new Voxeet participant view.
     *
     * @param context the context
     */
    @NoDocumentation
    public VoxeetParticipantsView(Context context) {
        super(context);

        internalInit();
    }

    /**
     * Instantiates a new Voxeet participant view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    @NoDocumentation
    public VoxeetParticipantsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        internalInit();
        updateAttrs(attrs);
    }

    private void internalInit() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Instantiates a new Voxeet participant view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    @NoDocumentation
    public VoxeetParticipantsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        updateAttrs(attrs);
    }

    /**
     * Displays or hides the names of the conference users.
     *
     * @param enabled the enabled
     */
    public void setNamesEnabled(boolean enabled) {
        adapter.setNamesEnabled(enabled);
        adapter.updateUsers();
    }

    /**
     * Sets the color of the overlay when a user is selected.
     *
     * @param color the color
     */
    public void setSelectedUserColor(int color) {
        adapter.setSelectedUserColor(color);
        adapter.updateUsers();
    }

    @NoDocumentation
    public boolean isDisplaySelf() {
        return displaySelf;
    }

    @NoDocumentation
    public boolean isDisplayNonAir() {
        return displayNonAir;
    }

    private void updateAttrs(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.VoxeetParticipantsView);

        boolean nameEnabled = attributes.getBoolean(R.styleable.VoxeetParticipantsView_display_name, true);

        displaySelf = attributes.getBoolean(R.styleable.VoxeetParticipantsView_display_self, false);

        displayNonAir = attributes.getBoolean(R.styleable.VoxeetParticipantsView_display_user_lefts, true);

        Users configuration = VoxeetToolkit.getInstance().getConferenceToolkit().Configuration.Users;
        ColorStateList color = attributes.getColorStateList(R.styleable.VoxeetParticipantsView_speaking_user_color);
        if (null != configuration.speaking_user_color)
            setSelectedUserColor(configuration.speaking_user_color);
        else if (color != null)
            setSelectedUserColor(color.getColorForState(getDrawableState(), 0));

        setNamesEnabled(nameEnabled);

        attributes.recycle();
    }

    public void update(@NonNull Conference conference) {
        adapter.setUsers(filter(conference.getParticipants()));
        adapter.updateUsers();
    }

    /**
     * Mehtod to call when a User Added Event has been fired externally
     *
     * @param conference the conference
     * @param user       the user
     */
    @Override
    public void onUserAddedEvent(@NonNull Conference conference, @NonNull Participant user) {
        super.onUserAddedEvent(conference, user);

        adapter.setUsers(filter(conference.getParticipants()));
        adapter.updateUsers();
    }

    /**
     * Mehtod to call when a User Updated Event has been fired externally
     *
     * @param conference the conference
     * @param user       the user
     */
    @Override
    public void onUserUpdatedEvent(@NonNull Conference conference, @NonNull Participant user) {
        super.onUserUpdatedEvent(conference, user);

        postOnUi(() -> {
            adapter.setUsers(filter(conference.getParticipants()));
            adapter.updateUsers();
        });
    }

    public List<Participant> filter(List<Participant> users) {
        SessionService sessionService = VoxeetSDK.session();
        List<Participant> filter = new ArrayList<>();
        int added = 0;
        int invited = 0;
        for (Participant user : users) {
            boolean invite = null != users && ConferenceParticipantStatus.RESERVED.equals(user.getStatus());

            if (isDisplaySelf() || (null != sessionService && !sessionService.isLocalParticipant(user))) {
                boolean had = isDisplayNonAir(); //if display every users
                if (!had) had = user.isLocallyActive();
                if (!had) had = invite;
                if (had) added++;
                if (had) filter.add(user);

                if (invite) invited++;
            }
        }

        if (added == 1 && invited < 1) {
            //TODO add configuration for this mode
            filter = new ArrayList<>();
        }
        return filter;
    }

    /**
     * Method to call when a Stream has been added to a specified user
     *
     * @param conference  the conference
     * @param user        the user
     * @param mediaStream the corresponding stream that fired the event
     */
    @Override
    public void onStreamAddedEvent(@NonNull Conference conference, @NonNull Participant user, @NonNull MediaStream mediaStream) {
        super.onStreamAddedEvent(conference, user, mediaStream);
        postOnUi(() -> {
            if (adapter != null) {
                adapter.updateUsers();
            }
        });
    }

    /**
     * Method to call when a Stream has been updated from a specified user
     *
     * @param conference  the conference
     * @param user        the user
     * @param mediaStream the corresponding stream that fired the event
     */
    @Override
    public void onStreamUpdatedEvent(@NonNull Conference conference, @NonNull Participant user, @NonNull MediaStream mediaStream) {
        super.onStreamUpdatedEvent(conference, user, mediaStream);
        postOnUi(() -> {
            if (adapter != null) {
                adapter.updateUsers();
            }
        });
    }

    /**
     * Method to call when a Stream has been removed from a specified user
     *
     * @param conference  the conference
     * @param user        the user
     * @param mediaStream the corresponding stream that fired the event
     */
    @Override
    public void onStreamRemovedEvent(@NonNull Conference conference, @NonNull Participant user, @NonNull MediaStream mediaStream) {
        super.onStreamRemovedEvent(conference, user, mediaStream);
        postOnUi(() -> {
            if (adapter != null) {
                adapter.updateUsers();
            }
        });
    }

    /**
     * Method to call when a conference has been destroyed
     */
    @Override
    public void onConferenceDestroyed() {
        super.onConferenceDestroyed();

        adapter.clearParticipants();
        adapter.updateUsers();
    }

    /**
     * Method call when a conference has been left
     */
    @Override
    public void onConferenceLeft() {
        super.onConferenceLeft();

        adapter.clearParticipants();
        adapter.updateUsers();
    }
    public void noUser(){
        adapter.clearParticipants();
        adapter.updateUsers();
    }

    @NoDocumentation
    @Override
    public void init() {
        horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        if (adapter == null)
            adapter = new ParticipantViewAdapter(horizontalLayout, getContext());


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(horizontalLayout);

        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.conference_view_avatar_size));
    }

    @NoDocumentation
    @Override
    protected int layout() {
        return R.layout.voxeet_participant_view;
    }

    @NoDocumentation
    @Override
    protected void bindView(View view) {
        recyclerView = view.findViewById(R.id.participant_recycler_view);
    }

    /**
     * Sets participant listener.
     *
     * @param listener the listener
     */
    public void setParticipantListener(IParticipantViewListener listener) {
        if (adapter != null)
            adapter.setParticipantListener(listener);
    }

    private void postOnUi(@NonNull Runnable runnable) {
        mHandler.post(runnable);
    }

    @NoDocumentation
    public void notifyDatasetChanged() {
        if (null != adapter) {
            adapter.updateUsers();
        }
    }
}
