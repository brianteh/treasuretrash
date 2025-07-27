package com.twj.yourtrashmytreasure;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

public class MyItemActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    GoogleMap mGoogleMap;
    private TextView item_name,item_description,item_category,item_used_status,item_latitude,item_longitude;
    private ImageView item_image_url, item_redeem_id;
    private Button btn_see_item_location,back_btn;
    private String latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);

        //Initialize Map
        initMap();

        //Set lat and long
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");

        //Hooks
        item_image_url = findViewById(R.id.item_image);
        item_category = findViewById(R.id.textview_item_category);
        item_name = findViewById(R.id.textview_item_name);
        item_description = findViewById(R.id.textview_item_description);
        item_used_status = findViewById(R.id.textview_item_used_status);
        item_longitude = findViewById(R.id.textview_item_longitude);
        item_latitude = findViewById(R.id.textview_item_latitude);
        item_redeem_id = findViewById(R.id.item_redeem_id);
        btn_see_item_location = findViewById(R.id.btn_see_item_location);

        //getIntent().getStringExtra()
        item_category.setText(getIntent().getStringExtra("item_category"));
        item_name.setText(getIntent().getStringExtra("item_name"));
        item_description.setText(getIntent().getStringExtra("item_description"));
        item_used_status.setText(getIntent().getStringExtra("used_status"));
        item_latitude.setText("Latitude: "+latitude);
        item_longitude.setText("Longitude: "+longitude);
        Picasso.with(this).load(getIntent().getStringExtra("item_image_url")).into(item_image_url);

        //Load QR Code
        MultiFormatWriter writer = new MultiFormatWriter();
        try{
            BitMatrix matrix = writer.encode(getIntent().getStringExtra("item_redeem_id").toString(), BarcodeFormat.QR_CODE,350,350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            item_redeem_id.setImageBitmap(bitmap);
        }catch(Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }

        //Mark Location
        btn_see_item_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markMap();
            }
        });


    }

    private void markMap(){
        //Mark Location on Google Map
        LatLng latlng = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng,17);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.addMarker(new MarkerOptions().position(latlng).title("Item Location"));
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_item_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}