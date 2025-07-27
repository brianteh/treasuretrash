package com.twj.yourtrashmytreasure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.twj.yourtrashmytreasure.model.User;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseFirestore firebase_database=FirebaseFirestore.getInstance();
    private CollectionReference bookmarkRef = firebase_database.collection("users");
    private TextView userName, userMail, title_fragment;
    private Button btn_google_signOut;
    private ImageView userImage;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private GoogleSignInClient googleSignInClient;
    public static User USER;
    public static List<String> list_bookmark = new ArrayList<String>();
    public FragmentManager startManager = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting Toolbar Action
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Setting User Basic Profile in Nav View
        navigationView = findViewById(R.id.nav_view);
        View navView = navigationView.inflateHeaderView(R.layout.nav_header);
        userImage = navView.findViewById(R.id.user_image);
        userName = navView.findViewById(R.id.user_name);
        userMail = navView.findViewById(R.id.user_email);
        title_fragment = findViewById(R.id.title_fragment);
        //btn_google_signOut = findViewById(R.id.btn_google_signOut);

        //Nav View Switch Fragments
        navigationView.setNavigationItemSelectedListener(this);

        //Grab user info
        USER = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        User user = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        if(user.getPhoto()!=""){
            Glide.with(this).load(user.getPhoto()).into(userImage);
        }
        userName.setText(user.getUsername());
        userMail.setText(user.getEmail());

        /*
        //Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Sign Out Button is Clicked
        btn_google_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            }
        });*/


        //Bookmark Initiation
        bookmarkRef.whereEqualTo("user_id",USER.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentSnapshot = task.getResult();
                if(documentSnapshot.isEmpty()){
                    Map<String,Object> bookmark_object = new HashMap<>();
                    bookmark_object.put("item_redeem_id","");
                    bookmark_object.put("user_id",USER.getId());
                    bookmarkRef.document().set(bookmark_object);
                    Toast.makeText(StartActivity.this,"Bookmark Successful",Toast.LENGTH_SHORT);
                }
            }
        });

        //If set
        if(savedInstanceState == null) {
            title_fragment.setText("My Items");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_add_item);
        }


    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_search_bookmark:
                title_fragment.setText("Bookmarks");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BookmarkFragment()).commit();
                break;
            case R.id.nav_add_item:
                title_fragment.setText("My Items");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AddFragment()).commit();
                break;
            case R.id.nav_search_item:
                Intent intent = new Intent(StartActivity.this,SearchActivity.class);
                intent.putExtra("used_status","Used");
                intent.putExtra("item_category","Home Appliances");
                startActivity(intent);
                break;
            case R.id.nav_signout:
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                googleSignInClient = GoogleSignIn.getClient(this, gso);
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            list_bookmark = new ArrayList<String>();
                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
                break;
            case R.id.nav_redeem_item:
                Intent i = new Intent(this,RedeemActivity.class);
                startActivity(i);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Update or Add Bookmark
        bookmarkRef.whereEqualTo("user_id",USER.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Update or Add Bookmark
        bookmarkRef.whereEqualTo("user_id",USER.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
    }
}
