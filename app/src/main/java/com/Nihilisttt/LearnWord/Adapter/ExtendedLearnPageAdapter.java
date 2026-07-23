package com.Nihilisttt.LearnWord.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class ExtendedLearnPageAdapter extends FragmentStateAdapter {
    // 当前版本只包含学习页面
    private boolean enableCustomAnimation = false;
    private static final float MIN_SCALE = 0.75f;
    private List<Fragment> fragmentList;

    public ExtendedLearnPageAdapter(FragmentActivity fragmentActivity, List<Fragment> fragmentList) {
        super(fragmentActivity);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置返回对应的Fragment（当前只有学习页面）
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public void enableCustomAnimation(boolean enable) {
        this.enableCustomAnimation = enable;
    }

    public ViewPager2.PageTransformer getCustomPageTransformer() {
        return (view, position) -> {
            if (!enableCustomAnimation) {
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);
                return;
            }

            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                view.setAlpha(0f);
            } else if (position <= 0) { // [-1,0]
                view.setAlpha(1 - position);
                view.setTranslationX(pageWidth * -position);
                float scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position)));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else if (position <= 1) { // (0,1]
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
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