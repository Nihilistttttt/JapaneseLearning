package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.ConjugationForm;
import com.Nihilisttt.LearnWord.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class ConjugationFormView extends LinearLayout {

    public ConjugationFormView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<ConjugationForm> forms) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        List<ConjugationForm> validForms = new ArrayList<>();
        for (ConjugationForm form : forms) {
            if (form != null) validForms.add(form);
        }

        for (int idx = 0; idx < validForms.size(); idx += 3) {
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(HORIZONTAL);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addView(row);

            int count = Math.min(3, validForms.size() - idx);
            for (int c = 0; c < count; c++) {
                ConjugationForm form = validForms.get(idx + c);
                View cell = View.inflate(context, R.layout.view_integrated_part_row, null);
                LinearLayout.LayoutParams cellParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
                cell.setLayoutParams(cellParams);

                LinearLayout rowContainer = cell.findViewById(R.id.integrated_part_row);
                TextView translation = cell.findViewById(R.id.integrated_part_translation);

                WrappedPhraseView phrase = new WrappedPhraseView(context, lifecycleOwner, layoutType,
                        form.getKanjiComponents(), form.getKanaComponents());
                rowContainer.addView(phrase);

                translation.setText(form.getFormName());
                row.addView(cell);
            }
            if (count < 3) {
                View spacer = new View(context);
                spacer.setLayoutParams(new LayoutParams(0, 1, 1f));
                row.addView(spacer);
            }
        }
    }
}
