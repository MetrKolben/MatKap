package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.Sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizSettings extends AppCompatActivity {


    TextView tv;
    Button btn;

    List<String> listFromDatabase;
    String[] movementArray;
    List<String> passDataList;
    boolean[] selectedMovement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_settings);
        tv = findViewById(R.id.selectMovement);
        btn = findViewById(R.id.goToQuizButton);

        Sql.setContext(this);
        listFromDatabase = Sql.FilterType.MOVEMENT.items;
        movementArray = listFromDatabase.toArray(new String[0]);
        passDataList = new ArrayList<>();
        selectedMovement = new boolean[listFromDatabase.size()];


        Arrays.fill(selectedMovement, true);
        createAlertDialogWindow(tv);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passDataList = movementList(movementArray, selectedMovement);
                if (!passDataList.isEmpty()) {
                    Intent intent = new Intent(QuizSettings.this, QuestionActivity.class);
                    intent.putExtra("passDataList", (Serializable) passDataList);
                    startActivity(intent);
                } else {
                    Toast.makeText(QuizSettings.this, "Nevybral si žádný směr", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * <p>Function that creates a list containing all the movements selected by the user.</p>
     * <p>Data are collected from the arrays in parameter.</p>
     * @param movements Array that holds strings of all movements in database.
     * @param values Array that holds boolean values. If it equals true, then the movement of that index was selected
     *
     *
     * @return list of selected movements
     */
    public static List<String> movementList(String[] movements, boolean[] values) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < movements.length; i++) {
            if (values[i]) {
                list.add(movements[i]);
            }
        }
        return list;
    }

    /**
     * <p>Method that creates on click a new <b>AlertDialog.builder</b>. Using this, user can easily select the movement for the test.</p>
     * <p>The <b>AlertDialog.builder</b> contains three buttons. One that adds the movements to the list of selected,
     * it also containes a cancel button. On top of that it contains button that removes all movements from the selected list.</p>
     * @param textView <b>TextView</b> that is being pressed in order to select the movement of users choice
     */
    public void createAlertDialogWindow(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizSettings.this);
                builder.setTitle("Vyber směr");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(movementArray, selectedMovement, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selectedMovement[i] = b;
                        String currentItems = listFromDatabase.get(i);
                        Toast.makeText(QuizSettings.this, currentItems + " " + b, Toast.LENGTH_SHORT).show();
                    }
                });


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textView.setText("Your selected movement\n");
                        for (int i = 0; i < selectedMovement.length; i++) {
                            boolean checked = selectedMovement[i];
                            if (checked) {
                                textView.setText(textView.getText() + "\u25AA " + listFromDatabase.get(i) + "\n");
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
                            // dayList.clear();
                            textView.setText("");
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}