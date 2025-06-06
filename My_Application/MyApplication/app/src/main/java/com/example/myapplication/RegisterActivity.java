package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityRegisterBinding;
import com.example.myapplication.firebase.Firestore;
import com.example.myapplication.firebase.Storage;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private Button signInButton;
    private ProgressBar loadBar;
    private ImageView appName;
    private static RegisterActivity loginActivity = null;

    GoogleApiClient GoogleApiClient;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth firebaseAuth;

    private ActivityRegisterBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_register);

        signInButton = findViewById(R.id.googleSignInButton);
        loadBar = findViewById(R.id.progressBar);
        appName = findViewById(R.id.app_name);


        loadBar.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.VISIBLE);
        appName.setVisibility(View.VISIBLE);
        signInButton.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        binding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin google sign in
                Log.d(TAG, "onClick: begin Google SignIn");
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void waitForFirebase() {
        loadBar.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.INVISIBLE);
        appName.setVisibility(View.INVISIBLE);
        loginActivity = this;
    }

    public static void firebaseLoaded() {
        new Thread(() -> {
            Storage.downloadPics();
            while (!Storage.isDownloaded());
            loginActivity.startActivity(new Intent(loginActivity, ProfileActivity.class));
            loginActivity.finish();
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkUser() {
        //if user is already signed in then go to profile activity
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            Firestore.setFirebaseUser(firebaseUser, false);
            Log.d(TAG, "checkUser: Already logged in");
            waitForFirebase();
//            startActivity(new Intent(this, ProfileActivity.class));
//            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSingInResult(result);
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google Signin intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "onActivityResult: "+e.getMessage());
            }
        }
    }
    // [END onactivityresult]

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.S)
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success
                        Log.d(TAG, "onSuccess: Logged In");

                        //get logged in user
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        //get user info
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        Log.d(TAG, "onSuccess: Email: "+email);
                        Log.d(TAG, "onSuccess: UID: "+uid);

                        //check if user is new or existing
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            Firestore.setFirebaseUser(firebaseUser, true);
                            //user is new - Account Created
                            Log.d(TAG, "onSuccess: Account Created...\n"+email);
                            Toast.makeText(RegisterActivity.this, "Account Created...\n"+email, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Firestore.setFirebaseUser(firebaseUser, false);
                            //existing user - Logged In
                            Log.d(TAG, "onSuccess: Existing user...\n"+email);
                            Toast.makeText(RegisterActivity.this, "Existing user...\n"+email, Toast.LENGTH_SHORT).show();
                        }

                        //start profile activity
                        waitForFirebase();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //login failed
                        Log.d(TAG, "onFailure: Login failed "+e.getMessage());
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]

    private void handleSingInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult?" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(RegisterActivity.this,"Signed in", Toast.LENGTH_SHORT).show();
        } else {
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult.toString());
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(GoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.googleSignInButton:
                signIn();
                break;
        }
    }


}