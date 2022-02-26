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
import com.example.myapplication.cheat_sheet.AuthorAdapter;
import com.example.myapplication.cheat_sheet.Book;
import com.example.myapplication.cheat_sheet.BookAdapter;
import com.example.myapplication.cheat_sheet.Movement;
import com.example.myapplication.cheat_sheet.MovementAdapter;
import com.example.myapplication.firebase.Sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    int selectedOption = 0;

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
        setRecyclerView(0);
    }

    private void setRecyclerView(int option) {

        BookAdapter bookAdapter = new BookAdapter(bookList);
        AuthorAdapter authorAdapter = new AuthorAdapter(authorList);
        MovementAdapter movementAdapter = new MovementAdapter(movementList);
//        BookAdapter bookAdapter = new BookAdapter(bookList);
//        recyclerView.setAdapter(bookAdapter);
//        recyclerView.setHasFixedSize(true);
        switch (option) {
            case 0:
                recyclerView.setAdapter(bookAdapter);
                System.out.println("bookAdapter " + selectedOption);
                break;
            case 1:
                recyclerView.setAdapter(authorAdapter);
                System.out.println("authorAdapter " + selectedOption);
                break;
            case 2:
                recyclerView.setAdapter(movementAdapter);
                System.out.println("movementAdapter " + selectedOption);
                break;
        }
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {

        bookList = new ArrayList<>();
        authorList = new ArrayList<>();
        movementList = new ArrayList<>();

        for (Sql.Author author : Sql.Author.getAuthorList()) {
            authorList.add(new Author(author.getName(), author.getBooks(), author.getMovement(), author.getCountry()));
        }

        for (Sql.Book book : Sql.Book.getBookList()) {
            bookList.add(new Book(book.getName(), book.getAuthor(), book.getMovement(), book.getDruh(), book.getGenre(), book.getYear()));
        }

        for (Sql.Movement movement : Sql.Movement.getMovementList()) {
            movementList.add(new Movement(movement.getName(), movement.getSign(), movement.getCentury(), movement.getAuthors()));
        }

    }

    private void changeFilter(int selectedOption) {
        switch (selectedOption) {
            case 0:
                setRecyclerView(0);
                break;
            case 1:
                setRecyclerView(1);
                break;
            case 2:
                setRecyclerView(2);
                break;
        }

    }

    private void createDialogBox() {
        String[] options = {BOOK_SELECT, AUTHOR_SELECT, MOVEMENT_SELECT};
        AlertDialog.Builder builder = new AlertDialog.Builder(CheatSheet.this);
        builder.setTitle("Vyber");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(options, selectedOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedOption = which;
                System.out.println(selectedOption);
                changeFilter(selectedOption);

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