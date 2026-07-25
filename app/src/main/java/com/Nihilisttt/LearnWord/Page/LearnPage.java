package com.Nihilisttt.LearnWord.Page;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.Adapter.LearnPageAdapter;
import com.Nihilisttt.LearnWord.R;

import com.Nihilisttt.LearnWord.ViewPager2.ViewPager2ScrollController;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween3LayersManager;

public class LearnPage extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private ViewPager2ScrollController scrollController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_page);
        int touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        viewPager2 = findViewById(R.id.learn_page_vp2_container);
        scrollController = new ViewPager2ScrollController(viewPager2, touchSlop, 3.0f,
                ViewPager2ScrollController.ScrollDirection.VERTICAL);
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
        scrollController.handleTouchEvent(ev);
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
