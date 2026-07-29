package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import androidx.core.content.ContextCompat;
import android.view.Gravity;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;
import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.AudioManager;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.Nihilisttt.LearnWord.UtilityClass.Judge;
import com.Nihilisttt.LearnWord.UtilityClass.PartialSelectorDrawable;
import com.Nihilisttt.LearnWord.UtilityClass.Select;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * 自定义部件：负责动态生成单词的子项布局（Kanji + Kana）并计算边距
 */
@SuppressLint("ViewConstructor")
public class WordComponentView extends LinearLayout {

    private final Select.layoutParams layoutParams;
    private final int layoutType;
    private final List<String> kanjiComponents;     // 汉字组件列表（如「日」「本」「語」）
    private final List<String> kanaComponents;      // 假名组件列表（如「に」「ほん」「ご」）
    private String wordId;
    private final float kanjiSize;
    private final float kanaSize;
    private int marginStart;
    private int marginEnd;
    private final LifecycleOwner lifecycleOwner;
    private boolean isDataInitialized = false;
    private WordRepository repository;
    private LiveData<BasicWord> basicWordLiveData;
    private LiveData<List<WordMeaning>> wordMeaningsLiveData;
    private WordComponentView linkedView;
    private boolean isSyncingPress = false;
    private final Rect drawingBounds = new Rect();
    private final Rect clickableArea = new Rect();

    public void setLinkedView(WordComponentView view) {
        this.linkedView = view;
        view.linkedView = this;
    }

    @Override
    public void setPressed(boolean pressed) {
        if (isSyncingPress) {
            super.setPressed(pressed);
            return;
        }
        super.setPressed(pressed);
        if (linkedView != null) {
            linkedView.isSyncingPress = true;
            linkedView.setPressed(pressed);
            linkedView.isSyncingPress = false;
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable bg = getBackground();
        if (bg instanceof PartialSelectorDrawable) {
            bg.setState(getDrawableState());
            invalidate();
        }
    }

    // region 构造函数

    /**
     * 完整参数的构造函数（推荐使用）
     *
     * @param layoutParams    布局参数配置（如字体大小、边距规则）
     * @param kanjiComponents 汉字组件
     * @param kanaComponents  假名组件
     */
    public WordComponentView(Context context, @NonNull LifecycleOwner lifecycleOwner, Select.layoutParams layoutParams, List<String> kanjiComponents, List<String> kanaComponents) {
        super(context);
        this.layoutParams = layoutParams;
        this.layoutType = layoutParams.getLayoutType();
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.kanjiSize = this.layoutParams.getKanjiSize();
        this.kanaSize = this.layoutParams.getKanaSize();
        this.lifecycleOwner = lifecycleOwner;
        setOrientation(LinearLayout.HORIZONTAL); // 强制水平排列
        initComponents();
    }

    public WordComponentView(Context context, @NonNull LifecycleOwner lifecycleOwner, Select.layoutParams layoutParams, String audioUid, List<String> kanjiComponents, List<String> kanaComponents) {
        super(context);
        this.layoutParams = layoutParams;
        this.layoutType = layoutParams.getLayoutType();
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.kanjiSize = this.layoutParams.getKanjiSize();
        this.kanaSize = this.layoutParams.getKanaSize();
        this.lifecycleOwner = lifecycleOwner;
        setOrientation(LinearLayout.HORIZONTAL); // 强制水平排列
        initComponents();
        setClickable(true);
        setFocusable(true);
        setBackground(new PartialSelectorDrawable(marginStart, marginEnd, ContextCompat.getColor(context, R.color.md_item_pressed)));
        setOnClickListener(v -> {
            AudioManager audioManager = AudioManager.getInstance(context);
            if (audioManager.isPlaying()) {
                audioManager.stopAudio();
            }
            audioManager.playAudio(audioUid);
        });
    }
    // endregion

    /**
     * 完整参数的构造函数（推荐使用）
     *
     * @param layoutParams    布局参数配置（如字体大小、边距规则）
     * @param kanjiComponents 汉字组件
     * @param kanaComponents  假名组件
     */
    public WordComponentView(Context context, @NonNull LifecycleOwner lifecycleOwner, Select.layoutParams layoutParams, List<String> kanjiComponents, List<String> kanaComponents, String wordId) {
        super(context);
        this.layoutParams = layoutParams;
        this.layoutType = layoutParams.getLayoutType();
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.wordId = wordId;
        this.kanjiSize = this.layoutParams.getKanjiSize();
        this.kanaSize = this.layoutParams.getKanaSize();
        this.lifecycleOwner = lifecycleOwner;
        setOrientation(LinearLayout.HORIZONTAL); // 强制水平排列
        initComponents();
        setClickable(true);
        setFocusable(true);
        setBackground(new PartialSelectorDrawable(marginStart, marginEnd, ContextCompat.getColor(context, R.color.md_item_pressed)));
        setOnClickListener(v -> showInfoDialog());
    }

    // endregion
    // region 初始化方法

    private void initComponents() {
        if (kanjiComponents == null || kanaComponents == null || kanjiComponents.isEmpty()) {
            Log.e("WordComponentsLayout", "Components data is null or empty!");
            return;
        }

        int textColor = ContextCompat.getColor(getContext(), R.color.md_on_surface);

        for (int i = 0; i < kanjiComponents.size(); i++) {
            String curKanji = kanjiComponents.get(i);
            String curKana = kanaComponents.get(i);

            LinearLayout itemView = new LinearLayout(getContext());
            itemView.setOrientation(LinearLayout.VERTICAL);
            itemView.setGravity(Gravity.CENTER_HORIZONTAL);
            itemView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            TextView kanaTextView = new TextView(getContext());
            kanaTextView.setGravity(Gravity.CENTER);
            kanaTextView.setTypeface(null, android.graphics.Typeface.BOLD);
            kanaTextView.setIncludeFontPadding(false);
            kanaTextView.setTextColor(textColor);
            kanaTextView.setClickable(false);

            TextView kanjiTextView = new TextView(getContext());
            kanjiTextView.setGravity(Gravity.CENTER);
            kanjiTextView.setTypeface(null, android.graphics.Typeface.BOLD);
            kanjiTextView.setIncludeFontPadding(false);
            kanjiTextView.setTextColor(textColor);
            kanjiTextView.setClickable(false);

            kanaTextView.setText(curKana);
            kanjiTextView.setText(curKanji);
            kanaTextView.setTextSize(layoutParams.getKanaSize());
            kanjiTextView.setTextSize(layoutParams.getKanjiSize());

            itemView.addView(kanaTextView);
            itemView.addView(kanjiTextView);
            itemView.setClickable(false);

            configureMargins(itemView, i);
            addView(itemView);
        }
    }
    // endregion

    // region 子项配置

    /**

     * 动态计算并设置子项边距
     */
    private void configureMargins(View itemView, int position) {

        // 计算边距所需参数
        String curKanji = kanjiComponents.get(position);
        String curKana = kanaComponents.get(position);
        float curKanaLength = Constants.getKanaLength(curKana);
        int curKanjiLength = curKanji.length();
        boolean curIsSmall = Judge.isSmallKana(curKanji);

        float marginValue = (curKanaLength * kanaSize - curKanjiLength * kanjiSize) / 2f;
        if (position == 0) {
            marginStart = Convert.dpToPx(getContext(), marginValue);
            return; // 第一个子项无需左边距
        } else if (position == kanjiComponents.size() - 1) {
            marginEnd = Convert.dpToPx(getContext(), marginValue);
        }

        String preKanji = kanjiComponents.get(position - 1);
        String preKana = kanaComponents.get(position - 1);
        float preKanaLength = Constants.getKanaLength(preKana);
        int preKanjiLength = preKanji.length();
        boolean preIsSmall = Judge.isSmallKana(preKanji);

        MarginLayoutParams params = (MarginLayoutParams) itemView.getLayoutParams();

        // 调用边距计算逻辑
        calculateMargin(params,
                curKanaLength, preKanaLength,
                curKanjiLength, preKanjiLength,
                curIsSmall, preIsSmall,
                position
        );
    }
    // endregion

    // region 边距计算（核心算法）

    /**
     * 计算子项左边距（基于假名和汉字长度差）
     *
     * @param params         布局参数
     * @param curKanaLength  当前假名长度（如「きょう」长度为2）
     * @param preKanaLength  前一个假名长度
     * @param curKanjiLength 当前汉字长度（如「今日」长度为2）
     * @param preKanjiLength 前一个汉字长度
     * @param curIsSmall     当前是否为小假名（如「っ」）
     * @param preIsSmall     前一个是否为小假名
     * @param position       子项位置
     */
    private void calculateMargin(
            MarginLayoutParams params,
            float curKanaLength, float preKanaLength,
            int curKanjiLength, int preKanjiLength,
            boolean curIsSmall, boolean preIsSmall,
            int position) {
        // 公式：边距 = (前假名总宽度 - 前汉字总宽度) 与 (当前假名总宽度 - 当前汉字总宽度) 的差值
        float prev = preKanaLength * kanaSize - preKanjiLength * kanjiSize;
        float curr = curKanaLength * kanaSize - curKanjiLength * kanjiSize;

        // 同符号时使用固定边距
        if (prev * curr >= 0) {
            float marginValue;
            if (curIsSmall) {
                marginValue = layoutParams.getCurrentIsSmallKanaMarginStart();
                Log.d("wordComponentMargin", "固定边距 position=" + position + ", curIsSmall_marginStart=" + marginValue);
            } else if (preIsSmall) {
                marginValue = layoutParams.getPreviousIsSmallKanaMarginStart();
                Log.d("wordComponentMargin", "固定边距 position=" + position + ", preIsSmall_marginStart=" + marginValue);
            } else {
                marginValue = layoutParams.getElseMarginStart();
                Log.d("wordComponentMargin", "固定边距 position=" + position + ", else_marginStart=" + marginValue);
            }
            params.setMarginStart(Convert.dpToPx(getContext(), marginValue));
            return;
        }

        // 异符号时动态计算
        float marginValue = -Math.min(Math.abs(prev), Math.abs(curr)) / 2f;
        params.setMarginStart(Convert.dpToPx(getContext(), marginValue));
        Log.d("wordComponentMargin", "动态边距 position=" + position + ", value=" + marginValue);
    }
    // endregion

    // region 数据更新方法

    // endregion
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect clickableArea = getClickableArea();
        float touchX = event.getX() - getPaddingLeft();
        float touchY = event.getY() - getPaddingTop();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (clickableArea.contains((int) touchX, (int) touchY)) {
                    setPressed(true);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                if (isPressed()) {
                    setPressed(false);
                    performClick();
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }



    private Rect getClickableArea() {
        getDrawingRect(drawingBounds);
        clickableArea.set(drawingBounds.left + marginStart, drawingBounds.top + drawingBounds.height() / 3,
                drawingBounds.right - marginEnd, drawingBounds.bottom);
        return clickableArea;
    }


    private void showInfoDialog() {
        Context context = getContext();
        if (!isDataInitialized) {
            repository = WordRepository.getInstance(context);
            basicWordLiveData = repository.getBasicWordById(wordId);
            wordMeaningsLiveData = repository.getWordMeaningsByWordId(wordId);
            basicWordLiveData.getValue();
            wordMeaningsLiveData.getValue();
            isDataInitialized = true;
        }

        setPressed(true);

        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(16);
        popupWindow.setFocusable(false);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialCardView cardView = new MaterialCardView(context);
        cardView.setRadius(16);
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_card_background));
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        NestedScrollView scrollView = new NestedScrollView(context);
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        int containerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
        container.setPadding(containerPadding, containerPadding, containerPadding, containerPadding);

        LinearLayout basicWordPart = new LinearLayout(context);
        basicWordPart.setOrientation(LinearLayout.VERTICAL);
        basicWordPart.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout meaningPart = new LinearLayout(context);
        meaningPart.setOrientation(LinearLayout.VERTICAL);

        LinearLayout buttonPart = new LinearLayout(context);
        buttonPart.setOrientation(LinearLayout.HORIZONTAL);
        Button wordDetails = new Button(context);
        wordDetails.setText("查看详情➡");
        buttonPart.addView(wordDetails);

        container.addView(basicWordPart);
        container.addView(meaningPart);
        container.addView(buttonPart);
        scrollView.addView(container);
        cardView.addView(scrollView);
        popupWindow.setContentView(cardView);

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int popupWidth = (int) (screenWidth * 0.95);
        popupWindow.setWidth(popupWidth);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        int[] location = new int[2];
        this.getLocationOnScreen(location);
        int yOffset = location[1] + this.getHeight() + (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int xOffset = (screenWidth - popupWidth) / 2;

        final boolean[] basicWordReady = {false};
        final boolean[] meaningReady = {false};

        final Observer<BasicWord> basicWordObserver = basicWord -> {
            basicWordPart.removeAllViews();
            if (basicWord == null || basicWord.getWordId().equals("null")) {
                TextView emptyView = new TextView(context);
                emptyView.setText("暂无单词信息");
                basicWordPart.addView(emptyView);
            } else {
                AudioManager audioManager = AudioManager.getInstance(context);
                if (audioManager.isPlaying()) {
                    audioManager.stopAudio();
                }
                audioManager.playAudio(basicWord.getAudioUrl());
                SharedPreferences fontPrefs = context.getSharedPreferences("FontSizePrefs", Context.MODE_PRIVATE);
                int wordLevel = fontPrefs.getInt("word_font_level", Constants.FONT_SIZE_NORMAL);
                BasicWordView basicWordView = new BasicWordView(getContext(), lifecycleOwner, wordLevel, basicWord);
                basicWordPart.addView(basicWordView);
            }
            basicWordReady[0] = true;
            if (meaningReady[0] && !popupWindow.isShowing()) {
                popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, xOffset, yOffset);
            }
        };

        final Observer<List<WordMeaning>> wordMeaningObserver = wordMeanings -> {
            meaningPart.removeAllViews();
            if (wordMeanings == null) {
                TextView emptyView = new TextView(context);
                emptyView.setText("暂无单词信息");
                meaningPart.addView(emptyView);
            } else {
                MeaningView meaningView = new MeaningView(getContext(), lifecycleOwner, layoutType, wordMeanings, Constants.SHOW_SENTENCE_POPUP);
                meaningPart.addView(meaningView);
            }
            meaningReady[0] = true;
            if (basicWordReady[0] && !popupWindow.isShowing()) {
                popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, xOffset, yOffset);
            }
        };

        popupWindow.setOnDismissListener(() -> {
            setPressed(false);
            basicWordLiveData.removeObserver(basicWordObserver);
            wordMeaningsLiveData.removeObserver(wordMeaningObserver);
        });

        basicWordLiveData.observe(lifecycleOwner, basicWordObserver);
        wordMeaningsLiveData.observe(lifecycleOwner, wordMeaningObserver);
    }


}
