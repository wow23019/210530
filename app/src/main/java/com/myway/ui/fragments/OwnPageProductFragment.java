package com.myway.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.myway.R;
import com.myway.models.Product;
import com.myway.ui.activities.ProductDetailsActivity;
import com.myway.ui.adapters.DashboardItemsListAdapter;
import com.myway.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OwnPageProductFragment extends BaseFragment {
    FirebaseFirestore mFireStore=FirebaseFirestore.getInstance();
    ArrayList<Product> pageProductList=new ArrayList<Product>();
    private RecyclerView recyclerView;
    private TextView textView;
    public static String product_owner="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_own_page_product, container, false);
        recyclerView = view.findViewById(R.id.rv_page_product_items);
        textView = view.findViewById(R.id.tv_no_page_product_found);
        Bundle bundle=getActivity().getIntent().getExtras();
        if(bundle.getString("product_owner")!=null){
            product_owner=bundle.getString("product_owner");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        pageProductList.clear();
        getPageProductList();
    }

    private void getPageProductList() {
        mFireStore.collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID,product_owner)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(int i=0;i<queryDocumentSnapshots.size();i++){
                            DocumentSnapshot document=queryDocumentSnapshots.getDocuments().get(i);
                            Product product=document.toObject(Product.class);
                            product.setProduct_id(document.getId());
                            pageProductList.add(product);
                        }
                        if(pageProductList.size()>0){
                            recyclerView.setVisibility(View.VISIBLE);
                            textView.setVisibility(View.GONE);

                            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
                            recyclerView.setHasFixedSize(true);
                            DashboardItemsListAdapter adapter=new DashboardItemsListAdapter(getActivity(),pageProductList);
                            recyclerView.setAdapter(adapter);

                            adapter.setOnClickListener(new DashboardItemsListAdapter.OnClickListener() {
                                @Override
                                public void onClick(int position, @NotNull Product product) {
                                    Intent intent=new Intent(getContext(), ProductDetailsActivity.class);
                                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.getProduct_id());
                                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.getUser_id());
                                    startActivity(intent);
                                }
                            });
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.e("Get Page Product List", "帶入資料發生錯誤", e);
            }
        });
    }
}