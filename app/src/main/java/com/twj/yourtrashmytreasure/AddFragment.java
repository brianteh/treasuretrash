package com.twj.yourtrashmytreasure;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.twj.yourtrashmytreasure.model.User;

public class AddFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference item_ref = db.collection("items");
    public static MyItemAdapter adapter;
    private FloatingActionButton btn_add_item;
    View v;
    RecyclerView recycler_view;
    private int item_count;

    public AddFragment() {
        // Required empty public constructor
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_add,container,false);
        btn_add_item = v.findViewById(R.id.btn_add_item);
        btn_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = StartActivity.USER;
                Intent i = new Intent(getContext(),AddItemActivity.class);
                i.putExtra("user",new Gson().toJson(user));
                startActivity(i);
            }
        });

        //Fixes the Inconsistency detected. Invalid view holder adapter positionViewHolder caused by RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManagerWrapper(getContext(), LinearLayoutManager.VERTICAL, false);

        Query query = item_ref.whereEqualTo("user_id",StartActivity.USER.getId()).limit(100);

        FirestoreRecyclerOptions<ItemHelper> options = new FirestoreRecyclerOptions.Builder<ItemHelper>()
                .setQuery(query,ItemHelper.class)
                .build();

        adapter = new MyItemAdapter(options);

        recycler_view=v.findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        //new LinearLayoutManager(getContext())
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyItemAdapter.OnItemClickListener() {
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
                String item_redeem_id = item.getItem_redeem_id();
                Intent intent = new Intent(getContext(),MyItemActivity.class);
                intent.putExtra("item_name",item_name);
                intent.putExtra("redeem_status",redeem_status);
                intent.putExtra("item_image_url",item_image_url);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("used_status",used_status);
                intent.putExtra("item_category",item_category);
                intent.putExtra("item_description",item_description);
                intent.putExtra("item_redeem_id",item_redeem_id);
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

            }
        }).attachToRecyclerView(recycler_view);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        //Check if Empty Fragment
        TextView textView = v.findViewById(R.id.fragment_add_text);
        CollectionReference search_item = db.collection("items");
        search_item.whereEqualTo("user_id",StartActivity.USER.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                item_count = 0;
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    textView.setVisibility(View.GONE);
                    item_count+=1;
                }
            }
        });
        if(item_count==0){
            textView.setVisibility(View.VISIBLE);
            textView.setText("No items have\nbeen added yet!");
        }else{
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
