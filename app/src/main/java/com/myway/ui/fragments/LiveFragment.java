package com.myway.ui.fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.bambuser.broadcaster.BroadcastPlayer;
import com.bambuser.broadcaster.PlayerState;
import com.bambuser.broadcaster.SurfaceViewWithAutoAR;
import com.myway.R;
import com.myway.models.Live;
import com.myway.ui.activities.AddLiveActivity;
import com.myway.ui.activities.DashboardActivity;
import com.myway.ui.adapters.LiveAdapter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LiveFragment extends BaseFragment {
    private static final String APPLICATION_ID = "6vM9j3HqnmulokFCRJWmig";
    private static final String API_KEY = "KKsg4wQbcnDppK5jN13RaU";
    private static final String TAG = LiveFragment.class.getSimpleName();
    final OkHttpClient mOkHttpClient = new OkHttpClient();

    View view;
    private RecyclerView recyclerView;
    private LiveAdapter liveAdapter;
    ArrayList<Live> liveList=new ArrayList<Live>();
    private TextView noLiveItem;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_live, container, false);

        recyclerView = view.findViewById(R.id.rv_live_items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        noLiveItem = view.findViewById(R.id.tv_no_live_items_found);

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        mOkHttpClient.dispatcher().cancelAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        liveList.clear();
        updateLive();
    }

    void updateLive() {
        Request request = new Request.Builder()
                .url("https://api.bambuser.com/broadcasts")
                .addHeader("Accept", "application/vnd.bambuser.v1+json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .get()
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {

            }
            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                String body = response.body().string();
//                String resourceUri = null;
                JSONObject liveObject=null;



                try {
//                    JSONObject json = new JSONObject(body);
//                    Log.d(TAG, "onResponse: "+json);
//                    JSONArray results = json.getJSONArray("results");
//                    Log.d(TAG, "onResponse: "+results);
//                    JSONObject latestBroadcast = results.optJSONObject(0);
//                    Log.d(TAG, "onResponse: "+latestBroadcast);
//                    resourceUri = latestBroadcast.optString("resourceUri");
//                    Log.d(TAG, "onResponse: "+resourceUri);
                    JSONObject json = new JSONObject(body);
                    Log.d(TAG, "onResponse: "+json);
                    JSONArray results = json.getJSONArray("results");
                    Log.d(TAG, "onResponse: "+results);
                    for(int i=0;i<results.length();i++){
                        Log.d(TAG, "onResponse: "+results.length());
                        String userId=null;
                        String title=null;
                        String preview_jpg=null;
                        boolean live=false;
                        String resourceUri=null;
                        long time=0;

                        liveObject= results.optJSONObject(i);
                        Log.d(TAG, "onResponse: "+liveObject);
                        if(liveObject.has("author")){
                            userId=liveObject.getString("author");

                        }
                        if(liveObject.has("title")){
                            title=liveObject.getString("title").substring(13);
                            time= Long.parseLong(liveObject.getString("title").substring(0,13));
                            Log.d(TAG, "onResponse: "+time);
                        }
                        if(liveObject.has("preview")){
                            preview_jpg=liveObject.getString("preview");

                        }
                        if(liveObject.getString("type").equals("live")){
                            live=true;

                        }
                        if(liveObject.has("resourceUri")){
                            resourceUri=liveObject.getString("resourceUri");

                        }

                        SimpleDateFormat formatter= new SimpleDateFormat("yy-MM-dd hh:mm a");
                        Date date= new Date(time);
//                        time= formatter.format(date);
                        liveList.add(new Live(userId,title,preview_jpg,live,resourceUri,formatter.format(date)));

                    }


                } catch (Exception ignored) {}
                getActivity().runOnUiThread(new Runnable() {

                    @Override public void run() {
                    if(liveList.size()>0){

                        recyclerView.setVisibility(View.VISIBLE);
                        noLiveItem.setVisibility(View.INVISIBLE);

                        liveAdapter=new LiveAdapter(getContext(),liveList);
                        recyclerView.setAdapter(liveAdapter);
                    }else{
                        recyclerView.setVisibility(View.INVISIBLE);
                        noLiveItem.setVisibility(View.VISIBLE);
                    }

                }});
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_live_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
//        Toast.makeText(getActivity(),"success",Toast.LENGTH_LONG).show();
        Intent liveIntent=new Intent(getActivity(), AddLiveActivity.class);
        startActivity(liveIntent);
        return true;
    }
}
