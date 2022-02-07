package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.myapplication.databinding.ActivityProfileBinding;
import com.example.myapplication.firebase.Firestore;
import com.example.myapplication.firebase.Storage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URI;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {
    public static boolean isCreated = false;
    //view binding
    private ActivityProfileBinding binding;

    private FirebaseAuth firebaseAuth;

    private static ImageView imgProfilePic;
    private static TextView emailTV;
    private static TextView nameTv;
    private static TextView levelTV;
    private static ProgressBar xpBar;

    public static void setProfilePic(String path) {
        imgProfilePic.setImageDrawable(Drawable.createFromPath(path));
    }

    public static void setEmailText(String text) {
        emailTV.setText(text);
    }

    public static void setNameText(String text) {
        nameTv.setText(text);
    }

    public static void setLevelText(String text) {
        levelTV.setText(text);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setXp(int xp, int lvl) {
        xpBar.setMax(lvl*Firestore.XP_PER_LEVEL);
        xpBar.setProgress(xp, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void fillInfo(String profilePath, String emailText, String nameText, String levelText, int xp, int lvl) {
//        new Thread(()-> {
//            while(!isCreated);
        if (emailTV == null) return;
        setEmailText(emailText);
        setProfilePic(profilePath);
        setNameText(nameText);
        setLevelText(levelText);
        setXp(xp, lvl);

//        }).start();
    }

    private ImageButton zuHomeGehen;
    private ImageButton zuQuestGehen;



    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private static final String TAG = "PROFILE_ACTIVITY_TAG";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        zuHomeGehen = findViewById(R.id.home_button_profile);

        zuHomeGehen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        zuQuestGehen = findViewById(R.id.quest_button_profile);

        zuQuestGehen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, QuestActivity.class);
                startActivity(intent);
            }
        });

        emailTV = findViewById(R.id.emailTv);
        nameTv = findViewById(R.id.name);
        levelTV = findViewById(R.id.tv_level);
        imgProfilePic = findViewById(R.id.profilePhoto);
        xpBar = findViewById(R.id.xpBar);

        // TODO změnit podle Firestore, danke bitte wiedersehen
        /**
         * Už jsem to udělal, Adame. Chtěl jsem ti trochu ulehčit práci.
         * @author Honza
         */
        if (Firestore.user != null) {
            fillInfo(
                    Storage.getProfilePicturePath(Firestore.user.getLvl()),
                    Firestore.getEmail(),
                    Firestore.getName(),
                    "" + Firestore.user.getLvl(),
                    Firestore.user.getXp(),
                    Firestore.user.getLvl());
        }

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //handle click, logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Google sign out
                signOut();
                checkUser();
            }
        });

        binding.deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this, R.style.Base_Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Opravdu chcete zrušit účet!");
                builder.setNegativeButton("Ano", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                        signOut();
                        checkUser();
                    }
                });// Delete account
                builder.setPositiveButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        /**
         * <p>isCreated is used to ensure that the function fillInfo() will
         * call the individual subfunctions only after the Activity is created</p>
         */
        isCreated = true;
    }

    private void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void deleteAccount() {
        Log.d(TAG, "delete account");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Firestore.deleteUser();
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG,"Account deleted successfully!");
                } else {
                    Log.w(TAG,"Something is wrong!");
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {

    }


    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            //user not logged in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else {
            //user logged in
            //get user info

        }
    }
}