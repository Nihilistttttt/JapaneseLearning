package com.Nihilisttt.LearnWord.Adapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.Fragment.LearnPage.ExtendedLearnPage.ExtendedLearnPageFragment;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage.MainLearnPageFragment;

public class MainLearnPageAdapter extends FragmentStateAdapter {
    // 当前版本只包含学习页面
    private static final int PAGE_COUNT = 2;
    private static final float MIN_SCALE = 0.75f;
    private final String TAG = "LearnPageAdapter";

    public MainLearnPageAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 根据位置返回对应的Fragment（当前只有学习页面）
        switch (position) {
            case 0:
                return new MainLearnPageFragment();
            // 未来扩展示例：
            case 1:
                return new ExtendedLearnPageFragment();
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
            // 打印子View信息
            // 废弃方案，但是保留，以后也许能用到
//            ViewGroup parent = (ViewGroup) view;
//            Log.d(TAG, "Children count: " + parent.getChildCount());
//            View child = parent.getChildAt(0);
//            Log.d(TAG, String.format("Child %d: %s (ID: %s)",
//                    0,
//                    child.getClass().getSimpleName(),
//                    child.getId() != View.NO_ID ? String.valueOf(child.getId()) : "no-id"));
//
//            if (!(child.getId() == R.id.learn_page_fragment)) {
//                view.setAlpha(1f);
//                view.setTranslationX(0f);
//                view.setScaleX(1f);
//                view.setScaleY(1f);
//                return;
//            }
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                view.setAlpha(0f);
            } else if (position <= 0) { // [-1,0]
                view.setAlpha(1 + position);
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