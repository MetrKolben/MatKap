package cz.matkap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cz.matkap.R;

import cz.matkap.firebase.Sql;

public class MainActivity extends AppCompatActivity {

    private Button goToSignInBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Sql.setContext(this);

        goToSignInBtn = findViewById(R.id.goToSignInButton);
        goToSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });





    }
}