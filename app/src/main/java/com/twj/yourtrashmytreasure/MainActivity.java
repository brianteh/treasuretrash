package com.twj.yourtrashmytreasure;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.twj.yourtrashmytreasure.auth.AuthListener;
import com.twj.yourtrashmytreasure.auth.GoogleAuth;
import com.twj.yourtrashmytreasure.model.User;

public class MainActivity extends AppCompatActivity {
    private Button btn_google_signIn;
    private GoogleAuth googleAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_google_signIn = findViewById(R.id.btn_google_signIn);
        btn_google_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleAuth.activityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            auth();
            Toast.makeText(this,"Signing in...",Toast.LENGTH_LONG).show();
        }
        super.onStart();
    }

    private void auth(){
        googleAuth = new GoogleAuth(MainActivity.this, new AuthListener() {
            @Override
            public void OnAuthentication(User user) {
                Intent i = new Intent(MainActivity.this,StartActivity.class);
                i.putExtra("user",new Gson().toJson(user));
                startActivity(i);
                finish();
            }
        });
    }
}