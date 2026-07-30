package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.Idiom;
import com.Nihilisttt.LearnWord.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class IdiomView extends LinearLayout {

    public IdiomView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<Idiom> idioms) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        List<Idiom> validIdioms = new ArrayList<>();
        for (Idiom idiom : idioms) {
            if (idiom != null) validIdioms.add(idiom);
        }

        for (int idx = 0; idx < validIdioms.size(); idx += 2) {
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(HORIZONTAL);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addView(row);

            for (int c = 0; c < 2 && idx + c < validIdioms.size(); c++) {
                Idiom idiom = validIdioms.get(idx + c);
                View cell = View.inflate(context, R.layout.view_integrated_part_row, null);
                LinearLayout.LayoutParams cellParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
                cell.setLayoutParams(cellParams);

                LinearLayout rowContainer = cell.findViewById(R.id.integrated_part_row);
                TextView translation = cell.findViewById(R.id.integrated_part_translation);

                WrappedPhraseView phrase = new WrappedPhraseView(context, lifecycleOwner, layoutType,
                        idiom.getKanjiComponents(), idiom.getKanaComponents());
                rowContainer.addView(phrase);
                translation.setText(idiom.getTranslation());
                row.addView(cell);
            }
            int count = Math.min(2, validIdioms.size() - idx);
            if (count < 2) {
                View spacer = new View(context);
                spacer.setLayoutParams(new LayoutParams(0, 1, 1f));
                row.addView(spacer);
            }
        }
    }
}
