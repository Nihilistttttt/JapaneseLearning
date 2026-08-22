package com.Nihilisttt.LearnWord.Fragment.LearnPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
        Button btnSrsWrong = rootView.findViewById(R.id.btn_srs_wrong);
        Button btnSrsNext = rootView.findViewById(R.id.btn_srs_next);
        LinearLayout reviewButtonBar = rootView.findViewById(R.id.review_button_bar);
        Button btnReviewForget = rootView.findViewById(R.id.btn_review_forget);
        Button btnReviewFuzzy = rootView.findViewById(R.id.btn_review_fuzzy);
        Button btnReviewRecognize = rootView.findViewById(R.id.btn_review_recognize);

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

        viewModel.getCurrentStage().observe(getViewLifecycleOwner(), stage -> {
            boolean srs = stage != null;
            preButton.setVisibility(srs ? View.GONE : View.VISIBLE);
            nextButton.setVisibility(srs ? View.GONE : View.VISIBLE);
        });

        viewModel.getReviewStage().observe(getViewLifecycleOwner(), stage -> {
            if (!viewModel.isReviewMode()) {
                reviewButtonBar.setVisibility(View.GONE);
                return;
            }
            if (stage == null) {
                reviewButtonBar.setVisibility(View.GONE);
                return;
            }
            preButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            boolean showReviewBar = stage == LearnPageViewModel.ReviewStage.SHOW_WORD;
            reviewButtonBar.setVisibility(showReviewBar ? View.VISIBLE : View.GONE);
            if (showReviewBar) {
                btnSrsWrong.setVisibility(View.GONE);
                btnSrsNext.setVisibility(View.GONE);
            } else {
                boolean showTwoButtons = stage == LearnPageViewModel.ReviewStage.REVEAL_RECOGNIZE
                        || stage == LearnPageViewModel.ReviewStage.REVEAL_FUZZY;
                ConstraintLayout.LayoutParams paramsNext = (ConstraintLayout.LayoutParams) btnSrsNext.getLayoutParams();
                if (showTwoButtons) {
                    btnSrsWrong.setText("记错了");
                    btnSrsWrong.setVisibility(View.VISIBLE);
                    btnSrsNext.setText("下一词");
                    btnSrsNext.setVisibility(View.VISIBLE);
                    paramsNext.startToStart = R.id.guideline2;
                    paramsNext.startToEnd = -1;
                } else {
                    btnSrsWrong.setVisibility(View.GONE);
                    btnSrsNext.setText("下一词");
                    btnSrsNext.setVisibility(View.VISIBLE);
                    paramsNext.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    paramsNext.startToEnd = -1;
                }
                btnSrsNext.setLayoutParams(paramsNext);
            }
        });

        viewModel.getReviewSessionComplete().observe(getViewLifecycleOwner(), complete -> {
            if (complete != null && complete && viewModel.isReviewMode()) {
                preButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                btnSrsWrong.setVisibility(View.GONE);
                btnSrsNext.setVisibility(View.GONE);
                reviewButtonBar.setVisibility(View.GONE);
            }
        });

        viewModel.getSrsButtonMode().observe(getViewLifecycleOwner(), mode -> {
            if (mode == null) mode = LearnPageViewModel.SrsButtonMode.HIDDEN;
            ConstraintLayout.LayoutParams paramsNext = (ConstraintLayout.LayoutParams) btnSrsNext.getLayoutParams();
            switch (mode) {
                case HIDDEN:
                    btnSrsWrong.setVisibility(View.GONE);
                    btnSrsNext.setVisibility(View.GONE);
                    break;
                case CHOICE:
                    btnSrsWrong.setText("不认识");
                    btnSrsNext.setText("认识");
                    btnSrsWrong.setVisibility(View.VISIBLE);
                    btnSrsNext.setVisibility(View.VISIBLE);
                    paramsNext.startToStart = R.id.guideline2;
                    paramsNext.startToEnd = -1;
                    btnSrsNext.setLayoutParams(paramsNext);
                    break;
                case SUBMIT:
                    btnSrsWrong.setText("记错了");
                    btnSrsNext.setText("下一词");
                    btnSrsWrong.setVisibility(View.VISIBLE);
                    btnSrsNext.setVisibility(View.VISIBLE);
                    paramsNext.startToStart = R.id.guideline2;
                    paramsNext.startToEnd = -1;
                    btnSrsNext.setLayoutParams(paramsNext);
                    break;
                case SUBMIT_PASS_ONLY:
                case SUBMIT_FAIL_ONLY:
                    btnSrsNext.setText("下一词");
                    btnSrsWrong.setVisibility(View.GONE);
                    btnSrsNext.setVisibility(View.VISIBLE);
                    paramsNext.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    paramsNext.startToEnd = -1;
                    btnSrsNext.setLayoutParams(paramsNext);
                    break;
            }
        });

        btnSrsWrong.setOnClickListener(v -> {
            if (viewModel.isReviewMode() && !viewModel.isRelearningCurrentWord()) {
                viewModel.reviewMarkWrong();
                return;
            }
            LearnPageViewModel.SrsButtonMode mode = viewModel.getSrsButtonMode().getValue();
            if (mode == LearnPageViewModel.SrsButtonMode.CHOICE) {
                viewModel.previewFail();
                viewModel.requestReveal(LearnPageViewModel.SrsButtonMode.SUBMIT_PASS_ONLY);
            } else {
                viewModel.submitFail();
            }
        });
        btnSrsNext.setOnClickListener(v -> {
            if (viewModel.isReviewMode() && !viewModel.isRelearningCurrentWord()) {
                viewModel.reviewAdvance();
                return;
            }
            LearnPageViewModel.SrsButtonMode mode = viewModel.getSrsButtonMode().getValue();
            if (mode == LearnPageViewModel.SrsButtonMode.CHOICE) {
                viewModel.previewPass();
                viewModel.requestReveal(LearnPageViewModel.SrsButtonMode.SUBMIT);
            } else if (mode == LearnPageViewModel.SrsButtonMode.SUBMIT) {
                viewModel.advanceToNext();
            } else if (mode == LearnPageViewModel.SrsButtonMode.SUBMIT_FAIL_ONLY) {
                viewModel.submitFail();
            } else if (mode == LearnPageViewModel.SrsButtonMode.SUBMIT_PASS_ONLY) {
                viewModel.advanceToNext();
            } else {
                viewModel.submitPass();
            }
        });

        btnReviewForget.setOnClickListener(v -> viewModel.reviewForget());
        btnReviewFuzzy.setOnClickListener(v -> viewModel.reviewFuzzy());
        btnReviewRecognize.setOnClickListener(v -> viewModel.reviewRecognize());
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
