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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.Nihilisttt.LearnWord.Algorithm.StudyStage;
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

    private LinearLayout indicatorBar;
    private View indicator1;
    private View indicator2;
    private View indicator3;
    private TextView stageHintText;
    private LinearLayout multipleChoiceContainer;
    private TextView[] choiceOptions = new TextView[4];

    private LinearLayout newModeButtonBar;
    private Button btnSeeAnswer;

    private BasicWordView basicWordView;
    private SentenceView sentenceView;
    private MeaningView meaningView;
    private IntegratedPartView integratedPartView;

    private LearnPageViewModel viewModel;
    private LearnPageStateViewModel stateViewModel;

    private boolean answerRevealed = false;
    private boolean wasCorrect = false;
    private boolean isChoiceAnswered = false;
    private boolean hasWrongChoice = false;
    private boolean sessionComplete = false;
    private String currentCorrectWordText = "";
    private List<LearnPageViewModel.ChoiceOption> currentChoiceOptions = null;
    private View rootView;

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

        indicatorBar = view.findViewById(R.id.indicator_bar);
        indicator1 = view.findViewById(R.id.indicator_1);
        indicator2 = view.findViewById(R.id.indicator_2);
        indicator3 = view.findViewById(R.id.indicator_3);
        stageHintText = view.findViewById(R.id.stage_hint_text);
        multipleChoiceContainer = view.findViewById(R.id.multiple_choice_container);
        choiceOptions[0] = view.findViewById(R.id.choice_option_0);
        choiceOptions[1] = view.findViewById(R.id.choice_option_1);
        choiceOptions[2] = view.findViewById(R.id.choice_option_2);
        choiceOptions[3] = view.findViewById(R.id.choice_option_3);

        newModeButtonBar = view.findViewById(R.id.new_mode_button_bar);
        btnSeeAnswer = view.findViewById(R.id.btn_see_answer);
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
        rootView = view;
        toolBar.setStateViewModel(stateViewModel);
        toolBar.observeFontSize(getViewLifecycleOwner());
        toolBar.setDebugSkipButtonListener(v -> viewModel.debugForcePass());
        toolBar.setDebugSkipButtonVisible(viewModel.isSRSMode());
        setupObservers();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupObservers() {
        viewModel.getCombinedWordInfo().observe(getViewLifecycleOwner(), combinedWordInfo -> {
            if (combinedWordInfo != null) renderWord(combinedWordInfo);
        });

        stateViewModel.getWordFontLevel().observe(getViewLifecycleOwner(), level -> {
            LearnPageViewModel.CombinedWordInfo info = viewModel.getCombinedWordInfo().getValue();
            if (info != null) renderWord(info);
        });

        stateViewModel.getSubFontLevel().observe(getViewLifecycleOwner(), level -> {
            LearnPageViewModel.CombinedWordInfo info = viewModel.getCombinedWordInfo().getValue();
            if (info != null) renderWord(info);
        });

        stateViewModel.getIsScrollMode().observe(getViewLifecycleOwner(), isScroll -> {
            if (integratedPartView != null) {
                Integer subLevel = stateViewModel.getSubFontLevel().getValue();
                if (subLevel == null) subLevel = Constants.FONT_SIZE_NORMAL;
                integratedPartView.setScrollMode(isScroll, subLevel);
            }
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSessionComplete().observe(getViewLifecycleOwner(), complete -> {
            if (complete != null && complete) {
                showSessionComplete();
            } else {
                sessionComplete = false;
            }
        });

        viewModel.getSessionProgress().observe(getViewLifecycleOwner(), progress -> {
            if (!sessionComplete) updateProgressText();
        });

        viewModel.getSessionTotal().observe(getViewLifecycleOwner(), total -> {
            if (!sessionComplete) updateProgressText();
        });

        viewModel.getSrsButtonMode().observe(getViewLifecycleOwner(), mode -> {
            if (mode == LearnPageViewModel.SrsButtonMode.HIDDEN && viewModel.isSRSMode()) {
                hideContentForLoading();
            }
        });

        viewModel.getEntryLoaded().observe(getViewLifecycleOwner(), version -> {
            if (version == null) return;
            if (viewModel.isReviewMode() && !viewModel.isRelearningCurrentWord()) {
                answerRevealed = false;
                isChoiceAnswered = false;
                hasWrongChoice = false;
                setupReviewUI();
                return;
            }
            if (viewModel.isSRSMode()) {
                StudyStage stage = viewModel.getCurrentStage().getValue();
                if (stage != null) {
                    answerRevealed = false;
                    isChoiceAnswered = false;
                    hasWrongChoice = false;
                    btnSeeAnswer.setText("看答案");
                    setupStageUI(stage);
                    LearnPageViewModel.CombinedWordInfo info = viewModel.getCombinedWordInfo().getValue();
                    if (info != null) renderWord(info);
                }
            }
        });

        viewModel.getCurrentCorrectCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null && viewModel.isSRSMode()) {
                updateIndicatorBar(count);
            }
        });

        viewModel.getMultipleChoiceOptions().observe(getViewLifecycleOwner(), options -> {
            if (options != null && viewModel.isSRSMode()) {
                setupMultipleChoice(options);
            } else {
                currentChoiceOptions = null;
                if (multipleChoiceContainer != null) {
                    multipleChoiceContainer.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getCorrectWordText().observe(getViewLifecycleOwner(), text -> {
            currentCorrectWordText = text != null ? text : "";
        });


        viewModel.getRevealRequested().observe(getViewLifecycleOwner(), requested -> {
            if (requested != null && requested) {
                revealAnswer(viewModel.getPendingRevealMode());
                viewModel.consumeRevealRequest();
            }
        });

        viewModel.getReviewStage().observe(getViewLifecycleOwner(), stage -> {
            if (stage == null || !viewModel.isReviewMode()) return;
            boolean revealed = stage != LearnPageViewModel.ReviewStage.SHOW_WORD;
            if (revealed) {
                meaningContainer.setVisibility(View.VISIBLE);
                sentenceContainer.setVisibility(View.VISIBLE);
                integratedPartContainer.setVisibility(View.VISIBLE);
                stageHintText.setVisibility(View.GONE);
            } else {
                meaningContainer.setVisibility(View.GONE);
                sentenceContainer.setVisibility(View.GONE);
                integratedPartContainer.setVisibility(View.GONE);
                stageHintText.setText("瞬间想起词义，选认识，思考后想起词义，选模糊");
                stageHintText.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getReviewSessionComplete().observe(getViewLifecycleOwner(), complete -> {
            if (complete != null && complete && viewModel.isReviewMode()) {
                showReviewSessionComplete();
            } else {
                sessionComplete = false;
            }
        });

        viewModel.getReviewSessionProgress().observe(getViewLifecycleOwner(), progress -> {
            if (viewModel.isReviewMode() && !sessionComplete) updateReviewProgressText();
        });

        viewModel.getReviewSessionTotal().observe(getViewLifecycleOwner(), total -> {
            if (viewModel.isReviewMode() && !sessionComplete) updateReviewProgressText();
        });

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        btnSeeAnswer.setOnClickListener(v -> {
            if (sessionComplete) {
                sessionComplete = false;
                if (viewModel.isReviewMode()) {
                    viewModel.startNewReviewSession();
                } else {
                    viewModel.startNewSession();
                }
            } else {
                onSeeAnswerClicked();
            }
        });

        for (int i = 0; i < 4; i++) {
            final int index = i;
            choiceOptions[i].setOnClickListener(v -> onChoiceClicked(index));
        }
    }

    private void hideContentForLoading() {
        wordContainer.setVisibility(View.GONE);
        infoRow.setVisibility(View.GONE);
        indicatorBar.setVisibility(View.GONE);
        meaningContainer.setVisibility(View.GONE);
        sentenceContainer.setVisibility(View.GONE);
        integratedPartContainer.setVisibility(View.GONE);
        stageHintText.setVisibility(View.GONE);
        multipleChoiceContainer.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);
        blankPart.setVisibility(View.GONE);

        for (int i = 0; i < 4; i++) {
            choiceOptions[i].setText("");
            choiceOptions[i].setBackgroundResource(R.drawable.card_choice_option);
            choiceOptions[i].setClickable(true);
        }
    }

    private void setupStageUI(StudyStage stage) {
        hideContentForLoading();
        hideAllButtonBars();

        switch (stage) {
            case NEW:
                newModeButtonBar.setVisibility(View.VISIBLE);
                stageHintText.setText("初学：先回想词义再选择");
                viewModel.setSrsButtonMode(LearnPageViewModel.SrsButtonMode.HIDDEN);
                break;
            case REVIEW:
                stageHintText.setText("复习：请回想词义");
                viewModel.setSrsButtonMode(LearnPageViewModel.SrsButtonMode.CHOICE);
                break;
            case FINAL:
                stageHintText.setText("最后一关：请在无提示的情况下判断");
                viewModel.setSrsButtonMode(LearnPageViewModel.SrsButtonMode.CHOICE);
                break;
        }
    }

    private void setupMultipleChoice(List<LearnPageViewModel.ChoiceOption> options) {
        if (options == null || options.isEmpty()) {
            multipleChoiceContainer.setVisibility(View.GONE);
            newModeButtonBar.setVisibility(View.GONE);
            viewModel.setSrsButtonMode(LearnPageViewModel.SrsButtonMode.CHOICE);
            return;
        }

        currentChoiceOptions = options;
        multipleChoiceContainer.setVisibility(View.VISIBLE);
        int count = Math.min(options.size(), 4);
        for (int i = 0; i < 4; i++) {
            if (i < count) {
                choiceOptions[i].setText(options.get(i).meaningText);
                choiceOptions[i].setVisibility(View.VISIBLE);
                choiceOptions[i].setBackgroundResource(R.drawable.card_choice_option);
                choiceOptions[i].setClickable(true);
            } else {
                choiceOptions[i].setVisibility(View.GONE);
            }
        }
    }

    private void updateIndicatorBar(int correctCount) {

        int filledColor = ContextCompat.getColor(requireContext(), R.color.md_success);
        int emptyColor = ContextCompat.getColor(requireContext(), R.color.md_surface_variant);

        indicator1.setBackgroundColor(correctCount >= 1 ? filledColor : emptyColor);
        indicator2.setBackgroundColor(correctCount >= 2 ? filledColor : emptyColor);
        indicator3.setBackgroundColor(correctCount >= 3 ? filledColor : emptyColor);
    }

    private void onChoiceClicked(int index) {
        if (isChoiceAnswered) return;
        if (!choiceOptions[index].isClickable()) return;

        Integer correctIdx = viewModel.getCorrectOptionIndex().getValue();
        if (correctIdx == null) return;

        boolean correct = (index == correctIdx);

        if (correct) {
            isChoiceAnswered = true;
            wasCorrect = true;
            for (int i = 0; i < 4; i++) {
                choiceOptions[i].setClickable(false);
            }
            choiceOptions[index].setBackgroundResource(R.drawable.card_choice_correct);
            for (int i = 0; i < 4; i++) {
                if (i != index) {
                    choiceOptions[i].setBackgroundResource(R.drawable.card_choice_wrong);
                    if (currentChoiceOptions != null && i < currentChoiceOptions.size()) {
                        choiceOptions[i].setText(currentChoiceOptions.get(i).wordText);
                    }
                }
            }
            if (!hasWrongChoice) {
                viewModel.previewPass();
            }
            rootView.postDelayed(() -> revealAnswer(LearnPageViewModel.SrsButtonMode.SUBMIT_PASS_ONLY), 1200);
        } else {
            hasWrongChoice = true;
            choiceOptions[index].setBackgroundResource(R.drawable.card_choice_wrong);
            if (currentChoiceOptions != null && index < currentChoiceOptions.size()) {
                choiceOptions[index].setText(currentChoiceOptions.get(index).wordText);
            }
            choiceOptions[index].setClickable(false);
        }
    }

    private void onSeeAnswerClicked() {
        if (isChoiceAnswered) return;
        isChoiceAnswered = true;

        Integer correctIdx = viewModel.getCorrectOptionIndex().getValue();
        int correct = correctIdx != null ? correctIdx : -1;
        if (correctIdx != null && correctIdx < 4) {
            choiceOptions[correctIdx].setBackgroundResource(R.drawable.card_choice_correct);
        }
        for (int i = 0; i < 4; i++) {
            choiceOptions[i].setClickable(false);
            if (i != correct && currentChoiceOptions != null && i < currentChoiceOptions.size()) {
                choiceOptions[i].setText(currentChoiceOptions.get(i).wordText);
            }
        }

        wasCorrect = false;
        rootView.postDelayed(() -> revealAnswer(LearnPageViewModel.SrsButtonMode.SUBMIT_FAIL_ONLY), 1000);
    }

    private void revealAnswer(LearnPageViewModel.SrsButtonMode mode) {
        answerRevealed = true;

        hideAllButtonBars();

        meaningContainer.setVisibility(View.VISIBLE);
        integratedPartContainer.setVisibility(View.VISIBLE);
        sentenceContainer.setVisibility(View.VISIBLE);
        stageHintText.setVisibility(View.GONE);
        multipleChoiceContainer.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);
        blankPart.setVisibility(View.GONE);

        viewModel.setSrsButtonMode(mode);
    }

    private void hideAllButtonBars() {
        newModeButtonBar.setVisibility(View.GONE);
    }

    private void updateProgressText() {
        Integer progress = viewModel.getSessionProgress().getValue();
        Integer total = viewModel.getSessionTotal().getValue();
        if (progress != null && total != null && total > 0) {
            toolBar.setCountText(progress + "/" + total);
        }
    }

    private void showSessionComplete() {
        sessionComplete = true;
        hideContentForLoading();

        Integer total = viewModel.getSessionTotal().getValue();
        if (total != null && total > 0) {
            int newCount = viewModel.getSessionNewCount();
            int reviewCount = viewModel.getSessionReviewCount();
            int mastered = viewModel.getSessionMasteredCount();
            stageHintText.setText("本组学习完成！\n新学 " + newCount + "  复习 " + reviewCount + "  掌握 " + mastered + "/" + total);
            stageHintText.setVisibility(View.VISIBLE);
            btnSeeAnswer.setText("继续学习下一组");
            newModeButtonBar.setVisibility(View.VISIBLE);
        } else {
            stageHintText.setText("今日学习已全部完成！");
            stageHintText.setVisibility(View.VISIBLE);
        }
        toolBar.setCountText("");
    }

    private void setupReviewUI() {
        hideContentForLoading();
        wordContainer.setVisibility(View.VISIBLE);
        stageHintText.setText("瞬间想起词义，选认识，思考后想起词义，选模糊");
        stageHintText.setVisibility(View.VISIBLE);
        indicatorBar.setVisibility(View.GONE);
        multipleChoiceContainer.setVisibility(View.GONE);
        meaningContainer.setVisibility(View.GONE);
        sentenceContainer.setVisibility(View.GONE);
        integratedPartContainer.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);
        blankPart.setVisibility(View.GONE);
        hideAllButtonBars();
    }

    private void showReviewSessionComplete() {
        sessionComplete = true;
        hideContentForLoading();

        Integer total = viewModel.getReviewSessionTotal().getValue();
        Integer completed = viewModel.getReviewSessionProgress().getValue();
        if (total != null && total > 0) {
            int done = completed != null ? completed : 0;
            stageHintText.setText("本组复习完成！\n已复习 " + done + "/" + total + " 个单词");
            stageHintText.setVisibility(View.VISIBLE);
            btnSeeAnswer.setText("继续复习下一组");
            newModeButtonBar.setVisibility(View.VISIBLE);
        } else {
            stageHintText.setText("暂无到期复习单词");
            stageHintText.setVisibility(View.VISIBLE);
        }
        toolBar.setCountText("");
    }

    private void updateReviewProgressText() {
        Integer progress = viewModel.getReviewSessionProgress().getValue();
        Integer total = viewModel.getReviewSessionTotal().getValue();
        if (progress != null && total != null && total > 0) {
            toolBar.setCountText(progress + "/" + total);
        }
    }

    private void renderWord(LearnPageViewModel.CombinedWordInfo combinedWordInfo) {

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

        BasicWord basicWord = combinedWordInfo.getBasicWord();
        int jlptLevel = basicWord.getJlptLevel();
        int wordFrequency = basicWord.getWordFrequency();
        infoRow.removeAllViews();
        if (jlptLevel > 0 || wordFrequency > 0) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            int primaryColor = ContextCompat.getColor(requireContext(), R.color.md_primary);
            int variantColor = ContextCompat.getColor(requireContext(), R.color.md_on_surface_variant);
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

        wordContainer.setVisibility(View.VISIBLE);
        infoRow.setVisibility(jlptLevel > 0 || wordFrequency > 0 ? View.VISIBLE : View.GONE);

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
                    combinedWordInfo.getDerivedWordList(),
                    combinedWordInfo.getRelatedWordList(),
                    combinedWordInfo.getConjugationFormList(),
                    combinedWordInfo.getEtymologyList(),
                    combinedWordInfo.getKanjiInfoList(),
                    combinedWordInfo.getUsageDistinctionList(),
                    combinedWordInfo.getGrammarPointList(),
                    combinedWordInfo.getIdiomList());
            integratedPartContainer.addView(integratedPartView);
            Boolean isScroll = stateViewModel.getIsScrollMode().getValue();
            if (isScroll != null && isScroll) {
                integratedPartView.setScrollMode(true, subLevel);
            }
        } else {
            integratedPartView.updateData(combinedWordInfo.getWordCollocationList(),
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

        if (viewModel.isSRSMode()) {
            StudyStage stage = viewModel.getCurrentStage().getValue();
            if (stage != null) {
                stageHintText.setVisibility(View.VISIBLE);
                sentenceContainer.setVisibility(stage == StudyStage.REVIEW ? View.VISIBLE : View.GONE);
                if (stage == StudyStage.NEW) {
                    multipleChoiceContainer.setVisibility(View.VISIBLE);
                }
                Integer count = viewModel.getCurrentCorrectCount().getValue();
                if (count != null) {
                    indicatorBar.setVisibility(View.VISIBLE);
                    updateIndicatorBar(count);
                }
            }
        }

        if (viewModel.isReviewMode() && !viewModel.isRelearningCurrentWord()) {
            LearnPageViewModel.ReviewStage stage = viewModel.getReviewStage().getValue();
            boolean revealed = stage != null && stage != LearnPageViewModel.ReviewStage.SHOW_WORD;
            indicatorBar.setVisibility(View.GONE);
            multipleChoiceContainer.setVisibility(View.GONE);
            blankText.setVisibility(View.GONE);
            blankPart.setVisibility(View.GONE);
            if (revealed) {
                meaningContainer.setVisibility(View.VISIBLE);
                sentenceContainer.setVisibility(View.VISIBLE);
                integratedPartContainer.setVisibility(View.VISIBLE);
                stageHintText.setVisibility(View.GONE);
            } else {
                meaningContainer.setVisibility(View.GONE);
                sentenceContainer.setVisibility(View.GONE);
                integratedPartContainer.setVisibility(View.GONE);
                stageHintText.setText("瞬间想起词义，选认识，思考后想起词义，选模糊");
                stageHintText.setVisibility(View.VISIBLE);
            }
        }

        if (!viewModel.isSRSMode() && !viewModel.isReviewMode()) {
            setupNonSRSMode();
        }
    }

    private void setupNonSRSMode() {
        stateViewModel.setViewPagerScrollEnabled(false);
        meaningContainer.setVisibility(View.INVISIBLE);
        integratedPartContainer.setVisibility(View.GONE);
        blankPart.setVisibility(View.VISIBLE);
        blankText.setVisibility(View.VISIBLE);
        sentenceContainer.setVisibility(View.VISIBLE);
        indicatorBar.setVisibility(View.GONE);
        hideAllButtonBars();

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
