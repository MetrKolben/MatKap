package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityRegisterBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

//    private FirebaseAuth mAuth;
//    private static final String TAG = "EmailPassword";
    private Button register;
    private EditText email, pass;
    Button signInButton;
    GoogleApiClient GoogleApiClient;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient GoogleSignInClient;

    private ActivityRegisterBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_register);


        signInButton = findViewById(R.id.googleSignInButton);
        signInButton.setOnClickListener(this);
//        mAuth = FirebaseAuth.getInstance();
        register = (Button) findViewById(R.id.Register);
        email = (EditText) findViewById(R.id.getEmailAddress);
        pass = (EditText) findViewById(R.id.getPassword);
        register.setOnClickListener(this);

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


        signInButton =  findViewById(R.id.googleSignInButton);
        signInButton.setOnClickListener(this);
//        mAuth = FirebaseAuth.getInstance();
        register = (Button) findViewById(R.id.Register);
        email = (EditText) findViewById(R.id.getEmailAddress);
        pass = (EditText) findViewById(R.id.getPassword);
        register.setOnClickListener(this);
    }

    private void checkUser() {
        //if user is already signed in then go to profile activity
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            Log.d(TAG, "checkUser: Already logged in");
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

//    private void hokusPokus(){
//        mAuth.createUserWithEmailAndPassword().addOnCompleteListener()
//    }
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

    // [START auth_with_google]
    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
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
                            //user is new - Account Created
                            Log.d(TAG, "onSuccess: Account Created...\n"+email);
                            Toast.makeText(RegisterActivity.this, "Account Created...\n"+email, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //existing user - Logged In
                            Log.d(TAG, "onSuccess: Existing user...\n"+email);
                            Toast.makeText(RegisterActivity.this, "Existing user...\n"+email, Toast.LENGTH_SHORT).show();
                        }

                        //start profile activity
                        startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          //login failed
                          Log.d(TAG, "onFailure: Login failed "+e.getMessage());
                      }
                });
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
//                        }
//                    }
//                });
    }
    // [END auth_with_google]

    // [START signin]



    private void handleSingInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSingInResult?" + result.isSuccess());
        System.out.println("/////////////////////////////////////////   " + result);
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(RegisterActivity.this,"Signed in", Toast.LENGTH_SHORT).show();
            System.out.println(result.getSignInAccount().getEmail());
        } else {
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult.toString());
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(GoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(RegisterActivity.this,"Signed out", Toast.LENGTH_SHORT).show();
            }
        });
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

            case R.id.Register:
                System.out.println("////////////////////////////////////////////////////////////// ");
                createAccount(email.getText().toString(), pass.getText().toString());
                break;
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            reload();
//        }
//    }
//    // [END on_start_check_user]
//
    private void createAccount(String email, String password) {
        // [START create_user_with_email]

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("////////////////////////////////////////////////////////////// "+task.isSuccessful());
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }
//
//    private void signIn(String email, String password) {
//        // [START sign_in_with_email]
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//        // [END sign_in_with_email]
//    }
//
//    private void sendEmailVerification() {
//        // Send verification email
//        // [START send_email_verification]
//        final FirebaseUser user = mAuth.getCurrentUser();
//        user.sendEmailVerification()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // Email sent
//                    }
//                });
//        // [END send_email_verification]
//    }
//
//    private void reload() { }
//
//    private void updateUI(FirebaseUser user) {
//
//    }
//
//    @Override
//    public void onClick(View view) {
//        createAccount(email.getText().toString(), pass.getText().toString());
//    }


}