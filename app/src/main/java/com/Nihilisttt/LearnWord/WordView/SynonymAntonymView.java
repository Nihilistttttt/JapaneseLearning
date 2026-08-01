package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Nihilisttt.LearnWord.UtilityClass.Constants;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Select;

import java.util.List;

@SuppressLint("ViewConstructor")
public class SynonymAntonymView extends LinearLayout {

    public SynonymAntonymView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType,
                              List<SynonymWord> synonymWords, List<AntonymWord> antonymWords) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        Select.layoutParams lp = Select.selectLayout(layoutType);

        if (!synonymWords.isEmpty()) {
            addSectionTitle("近义词");
            addWordRows(context, lifecycleOwner, lp, layoutType, synonymWords);
        }

        if (!antonymWords.isEmpty()) {
            if (!synonymWords.isEmpty()) {
                View divider = new View(context);
                int dividerHeight = (int) (Constants.SECTION_DIVIDER_HEIGHT_DP * context.getResources().getDisplayMetrics().density);
                divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dividerHeight));
                addView(divider);
            }
            addSectionTitle("反义词");
            addWordRows(context, lifecycleOwner, lp, layoutType, antonymWords);
        }
    }

    private <T extends com.Nihilisttt.LearnWord.JavaBean.WordComponent> void addWordRows(
            Context context, LifecycleOwner lifecycleOwner, Select.layoutParams lp, int layoutType, List<T> words) {
        for (int idx = 0; idx < words.size(); idx += 3) {
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(HORIZONTAL);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addView(row);

            int count = Math.min(3, words.size() - idx);
            for (int c = 0; c < count; c++) {
                T word = words.get(idx + c);

                String cwid = null;
                if (word instanceof SynonymWord) cwid = ((SynonymWord) word).getCorrespondingWordId();
                else if (word instanceof AntonymWord) cwid = ((AntonymWord) word).getCorrespondingWordId();

                LinearLayout cell = new LinearLayout(context);
                cell.setOrientation(HORIZONTAL);
                cell.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

                if (cwid != null && !cwid.equals("0")) {
                    cell.addView(new WordComponentView(context, lifecycleOwner, lp,
                            word.getKanjiComponents(), word.getKanaComponents(), cwid));
                } else {
                    cell.addView(new WrappedPhraseView(context, lifecycleOwner, layoutType,
                            word.getKanjiComponents(), word.getKanaComponents()));
                }
                row.addView(cell);
            }
            for (int c = count; c < 3; c++) {
                View spacer = new View(context);
                spacer.setLayoutParams(new LayoutParams(0, 1, 1f));
                row.addView(spacer);
            }
        }
    }

    private void addSectionTitle(String title) {
        TextView tv = new TextView(getContext());
        tv.setText(title);
        tv.setTextSize(14);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(getContext().getResources().getColor(R.color.md_detail_label));
        int marginBottom = (int) (Constants.SECTION_TITLE_BOTTOM_MARGIN_DP * getContext().getResources().getDisplayMetrics().density);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, marginBottom);
        tv.setLayoutParams(params);
        addView(tv);
    }
}
