package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.AudioManager;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.Nihilisttt.LearnWord.UtilityClass.Judge;
import com.Nihilisttt.LearnWord.UtilityClass.Select;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class SentenceView extends LinearLayout {
    private final int layoutType;
    private final Select.layoutParams layoutParams;
    private final LifecycleOwner lifecycleOwner;

    public SentenceView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<WordSentence> sentenceList) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        this.layoutType = layoutType;
        this.layoutParams = Select.selectLayout(this.layoutType);
        this.lifecycleOwner = lifecycleOwner;
        initViews(sentenceList);
    }

    public SentenceView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, WordSentence sentence) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        this.layoutType = layoutType;
        this.layoutParams = Select.selectLayout(this.layoutType);
        this.lifecycleOwner = lifecycleOwner;
        initViews(sentence);
    }

    private void initViews(WordSentence sentence) {
        LinearLayout sentencePart = new LinearLayout(getContext());
        sentencePart.setOrientation(LinearLayout.VERTICAL);
        processSentence(sentence, sentencePart);
        addView(sentencePart);
    }

    private void initViews(List<WordSentence> sentenceList) {
        LinearLayout sentencePart = new LinearLayout(getContext());
        sentencePart.setOrientation(LinearLayout.VERTICAL);
        for (WordSentence sentence : sentenceList) {
            processSentence(sentence, sentencePart);
        }
        addView(sentencePart);
    }

    private boolean isNonClickableWordId(String wordId) {
        return wordId.equals(String.valueOf(99)) || wordId.equals("0");
    }

    private float estimateWordWidth(List<String> kanjiList, List<String> kanaList) {
        float kanjiSize = layoutParams.getKanjiSize();
        float kanaSize = layoutParams.getKanaSize();
        float maxWidth = 0;
        for (int i = 0; i < kanjiList.size(); i++) {
            float kanaW = Constants.getKanaLength(kanaList.get(i)) * kanaSize;
            float kanjiW = kanjiList.get(i).length() * kanjiSize;
            float charW = Math.max(kanaW, kanjiW);
            maxWidth += charW;
        }
        return Convert.dpToPx(getContext(), maxWidth);
    }

    private void processSentence(WordSentence sentence, LinearLayout sentencePart) {
        Context context = getContext();
        List<List<String>> originalsKanji = sentence.getKanjiComponents();
        List<List<String>> originalsKana = sentence.getKanaComponents();

        View sentenceColumn = View.inflate(context, R.layout.view_sentence_column, null);
        sentencePart.addView(sentenceColumn);
        sentenceColumn.setBackgroundResource(R.drawable.word_layout_selector);
        sentenceColumn.setOnClickListener(v -> {
            AudioManager audioManager = AudioManager.getInstance(context);
            if (audioManager.isPlaying()) audioManager.stopAudio();
            audioManager.playAudio(sentence.getAudioUrl());
        });

        LinearLayout sentenceRowContainer = sentenceColumn.findViewById(R.id.sentence_row);
        sentenceRowContainer.setOrientation(LinearLayout.VERTICAL);
        TextView translation = sentenceColumn.findViewById(R.id.sentence_translation);

        int screenWidthPx = context.getResources().getDisplayMetrics().widthPixels;
        int availableWidthPx = screenWidthPx - Convert.dpToPx(context, 40);

        List<Integer> lastKanjiLengthList = new ArrayList<>();
        List<Float> lastKanaLengthList = new ArrayList<>();
        List<Integer> firstKanjiLengthList = new ArrayList<>();
        List<Float> firstKanaLengthList = new ArrayList<>();

        LinearLayout currentRow = new LinearLayout(context);
        currentRow.setOrientation(LinearLayout.HORIZONTAL);
        currentRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        sentenceRowContainer.addView(currentRow);
        float currentRowWidth = 0;

        for (int i = 0; i < originalsKanji.size(); i++) {
            String wordId = sentence.getWordIdList().get(i);
            List<String> kanjiList = originalsKanji.get(i);
            List<String> kanaList = originalsKana.get(i);

            String firstKanji = kanjiList.get(0);
            String lastKanji = kanjiList.get(kanjiList.size() - 1);
            String firstKana = kanaList.get(0);
            String lastKana = kanaList.get(kanaList.size() - 1);
            firstKanjiLengthList.add(firstKanji.length());
            lastKanjiLengthList.add(lastKanji.length());
            firstKanaLengthList.add(Constants.getKanaLength(firstKana));
            lastKanaLengthList.add(Constants.getKanaLength(lastKana));

            WordComponentView wordComponentLayout;
            if (isNonClickableWordId(wordId)) {
                wordComponentLayout = new WordComponentView(context, lifecycleOwner, layoutParams, kanjiList, kanaList);
            } else {
                wordComponentLayout = new WordComponentView(context, lifecycleOwner, layoutParams, kanjiList, kanaList, wordId);
            }

            float wordWidthPx = estimateWordWidth(kanjiList, kanaList);

            if (i > 0) {
                float prev = lastKanaLengthList.get(i - 1) * layoutParams.getKanaSize() - lastKanjiLengthList.get(i - 1) * layoutParams.getKanjiSize();
                float curr = firstKanaLengthList.get(i) * layoutParams.getKanaSize() - firstKanjiLengthList.get(i) * layoutParams.getKanjiSize();

                float marginPx;
                if (prev * curr >= 0) {
                    float marginDp;
                    if (Judge.isSmallKana(firstKanji)) {
                        marginDp = layoutParams.getCurrentIsSmallKanaMarginStart();
                    } else if (Judge.isSmallKana(lastKanji)) {
                        marginDp = layoutParams.getPreviousIsSmallKanaMarginStart();
                    } else {
                        marginDp = layoutParams.getElseMarginStart();
                    }
                    marginPx = Convert.dpToPx(context, marginDp);
                } else {
                    marginPx = Convert.dpToPx(context, -Math.min(Math.abs(prev), Math.abs(curr)) / 2f);
                }

                if (currentRowWidth + marginPx + wordWidthPx > availableWidthPx && currentRowWidth > 0) {
                    currentRow = new LinearLayout(context);
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    currentRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    sentenceRowContainer.addView(currentRow);
                    currentRowWidth = 0;
                } else {
                    LinearLayout.LayoutParams innerLp = new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    wordComponentLayout.setLayoutParams(innerLp);
                    MarginLayoutParams params = (MarginLayoutParams) wordComponentLayout.getLayoutParams();
                    params.setMarginStart((int) marginPx);
                    currentRowWidth += marginPx;
                }
            }

            currentRow.addView(wordComponentLayout);
            currentRowWidth += wordWidthPx;
        }

        translation.setText(sentence.getTranslation());
    }
}
