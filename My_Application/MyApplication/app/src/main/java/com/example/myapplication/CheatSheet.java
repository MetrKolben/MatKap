package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.myapplication.cheat_sheet.Author;
import com.example.myapplication.cheat_sheet.Book;
import com.example.myapplication.cheat_sheet.BookAdapter;
import com.example.myapplication.cheat_sheet.Movement;

import java.util.ArrayList;
import java.util.List;

public class CheatSheet extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;


    ImageButton filter;
    ImageButton goToMenu;
    ImageButton goToQuest;

    List<Book> bookList;
    List<Author> authorList;
    List<Movement> movementList;

    private static final String BOOK_SELECT = "Knihy";
    private static final String AUTHOR_SELECT = "Autoři";
    private static final String MOVEMENT_SELECT = "Směry";

    int selectedOption = 1;

    /* TODO  uloží si int toho co uživatel chce vidět (0 = knihy; 1 = autori; 2 = smery). Dále je potřeba naplit Listy informacemi z databáze*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat_sheet);

        goToQuest = findViewById(R.id.quest_button_cs);
        goToMenu = findViewById(R.id.home_button_cs);

        goToQuest.setOnClickListener(this);
        goToMenu.setOnClickListener(this);

        recyclerView = findViewById(R.id.cheatsheet);
        filter = findViewById(R.id.cheatsheet_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogBox();
            }

        });

        initData();
        setRecyclerView();
    }

    private void setRecyclerView() {
        BookAdapter bookAdapter = new BookAdapter(bookList);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {

        bookList = new ArrayList<>();

        bookList.add(new Book("Ahoj tati", "martin chmelar", "dětská literatura", "detsky pudr", "divne deti", 1984));
        bookList.add(new Book("sad", "dsa", "ads", " ", " ", 41));
        bookList.add(new Book("Ahoj tati", "martin chmelar", "dětská literatura", "detsky pudr", "divne deti", 1984));
        bookList.add(new Book("sad", "dsa", "ads", " ", " ", 41));
        bookList.add(new Book("Ahoj tati", "martin chmelar", "dětská literatura", "detsky pudr", "divne deti", 1984));
        bookList.add(new Book("sad", "dsa", "ads", " ", " ", 41));
        bookList.add(new Book("Ahoj tati", "martin chmelar", "dětská literatura", "detsky pudr", "divne deti", 1984));
        bookList.add(new Book("sad", "dsa", "ads", " ", " ", 41));
        bookList.add(new Book("Ahoj tati", "martin chmelar", "dětská literatura", "detsky pudr", "divne deti", 1984));
        bookList.add(new Book("sad", "dsa", "ads", " ", " ", 41));
        bookList.add(new Book("Ahoj tati", "martin chmelar", "dětská literatura", "detsky pudr", "divne deti", 1984));
        bookList.add(new Book("sad", "dsa", "ads", " ", " ", 41));

        // TODO doplnit. Zde se naplní listy těmi informacemi
    }

    private void createDialogBox() {
        String[] options = {BOOK_SELECT, AUTHOR_SELECT, MOVEMENT_SELECT};
        AlertDialog.Builder builder = new AlertDialog.Builder(CheatSheet.this);
        builder.setTitle("Vyber");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedOption = which;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.quest_button_cs:
                Intent intent_Three = new Intent(this, QuestActivity.class);
                startActivity(intent_Three);
                break;

            case R.id.home_button_cs:
                Intent intentpopadesatepate = new Intent(this, MenuActivity.class);
                startActivity(intentpopadesatepate);
                break;

        }
    }
}