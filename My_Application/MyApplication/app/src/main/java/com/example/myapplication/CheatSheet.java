package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.myapplication.cheat_sheet.Book;
import com.example.myapplication.cheat_sheet.BookAdapter;

import java.util.ArrayList;
import java.util.List;

public class CheatSheet extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Book> bookList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat_sheet);

        recyclerView = findViewById(R.id.cheatsheet);

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

        // todo doplnit
    }
}