package com.Nihilisttt.LearnWord.Fragment.SearchPage;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.Page.ViewModel.SearchDetailViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.BasicWordView;
import com.Nihilisttt.LearnWord.WordView.IntegratedPartView;
import com.Nihilisttt.LearnWord.WordView.MeaningView;

import java.util.List;

public class SearchDetailFragment extends Fragment {
    private static final String ARG_WORD_ID = "wordId";
    private SearchDetailViewModel viewModel;
    private LearnPageStateViewModel stateViewModel;
    private LinearLayout detailContent;
    private BasicWordView basicWordView;
    private MeaningView meaningView;
    private IntegratedPartView integratedPartView;
    private LinearLayout infoRow;
    private ImageButton scrollToggle;

    public static SearchDetailFragment newInstance(String wordId) {
        SearchDetailFragment f = new SearchDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORD_ID, wordId);
        f.setArguments(args);
        return f;
    }

    public String getWordId() {
        return getArguments() != null ? getArguments().getString(ARG_WORD_ID) : null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(SearchDetailViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageStateViewModel.class);

        detailContent = view.findViewById(R.id.search_detail_content);

        String wordId = getWordId();
        if (wordId != null) {
            viewModel.setCurrentWordId(wordId);
        }

        setupObservers();
    }

    private void setupObservers() {
        stateViewModel.getIsScrollMode().observe(getViewLifecycleOwner(), isScroll -> {
            if (integratedPartView != null) {
                Integer subLevel = stateViewModel.getSubFontLevel().getValue();
                if (subLevel == null) subLevel = Constants.FONT_SIZE_NORMAL;
                integratedPartView.setScrollMode(isScroll, subLevel);
            }
        });

        viewModel.getCombinedWordInfo().observe(getViewLifecycleOwner(), combinedWordInfo -> {
            if (combinedWordInfo == null) return;

            BasicWord basicWord = combinedWordInfo.getBasicWord();
            Integer wordFontLevel = stateViewModel.getWordFontLevel().getValue();
            if (wordFontLevel == null) wordFontLevel = Constants.FONT_SIZE_NORMAL;
            Integer subFontLevel = stateViewModel.getSubFontLevel().getValue();
            if (subFontLevel == null) subFontLevel = Constants.FONT_SIZE_NORMAL;

            renderHeader(basicWord, wordFontLevel, subFontLevel, combinedWordInfo.getWordMeaningList());

            if (integratedPartView == null) {
                integratedPartView = new IntegratedPartView(requireContext(),
                        combinedWordInfo.getWordCollocationList(),
                        combinedWordInfo.getAntonymWordList(),
                        combinedWordInfo.getSynonymWordList(),
                        combinedWordInfo.getDerivedWordList(),
                        combinedWordInfo.getRelatedWordList(),
                        combinedWordInfo.getConjugationFormList(),
                        combinedWordInfo.getEtymologyList(),
                        combinedWordInfo.getKanjiInfoList(),
                        combinedWordInfo.getUsageDistinctionList(),
                        combinedWordInfo.getGrammarPointList(),
                        combinedWordInfo.getIdiomList());
                detailContent.addView(integratedPartView);
            } else {
                integratedPartView.updateData(
                        combinedWordInfo.getWordCollocationList(),
                        combinedWordInfo.getAntonymWordList(),
                        combinedWordInfo.getSynonymWordList(),
                        combinedWordInfo.getDerivedWordList(),
                        combinedWordInfo.getRelatedWordList(),
                        combinedWordInfo.getConjugationFormList(),
                        combinedWordInfo.getEtymologyList(),
                        combinedWordInfo.getKanjiInfoList(),
                        combinedWordInfo.getUsageDistinctionList(),
                        combinedWordInfo.getGrammarPointList(),
                        combinedWordInfo.getIdiomList());
            }
        });
    }

    private void renderHeader(BasicWord basicWord, int wordFontLevel, int subFontLevel,
                              List<WordMeaning> meanings) {
        if (basicWordView == null) {
            basicWordView = new BasicWordView(getContext(), this, wordFontLevel, basicWord);
            detailContent.addView(basicWordView, 0);
        } else {
            basicWordView.update(basicWord, wordFontLevel);
        }

        int insertIdx = 1;

        if (infoRow != null) {
            detailContent.removeView(infoRow);
            infoRow = null;
        }
        int jlptLevel = basicWord.getJlptLevel();
        int wordFrequency = basicWord.getWordFrequency();
        if (jlptLevel > 0 || wordFrequency > 0) {
            infoRow = new LinearLayout(requireContext());
            infoRow.setOrientation(LinearLayout.HORIZONTAL);
            infoRow.setGravity(Gravity.CENTER_HORIZONTAL);
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            int primaryColor = requireContext().getResources().getColor(R.color.md_primary);
            int variantColor = requireContext().getResources().getColor(R.color.md_on_surface_variant);
            if (jlptLevel > 0) {
                int start = ssb.length();
                ssb.append("N" + jlptLevel);
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
            detailContent.addView(infoRow, insertIdx);
            insertIdx++;
        }

        if (meaningView != null) {
            detailContent.removeView(meaningView);
            meaningView = null;
        }
        if (meanings != null && !meanings.isEmpty()) {
            meaningView = new MeaningView(requireContext(), this, subFontLevel, meanings, Constants.SHOW_SENTENCE_POPUP);
            detailContent.addView(meaningView, insertIdx);
            insertIdx++;
        }

        if (scrollToggle != null) {
            detailContent.removeView(scrollToggle);
            scrollToggle = null;
        }
        scrollToggle = new ImageButton(requireContext());
        scrollToggle.setImageResource(R.drawable.ic_scroll_mode);
        scrollToggle.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        int btnSize = (int) (32 * requireContext().getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams toggleLp = new LinearLayout.LayoutParams(btnSize, btnSize);
        toggleLp.gravity = Gravity.CENTER_HORIZONTAL;
        toggleLp.topMargin = (int) (4 * requireContext().getResources().getDisplayMetrics().density);
        scrollToggle.setLayoutParams(toggleLp);
        scrollToggle.setOnClickListener(v -> {
            Boolean current = stateViewModel.getIsScrollMode().getValue();
            stateViewModel.setScrollMode(current == null || !current);
        });
        detailContent.addView(scrollToggle, insertIdx);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        basicWordView = null;
        meaningView = null;
        integratedPartView = null;
        infoRow = null;
        scrollToggle = null;
    }
}
