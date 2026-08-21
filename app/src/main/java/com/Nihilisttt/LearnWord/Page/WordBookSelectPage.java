package com.Nihilisttt.LearnWord.Page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Nihilisttt.LearnWord.Adapter.WordBookAdapter;
import com.Nihilisttt.LearnWord.Database.Entities.WordBookEntity;
import com.Nihilisttt.LearnWord.Database.Repository.WordBookRepository;
import com.Nihilisttt.LearnWord.R;

import java.util.List;

public class WordBookSelectPage extends AppCompatActivity {

    private WordBookRepository repository;
    private WordBookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_book_select);

        repository = WordBookRepository.getInstance(this);

        View btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WordBookAdapter(book -> {
            Intent intent = new Intent(this, LearnPage.class);
            intent.putExtra("bookId", book.getBookId());
            intent.putExtra("bookName", book.getName());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        repository.getAllWordBooks().observe(this, new Observer<List<WordBookEntity>>() {
            @Override
            public void onChanged(List<WordBookEntity> books) {
                adapter.setBookList(books);
            }
        });
    }
}