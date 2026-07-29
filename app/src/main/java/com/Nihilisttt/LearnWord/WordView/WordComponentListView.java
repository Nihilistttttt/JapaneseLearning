package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.WordComponent;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.Nihilisttt.LearnWord.UtilityClass.Select;

import java.util.List;

@SuppressLint("ViewConstructor")
public abstract class WordComponentListView<T extends WordComponent> extends LinearLayout {

    protected final Select.layoutParams layoutParams;
    protected final LifecycleOwner lifecycleOwner;

    public WordComponentListView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<T> items) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.layoutParams = Select.selectLayout(layoutType);
        this.lifecycleOwner = lifecycleOwner;
        setClickable(true);
        setFocusable(true);
        setBackgroundResource(R.drawable.word_layout_selector);
        initViews(context, items);
    }

    protected abstract String getClickId(T item);

    private void initViews(Context context, List<T> items) {
        for (T item : items) {
            View container = View.inflate(context, R.layout.view_basic_word, null);
            addView(container);
            LinearLayout word_component_part = container.findViewById(R.id.word_component_container);

            List<String> kanjiComponents = item.getKanjiComponents();
            List<String> kanaComponents = item.getKanaComponents();
            applyFirstComponentMargin(context, word_component_part, kanjiComponents, kanaComponents);

            WordComponentView wordComponentLayout = new WordComponentView(
                    context, lifecycleOwner, layoutParams,
                    kanjiComponents, kanaComponents, getClickId(item)
            );
            word_component_part.addView(wordComponentLayout);
        }
    }

    protected void applyFirstComponentMargin(Context context, LinearLayout wordComponentPart,
                                              List<String> kanjiComponents, List<String> kanaComponents) {
        int firstKanjiLength = kanjiComponents.get(0).length();
        float firstKanaLength = Constants.getKanaLength(kanaComponents.get(0));
        float tempMarginStart = (firstKanjiLength * layoutParams.getKanjiSize() - firstKanaLength * layoutParams.getKanaSize()) / 2f;
        ConstraintLayout.LayoutParams containerParams = (ConstraintLayout.LayoutParams) wordComponentPart.getLayoutParams();
        if (tempMarginStart < 0) {
            containerParams.setMarginStart(Convert.dpToPx(context, Constants.BASIC_WORD_LAYOUT_MARGIN_START + tempMarginStart));
        } else {
            containerParams.setMarginStart(Convert.dpToPx(context, Constants.BASIC_WORD_LAYOUT_MARGIN_START));
        }
        wordComponentPart.setLayoutParams(containerParams);
    }
}