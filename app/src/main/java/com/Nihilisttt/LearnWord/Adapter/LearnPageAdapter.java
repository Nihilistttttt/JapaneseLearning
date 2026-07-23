package com.Nihilisttt.LearnWord.Adapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.Fragment.SearchPage.SearchPageFragment;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.LearnPageFragment;

public class LearnPageAdapter extends FragmentStateAdapter {
    // 当前版本只包含学习页面
    private static final int PAGE_COUNT = 2;
    private static final float MIN_SCALE = 0.85f;
    private final String TAG = "LearnPageAdapter";

    public LearnPageAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置返回对应的Fragment（当前只有学习页面）
        switch (position) {
            case 0:
                return new SearchPageFragment();
            // 未来扩展示例：
            case 1:
                return new LearnPageFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }

    // Custom page transformer for animation
    public ViewPager2.PageTransformer getCustomPageTransformer() {
        return (view, position) -> {
            int pageHeight = view.getHeight();

            if (position < -1) { // 完全滑出屏幕上方
                view.setAlpha(0f);
            } else if (position <= 0) {
                view.setAlpha(1 + position);
                // 垂直移动：从下方滑入
                view.setTranslationY(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);
            } else if (position <= 1) {
                // 透明度：从1到0渐变
                view.setAlpha(1 - 3*position);
                // 垂直移动：向上滑动（负值表示向上）
                view.setTranslationY(pageHeight * -position);
                // 缩放效果：从1缩小到MIN_SCALE
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // 完全滑出屏幕下方
                view.setAlpha(0f);
            }
        };
    }

    // 未来扩展方法（示例）
    public void addNewPage(@NonNull Class<? extends Fragment> fragmentClass) {
        // 这里可以添加扩展逻辑
        // 需要配合数据源修改和notifyDataSetChanged()
    }
}