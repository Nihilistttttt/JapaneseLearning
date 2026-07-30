package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.ToolBar.LearnPageToolBar;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.BasicWordView;
import com.Nihilisttt.LearnWord.WordView.IntegratedPartView;
import com.Nihilisttt.LearnWord.WordView.MeaningView;
import com.Nihilisttt.LearnWord.WordView.SentenceView;

import java.util.List;

public class MainLearnPageFragment extends Fragment {
    private static final String TAG = "MainLearnPageFragment";
    private LearnPageToolBar toolBar;
    private LinearLayout wordContainer;
    private LinearLayout infoRow;
    private LinearLayout meaningContainer;
    private CardView integratedPartContainer;
    private CardView sentenceContainer;
    private LinearLayout blankPart;
    private TextView blankText;

    private BasicWordView basicWordView;
    private SentenceView sentenceView;
    private MeaningView meaningView;
    private IntegratedPartView integratedPartView;

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
        infoRow = view.findViewById(R.id.info_row);
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
        toolBar.observeFontSize(getViewLifecycleOwner());
        setupObservers();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupObservers() {
        viewModel.getCombinedWordInfo().observe(getViewLifecycleOwner(), combinedWordInfo -> {
            renderWord(combinedWordInfo);
        });

        stateViewModel.getWordFontLevel().observe(getViewLifecycleOwner(), level -> {
            LearnPageViewModel.CombinedWordInfo info = viewModel.getCombinedWordInfo().getValue();
            if (info != null) renderWord(info);
        });

        stateViewModel.getSubFontLevel().observe(getViewLifecycleOwner(), level -> {
            LearnPageViewModel.CombinedWordInfo info = viewModel.getCombinedWordInfo().getValue();
            if (info != null) renderWord(info);
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderWord(LearnPageViewModel.CombinedWordInfo combinedWordInfo) {
        stateViewModel.setViewPagerScrollEnabled(false);
        wordContainer.setVisibility(View.INVISIBLE);
        infoRow.setVisibility(View.GONE);
        meaningContainer.setVisibility(View.INVISIBLE);
        sentenceContainer.setVisibility(View.INVISIBLE);
        integratedPartContainer.setVisibility(View.GONE);
        blankPart.setVisibility(View.VISIBLE);
        blankText.setVisibility(View.VISIBLE);

        Integer wordLevel = stateViewModel.getWordFontLevel().getValue();
        if (wordLevel == null) wordLevel = Constants.FONT_SIZE_NORMAL;
        Integer subLevel = stateViewModel.getSubFontLevel().getValue();
        if (subLevel == null) subLevel = Constants.FONT_SIZE_NORMAL;

        if (basicWordView == null) {
            basicWordView = new BasicWordView(requireContext(), requireActivity(), wordLevel, combinedWordInfo.getBasicWord());
            wordContainer.addView(basicWordView);
        } else {
            basicWordView.update(combinedWordInfo.getBasicWord(), wordLevel);
        }

        List<WordSentence> allSentences = combinedWordInfo.getWordSentenceList();
        int maxSentences = Constants.getSentenceCardMinSentences(subLevel);
        List<WordSentence> limitedSentences = allSentences.subList(0, Math.min(maxSentences, allSentences.size()));
        if (sentenceView == null) {
            sentenceView = new SentenceView(requireContext(), requireActivity(), subLevel, limitedSentences);
            sentenceContainer.addView(sentenceView);
        } else {
            sentenceView.update(limitedSentences, subLevel);
        }

        BasicWord basicWord = combinedWordInfo.getBasicWord();
        int jlptLevel = basicWord.getJlptLevel();
        int wordFrequency = basicWord.getWordFrequency();
        infoRow.removeAllViews();
        if (jlptLevel > 0 || wordFrequency > 0) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            int primaryColor = getResources().getColor(R.color.md_primary);
            int variantColor = getResources().getColor(R.color.md_on_surface_variant);
            if (jlptLevel > 0) {
                String jlptText = "N" + jlptLevel;
                int start = ssb.length();
                ssb.append(jlptText);
                ssb.setSpan(new ForegroundColorSpan(primaryColor), start, ssb.length(), 0);
            }
            if (jlptLevel > 0 && wordFrequency > 0) {
                int start = ssb.length();
                ssb.append(" · ");
                ssb.setSpan(new ForegroundColorSpan(variantColor), start, ssb.length(), 0);
            }
            if (wordFrequency > 0) {
                int start = ssb.length();
                ssb.append(String.valueOf(wordFrequency));
                ssb.setSpan(new ForegroundColorSpan(variantColor), start, ssb.length(), 0);
            }
            TextView infoText = new TextView(requireContext());
            infoText.setText(ssb);
            infoText.setTextSize(13);
            infoRow.addView(infoText);
            infoRow.setVisibility(View.VISIBLE);
        }

        wordContainer.setVisibility(View.VISIBLE);
        sentenceContainer.setVisibility(View.VISIBLE);

        if (meaningView == null) {
            meaningView = new MeaningView(requireContext(), requireActivity(), subLevel, combinedWordInfo.getWordMeaningList(), Constants.TURN_TO_DETAIL_PAGE);
            meaningContainer.addView(meaningView);
        } else {
            meaningView.update(combinedWordInfo.getWordMeaningList(), subLevel);
        }

        if (integratedPartView == null) {
            integratedPartView = new IntegratedPartView(requireContext(),
                    combinedWordInfo.getWordCollocationList(),
                    combinedWordInfo.getAntonymWordList(),
                    combinedWordInfo.getSynonymWordList(),
                    combinedWordInfo.getConjugationFormList(),
                    combinedWordInfo.getEtymologyList(),
                    combinedWordInfo.getKanjiInfoList(),
                    combinedWordInfo.getUsageDistinctionList(),
                    combinedWordInfo.getGrammarPointList(),
                    combinedWordInfo.getIdiomList());
            integratedPartContainer.addView(integratedPartView);
        } else {
            integratedPartView.update(combinedWordInfo.getWordCollocationList(),
                    combinedWordInfo.getAntonymWordList(),
                    combinedWordInfo.getSynonymWordList(),
                    combinedWordInfo.getConjugationFormList(),
                    combinedWordInfo.getEtymologyList(),
                    combinedWordInfo.getKanjiInfoList(),
                    combinedWordInfo.getUsageDistinctionList(),
                    combinedWordInfo.getGrammarPointList(),
                    combinedWordInfo.getIdiomList());
        }

        final int touchSlop = ViewConfiguration.get(requireContext()).getScaledTouchSlop();
        blankPart.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;
            private boolean isDragging = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (stateViewModel.getWhichFragmentInLearnPage().getValue() !=
                        LearnPageStateViewModel.FragmentInLearnPage.LearnPageFragment) {
                    return false;
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
                            float dx = currentX - startX;
                            float dy = currentY - startY;
                            float absDx = Math.abs(dx);
                            float absDy = Math.abs(dy);

                            if (absDx > touchSlop && absDx > absDy) {
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
                            stateViewModel.setViewPagerScrollEnabled(true);
                            if (infoRow.getVisibility() != View.GONE) {
                                infoRow.setVisibility(View.VISIBLE);
                            }
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        basicWordView = null;
        sentenceView = null;
        meaningView = null;
        integratedPartView = null;
        viewModel.getBasicWord().removeObservers(getViewLifecycleOwner());
        viewModel.getWordMeaningListLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.getWordCollocationListLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.getToastMessage().removeObservers(getViewLifecycleOwner());
    }

}
