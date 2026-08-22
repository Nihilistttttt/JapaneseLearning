package com.Nihilisttt.LearnWord.Page;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Nihilisttt.LearnWord.Database.Repository.WordBookRepository;
import com.Nihilisttt.LearnWord.Fragment.FirstPage.AfterSignInFragment;
import com.Nihilisttt.LearnWord.Fragment.FirstPage.BeforeSignInFragment;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.BookSelectionManager;
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
        binding.bookDisplayText.setOnClickListener(this);

        updateBookDisplay();
        createTestReviewsIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBookDisplay();
    }

    private void updateBookDisplay() {
        String bookName = BookSelectionManager.getSelectedBookName(this);
        TextView tv = binding.bookDisplayText;
        if (bookName != null && !bookName.isEmpty()) {
            tv.setText("当前词书: " + bookName);
        } else {
            tv.setText("点击选择词书");
        }
    }

    private void createTestReviewsIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("debug_prefs", MODE_PRIVATE);
        if (prefs.getBoolean("test_reviews_created_v3", false)) return;

        WordBookRepository repo = WordBookRepository.getInstance(this);
        String[] bookIds = {"jlpt_n5", "jlpt_n4", "jlpt_n3", "jlpt_n2", "jlpt_n1"};
        new Thread(() -> {
            for (int attempt = 0; attempt < 15; attempt++) {
                try { Thread.sleep(3000); } catch (InterruptedException e) { return; }
                boolean allReady = true;
                for (String bookId : bookIds) {
                    if (repo.getTotalWordCount(bookId) == 0) { allReady = false; break; }
                }
                if (!allReady) continue;
                for (String bookId : bookIds) {
                    repo.createTestReviews(bookId, 20);
                }
                prefs.edit().putBoolean("test_reviews_created_v3", true).apply();
                return;
            }
        }).start();
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
            String bookId = BookSelectionManager.getSelectedBookId(this);
            if (bookId != null && !bookId.isEmpty()) {
                Intent intent = new Intent(this, LearnPage.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, WordBookSelectPage.class);
                startActivity(intent);
            }
        } else if (id == R.id.review_button) {
            String bookId = BookSelectionManager.getSelectedBookId(this);
            if (bookId != null && !bookId.isEmpty()) {
                Intent intent = new Intent(this, LearnPage.class);
                intent.putExtra("mode", "review");
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "请先选择词书", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.book_display_text) {
            Intent intent = new Intent(this, WordBookSelectPage.class);
            intent.putExtra("select_only", true);
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
