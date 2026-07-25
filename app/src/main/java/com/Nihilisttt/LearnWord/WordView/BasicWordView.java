package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.AudioManager;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.Nihilisttt.LearnWord.UtilityClass.Select;

import java.util.List;

@SuppressLint("ViewConstructor")
public class BasicWordView extends LinearLayout {
    private final LifecycleOwner lifecycleOwner;

    public BasicWordView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, BasicWord basicWord) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.lifecycleOwner = lifecycleOwner;
        setClickable(true);
        setFocusable(true);
        setBackgroundResource(R.drawable.word_layout_selector);
        setOnClickListener(v -> {
            AudioManager audioManager = AudioManager.getInstance(context);
            if (audioManager.isPlaying()) audioManager.stopAudio();
            audioManager.playAudio(basicWord.getAudioUrl());
        });
        initViews(context, basicWord, layoutType);
    }

    private float estimateWordWidth(List<String> kanjiComponents, List<String> kanaComponents, Select.layoutParams lp) {
        float total = 0;
        for (int i = 0; i < kanjiComponents.size(); i++) {
            float kanaW = Constants.getKanaLength(kanaComponents.get(i)) * lp.getKanaSize();
            float kanjiW = kanjiComponents.get(i).length() * lp.getKanjiSize();
            total += Math.max(kanaW, kanjiW);
        }
        return Convert.dpToPx(getContext(), total);
    }

    private void initViews(Context context, BasicWord basicWord, int layoutType) {
        View container = View.inflate(context, R.layout.view_basic_word, null);
        addView(container);
        LinearLayout word_component_part = container.findViewById(R.id.word_component_container);
        LinearLayout accent_mark_part = container.findViewById(R.id.accent_mark_container);

        List<String> kanjiComponents = basicWord.getKanjiComponents();
        List<String> kanaComponents = basicWord.getKanaComponents();

        int screenWidthPx = context.getResources().getDisplayMetrics().widthPixels;
        int availableWidthPx = screenWidthPx - Convert.dpToPx(context, 32);

        Select.layoutParams lp = Select.selectLayout(layoutType);
        float estimatedWidth = estimateWordWidth(kanjiComponents, kanaComponents, lp);

        if (estimatedWidth > availableWidthPx && layoutType == Constants.LARGE) {
            lp = Select.selectLayout(Constants.NORMAL);
            estimatedWidth = estimateWordWidth(kanjiComponents, kanaComponents, lp);
        }
        if (estimatedWidth > availableWidthPx && layoutType != Constants.SMALL) {
            lp = Select.selectLayout(Constants.SMALL);
        }


        WordComponentView wordComponentLayout = new WordComponentView(
                context, lifecycleOwner, lp,
                basicWord.getAudioUrl(),
                kanjiComponents, kanaComponents
        );
        wordComponentLayout.setClickable(false);

        TextView accentMark = new TextView(context);
        accentMark.setText(basicWord.getAccentMark());

        word_component_part.addView(wordComponentLayout);
        accent_mark_part.addView(accentMark);
    }
}
