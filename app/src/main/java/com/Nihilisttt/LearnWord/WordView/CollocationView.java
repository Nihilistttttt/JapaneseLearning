package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.AudioManager;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.Nihilisttt.LearnWord.UtilityClass.Judge;
import com.Nihilisttt.LearnWord.UtilityClass.Select;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("ViewConstructor")
public class CollocationView extends LinearLayout {

    private final int layoutType;
    private List<String> kanjiList;
    private List<String> kanaList;
    private String wordId;
    private final LifecycleOwner lifecycleOwner;
    private final Select.layoutParams layoutParams;

    public CollocationView(Context context, @Nullable AttributeSet attrs, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<WordCollocation> collocations) {
        super(context);
        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,  // 宽度设为match_parent
                LayoutParams.WRAP_CONTENT   // 高度保持wrap_content
        ));
        setOrientation(LinearLayout.VERTICAL);
        this.lifecycleOwner = lifecycleOwner;
        this.layoutType = layoutType;
        this.layoutParams = Select.selectLayout(this.layoutType);
        initViews(collocations);


    }
    public CollocationView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<WordCollocation> collocations) {
        super(context);
        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,  // 宽度设为match_parent
                LayoutParams.WRAP_CONTENT   // 高度保持wrap_content
        ));
        setOrientation(LinearLayout.VERTICAL);
        this.lifecycleOwner = lifecycleOwner;
        this.layoutType = layoutType;
        this.layoutParams = Select.selectLayout(this.layoutType);
        initViews(collocations);


    }


    private void initViews(List<WordCollocation> collocations) {
        Context context = getContext();

        for (int idx = 0; idx < collocations.size(); idx += 2) {
            LinearLayout gridRow = new LinearLayout(context);
            gridRow.setOrientation(HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            if (idx > 0) {
                rowParams.topMargin = (int) (Constants.COLLOCATION_ROW_MARGIN_DP * context.getResources().getDisplayMetrics().density);
            }
            gridRow.setLayoutParams(rowParams);
            addView(gridRow);

            for (int c = 0; c < 2 && idx + c < collocations.size(); c++) {
                WordCollocation collocation = collocations.get(idx + c);
                View collocationLayout = View.inflate(context, R.layout.view_collocation_column, null);
                LinearLayout.LayoutParams cellParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
                collocationLayout.setLayoutParams(cellParams);
                gridRow.addView(collocationLayout);
                collocationLayout.setBackgroundResource(R.drawable.word_layout_selector);
                collocationLayout.setOnClickListener(v -> {
                    AudioManager audioManager = AudioManager.getInstance(context);
                    if (audioManager.isPlaying()) {
                        audioManager.stopAudio();
                    }
                    audioManager.playAudio(collocation.getAudioUrl());
                });

                LinearLayout collocationRow = collocationLayout.findViewById(R.id.collocation_row);
                TextView translation = collocationLayout.findViewById(R.id.collocation_translation);
                translation.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.getSubDefinitionSize(layoutType));


                List<List<String>> originalsKanji = collocation.getKanjiComponents();
                List<List<String>> originalsKana = collocation.getKanaComponents();


                List<Integer> lastKanjiLengthList = new ArrayList<>();
                List<Float> lastKanaLengthList = new ArrayList<>();
                List<Integer> firstKanjiLengthList = new ArrayList<>();
                List<Float> firstKanaLengthList = new ArrayList<>();
                String firstKanji;
                String lastKanji;
                String firstKana;
                String lastKana;
                boolean isFirstInnerLayout = true;


                for (int i = 0; i < originalsKanji.size(); i++) {
                    wordId = collocation.getWordIdList().get(i);
                    kanjiList = originalsKanji.get(i);
                    kanaList = originalsKana.get(i);
                    List<String> cleanedKana = cleanKanaAnnotations(kanjiList, kanaList);
                    firstKanji = kanjiList.get(0);
                    lastKanji = kanjiList.get(kanjiList.size() - 1);
                    firstKana = cleanedKana.get(0);
                    lastKana = cleanedKana.get(cleanedKana.size() - 1);
                    int firstKanjiLength = firstKanji.length();
                    firstKanjiLengthList.add(firstKanjiLength);
                    int lastKanjiLength = lastKanji.length();
                    lastKanjiLengthList.add(lastKanjiLength);
                    float firstKanaLength = Constants.getKanaLength(firstKana);
                    firstKanaLengthList.add(firstKanaLength);
                    float lastKanaLength = Constants.getKanaLength(lastKana);
                    lastKanaLengthList.add(lastKanaLength);

                    WordComponentView wordComponentLayout = new WordComponentView(context, lifecycleOwner, layoutParams, kanjiList, cleanedKana, wordId);

                    LayoutParams innerLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    wordComponentLayout.setLayoutParams(innerLp);
                    MarginLayoutParams params = (MarginLayoutParams) wordComponentLayout.getLayoutParams();

                    if (isFirstInnerLayout) {
                        isFirstInnerLayout = false;
                        collocationRow.addView(wordComponentLayout);
                        continue;
                    }

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
                        collocationRow.addView(wordComponentLayout);
                        continue;
                    }
                    float marginValue = -Math.min(Math.abs(prev), Math.abs(curr)) / 2f;
                    params.setMarginStart(Convert.dpToPx(context, marginValue));
                    collocationRow.addView(wordComponentLayout);
                }
                translation.setText(collocation.getTranslation());
            }
        }
    }

    private static List<String> cleanKanaAnnotations(List<String> kanjiComponents, List<String> kanaComponents) {
        List<String> cleaned = new ArrayList<>();
        for (int i = 0; i < kanjiComponents.size(); i++) {
            String kanji = kanjiComponents.get(i);
            if (Judge.isKana(kanji)) {
                cleaned.add("");
            } else {
                cleaned.add(i < kanaComponents.size() ? kanaComponents.get(i) : "");
            }
        }
        return cleaned;
    }

}
