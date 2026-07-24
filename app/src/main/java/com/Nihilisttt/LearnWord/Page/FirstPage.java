package com.Nihilisttt.LearnWord.Page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Nihilisttt.LearnWord.Fragment.FirstPage.AfterSignInFragment;
import com.Nihilisttt.LearnWord.Fragment.FirstPage.BeforeSignInFragment;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.DataImporter;

public class FirstPage extends AppCompatActivity implements View.OnClickListener {

    private Button review_button;
    private Button learn_button;
    private ImageButton user_button;
    private FrameLayout sign_in_frame_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        DataImporter.importIfNeeded(this);
        intiViews();

        replaceFragment(new BeforeSignInFragment());
        user_button.setOnClickListener(this);
        learn_button.setOnClickListener(this);
        review_button.setOnClickListener(this);
        sign_in_frame_layout.setOnClickListener(this);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sign_in_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void intiViews() {
        learn_button = findViewById(R.id.learn_button);
        review_button = findViewById(R.id.review_button);
        user_button = findViewById(R.id.user_page);
        learn_button = findViewById(R.id.learn_button);
        review_button = findViewById(R.id.review_button);
        sign_in_frame_layout = findViewById(R.id.sign_in_frame_layout);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_page: {
                Intent intent = new Intent(this, UserPage.class);
                startActivity(intent);
                break;
            }
            case R.id.learn_button: {
                Intent intent = new Intent(this, LearnPage.class);
                startActivity(intent);
                break;
            }
            case R.id.review_button: {
                Intent intent = new Intent(this, ReviewPage.class);
                startActivity(intent);
                break;
            }
            case R.id.sign_in_frame_layout: {
                replaceFragment(new AfterSignInFragment());
                sign_in_frame_layout.setClickable(false);
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    sign_in_frame_layout.setVisibility(View.INVISIBLE);
                    handler.removeCallbacksAndMessages(null);
                }, 5000);
                break;
            }
        }
    }
}