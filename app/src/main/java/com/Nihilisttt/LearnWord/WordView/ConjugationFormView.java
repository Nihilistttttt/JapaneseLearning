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

import java.util.List;

@SuppressLint("ViewConstructor")
public class ConjugationFormView extends LinearLayout {

    public ConjugationFormView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<ConjugationForm> forms) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        for (int idx = 0; idx < forms.size(); idx += 3) {
            LinearLayout row = new LinearLayout(context);
            row.setOrientation(HORIZONTAL);
            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addView(row);

            for (int c = 0; c < 3 && idx + c < forms.size(); c++) {
                ConjugationForm form = forms.get(idx + c);
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
        }
    }
}
