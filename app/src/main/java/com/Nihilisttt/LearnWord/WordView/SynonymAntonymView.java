package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.R;

import java.util.List;

@SuppressLint("ViewConstructor")
public class SynonymAntonymView extends LinearLayout {

    public SynonymAntonymView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType,
                              List<SynonymWord> synonymWords, List<AntonymWord> antonymWords) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        if (!synonymWords.isEmpty()) {
            addSectionTitle("近义词");
            addThreeColumnRows(context, lifecycleOwner, layoutType, synonymWords);
        }

        if (!antonymWords.isEmpty()) {
            if (!synonymWords.isEmpty()) {
                View divider = new View(context);
                int dividerHeight = (int) (8 * context.getResources().getDisplayMetrics().density);
                divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dividerHeight));
                addView(divider);
            }
            addSectionTitle("反义词");
            addThreeColumnRows(context, lifecycleOwner, layoutType, antonymWords);
        }
    }

    private void addSectionTitle(String title) {
        TextView tv = new TextView(getContext());
        tv.setText(title);
        tv.setTextSize(14);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(getContext().getResources().getColor(R.color.md_detail_label));
        int marginBottom = (int) (4 * getContext().getResources().getDisplayMetrics().density);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, marginBottom);
        tv.setLayoutParams(params);
        addView(tv);
    }

    private void addThreeColumnRows(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<? extends com.Nihilisttt.LearnWord.JavaBean.WordComponent> words) {
        for (int idx = 0; idx < words.size(); idx += 3) {
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(HORIZONTAL);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addView(row);

            int count = Math.min(3, words.size() - idx);
            for (int c = 0; c < count; c++) {
                com.Nihilisttt.LearnWord.JavaBean.WordComponent word = words.get(idx + c);
                View cell = View.inflate(context, R.layout.view_integrated_part_row, null);
                LinearLayout.LayoutParams cellParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
                cell.setLayoutParams(cellParams);

                LinearLayout rowContainer = cell.findViewById(R.id.integrated_part_row);
                WrappedPhraseView phrase = new WrappedPhraseView(context, lifecycleOwner, layoutType,
                        word.getKanjiComponents(), word.getKanaComponents());
                rowContainer.addView(phrase);
                row.addView(cell);
            }
            for (int c = count; c < 3; c++) {
                View spacer = new View(context);
                LinearLayout.LayoutParams spacerParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
                spacer.setLayoutParams(spacerParams);
                row.addView(spacer);
            }
        }
    }
}