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
        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));
        setOrientation(LinearLayout.VERTICAL);
        this.layoutType = layoutType;
        this.layoutParams = Select.selectLayout(this.layoutType);
        this.lifecycleOwner = lifecycleOwner;
        initViews(sentenceList);
    }

    public SentenceView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, WordSentence sentence) {
        super(context);
        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));
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
        processSentenceList(sentenceList);
    }

    private void processSentenceList(List<WordSentence> sentences) {

        LinearLayout sentencePart = new LinearLayout(getContext());
        sentencePart.setOrientation(LinearLayout.VERTICAL);

        for (WordSentence sentence : sentences) {
            processSentence(sentence, sentencePart);
        }
        addView(sentencePart);


    }

    private boolean isNonClickableWordId(String wordId) {
        return wordId.equals(String.valueOf(99)) || wordId.equals("0");
    }

    private void processSentence(WordSentence sentence, LinearLayout sentencePart) {
        Context context = getContext();
        List<Integer> lastKanjiLengthList = new ArrayList<>();
        List<Float> lastKanaLengthList = new ArrayList<>();
        List<Integer> firstKanjiLengthList = new ArrayList<>();
        List<Float> firstKanaLengthList = new ArrayList<>();
        String firstKanji;
        String lastKanji;
        String firstKana;
        String lastKana;
        int firstKanjiLength;
        int lastKanjiLength;
        float firstKanaLength;
        float lastKanaLength;
        List<String> kanjiList;
        List<String> kanaList;
        String wordId;

        List<List<String>> originalsKanji = sentence.getKanjiComponents();
        List<List<String>> originalsKana = sentence.getKanaComponents();

        View sentenceColumn = View.inflate(context, R.layout.view_sentence_column, null);
        sentencePart.addView(sentenceColumn);
        sentenceColumn.setBackgroundResource(R.drawable.word_layout_selector);
        sentenceColumn.setOnClickListener(v -> {
            AudioManager audioManager = AudioManager.getInstance(context);
            if (audioManager.isPlaying()) {
                audioManager.stopAudio();
            }
            audioManager.playAudio(sentence.getAudioUrl());
        });
        LinearLayout sentenceRow = sentenceColumn.findViewById(R.id.sentence_row);
        TextView translation = sentenceColumn.findViewById(R.id.sentence_translation);

        float tempMarginStart = (originalsKanji.get(0).get(0).length() * layoutParams.getKanjiSize() - Constants.getKanaLength(originalsKana.get(0).get(0)) * layoutParams.getKanaSize()) / 2f;
        ConstraintLayout.LayoutParams collocationRowParams = (ConstraintLayout.LayoutParams) sentenceRow.getLayoutParams();
        if (tempMarginStart < 0) {
            collocationRowParams.setMarginStart(Convert.dpToPx(context, Constants.SENTENCE_ROW_MARGIN_START + tempMarginStart));
        } else {
            collocationRowParams.setMarginStart(Convert.dpToPx(context, Constants.SENTENCE_ROW_MARGIN_START));
        }
        sentenceRow.setLayoutParams(collocationRowParams);

        // First word (i=0): add directly without margin calculation
        if (!originalsKanji.isEmpty()) {
            int i = 0;
            wordId = sentence.getWordIdList().get(i);
            kanjiList = originalsKanji.get(i);
            kanaList = originalsKana.get(i);
            firstKanji = kanjiList.get(0);
            lastKanji = kanjiList.get(kanjiList.size() - 1);
            firstKana = kanaList.get(0);
            lastKana = kanaList.get(kanaList.size() - 1);
            firstKanjiLengthList.add(firstKanji.length());
            lastKanjiLengthList.add(lastKanji.length());
            firstKanaLengthList.add(Constants.getKanaLength(firstKana));
            lastKanaLengthList.add(Constants.getKanaLength(lastKana));

            WordComponentView wordComponentLayout;
            if (isNonClickableWordId(wordId)) {
                wordComponentLayout = new WordComponentView(
                        context, lifecycleOwner, layoutParams,
                        kanjiList, kanaList
                );
            } else {
                wordComponentLayout = new WordComponentView(
                        context, lifecycleOwner, layoutParams,
                        kanjiList, kanaList, wordId
                );
            }
            sentenceRow.addView(wordComponentLayout);
        }

        // Remaining words (i>=1): with margin calculation
        for (int i = 1; i < originalsKanji.size(); i++) {
            wordId = sentence.getWordIdList().get(i);
            kanjiList = originalsKanji.get(i);
            kanaList = originalsKana.get(i);
            firstKanji = kanjiList.get(0);
            lastKanji = kanjiList.get(kanjiList.size() - 1);
            firstKana = kanaList.get(0);
            lastKana = kanaList.get(kanaList.size() - 1);
            firstKanjiLengthList.add(firstKanji.length());
            lastKanjiLengthList.add(lastKanji.length());
            firstKanaLengthList.add(Constants.getKanaLength(firstKana));
            lastKanaLengthList.add(Constants.getKanaLength(lastKana));

            WordComponentView wordComponentLayout;
            if (isNonClickableWordId(wordId)) {
                wordComponentLayout = new WordComponentView(
                        context, lifecycleOwner, layoutParams,
                        kanjiList, kanaList
                );
            } else {
                wordComponentLayout = new WordComponentView(
                        context, lifecycleOwner, layoutParams,
                        kanjiList, kanaList, wordId
                );
            }

            LinearLayout.LayoutParams innerLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            wordComponentLayout.setLayoutParams(innerLp);
            MarginLayoutParams params = (MarginLayoutParams) wordComponentLayout.getLayoutParams();

            float prev = lastKanaLengthList.get(i - 1) * layoutParams.getKanaSize() - lastKanjiLengthList.get(i - 1) * layoutParams.getKanjiSize();
            float curr = firstKanaLengthList.get(i) * layoutParams.getKanaSize() - firstKanjiLengthList.get(i) * layoutParams.getKanjiSize();
            if (prev * curr >= 0) {
                float marginStart;
                if (Judge.isSmallKana(firstKanji)) {
                    marginStart = layoutParams.getCurrentIsSmallKanaMarginStart();
                } else if (Judge.isSmallKana(lastKanji)) {
                    marginStart = layoutParams.getPreviousIsSmallKanaMarginStart();
                } else marginStart = layoutParams.getElseMarginStart();
                params.setMarginStart(Convert.dpToPx(context, marginStart));
                sentenceRow.addView(wordComponentLayout);
                continue;
            }

            float marginValue = -Math.min(Math.abs(prev), Math.abs(curr)) / 2f;
            params.setMarginStart(Convert.dpToPx(context, marginValue));
            sentenceRow.addView(wordComponentLayout);
        }
        translation.setText(sentence.getTranslation());
    }

}
