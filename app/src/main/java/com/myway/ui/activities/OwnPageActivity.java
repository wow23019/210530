package com.myway.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myway.R;
import com.myway.ui.fragments.OwnPageProductFragment;
import com.myway.utils.Constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;


public class OwnPageActivity extends AppCompatActivity {

    private static final String TAG = OwnPageActivity.class.getSimpleName();
    private ImageView background;
    private Uri backgroundUri = null;
    private boolean backgroundIsChanged = false;
    private FloatingActionButton add;
    private FragmentContainerView frag_product;
//    private FragmentContainerView frag_post;
    private ImageView userImage;
    private ImageView male;
    private ImageView female;
    private ImageView noGender;
//    private Switch checkPost;
    private FirebaseFirestore firestore;
    private StorageReference storage;
    private String product_owner="";
    private String userID= FirebaseAuth.getInstance().getUid();
    private TextView username;
    private Button saveBackground;
    private ImageButton send_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_page);

        background = findViewById(R.id.iv_page_background);
        saveBackground = findViewById(R.id.bt_page_save_background);
        username = findViewById(R.id.tv_page_username);
        add= findViewById(R.id.fab_page_add);
        frag_product = findViewById(R.id.frag_own_page_product);
//        frag_post = findViewById(R.id.frag_own_page_post);
        userImage = findViewById(R.id.iv_page_userimage);
        male = findViewById(R.id.iv_page_male);
        female = findViewById(R.id.iv_page_female);
        noGender = findViewById(R.id.iv_page_no_gender);
        send_message = findViewById(R.id.bt_page_send_message);
//        checkPost = findViewById(R.id.sw_page_checkPost);
        /////////////////////////////////////////////
//        checkPost.setVisibility(View.GONE);
        /////////////////////////////////////////////
        Bundle bundle=getIntent().getExtras();
        if(bundle.getString("product_owner")!=null){
            product_owner=bundle.getString("product_owner");
        }
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        firestore.collection("users").document(product_owner).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String firstname = task.getResult().getString("firstName");
                        String lastname = task.getResult().getString("lastName");
                        username.setText(firstname+" "+lastname);

                        String userimage = task.getResult().getString("image");
                        Glide.with(OwnPageActivity.this).load(userimage).centerCrop().into(userImage);

                        String gender=task.getResult().getString("gender");
                        if (gender.equals("Female")) {
                            male.setVisibility(View.GONE);
                            female.setVisibility(View.VISIBLE);
                            noGender.setVisibility(View.GONE);
                        }else if(gender.equals("Male")){
                            male.setVisibility(View.VISIBLE);
                            female.setVisibility(View.GONE);
                            noGender.setVisibility(View.GONE);
                        }else{
                            male.setVisibility(View.GONE);
                            female.setVisibility(View.GONE);
                            noGender.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }
        });
        firestore.collection("Background").document(product_owner).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String backgroundImage=task.getResult().getString("image");
                        Glide.with(OwnPageActivity.this).load(backgroundImage).centerCrop().into(background);
                    }
                }
            }
        });

        if(product_owner.equals(userID)){
            background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (ContextCompat.checkSelfPermission(OwnPageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(OwnPageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.READ_STORAGE_PERMISSION_CODE);
                        }else{
                            Intent gallery = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(gallery,Constants.PICK_IMAGE_REQUEST_CODE);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            saveBackground.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            saveBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (backgroundUri != null){
                        StorageReference imageRef = storage.child("backgroundPic").child(FieldValue.serverTimestamp().toString()+ ".jpg");
                        Log.d(TAG, "onClick: "+imageRef.toString());
                        if (backgroundIsChanged){
                            imageRef.putFile(backgroundUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()){
                                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri){
                                                saveToFireStore(task ,uri);
                                            }
                                        });

                                    }else{
                                        Toast.makeText(OwnPageActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            saveToFireStore(null , backgroundUri);
                        }
                    }else{
                        Toast.makeText(OwnPageActivity.this, "請選擇背景圖片", Toast.LENGTH_SHORT).show();
                    }
                    saveBackground.setVisibility(View.GONE);
                }
            });

//            checkPost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(checkPost.isChecked()){
//                        checkPost.setText("檢視商品");
//                        frag_product.setVisibility(View.VISIBLE);
//                        add.show();
//                        frag_product.setVisibility(View.GONE);
//                    }else{
//                        checkPost.setText("檢視貼文");
//                        frag_product.setVisibility(View.GONE);
//                        frag_product.setVisibility(View.VISIBLE);
//                        add.show();
//                    }
//                }
//            });

        }else{
            add.hide();
//            checkPost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(checkPost.isChecked()){
//                        checkPost.setText("檢視商品");
//                        frag_product.setVisibility(View.VISIBLE);
//                        frag_product.setVisibility(View.GONE);
//                    }else{
//                        checkPost.setText("檢視貼文");
//                        frag_product.setVisibility(View.GONE);
//                        frag_product.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
        }


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(checkPost.isChecked()==false){
                    Intent intent=new Intent(OwnPageActivity.this,AddProductActivity.class);
                    startActivity(intent);
//                }
            }
        });

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent=new Intent(OwnPageActivity.this,ChatroomActivity.class);
                chatIntent.putExtra("product_owner",product_owner);
                startActivity(chatIntent);
            }
        });
    }

    private void saveToFireStore(Task<UploadTask.TaskSnapshot> task, Uri downloadUri) {

        HashMap<String , Object> map = new HashMap<>();
        map.put("image" , downloadUri.toString());
        firestore.collection("Background").document(product_owner).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(OwnPageActivity.this, "個人頁面背景更改成功", Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(OwnPageActivity.this, "個人頁面背景更改失敗", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
            backgroundUri=data.getData();
            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),backgroundUri);
                background.setImageBitmap(bitmap);
                backgroundIsChanged = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firestore.collection("users").document(product_owner).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String firstname = task.getResult().getString("firstName");
                        String lastname = task.getResult().getString("lastName");
                        username.setText(firstname+" "+lastname);

                        String userimage = task.getResult().getString("image");
                        Glide.with(OwnPageActivity.this).load(userimage).centerCrop().into(userImage);

                        String gender=task.getResult().getString("gender");
                        if (gender.equals("Female")) {
                            male.setVisibility(View.GONE);
                            female.setVisibility(View.VISIBLE);
                            noGender.setVisibility(View.GONE);
                        }else if(gender.equals("Male")){
                            male.setVisibility(View.VISIBLE);
                            female.setVisibility(View.GONE);
                            noGender.setVisibility(View.GONE);
                        }else{
                            male.setVisibility(View.GONE);
                            female.setVisibility(View.GONE);
                            noGender.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }
        });
        firestore.collection("Background").document(product_owner).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String backgroundImage=task.getResult().getString("image");
                        Glide.with(OwnPageActivity.this).load(backgroundImage).centerCrop().into(background);
                    }
                }
            }
        });
    }
}