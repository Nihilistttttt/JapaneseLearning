package com.Nihilisttt.LearnWord.Page;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.Nihilisttt.LearnWord.databinding.ActivityUserPageBinding;

public class UserPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserPageBinding binding = ActivityUserPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}