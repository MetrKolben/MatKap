package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.myapplication.database.Firestore;
import com.example.myapplication.database.Sql;

import java.util.List;

public class QuizSettings extends AppCompatActivity {
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_settings);

        spinner = (Spinner) findViewById(R.id.spinner);

        List<String> list = Sql.FilterType.fillItems(Sql.FilterType.MOVEMENT.toString());
        System.out.println(list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);

//        Firestore.addUser("");
    }

}