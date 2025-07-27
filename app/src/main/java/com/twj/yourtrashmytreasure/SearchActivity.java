package com.twj.yourtrashmytreasure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference item_ref = db.collection("items");
    private ItemAdapter adapter;
    private RelativeLayout item_search_box;
    private Button btn_search_item;
    private Spinner spinner_item_category;
    private Spinner spinner_item_used_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Hooks
        item_search_box = findViewById(R.id.item_search_box);
        btn_search_item = item_search_box.findViewById(R.id.btn_search_item);
        spinner_item_category = item_search_box.findViewById(R.id.spinner_item_category);
        spinner_item_used_status = item_search_box.findViewById(R.id.spinner_item_used_status);

        //Adapter for Spinner (item_used_status)
        ArrayList<String> usage_status = new ArrayList<>();
        usage_status.add("Used");
        usage_status.add("Unused");
        ArrayAdapter<String> usageStatusAdapter = new ArrayAdapter<>(
                this,
                R.layout.search_spinner_item,
                usage_status
        );
        spinner_item_used_status.setAdapter(usageStatusAdapter);

        //Adapter for Spinner (item_category)
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Home Appliances");
        categories.add("Electronic Gadgets");
        categories.add("Sports Equipments");
        categories.add("Men Clothing");
        categories.add("Women Clothing");
        categories.add("Baby Clothing");
        categories.add("Health & Beauty");
        categories.add("Tickets & Voucher");
        categories.add("Groceries & Pets");
        categories.add("Books");
        categories.add("Toys");
        categories.add("Automotive");
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(
                this,
                R.layout.search_spinner_item,
                categories
        );
        spinner_item_category.setAdapter(categoriesAdapter);

        //Set the default according to value
        String value_used_status=getIntent().getStringExtra("used_status");
        ArrayAdapter used_statusAdapter = (ArrayAdapter) spinner_item_used_status.getAdapter();
        int spinnerUsedStatus = used_statusAdapter.getPosition(value_used_status);
        spinner_item_used_status.setSelection(spinnerUsedStatus);

        String value_category=getIntent().getStringExtra("item_category");
        ArrayAdapter categoryAdapter = (ArrayAdapter) spinner_item_category.getAdapter();
        int spinnerCategory = categoryAdapter.getPosition(value_category);
        spinner_item_category.setSelection(spinnerCategory);

        //Search Function
        btn_search_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this,SearchActivity.class);
                intent.putExtra("used_status",spinner_item_used_status.getSelectedItem().toString());
                intent.putExtra("item_category",spinner_item_category.getSelectedItem().toString());
                startActivity(intent);
                finish();
            }
        });
        //Set Up Recycler View
        setUpRecyclerView();
    }
    private void setUpRecyclerView(){
        Query query = item_ref.whereEqualTo("used_status",getIntent().getStringExtra("used_status"))
                .whereEqualTo("item_category",getIntent().getStringExtra("item_category"))
                .whereEqualTo("redeemed_status",false)
                .limit(100);

        FirestoreRecyclerOptions<ItemHelper> options = new FirestoreRecyclerOptions.Builder<ItemHelper>()
                .setQuery(query,ItemHelper.class)
                .build();

        adapter = new ItemAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
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
                Intent intent = new Intent(SearchActivity.this,SeeItemActivity.class);
                intent.putExtra("item_name",item_name);
                intent.putExtra("redeem_status",redeem_status);
                intent.putExtra("item_image_url",item_image_url);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("used_status",used_status);
                intent.putExtra("item_category",item_category);
                intent.putExtra("item_description",item_description);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}