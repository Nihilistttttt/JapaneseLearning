package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.Nihilisttt.LearnWord.UtilityClass.FlowLayout;
import com.Nihilisttt.LearnWord.UtilityClass.Select;
import com.Nihilisttt.LearnWord.ViewPager2.ViewPager2Navigation;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class MeaningView extends LinearLayout {

    private final int layoutType;
    private final int mode;
    private final Select.layoutParams layoutParams;
    private final LifecycleOwner lifecycleOwner;
    private final float definitionTextSize;
    private final float posTextSize;
    private final LinkedHashMap<Constants.PartOfSpeech, List<WordMeaning>> meaningMap = new LinkedHashMap<>();
    private WordRepository repository;
    private LiveData<List<WordSentence>> sentenceLiveData;

    // 主构造函数（动态创建时使用）
    public MeaningView(Context context, @NonNull LifecycleOwner lifecycleOwner,
                       int layoutType, List<WordMeaning> meanings,int mode) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        this.layoutType = layoutType;
        this.mode = mode;
        this.layoutParams = Select.selectLayout(this.layoutType);
        this.lifecycleOwner = lifecycleOwner;
        this.definitionTextSize = Constants.getSubDefinitionSize(layoutType);
        this.posTextSize = Constants.getSubDefinitionSize(layoutType);
        initViews(meanings);
    }

    private void initViews(List<WordMeaning> meanings) {
        meaningMap.clear();
        removeAllViews();

        for (WordMeaning meaning : meanings) {
            Constants.PartOfSpeech pos = meaning.getPartOfSpeech();
            meaningMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(meaning);
        }
        int i = 0;
        for (Map.Entry<Constants.PartOfSpeech, List<WordMeaning>> entry : meaningMap.entrySet()) {
            FlowLayout partLayout = createPartOfSpeechLayout(entry.getKey());

            for (WordMeaning detail : entry.getValue()) {
                partLayout.addView(createTranslationView(detail, i));
                i++;
            }

            addView(partLayout);
        }
    }

    private FlowLayout createPartOfSpeechLayout(Constants.PartOfSpeech pos) {
        FlowLayout posPart = new FlowLayout(getContext());
        posPart.setLineSpacing(Convert.dpToPx(getContext(), 4));
        posPart.setPadding(0, Convert.dpToPx(getContext(), 6), 0, Convert.dpToPx(getContext(), 2));

        TextView posView = new TextView(getContext());
        posView.setText(String.format("%s  ", pos.getAbbreviation()));
        posView.setTextSize(TypedValue.COMPLEX_UNIT_SP, posTextSize);
        posView.setTypeface(null, android.graphics.Typeface.BOLD);
        posView.setTextColor(ContextCompat.getColor(getContext(), R.color.md_part_of_speech));

        posPart.addView(posView);
        return posPart;
    }

    private TextView createTranslationView(WordMeaning meaning,int position) {
        TextView view = new TextView(getContext());

        String def = meaning.getDefinitionSummary();
        SpannableString spannable = new SpannableString(def);
        spannable.setSpan(new UnderlineSpan(), 0, spannable.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        view.setText(spannable);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, definitionTextSize);
        view.setTextColor(ContextCompat.getColor(getContext(), R.color.md_definition_text));
        view.setTag(meaning);
        if (mode == Constants.TURN_TO_DETAIL_PAGE) {
            view.setMaxLines(1);
            view.setEllipsize(android.text.TextUtils.TruncateAt.END);
        }
        int marginEnd = Convert.dpToPx(getContext(), 8);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = marginEnd;
        view.setLayoutParams(lp);
        switch (mode){
            case Constants.SHOW_SENTENCE_POPUP:view.setOnClickListener(v -> showSentencePopup((WordMeaning) v.getTag()));break;
            case Constants.TURN_TO_DETAIL_PAGE:view.setOnClickListener(v -> ViewPager2Navigation.getInstance().turnToDetailPage(position));break;
        }
        return view;
    }


    private void showSentencePopup(WordMeaning meaning) {
        final Context context = getContext();
        repository = WordRepository.getInstance(context);
        sentenceLiveData = repository.getWordSentenceByWordMeaningId(meaning.getWordMeaningId());
        sentenceLiveData.getValue();

        // 创建弹窗容器
        final MaterialCardView cardView = new MaterialCardView(context);
        cardView.setRadius(Convert.dpToPx(getContext(), 12));
        cardView.setCardElevation(Convert.dpToPx(getContext(), 4));
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_card_background));

        // 内容容器
        final LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);

        // 弹窗配置
        final PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setContentView(cardView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 日文翻译
        TextView originalDefinition = new TextView(context);
        originalDefinition.setText(String.format("日: %s", meaning.getOriginalDefinition()));
        originalDefinition.setTextSize(TypedValue.COMPLEX_UNIT_SP, definitionTextSize);
        originalDefinition.setTextColor(ContextCompat.getColor(context, R.color.md_detail_label));
        originalDefinition.setPadding(Convert.dpToPx(getContext(), 16), Convert.dpToPx(getContext(), 12), Convert.dpToPx(getContext(), 16), Convert.dpToPx(getContext(), 4));
        container.addView(originalDefinition);

        // 中文翻译
        TextView translationDefinition = new TextView(context);
        translationDefinition.setText(String.format("中: %s", meaning.getTranslationDefinition()));
        translationDefinition.setTextSize(TypedValue.COMPLEX_UNIT_SP, definitionTextSize);
        translationDefinition.setTextColor(ContextCompat.getColor(context, R.color.md_detail_label));
        translationDefinition.setPadding(Convert.dpToPx(getContext(), 16), Convert.dpToPx(getContext(), 4), Convert.dpToPx(getContext(), 16), Convert.dpToPx(getContext(), 8));
        container.addView(translationDefinition);

        cardView.addView(container);

        // 获取LiveData并观察


        final Observer<List<WordSentence>> observer = sentences -> {
            if (sentences == null || sentences.isEmpty()) {
                TextView emptyView = new TextView(context);
                emptyView.setText("暂无可用例句");
                container.addView(emptyView);
                return;
            }

            SentenceView sentenceView = new SentenceView(context, lifecycleOwner, layoutType, sentences);
            sentenceView.setPadding(Convert.dpToPx(getContext(), 12), Convert.dpToPx(getContext(), 4), Convert.dpToPx(getContext(), 12), Convert.dpToPx(getContext(), 8));
            container.addView(sentenceView);
        };

        // 绑定生命周期观察
        sentenceLiveData.observe(lifecycleOwner, observer);

        // 确保弹窗关闭时移除观察
        popupWindow.setOnDismissListener(() -> sentenceLiveData.removeObserver(observer));

        // 定位显示
        View anchor = findAnchorView(meaning);
        if (anchor != null && anchor.isAttachedToWindow()) {
            // 确保锚点视图可见且已附加到窗口
            popupWindow.showAsDropDown(anchor, 0, Convert.dpToPx(getContext(), 4));
        } else {
            // 回退到中心显示
            View rootView = ((Activity) getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
            popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        }
    }

    private View findAnchorView(WordMeaning meaning) {
        // 仅遍历当前视图层级
        for (int i = 0; i < getChildCount(); i++) {
            View partLayout = getChildAt(i);
            if (partLayout instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) partLayout;
                for (int j = 0; j < layout.getChildCount(); j++) {
                    View child = layout.getChildAt(j);
                    if (meaning.equals(child.getTag())
                            && child.getVisibility() == VISIBLE
                            && child.isAttachedToWindow()) {
                        return child;
                    }
                }
            }
        }
        return null;
    }


    private List<View> getViewsByTag(View root, Object tag) {
        List<View> views = new ArrayList<>();
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (tag.equals(child.getTag())) {
                    views.add(child);
                }
                if (child instanceof ViewGroup) {
                    views.addAll(getViewsByTag(child, tag));
                }
            }
        }
        return views;
    }
}
