package com.twj.yourtrashmytreasure.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.twj.yourtrashmytreasure.MainActivity;
import com.twj.yourtrashmytreasure.model.User;

public class GoogleAuth {
    private int GOOGLE_SIGN_IN = 101;
    private GoogleSignInClient googleSignInClient;

    private Context context;
    private AuthListener authListener;

    public GoogleAuth(Context context, AuthListener authListener){
        this.context=context;
        this.authListener=authListener;
        auth();
    }
    private void auth(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(context,gso);

        Intent i = googleSignInClient.getSignInIntent();
        ((Activity) context).startActivityForResult(i,GOOGLE_SIGN_IN);

    }
    public void activityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == GOOGLE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String url ;
            if(account.getPhotoUrl() == null){
                url = "";
            }else{
                url=account.getPhotoUrl().toString();
            }
            User user = new User(account.getId(),account.getDisplayName(),account.getEmail(),url);
            authListener.OnAuthentication(user);
        }catch (ApiException e){
            Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }
}
