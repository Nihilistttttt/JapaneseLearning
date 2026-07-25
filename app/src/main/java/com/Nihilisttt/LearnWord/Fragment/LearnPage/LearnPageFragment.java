package com.Nihilisttt.LearnWord.Fragment.LearnPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.Adapter.MainLearnPageAdapter;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween3LayersManager;
import com.Nihilisttt.LearnWord.ViewPager2.ViewPager2Navigation;

public class LearnPageFragment extends Fragment {
    private ViewPager2 viewPager2;
    private LearnPageViewModel viewModel;
    private LearnPageStateViewModel stateViewModel;
    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser()) {
            stateViewModel.setWhichFragmentInLearnPage(
                    LearnPageStateViewModel.FragmentInLearnPage.LearnPageFragment
            );
        }
    }

    private boolean isVisibleToUser() {
        return getUserVisibleHint() || isVisible();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 使用新的布局文件名
        return inflater.inflate(R.layout.fragment_learn_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化视图组件
        viewPager2 = view.findViewById(R.id.learn_page_vp2_container);

        // 设置嵌套滚动管理器
        NestedScrollableHostBetween3LayersManager nestedManager =
                NestedScrollableHostBetween3LayersManager.getInstance();
        nestedManager.setOuterViewPager2Id(viewPager2.getId());
        nestedManager.setOuterViewPager2(viewPager2);

        // 设置ViewPager
        setupViewPager(view);
    }

    private void setupViewPager(View rootView) {
        // 初始化适配器
        MainLearnPageAdapter adapter = new MainLearnPageAdapter(requireActivity());
        viewPager2.setPageTransformer(adapter.getCustomPageTransformer());
        viewPager2.setAdapter(adapter);

        // 禁用滑动边缘效果
        View childAt = viewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        // 设置导航管理器
        ViewPager2Navigation.getInstance().setOverviewDetailVp2(viewPager2);

        // 初始化按钮
        Button preButton = rootView.findViewById(R.id.pre_word);
        Button nextButton = rootView.findViewById(R.id.next_word);

        // 初始化ViewModel（使用Activity作用域）
        viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageStateViewModel.class);

        // 观察 ViewPager 滚动状态的 LiveData
        stateViewModel.getIsViewPagerScrollEnabled().observe(getViewLifecycleOwner(), enabled -> {
            viewPager2.setUserInputEnabled(enabled);
            if (!enabled) {
                viewPager2.setCurrentItem(0, false);
            }
        });

        // 设置按钮点击监听
        setupRepeatButton(preButton, () -> {
            viewModel.navigatePrevious();
            viewPager2.setCurrentItem(0, false);
            ViewPager2Navigation.getInstance().clearPendingNavigation();
        });
        setupRepeatButton(nextButton, () -> {
            viewModel.navigateNext();
            viewPager2.setCurrentItem(0, false);
            ViewPager2Navigation.getInstance().clearPendingNavigation();
        });
    }

    private void setupRepeatButton(Button button, Runnable action) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable[] repeatRef = new Runnable[1];

        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setPressed(true);
                    action.run();
                    repeatRef[0] = new Runnable() {
                        @Override
                        public void run() {
                            if (v.isPressed()) {
                                action.run();
                                handler.postDelayed(this, 1);
                            }
                        }
                    };
                    handler.postDelayed(repeatRef[0], 500);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setPressed(false);
                    if (repeatRef[0] != null) {
                        handler.removeCallbacks(repeatRef[0]);
                    }
                    return true;
            }
            return false;
        });
    }

    private void addPracticePage() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewPager2Navigation.getInstance().setOverviewDetailVp2(null);
        NestedScrollableHostBetween3LayersManager.getInstance().setOuterViewPager2(null);
    }
}
