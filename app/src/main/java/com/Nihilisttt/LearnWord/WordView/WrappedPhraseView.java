package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.Nihilisttt.LearnWord.UtilityClass.Judge;
import com.Nihilisttt.LearnWord.UtilityClass.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("ViewConstructor")
public class WrappedPhraseView extends LinearLayout {

    private final Select.layoutParams layoutParams;
    private final LifecycleOwner lifecycleOwner;
    private final Paint kanjiPaint;
    private final Paint kanaPaint;

    public WrappedPhraseView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType,
                              List<String> kanjiComponents, List<String> kanaComponents) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.layoutParams = Select.selectLayout(layoutType);
        this.lifecycleOwner = lifecycleOwner;
        this.kanjiPaint = createPaint(this.layoutParams.getKanjiSize());
        this.kanaPaint = createPaint(this.layoutParams.getKanaSize());
        List<String> cleanedKana = cleanKanaAnnotations(kanjiComponents, kanaComponents);
        wrapComponents(kanjiComponents, cleanedKana);
    }

    private Paint createPaint(float textSizeSp) {
        Paint paint = new Paint();
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeSp, getContext().getResources().getDisplayMetrics()));
        return paint;
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

    private float measureCharWidthPx(String kanji, String kana) {

        float kanaW = kanaPaint.measureText(kana);
        float kanjiW = kanjiPaint.measureText(kanji);
        return Math.max(kanaW, kanjiW);
    }

    private float measureSegmentWidthPx(List<String> kanjiList, List<String> kanaList) {
        float total = 0;
        for (int i = 0; i < kanjiList.size(); i++) {
            total += measureCharWidthPx(kanjiList.get(i), kanaList.get(i));
        }
        return total;
    }

    private float calcMarginPx(String prevKanji, String prevKana, String curKanji, String curKana) {

        float prev = kanaPaint.measureText(prevKana) - kanjiPaint.measureText(prevKanji);
        float curr = kanaPaint.measureText(curKana) - kanjiPaint.measureText(curKanji);

        if (prev * curr >= 0) {
            float marginDp;
            if (Judge.isSmallKana(curKanji)) {
                marginDp = layoutParams.getCurrentIsSmallKanaMarginStart();
            } else if (Judge.isSmallKana(prevKanji)) {
                marginDp = layoutParams.getPreviousIsSmallKanaMarginStart();
            } else {
                marginDp = layoutParams.getElseMarginStart();
            }
            return Convert.dpToPx(getContext(), marginDp);
        } else {
            return -Math.min(Math.abs(prev), Math.abs(curr)) / 2f;
        }
    }

    private int findSplitPoint(List<String> kanjiList, List<String> kanaList, float remainingWidthPx) {
        float accumulated = 0;
        for (int i = 0; i < kanjiList.size(); i++) {
            float charWidth = measureCharWidthPx(kanjiList.get(i), kanaList.get(i));
            if (accumulated + charWidth > remainingWidthPx) {
                return i;
            }
            accumulated += charWidth;
        }
        return kanjiList.size();
    }

    private void wrapComponents(List<String> kanjiComponents, List<String> kanaComponents) {
        if (kanjiComponents == null || kanjiComponents.isEmpty()) return;
        Context context = getContext();

        int screenWidthPx = context.getResources().getDisplayMetrics().widthPixels;
        int availableWidthPx = screenWidthPx - Convert.dpToPx(context, 40);

        LinearLayout currentRow = new LinearLayout(context);
        currentRow.setOrientation(HORIZONTAL);
        currentRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(currentRow);
        float currentRowWidth = 0;

        String prevKanji = null;
        String prevKana = null;

        for (int i = 0; i < kanjiComponents.size(); i++) {
            String curKanji = kanjiComponents.get(i);
            String curKana = kanaComponents.get(i);

            float charWidthPx = measureCharWidthPx(curKanji, curKana);
            float marginPx = 0;

            if (prevKanji != null) {
                marginPx = calcMarginPx(prevKanji, prevKana, curKanji, curKana);
            }

            boolean needsNewLine = currentRowWidth + marginPx + charWidthPx > availableWidthPx && currentRowWidth > 0;

            if (needsNewLine) {
                float remainingPx = availableWidthPx - currentRowWidth - marginPx;
                if (remainingPx > 0 && i < kanjiComponents.size()) {
                    List<String> part1Kanji = new ArrayList<>(kanjiComponents.subList(0, i));
                    List<String> part1Kana = new ArrayList<>(kanaComponents.subList(0, i));
                    List<String> part2Kanji = new ArrayList<>(kanjiComponents.subList(i, kanjiComponents.size()));
                    List<String> part2Kana = new ArrayList<>(kanaComponents.subList(i, kanaComponents.size()));

                    currentRow.removeAllViews();
                    WordComponentView part1 = new WordComponentView(context, lifecycleOwner, layoutParams, part1Kanji, part1Kana);
                    part1.setClickable(false);
                    currentRow.addView(part1);

                    currentRow = new LinearLayout(context);
                    currentRow.setOrientation(HORIZONTAL);
                    currentRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    addView(currentRow);
                    currentRowWidth = 0;

                    WordComponentView part2 = new WordComponentView(context, lifecycleOwner, layoutParams, part2Kanji, part2Kana);
                    part2.setClickable(false);
                    part1.setLinkedView(part2);
                    currentRow.addView(part2);
                    currentRowWidth = measureSegmentWidthPx(part2Kanji, part2Kana);
                    return;
                }

                currentRow = new LinearLayout(context);
                currentRow.setOrientation(HORIZONTAL);
                currentRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                addView(currentRow);
                currentRowWidth = 0;
                marginPx = 0;
            }

            WordComponentView wcv = new WordComponentView(context, lifecycleOwner, layoutParams,
                    Collections.singletonList(curKanji), Collections.singletonList(curKana));
            wcv.setClickable(false);

            if (marginPx != 0) {
                LayoutParams innerLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                wcv.setLayoutParams(innerLp);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) wcv.getLayoutParams();
                params.setMarginStart((int) marginPx);
                currentRowWidth += marginPx;
            }

            currentRow.addView(wcv);
            currentRowWidth += charWidthPx;

            prevKanji = curKanji;
            prevKana = curKana;
        }
    }
}