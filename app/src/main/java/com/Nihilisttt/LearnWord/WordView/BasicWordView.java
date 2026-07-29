package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.SparseArray;
import android.util.TypedValue;
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

    private static final SparseArray<Paint> paintCache = new SparseArray<>();

    private static Paint getCachedPaint(int textSizePx) {
        Paint paint = paintCache.get(textSizePx);
        if (paint == null) {
            paint = new Paint();
            paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            paint.setTextSize(textSizePx);
            paintCache.put(textSizePx, paint);
        }
        return paint;
    }

    public BasicWordView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, BasicWord basicWord) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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
        android.util.DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int kanjiTextSizePx = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, lp.getKanjiSize(), dm));
        int kanaTextSizePx = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, lp.getKanaSize(), dm));
        Paint cachedKanjiPaint = getCachedPaint(kanjiTextSizePx);
        Paint cachedKanaPaint = getCachedPaint(kanaTextSizePx);
        float total = 0;
        for (int i = 0; i < kanjiComponents.size(); i++) {
            float kanaW = cachedKanaPaint.measureText(kanaComponents.get(i));
            float kanjiW = cachedKanjiPaint.measureText(kanjiComponents.get(i));
            total += Math.max(kanaW, kanjiW);
        }
        return total;
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

        int currentLevel = layoutType;
        while (estimatedWidth > availableWidthPx && currentLevel > Constants.FONT_SIZE_SMALL) {
            currentLevel--;
            lp = Select.selectLayout(currentLevel);
            estimatedWidth = estimateWordWidth(kanjiComponents, kanaComponents, lp);
        }


        WordComponentView wordComponentLayout = new WordComponentView(
                context, lifecycleOwner, lp,
                basicWord.getAudioUrl(),
                kanjiComponents, kanaComponents
        );
        wordComponentLayout.setClickable(false);

        TextView accentMark = new TextView(context);
        String accentStr = basicWord.getAccentMark();
        if (accentStr != null && !accentStr.isEmpty()) {
            try {
                int num = Integer.parseInt(accentStr);
                if (num == 0) {
                    accentStr = "\u24EA";
                } else if (num >= 1 && num <= 20) {
                    accentStr = String.valueOf((char) (0x2460 + num - 1));
                }
            } catch (NumberFormatException ignored) {}
        }
        accentMark.setText(accentStr);

        word_component_part.addView(wordComponentLayout);
        accent_mark_part.addView(accentMark);
    }
}
