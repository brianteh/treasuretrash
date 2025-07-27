package com.twj.yourtrashmytreasure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.twj.yourtrashmytreasure.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddItemActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_LOCATION = 1;
    private boolean descriptionUploadSuccess = false;
    private boolean photoUploadSuccess = false;
    boolean isPermissionGranted;
    GoogleMap mgoogleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationManager locationManager;

    private EditText edittext_item_name, edittext_item_description, edittext_item_latitude, edittext_item_longitude;
    private ImageView image_item;
    private Button btn_upload_item;
    private Button btn_choose_file;
    private Button btn_get_current_location;
    private TextView btn_show_upload;
    private ProgressBar progressbar_item;
    private Spinner spinner_item_category;
    private RadioGroup radiogroup_btn;

    private String uniqueID;
    private Uri uriImage;

    private StorageReference firebase_storage;
    private FirebaseFirestore firebase_database=FirebaseFirestore.getInstance();

    private User user;
    private Context con;

    private double latitudeknown;
    private double longitudeknown;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //Initiate Map
        checkMyPermission();
        initMap();
        //Create Client
        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        //Request Permission
        ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);



        //Grab User Info
        user = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);

        //Firebase Storage Reference
        firebase_storage = FirebaseStorage.getInstance().getReference("uploads");

        //Hooks
        btn_choose_file = findViewById(R.id.btn_choose_file);
        btn_upload_item = findViewById(R.id.btn_upload_item);
        btn_get_current_location = findViewById(R.id.btn_get_current_location);
        progressbar_item = findViewById(R.id.progressbar_item);
        image_item = findViewById(R.id.image_item);
        edittext_item_name = findViewById(R.id.edittext_item_name);
        edittext_item_description = findViewById(R.id.edittext_item_description);
        edittext_item_latitude = findViewById(R.id.edittext_item_latitude);
        edittext_item_longitude = findViewById(R.id.edittext_item_longitude);
        spinner_item_category = findViewById(R.id.spinner_item_category);
        radiogroup_btn = findViewById(R.id.radiogroup_btn);

        //Button Functions
        btn_choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btn_upload_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        btn_get_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if GPS is On
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getCurrentLocation();
                }
            }
        });

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
                R.layout.my_spinner_item,
                categories
        );
        spinner_item_category.setAdapter(categoriesAdapter);


    }

    private void OnGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS? \nThis process might take up a few moment... \n").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Location location = task.getResult();
                if(location != null){
                    goToLocation(location.getLatitude(),location.getLongitude());
                }else{
                    try{
                        find_Location(this);
                        goToLocation(latitudeknown,longitudeknown);
                        Toast.makeText(this, "Location might not be accurate, please consider to try again ", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(this, "Make sure to disable the 'Auto Revoke Permissions' for App", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this,"Note that enabling GPS might take sometime, please wait until the app reverts back to the main page!",Toast.LENGTH_LONG).show();
                        Toast.makeText(this,"Enabling GPS...",Toast.LENGTH_LONG).show();
                        Toast.makeText(this,"Please click the 'SET AS CURRENT LOCATION' button again to revert back to the main page!",Toast.LENGTH_SHORT).show();
                        Toast.makeText(this,"GPS is enabled!",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    private void goToLocation(double latitude, double longitude) {
        LatLng latlng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng,18);
        mgoogleMap.moveCamera(cameraUpdate);
        if(marker != null){
            marker.remove();
            marker = mgoogleMap.addMarker(new MarkerOptions().position(latlng).title("Your Current Location"));
        }else{
            marker = mgoogleMap.addMarker(new MarkerOptions().position(latlng).title("Your Current Location"));
        }
        mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        edittext_item_latitude.setText(Double.toString(latitude));
        edittext_item_longitude.setText(Double.toString(longitude));
    }

    private void initMap(){
        //Check Permission
        if(isPermissionGranted){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_item_map);
            mapFragment.getMapAsync(this);
        }
    }

    @SuppressLint("MissingPermission")
    public void find_Location(Context con) {
        Log.d("Find Location", "in find_location");
        this.con = con;
        String location_context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) con.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {}

                        public void onProviderDisabled(String provider) {}

                        public void onProviderEnabled(String provider) {}

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {}
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                latitudeknown = location.getLatitude();
                longitudeknown = location.getLongitude();
            }
        }
    }

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(AddItemActivity.this,"Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted=true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    //Open File Chooser
    private void openFileChooser(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,PICK_IMAGE_REQUEST);
    }
    //Load Image upon Choosing
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();
            Picasso.with(this).load(uriImage).into(image_item);
        }
    }
    //Get Extension of File
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    //Upload File
    private void uploadFile(){
        String value_latitude = edittext_item_latitude.getText().toString();
        String value_longitude = edittext_item_longitude.getText().toString();
        if(uriImage != null && !value_latitude.trim().equals("") && !value_longitude.trim().equals("")){
            StorageReference fileReference = firebase_storage.child(System.currentTimeMillis()+"."+getFileExtension(uriImage));

            //START
            UploadTask uploadTask = fileReference.putFile(uriImage);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();

                        int selectedId = radiogroup_btn.getCheckedRadioButtonId();

                        //Find the radiobutton by returned id
                        RadioButton radiobtn_item_usage_status = findViewById(selectedId);
                        //Creating uniqueID for Item
                        uniqueID = UUID.randomUUID().toString();
                        //Initialize Object
                        ItemHelper item = new ItemHelper(downloadUri.toString(),user.getId(),edittext_item_name.getText().toString(),uniqueID,spinner_item_category.getSelectedItem().toString(),edittext_item_description.getText().toString(),
                                false,radiobtn_item_usage_status.getText().toString(),Double.parseDouble(edittext_item_longitude.getText().toString()),Double.parseDouble(edittext_item_latitude.getText().toString()));

                        //Add Item Reference
                        CollectionReference item_ref = firebase_database.collection("items");
                        item_ref.document(uniqueID).set(item);
                        Toast.makeText(AddItemActivity.this,"Successful",Toast.LENGTH_LONG).show();
                        descriptionUploadSuccess = true;
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

            //END
            fileReference.putFile(uriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressbar_item.setProgress(0);
                                }
                            },500);
                            photoUploadSuccess = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddItemActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressbar_item.setProgress((int) progress);
                        }
                    });
        }else{
            Toast.makeText(this,"No File Selected / Empty location coordinates",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        /*try{
            Geocoder geocoder = new Geocoder(AddItemActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
        }catch (Exception e){
            Log.e("ERROR",e.toString());
        }*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

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
        mgoogleMap = googleMap;
        //Map CLick Update
        mgoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if(marker != null){
                    marker.remove();
                }
                marker = mgoogleMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location"));
                edittext_item_latitude.setText(Double.toString(latLng.latitude));
                edittext_item_longitude.setText(Double.toString(latLng.longitude));
            }
        });
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