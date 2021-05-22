package com.myway.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import com.bambuser.broadcaster.BroadcastPlayer;
import com.bambuser.broadcaster.PlayerState;
import com.bambuser.broadcaster.SurfaceViewWithAutoAR;
import com.myway.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LiveActivity extends AppCompatActivity {
    private static final String APPLICATION_ID = "6vM9j3HqnmulokFCRJWmig";
    private static final String API_KEY = "KKsg4wQbcnDppK5jN13RaU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        mDefaultDisplay = getWindowManager().getDefaultDisplay();
        mVideoSurface = findViewById(R.id.VideoSurfaceView);
        mPlayerStatusTextView = findViewById(R.id.PlayerStatusTextView);
        mPlayerContentView = findViewById(R.id.PlayerContentView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOkHttpClient.dispatcher().cancelAll();
        mVideoSurface = null;
        if (mBroadcastPlayer != null)
            mBroadcastPlayer.close();
        mBroadcastPlayer = null;
        if (mMediaController != null)
            mMediaController.hide();
        mMediaController = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoSurface = findViewById(R.id.VideoSurfaceView);
        mPlayerStatusTextView.setText("Loading broadcast");
        Bundle bundle=getIntent().getExtras();
        initPlayer(bundle.getString("resourceUri"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_UP && mBroadcastPlayer != null && mMediaController != null) {
            PlayerState state = mBroadcastPlayer.getState();
            if (state == PlayerState.PLAYING ||
                    state == PlayerState.BUFFERING ||
                    state == PlayerState.PAUSED ||
                    state == PlayerState.COMPLETED) {
                if (mMediaController.isShowing())
                    mMediaController.hide();
                else
                    mMediaController.show();
            } else {
                mMediaController.hide();
            }
        }
        return false;
    }


    void initPlayer(String resourceUri) {
        if (resourceUri == null) {
            if (mPlayerStatusTextView != null)
                mPlayerStatusTextView.setText("Could not get info about latest broadcast");
            return;
        }
        if (mVideoSurface == null) {
            // UI no longer active
            return;
        }
        if (mBroadcastPlayer != null)
            mBroadcastPlayer.close();
        mBroadcastPlayer = new BroadcastPlayer(this, resourceUri, APPLICATION_ID, mBroadcastPlayerObserver);
        mBroadcastPlayer.setSurfaceView(mVideoSurface);
        mBroadcastPlayer.load();

    }

    BroadcastPlayer.Observer mBroadcastPlayerObserver = new BroadcastPlayer.Observer() {
        @Override
        public void onStateChange(PlayerState playerState) {
            if (mPlayerStatusTextView != null)
                mPlayerStatusTextView.setText("Status: " + playerState);
            if (playerState == PlayerState.PLAYING || playerState == PlayerState.PAUSED || playerState == PlayerState.COMPLETED) {
                if (mMediaController == null && mBroadcastPlayer != null && !mBroadcastPlayer.isTypeLive()) {
                    mMediaController = new MediaController(LiveActivity.this);
                    mMediaController.setAnchorView(mPlayerContentView);
                    mMediaController.setMediaPlayer(mBroadcastPlayer);
                }
                if (mMediaController != null) {
                    mMediaController.setEnabled(true);
                    mMediaController.show();
                }
            } else if (playerState == PlayerState.ERROR || playerState == PlayerState.CLOSED) {
                if (mMediaController != null) {
                    mMediaController.setEnabled(false);
                    mMediaController.hide();
                }
                mMediaController = null;
            }
        }
        @Override
        public void onBroadcastLoaded(boolean live, int width, int height) {
            Point size = getScreenSize();
            float screenAR = size.x / (float) size.y;
            float videoAR = width / (float) height;
            float arDiff = screenAR - videoAR;
            mVideoSurface.setCropToParent(Math.abs(arDiff) < 0.2);

        }
    };

    private Point getScreenSize() {
        if (mDefaultDisplay == null)
            mDefaultDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            // this is officially supported since SDK 17 and said to work down to SDK 14 through reflection,
            // so it might be everything we need.
            mDefaultDisplay.getClass().getMethod("getRealSize", Point.class).invoke(mDefaultDisplay, size);
        } catch (Exception e) {
            // fallback to approximate size.
            mDefaultDisplay.getSize(size);
        }
        return size;
    }


    final OkHttpClient mOkHttpClient = new OkHttpClient();
    BroadcastPlayer mBroadcastPlayer;
    MediaController mMediaController = null;
    SurfaceViewWithAutoAR mVideoSurface;
    TextView mPlayerStatusTextView;
    View mPlayerContentView;
    Display mDefaultDisplay;
}