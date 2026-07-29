package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.TypedValue;
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
    private int layoutType;
    private Select.layoutParams layoutParams;
    private final LifecycleOwner lifecycleOwner;
    private Paint kanjiPaint;
    private Paint kanaPaint;

    public SentenceView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<WordSentence> sentenceList) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        this.lifecycleOwner = lifecycleOwner;
        initPaints(layoutType);
        update(sentenceList, layoutType);
    }

    public SentenceView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, WordSentence sentence) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        this.lifecycleOwner = lifecycleOwner;
        initPaints(layoutType);
        update(sentence, layoutType);
    }

    public void update(List<WordSentence> sentenceList, int layoutType) {
        if (this.layoutType != layoutType) {
            initPaints(layoutType);
        }
        removeAllViews();
        LinearLayout sentencePart = new LinearLayout(getContext());
        sentencePart.setOrientation(LinearLayout.VERTICAL);
        for (WordSentence sentence : sentenceList) {
            processSentence(sentence, sentencePart);
        }
        addView(sentencePart);
    }

    public void update(WordSentence sentence, int layoutType) {
        if (this.layoutType != layoutType) {
            initPaints(layoutType);
        }
        removeAllViews();
        LinearLayout sentencePart = new LinearLayout(getContext());
        sentencePart.setOrientation(LinearLayout.VERTICAL);
        processSentence(sentence, sentencePart);
        addView(sentencePart);
    }

    private void initPaints(int layoutType) {
        this.layoutType = layoutType;
        this.layoutParams = Select.selectLayout(layoutType);
        this.kanjiPaint = createPaint(this.layoutParams.getKanjiSize());
        this.kanaPaint = createPaint(this.layoutParams.getKanaSize());
    }

    private Paint createPaint(float textSizeSp) {
        Paint paint = new Paint();
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeSp, getContext().getResources().getDisplayMetrics()));
        return paint;
    }

    private boolean isNonClickableWordId(String wordId) {
        return wordId.equals("0");
    }

    private float measureWordWidth(List<String> kanjiList, List<String> kanaList) {
        float totalWidth = 0;
        for (int i = 0; i < kanjiList.size(); i++) {
            float kanaW = kanaPaint.measureText(kanaList.get(i));
            float kanjiW = kanjiPaint.measureText(kanjiList.get(i));
            totalWidth += Math.max(kanaW, kanjiW);
        }
        return totalWidth;
    }

    private boolean isClosingPunctuation(List<String> kanjiList) {
        if (kanjiList.size() != 1) return false;
        String text = kanjiList.get(0);
        if (text.length() != 1) return false;
        char c = text.charAt(0);
        return c == '。' || c == '、' || c == '！' || c == '？' || c == '」' || c == '』'
                || c == '）' || c == ')' || c == '】';
    }

    private boolean isEllipsisToken(List<String> kanjiList) {
        if (kanjiList.size() != 1) return false;
        String text = kanjiList.get(0);
        return text.equals("…") || text.equals("・");
    }

    private boolean isSplittableWord(List<String> kanjiList, List<String> kanaList) {
        for (int i = 0; i < kanjiList.size(); i++) {
            if (kanjiList.get(i).length() > 1 && !kanaList.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private float measureCharWidthPx(String kanji, String kana) {
        List<String> singleKanji = new ArrayList<>();
        singleKanji.add(kanji);
        List<String> singleKana = new ArrayList<>();
        singleKana.add(kana);
        return measureWordWidth(singleKanji, singleKana);
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
//            float marginDp;
//            marginDp = layoutParams.getElseMarginStart();
//            return Convert.dpToPx(getContext(), marginDp) - curr / 2f;
        }
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
        translation.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.getSubDefinitionSize(layoutType));

        int screenWidthPx = context.getResources().getDisplayMetrics().widthPixels;
        int availableWidthPx = screenWidthPx - Convert.dpToPx(context, 40);

        LinearLayout currentRow = new LinearLayout(context);
        currentRow.setOrientation(LinearLayout.HORIZONTAL);
        currentRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        sentenceRowContainer.addView(currentRow);
        float currentRowWidth = 0;

        String prevLastKanji = null;
        String prevLastKana = null;

        for (int i = 0; i < originalsKanji.size(); i++) {
            String wordId = sentence.getWordIdList().get(i);
            List<String> kanjiList = originalsKanji.get(i);
            List<String> kanaList = originalsKana.get(i);

            String firstKanji = kanjiList.get(0);
            String firstKana = kanaList.get(0);
            String lastKanji = kanjiList.get(kanjiList.size() - 1);
            String lastKana = kanaList.get(kanaList.size() - 1);

            float wordWidthPx = measureWordWidth(kanjiList, kanaList);
            float marginPx = 0;

            if (prevLastKanji != null) {
                marginPx = calcMarginPx(prevLastKanji, prevLastKana, firstKanji, firstKana);
            }

            float punctReservePx = 0;
            if (i + 1 < originalsKanji.size()) {
                List<String> nextKanji = originalsKanji.get(i + 1);
                List<String> nextKana = originalsKana.get(i + 1);
                if (isClosingPunctuation(nextKanji) && !isEllipsisToken(nextKanji)) {
                    punctReservePx = measureWordWidth(nextKanji, nextKana);
                    float punctMarginPx = calcMarginPx(lastKanji, lastKana, nextKanji.get(0), nextKana.get(0));
                    if (punctMarginPx > 0) punctReservePx += punctMarginPx;
                }
            }

            boolean needsNewLine = currentRowWidth + marginPx + wordWidthPx + punctReservePx > availableWidthPx && currentRowWidth > 0;

            if (needsNewLine && isClosingPunctuation(kanjiList) && !isEllipsisToken(kanjiList)) {
                needsNewLine = false;
            }

            if (needsNewLine) {
                if (isSplittableWord(kanjiList, kanaList) && currentRowWidth > 0) {
                    float remainingPx = availableWidthPx - currentRowWidth - marginPx - punctReservePx;
                    int splitPoint = findSplitPoint(kanjiList, kanaList, remainingPx);

                    if (splitPoint > 0 && splitPoint < kanjiList.size()) {
                        List<String> part1Kanji = new ArrayList<>(kanjiList.subList(0, splitPoint));
                        List<String> part1Kana = new ArrayList<>(kanaList.subList(0, splitPoint));
                        List<String> part2Kanji = new ArrayList<>(kanjiList.subList(splitPoint, kanjiList.size()));
                        List<String> part2Kana = new ArrayList<>(kanaList.subList(splitPoint, kanaList.size()));

                        WordComponentView part1;
                        if (isNonClickableWordId(wordId)) {
                            part1 = new WordComponentView(context, lifecycleOwner, layoutParams, part1Kanji, part1Kana);
                        } else {
                            part1 = new WordComponentView(context, lifecycleOwner, layoutParams, part1Kanji, part1Kana, wordId);
                        }
                        if (marginPx != 0) {
                            LinearLayout.LayoutParams innerLp = new LinearLayout.LayoutParams(
                                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            part1.setLayoutParams(innerLp);
                            MarginLayoutParams params = (MarginLayoutParams) part1.getLayoutParams();
                            params.setMarginStart((int) marginPx);
                        }
                        currentRow.addView(part1);

                        currentRow = new LinearLayout(context);
                        currentRow.setOrientation(LinearLayout.HORIZONTAL);
                        currentRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        sentenceRowContainer.addView(currentRow);
                        currentRowWidth = 0;

                        WordComponentView part2;
                        if (isNonClickableWordId(wordId)) {
                            part2 = new WordComponentView(context, lifecycleOwner, layoutParams, part2Kanji, part2Kana);
                        } else {
                            part2 = new WordComponentView(context, lifecycleOwner, layoutParams, part2Kanji, part2Kana, wordId);
                        }
                        part1.setLinkedView(part2);
                        currentRow.addView(part2);
                        currentRowWidth = measureWordWidth(part2Kanji, part2Kana);

                        prevLastKanji = part2Kanji.get(part2Kanji.size() - 1);
                        prevLastKana = part2Kana.get(part2Kana.size() - 1);
                        continue;
                    }
                }

                currentRow = new LinearLayout(context);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                sentenceRowContainer.addView(currentRow);
                currentRowWidth = 0;
                marginPx = 0;
            }

            WordComponentView wordComponentLayout;
            if (isNonClickableWordId(wordId)) {
                wordComponentLayout = new WordComponentView(context, lifecycleOwner, layoutParams, kanjiList, kanaList);
            } else {
                wordComponentLayout = new WordComponentView(context, lifecycleOwner, layoutParams, kanjiList, kanaList, wordId);
            }

            if (marginPx != 0) {
                LinearLayout.LayoutParams innerLp = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                wordComponentLayout.setLayoutParams(innerLp);
                MarginLayoutParams params = (MarginLayoutParams) wordComponentLayout.getLayoutParams();
                params.setMarginStart((int) marginPx);
                currentRowWidth += marginPx;
            }

            currentRow.addView(wordComponentLayout);
            currentRowWidth += wordWidthPx;

            prevLastKanji = lastKanji;
            prevLastKana = lastKana;
        }

        translation.setText(sentence.getTranslation());
    }
}
