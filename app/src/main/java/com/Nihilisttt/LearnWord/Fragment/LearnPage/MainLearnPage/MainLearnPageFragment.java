package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.ToolBar.LearnPageToolBar;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.BasicWordView;
import com.Nihilisttt.LearnWord.WordView.IntegratedPartView;
import com.Nihilisttt.LearnWord.WordView.MeaningView;
import com.Nihilisttt.LearnWord.WordView.SentenceView;

public class MainLearnPageFragment extends Fragment {
    private static final String TAG = "MainLearnPageFragment";
    // 界面组件
    private LearnPageToolBar toolBar;
    private LinearLayout wordContainer;
    private LinearLayout meaningContainer;
    private CardView integratedPartContainer;
    private CardView sentenceContainer;
    private LinearLayout blankPart;
    private TextView blankText;

    // ViewModel
    private LearnPageViewModel viewModel;
    private LearnPageStateViewModel stateViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_learn_page, container, false);
    }


    private void initViews(View view) {
        toolBar = view.findViewById(R.id.learn_page_tool_bar);
        wordContainer = view.findViewById(R.id.word_container);
        meaningContainer = view.findViewById(R.id.meaning_container);
        sentenceContainer = view.findViewById(R.id.sentence_container);
        integratedPartContainer = view.findViewById(R.id.integrated_part_container);
        blankPart = view.findViewById(R.id.blank_part);
        blankText = view.findViewById(R.id.blank_text);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageStateViewModel.class);
        initViews(view);
        toolBar.setStateViewModel(stateViewModel);
        setupObservers();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupObservers() {
        viewModel.getCombinedWordInfo().observe(getViewLifecycleOwner(), combinedWordInfo -> {
            stateViewModel.setViewPagerScrollEnabled(false);
            meaningContainer.setVisibility(View.INVISIBLE);
            integratedPartContainer.setVisibility(View.GONE);
            blankPart.setVisibility(View.VISIBLE);
            blankText.setVisibility(View.VISIBLE);

            BasicWordView basicWordView = new BasicWordView(requireContext(), requireActivity(), Constants.LARGE, combinedWordInfo.getBasicWord());

            SentenceView sentenceView = new SentenceView(requireContext(), requireActivity(), Constants.NORMAL, combinedWordInfo.getWordSentenceList());
            updateView(basicWordView, wordContainer);
            updateView(sentenceView, sentenceContainer);
            sentenceContainer.setVisibility(View.VISIBLE);

            MeaningView meaningView = new MeaningView(requireContext(), requireActivity(), Constants.NORMAL, combinedWordInfo.getWordMeaningList(), Constants.TURN_TO_DETAIL_PAGE);
            updateView(meaningView, meaningContainer);

            IntegratedPartView integratedPartView = new IntegratedPartView(requireContext(), combinedWordInfo.getWordCollocationList(),
                    combinedWordInfo.getAntonymWordList(), combinedWordInfo.getSynonymWordList());
            updateView(integratedPartView, integratedPartContainer);

            // 修改空白区域的触摸事件处理
            final int touchSlop = ViewConfiguration.get(requireContext()).getScaledTouchSlop();
            blankPart.setOnTouchListener(new View.OnTouchListener() {
                private float startX, startY;
                private boolean isDragging = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 检查当前是否在LearnPageFragment中
                    if (stateViewModel.getWhichFragmentInLearnPage().getValue() !=
                            LearnPageStateViewModel.FragmentInLearnPage.LearnPageFragment) {
                        return false; // 不在LearnPageFragment中，不处理事件
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getX();
                            startY = event.getY();
                            isDragging = false;
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            if (!isDragging) {
                                float currentX = event.getX();
                                float currentY = event.getY();
                                float dx = currentX - startX;  // 水平滑动距离（负值表示向左）
                                float dy = currentY - startY;  // 垂直滑动距离
                                float absDx = Math.abs(dx);
                                float absDy = Math.abs(dy);

                                // 判断是否达到滑动阈值且主要是水平滑动
                                if (absDx > touchSlop && absDx > absDy) {
                                    // 从右向左滑动（dx为负值）
                                    isDragging = true;
                                    if (dx < 0) {
                                        Toast.makeText(requireContext(),
                                                "完成测试后可查看例句",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            return true;

                        case MotionEvent.ACTION_UP:
                            if (!isDragging) {
                                // 启用 ViewPager 滚动
                                stateViewModel.setViewPagerScrollEnabled(true);

                                meaningContainer.setVisibility(View.VISIBLE);
                                integratedPartContainer.setVisibility(View.VISIBLE);
                                blankPart.setVisibility(View.GONE);
                                blankText.setVisibility(View.GONE);
                            }
                            return true;
                    }
                    return false;
                }
            });
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 修改后的updateView方法（统一ViewGroup参数）
    private void updateView(View view, ViewGroup container) {
        container.removeAllViews();
        container.addView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清除所有观察者
        viewModel.getBasicWord().removeObservers(getViewLifecycleOwner());
        viewModel.getWordMeaningListLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.getWordCollocationListLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.getToastMessage().removeObservers(getViewLifecycleOwner());
    }
}
