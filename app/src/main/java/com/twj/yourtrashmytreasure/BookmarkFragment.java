package com.twj.yourtrashmytreasure;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.model.Model;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.twj.yourtrashmytreasure.StartActivity.USER;
import static com.twj.yourtrashmytreasure.StartActivity.list_bookmark;


public class BookmarkFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference item_ref = db.collection("items");
    private CollectionReference bookmark_ref = db.collection("users");
    public BookmarkItemAdapter adapter;
    SwipeRefreshLayout refresh_layout;
    RecyclerView recycler_view;
    View v;
    public BookmarkFragment() {
        // Required empty public constructor
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bookmark,container,false);

        bookmark_ref.whereEqualTo("user_id",USER.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Bookmark bookmark_item = documentSnapshot.toObject(Bookmark.class);
                    String bookmark_id = bookmark_item.getItem_redeem_id();
                    list_bookmark.remove("");
                    if(list_bookmark.size()<10 && !list_bookmark.contains(bookmark_id)) {
                        list_bookmark.add(bookmark_id);
                    }
                }
            }
        });

        //Fixes the Inconsistency detected. Invalid view holder adapter positionViewHolder caused by RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManagerWrapper(getContext(), LinearLayoutManager.VERTICAL, false);

        if(list_bookmark.size()<1){
            list_bookmark.add("");
        }
        Query query = item_ref.whereIn("item_redeem_id",list_bookmark).limit(100);

        FirestoreRecyclerOptions<ItemHelper> options = new FirestoreRecyclerOptions.Builder<ItemHelper>()
                .setQuery(query,ItemHelper.class)
                .build();

        adapter = new BookmarkItemAdapter(options);

        recycler_view=v.findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setAdapter(adapter);

        adapter.setOnItemClickListener(new BookmarkItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                ItemHelper item = documentSnapshot.toObject(ItemHelper.class);
                String item_name = item.getItem_name();
                String item_image_url = item.getItem_image_url();
                String redeem_status = item.getRedeemed_status().toString();
                String item_category = item.getItem_category();
                String item_description = item.getItem_description();
                String latitude = Double.toString(item.getLatitude());
                String longitude = Double.toString(item.getLongitude());
                String used_status = item.getUsed_status();
                Intent intent = new Intent(getContext(),SeeItemActivity.class);
                intent.putExtra("item_name",item_name);
                intent.putExtra("redeem_status",redeem_status);
                intent.putExtra("item_image_url",item_image_url);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("used_status",used_status);
                intent.putExtra("item_category",item_category);
                intent.putExtra("item_description",item_description);
                startActivity(intent);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAbsoluteAdapterPosition());
                recycler_view.removeView(viewHolder.itemView);
            }
        }).attachToRecyclerView(recycler_view);

        refresh_layout = v.findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getParentFragmentManager().beginTransaction(). replace(R.id.fragment_container,
                        new BookmarkFragment()).commit();
                refresh_layout.setRefreshing(false);
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Update or Add Bookmark
        bookmark_ref.whereEqualTo("user_id",USER.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Bookmark bookmark_item = documentSnapshot.toObject(Bookmark.class);
                    String bookmark_id = bookmark_item.getItem_redeem_id();
                    list_bookmark.remove("");
                    if(list_bookmark.size()<10 && !list_bookmark.contains(bookmark_id)) {
                        list_bookmark.add(bookmark_id);
                    }
                }
            }
        });
        adapter.startListening();
        //Check if Fragment Empty
        TextView textView = v.findViewById(R.id.fragment_bookmark_text);
        if(list_bookmark.size()==1){
            textView.setVisibility(View.VISIBLE);
            textView.setText("No bookmarks have\nbeen added yet!");
        }else{
            textView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        bookmark_ref.whereEqualTo("user_id",USER.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Bookmark bookmark_item = documentSnapshot.toObject(Bookmark.class);
                    String bookmark_id = bookmark_item.getItem_redeem_id();
                    list_bookmark.remove("");
                    if(list_bookmark.size()<10 && !list_bookmark.contains(bookmark_id)){
                        list_bookmark.add(bookmark_id);
                    }
                }
            }
        });
        adapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        bookmark_ref.whereEqualTo("user_id",USER.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Bookmark bookmark_item = documentSnapshot.toObject(Bookmark.class);
                    String bookmark_id = bookmark_item.getItem_redeem_id();
                    list_bookmark.remove("");
                    if(list_bookmark.size()<10 && !list_bookmark.contains(bookmark_id)){
                        list_bookmark.add(bookmark_id);
                    }
                }
            }
        });
        adapter.startListening();
    }

}
