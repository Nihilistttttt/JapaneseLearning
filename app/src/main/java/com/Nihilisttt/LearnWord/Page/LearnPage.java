package com.Nihilisttt.LearnWord.Page;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.Adapter.LearnPageAdapter;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageViewModel;
import com.Nihilisttt.LearnWord.UtilityClass.BookSelectionManager;
import com.Nihilisttt.LearnWord.ViewPager2.ViewPager2ScrollController;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween3LayersManager;
import com.Nihilisttt.LearnWord.databinding.ActivityLearnPageBinding;

public class LearnPage extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private ViewPager2ScrollController scrollController;
    private LearnPageViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLearnPageBinding binding = ActivityLearnPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        viewPager2 = binding.learnPageVp2Container;
        scrollController = new ViewPager2ScrollController(viewPager2, touchSlop, 3.0f,
                ViewPager2ScrollController.ScrollDirection.VERTICAL);

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(LearnPageViewModel.class);

        String mode = getIntent().getStringExtra("mode");
        if ("review".equals(mode)) {
            String bookId = getIntent().getStringExtra("bookId");
            if (bookId == null || bookId.isEmpty()) {
                bookId = BookSelectionManager.getSelectedBookId(this);
            }
            viewModel.setReviewMode(true, bookId);
        } else {
            String bookId = getIntent().getStringExtra("bookId");
            if (bookId == null || bookId.isEmpty()) {
                bookId = BookSelectionManager.getSelectedBookId(this);
            }
            if (bookId != null && !bookId.isEmpty()) {
                viewModel.setBookId(bookId);
            }
        }

        setupViewPager();
    }

    @Override
    public void onBackPressed() {
        if (viewPager2.getCurrentItem() == 0) {
            viewPager2.setCurrentItem(1, true);
            return;
        }

        ViewPager2 overviewDetailVp2 = NestedScrollableHostBetween3LayersManager.getInstance().getOuterViewPager2();
        if (overviewDetailVp2 != null && overviewDetailVp2.getCurrentItem() != 0) {
            overviewDetailVp2.setCurrentItem(0, true);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handled = super.dispatchTouchEvent(ev);
        if (viewPager2.getCurrentItem() == 0) {
            viewPager2.setUserInputEnabled(false);
        } else {
            scrollController.handleTouchEvent(ev);
        }
        return handled;
    }

    private void setupViewPager() {
        LearnPageAdapter adapter = new LearnPageAdapter(this);
        viewPager2.setPageTransformer(adapter.getCustomPageTransformer());
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(1, false);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        View childAt = viewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

}
