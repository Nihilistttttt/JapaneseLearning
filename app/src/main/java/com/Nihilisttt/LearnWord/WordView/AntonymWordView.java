package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.Nihilisttt.LearnWord.UtilityClass.Select;

import java.util.List;

@SuppressLint("ViewConstructor")
public class AntonymWordView extends LinearLayout {

    private static final String TAB = "AntonymWordView";
    private final Select.layoutParams layoutParams;
    private final LifecycleOwner lifecycleOwner;

    public AntonymWordView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<AntonymWord> antonymWords) {
        super(context);
        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,  // 宽度设为match_parent
                LayoutParams.WRAP_CONTENT   // 高度保持wrap_content
        ));
        this.layoutParams = Select.selectLayout(layoutType);
        this.lifecycleOwner = lifecycleOwner;
        setClickable(true);
        setFocusable(true);
        setBackgroundResource(R.drawable.word_layout_selector);

        initViews(context, antonymWords);
    }


    private void initViews(Context context, List<AntonymWord> antonymWords) {
        for (AntonymWord antonymWord : antonymWords) {
            View container = View.inflate(context, R.layout.view_basic_word, null);
            addView(container);  // 将新布局添加到主容器
            LinearLayout word_component_part = container.findViewById(R.id.word_component_container);

            List<String> kanjiComponents = antonymWord.getKanjiComponents();
            List<String> kanaComponents = antonymWord.getKanaComponents();
            int firstKanjiLength = kanjiComponents.get(0).length();
            float firstKanaLength = Constants.getKanaLength(kanaComponents.get(0));

            float tempMarginStart = (firstKanjiLength * layoutParams.getKanjiSize() - firstKanaLength * layoutParams.getKanaSize()) / 2f;
            ConstraintLayout.LayoutParams containerParams = (ConstraintLayout.LayoutParams) word_component_part.getLayoutParams();
            Log.d(TAB, "   kanjiComponents.get(0): " + kanjiComponents.get(0) + "   tempMarginStart: " + tempMarginStart);
            if (tempMarginStart < 0) {
                containerParams.setMarginStart(Convert.dpToPx(context, Constants.BASIC_WORD_LAYOUT_MARGIN_START + tempMarginStart));
            } else {
                containerParams.setMarginStart(Convert.dpToPx(context, Constants.BASIC_WORD_LAYOUT_MARGIN_START));
            }
            word_component_part.setLayoutParams(containerParams);
            // 创建子项布局部件
            WordComponentView wordComponentLayout = new WordComponentView(
                    context,
                    lifecycleOwner,
                    layoutParams,
                    antonymWord.getKanjiComponents(),
                    antonymWord.getKanaComponents(),
                    antonymWord.getCorrespondingWordId()
            );
            // 整体布局结构
            word_component_part.addView(wordComponentLayout);
        }
    }
}
