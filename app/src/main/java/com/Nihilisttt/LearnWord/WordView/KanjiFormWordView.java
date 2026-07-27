package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.KanjiFormWord;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.Nihilisttt.LearnWord.UtilityClass.Select;

import java.util.List;

@SuppressLint("ViewConstructor")
public class KanjiFormWordView extends LinearLayout {

    private final Select.layoutParams layoutParams;
    private final LifecycleOwner lifecycleOwner;

    public KanjiFormWordView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<KanjiFormWord> kanjiFormWords) {
        super(context);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.layoutParams = Select.selectLayout(layoutType);
        this.lifecycleOwner = lifecycleOwner;
        setClickable(true);
        setFocusable(true);
        setBackgroundResource(R.drawable.word_layout_selector);
        initViews(context, kanjiFormWords);
    }

    private void initViews(Context context, List<KanjiFormWord> kanjiFormWords) {
        for (KanjiFormWord kanjiFormWord : kanjiFormWords) {
            View container = View.inflate(context, R.layout.view_basic_word, null);
            addView(container);
            LinearLayout word_component_part = container.findViewById(R.id.word_component_container);

            List<String> kanjiComponents = kanjiFormWord.getKanjiComponents();
            List<String> kanaComponents = kanjiFormWord.getKanaComponents();
            int firstKanjiLength = kanjiComponents.get(0).length();
            float firstKanaLength = Constants.getKanaLength(kanaComponents.get(0));

            float tempMarginStart = (firstKanjiLength * layoutParams.getKanjiSize() - firstKanaLength * layoutParams.getKanaSize()) / 2f;
            ConstraintLayout.LayoutParams containerParams = (ConstraintLayout.LayoutParams) word_component_part.getLayoutParams();
            if (tempMarginStart < 0) {
                containerParams.setMarginStart(Convert.dpToPx(context, Constants.BASIC_WORD_LAYOUT_MARGIN_START + tempMarginStart));
            } else {
                containerParams.setMarginStart(Convert.dpToPx(context, Constants.BASIC_WORD_LAYOUT_MARGIN_START));
            }
            word_component_part.setLayoutParams(containerParams);

            WordComponentView wordComponentLayout = new WordComponentView(
                    context,
                    lifecycleOwner,
                    layoutParams,
                    kanjiFormWord.getKanjiComponents(),
                    kanjiFormWord.getKanaComponents(),
                    kanjiFormWord.getWordId()
            );
            word_component_part.addView(wordComponentLayout);
        }
    }
}