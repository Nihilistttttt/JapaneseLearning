package com.Nihilisttt.LearnWord.Page;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Nihilisttt.LearnWord.Fragment.FirstPage.AfterSignInFragment;
import com.Nihilisttt.LearnWord.Fragment.FirstPage.BeforeSignInFragment;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.databinding.ActivityFirstPageBinding;

public class FirstPage extends AppCompatActivity implements View.OnClickListener {

    private ActivityFirstPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirstPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new BeforeSignInFragment());
        binding.userPage.setOnClickListener(this);
        binding.learnButton.setOnClickListener(this);
        binding.reviewButton.setOnClickListener(this);
        binding.signInFrameLayout.setOnClickListener(this);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sign_in_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.user_page) {
            Intent intent = new Intent(this, UserPage.class);
            startActivity(intent);
        } else if (id == R.id.learn_button) {
            Intent intent = new Intent(this, WordBookSelectPage.class);
            startActivity(intent);
        } else if (id == R.id.review_button) {
            Intent intent = new Intent(this, ReviewPage.class);
            startActivity(intent);
        } else if (id == R.id.sign_in_frame_layout) {
            replaceFragment(new AfterSignInFragment());
            binding.signInFrameLayout.setClickable(false);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                binding.signInFrameLayout.setVisibility(View.INVISIBLE);
                handler.removeCallbacksAndMessages(null);
            }, 5000);
        }
    }
}
