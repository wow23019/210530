package com.myway.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myway.R;
import com.myway.models.Live;
import com.myway.ui.activities.DashboardActivity;
import com.myway.ui.activities.LiveActivity;
import com.myway.ui.activities.OwnPageActivity;
import com.myway.utils.Constants;

import java.util.List;

public class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {

    private Context context;
    private List<Live> mLive ;
    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

    public LiveAdapter(Context context, List<Live> mLive) {
        this.context = context;
        this.mLive = mLive;
    }

    @NonNull
    @Override
    public LiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_live,parent,false);
            return new LiveAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull LiveAdapter.ViewHolder holder, int position) {

        Live live = mLive.get(position);
        holder.live_title.setText(live.getTitle());
        holder.live_date.setText(live.getTime().toString());

        if(live.isLive()){
            holder.live_title.setTextColor(Color.RED);
            holder.live_date.setTextColor(Color.RED);
        }else{
            holder.live_title.setTextColor(Color.rgb(84,83,83));
            holder.live_date.setTextColor(Color.rgb(84,83,83));
        }
            Glide.with(context).load(live.getPreview_jpg()).into(holder.live_preview);

            mFireStore.collection(Constants.USERS)
                .document(live.getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Glide.with(context).load(documentSnapshot.get("image").toString()).into(holder.userImage);
                    }
                });

        holder.live_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent liveIntent=new Intent(context,LiveActivity.class);
                liveIntent.putExtra("resourceUri",live.getResourceUri());
                context.startActivity(liveIntent);
            }
        });

        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, OwnPageActivity.class);
                intent.putExtra("product_owner",live.getUserId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mLive.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView live_preview;
        public TextView live_title;
        public TextView live_date;
        public ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            live_preview = itemView.findViewById(R.id.iv_live_preview_jpg);
            live_title = itemView.findViewById(R.id.tv_live_item_title);
            live_date = itemView.findViewById(R.id.tv_live_live_date);
            userImage = itemView.findViewById(R.id.iv_live_user_image);
        }
    }


}
