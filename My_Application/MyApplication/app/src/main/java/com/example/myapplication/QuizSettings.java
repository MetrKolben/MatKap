package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.Sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizSettings extends AppCompatActivity {
    TextView tv;
    boolean[] selectedMovement;


    ArrayList<Integer> dayList = new ArrayList<>();
    List<String> list = Sql.FilterType.fillItems(Sql.FilterType.MOVEMENT.toString());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_settings);
        tv = findViewById(R.id.selectMovement);

        selectedMovement = new boolean[list.size()];

        Arrays.fill(selectedMovement, true);

        String[] movementArray = list.toArray(new String[0]);

        System.out.println(Arrays.toString(movementArray) + Arrays.toString(selectedMovement));

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(QuizSettings.this);

                builder.setTitle("Vyber směr");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(movementArray, selectedMovement, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selectedMovement[i] = b;

                        String currentItems = list.get(i);

                        Toast.makeText(QuizSettings.this, currentItems + " " + b, Toast.LENGTH_SHORT).show();
                    }
                });


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        StringBuilder sb = new StringBuilder();
//                        for (int j = 0; j < dayList.size(); j++) {
//                            sb.append(list.get(dayList.get(j)));
//                            if(j != dayList.size()-1) {
//                                sb.append(", ");
//                            }
//                        }
//                        tv.setText(sb.toString());

                        tv.setText("Your selected movement\n");
                        for (int i = 0; i < selectedMovement.length; i++) {
                            boolean checked = selectedMovement[i];

                            if (checked) {
                                tv.setText(tv.getText() + "\u25AA " + list.get(i) + "\n");
                            }

                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int j = 0; j < selectedMovement.length; j++) {
                            selectedMovement[j] = false;
                            dayList.clear();
                            tv.setText("");
                        }
                    }
                });



                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
//
//        spinner = (Spinner) findViewById(R.id.spinner);
//
//        List<String> list = Sql.FilterType.fillItems(Sql.FilterType.MOVEMENT.toString());
//        System.out.println(list);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
//        spinner.setAdapter(adapter);


    }
}