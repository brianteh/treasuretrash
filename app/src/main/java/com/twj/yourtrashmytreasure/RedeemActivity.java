package com.twj.yourtrashmytreasure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class RedeemActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference item_ref = db.collection("items");
    private ItemHelper item ;
    private List<String> id = new ArrayList<String>();
    private List<String> item_data = new ArrayList<String>();
    private Button btn_scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        btn_scan = findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize Intent Integrator
                IntentIntegrator intentIntegrator = new IntentIntegrator(RedeemActivity.this);
                //Set Stuffs in Intent Integrator
                intentIntegrator.setPrompt("Press Volume Up For Flash Light");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);

                //Start Scan
                intentIntegrator.initiateScan();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Initialize Intent Result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        //Check Conditions
        if(intentResult.getContents() != null){
            String item_redeem_id = intentResult.getContents();
            AlertDialog.Builder builder = new AlertDialog.Builder(RedeemActivity.this);

            //Set Stuffs for the Dialog Interface
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Close Dialog
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    item_ref.document(item_redeem_id).update("redeemed_status",true);
                    Toast.makeText(RedeemActivity.this,"Item redeemed successfully!",Toast.LENGTH_LONG).show();
                    //Close Dialog
                    dialog.dismiss();
                }
            });
            item_ref.document(item_redeem_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(task.isSuccessful()){
                        item = documentSnapshot.toObject(ItemHelper.class);
                        if(!item.getRedeemed_status()){
                            builder.setTitle("Redeem Item?");
                            builder.setMessage("Item Name: "+item.getItem_name()+"\n"+"Item Category: "+item.getItem_category()+"\n"+"Usage Status: "+item.getUsed_status()+"\n");
                            //Show Dialog
                            builder.show();
                        }else{
                            Toast.makeText(RedeemActivity.this,"Item Redeemed!",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                       Toast.makeText(RedeemActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(getApplicationContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
        }
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