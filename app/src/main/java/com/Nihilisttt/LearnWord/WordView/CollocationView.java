package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
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

        for (WordCollocation collocation : collocations) {
            View collocationLayout = View.inflate(context, R.layout.view_collocation_column, null);
            addView(collocationLayout);  // 将新布局添加到主容器
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

            float tempMarginStart = (originalsKanji.get(0).get(0).length() * layoutParams.getKanjiSize() - Constants.getKanaLength(originalsKana.get(0).get(0)) * layoutParams.getKanaSize()) / 2f;
            ConstraintLayout.LayoutParams collocationRowParams = (ConstraintLayout.LayoutParams) collocationRow.getLayoutParams();
            if (tempMarginStart < 0) {
                collocationRowParams.setMarginStart(Convert.dpToPx(context, Constants.COLLOCATION_ROW_MARGIN_START + tempMarginStart));
            } else {
                collocationRowParams.setMarginStart(Convert.dpToPx(context, Constants.COLLOCATION_ROW_MARGIN_START));
            }
            collocationRow.setLayoutParams(collocationRowParams);

            for (int i = 0; i < originalsKanji.size(); i++) {
                wordId = collocation.getWordIdList().get(i);
                kanjiList = originalsKanji.get(i);
                kanaList = originalsKana.get(i);
                firstKanji = kanjiList.get(0);
                lastKanji = kanjiList.get(kanjiList.size() - 1);
                firstKana = kanaList.get(0);
                lastKana = kanaList.get(kanaList.size() - 1);
                int firstKanjiLength = firstKanji.length();
                firstKanjiLengthList.add(firstKanjiLength);
                int lastKanjiLength = lastKanji.length();
                lastKanjiLengthList.add(lastKanjiLength);
                float firstKanaLength = Constants.getKanaLength(firstKana);
                firstKanaLengthList.add(firstKanaLength);
                float lastKanaLength = Constants.getKanaLength(lastKana);
                lastKanaLengthList.add(lastKanaLength);

                WordComponentView wordComponentLayout = new WordComponentView(context, lifecycleOwner, layoutParams, kanjiList, kanaList, wordId);

                LayoutParams innerLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                wordComponentLayout.setLayoutParams(innerLp); // 关键修复：确保LayoutParams存在
                MarginLayoutParams params = (MarginLayoutParams) wordComponentLayout.getLayoutParams();// 然后再获取Margin参数

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
                        marginStart = layoutParams.getCurrentIsSmallKanaMarginStart(); //小假名与前一个汉字的间距
                    } else if (Judge.isSmallKana(lastKanji)) {
                        marginStart = layoutParams.getPreviousIsSmallKanaMarginStart(); //汉字与前一个小假名的间距
                    } else marginStart = layoutParams.getElseMarginStart();
                    params.setMarginStart(Convert.dpToPx(context, marginStart));
                    collocationRow.addView(wordComponentLayout);
                    continue; // 仅跳过严格同号的情况
                }
                float marginValue = -Math.min(Math.abs(prev), Math.abs(curr)) / 2f;
                params.setMarginStart(Convert.dpToPx(context, marginValue));
                collocationRow.addView(wordComponentLayout);
            }
            translation.setText(collocation.getTranslation());
        }
    }

}
