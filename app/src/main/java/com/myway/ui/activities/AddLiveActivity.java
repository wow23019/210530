package com.myway.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.bambuser.broadcaster.BroadcastStatus;
import com.bambuser.broadcaster.Broadcaster;
import com.bambuser.broadcaster.CameraError;
import com.bambuser.broadcaster.ConnectionError;
import com.google.firebase.auth.FirebaseAuth;
import com.myway.R;
import com.myway.firestore.FirestoreClass;

import java.time.Clock;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AddLiveActivity extends AppCompatActivity {
    private static final String LOGTAG = "Mybroadcastingapp";

    private static final String APPLICATION_ID = "mEdmew4vDe1ctv0ZXgMxjA";

    private String userID=FirebaseAuth.getInstance().getUid();

    public static final long UTC=8;
    public static final long an_hour=3600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_live);
        mLiveTitle = findViewById(R.id.et_live_title);
        mPreviewSurface = findViewById(R.id.PreviewSurfaceView);
        mBroadcaster = new Broadcaster(this, APPLICATION_ID, mBroadcasterObserver);
        mBroadcaster.setRotation(getWindowManager().getDefaultDisplay().getRotation());
        mBroadcaster.setAuthor(userID);
        mBroadcastButton = findViewById(R.id.BroadcastButton);
        if(mLiveTitle.getText()==null){
            mBroadcastButton.setClickable(false);
        }else{
            mBroadcastButton.setClickable(true);
            mBroadcastButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    if (mBroadcaster.canStartBroadcasting()){
                        mBroadcaster.startBroadcast();
                        long localtime=UTC*an_hour;
                        mBroadcaster.setTitle(System.currentTimeMillis()+localtime*1000+mLiveTitle.getText().toString());
                    }
                    else
                        mBroadcaster.stopBroadcast();
                    mLiveTitle.setText("");
                }
            });
        }


        //https://bambuser.com/docs/reference/android-sdk-current/

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mBroadcaster.onActivityDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        mBroadcaster.onActivityPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasPermission(Manifest.permission.CAMERA)
                && !hasPermission(Manifest.permission.RECORD_AUDIO))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO}, 1);
        else if (!hasPermission(Manifest.permission.RECORD_AUDIO))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 1);
        else if (!hasPermission(Manifest.permission.CAMERA))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
        mBroadcaster.setCameraSurface(mPreviewSurface);
        mBroadcaster.onActivityResume();
        mBroadcaster.setRotation(getWindowManager().getDefaultDisplay().getRotation());

    }

    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private Broadcaster.Observer mBroadcasterObserver = new Broadcaster.Observer() {
        @Override
        public void onConnectionStatusChange(BroadcastStatus broadcastStatus) {
            Log.i(LOGTAG, "Received status change: " + broadcastStatus);
            if (broadcastStatus == BroadcastStatus.STARTING)
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (broadcastStatus == BroadcastStatus.IDLE)
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mBroadcastButton.setText(broadcastStatus == BroadcastStatus.IDLE ? "Broadcast" : "Disconnect");
        }
        @Override
        public void onStreamHealthUpdate(int i) {
        }
        @Override
        public void onConnectionError(ConnectionError connectionError, String s) {
            Log.w(LOGTAG, "Received connection error: " + connectionError + ", " + s);
        }
        @Override
        public void onCameraError(CameraError cameraError) {
            Log.w(LOGTAG, "Received camera error: " + cameraError);
        }
        @Override
        public void onChatMessage(String s) {
        }
        @Override
        public void onResolutionsScanned() {
        }
        @Override
        public void onCameraPreviewStateChanged() {
        }
        @Override
        public void onBroadcastInfoAvailable(String s, String s1) {
        }
        @Override
        public void onBroadcastIdAvailable(String s) {
        }
    };

    SurfaceView mPreviewSurface;
    Broadcaster mBroadcaster;
    Button mBroadcastButton;
    EditText mLiveTitle;
}
