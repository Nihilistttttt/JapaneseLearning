package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.Etymology;
import com.Nihilisttt.LearnWord.R;

import java.util.List;

@SuppressLint("ViewConstructor")
public class EtymologyView extends LinearLayout {

    public EtymologyView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<Etymology> etymologies) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        for (Etymology etymology : etymologies) {
            View row = View.inflate(context, R.layout.view_integrated_part_row, null);
            LinearLayout rowContainer = row.findViewById(R.id.integrated_part_row);
            TextView translation = row.findViewById(R.id.integrated_part_translation);

            if (etymology.getKanjiComponents() != null && !etymology.getKanjiComponents().isEmpty()) {
                PhraseComponentView phrase = PhraseComponentView.fromPhrase(context, lifecycleOwner, layoutType,
                        etymology.getKanjiComponents(), etymology.getKanaComponents());
                rowContainer.addView(phrase);
            }

            StringBuilder sb = new StringBuilder();
            if (etymology.getEtymologyType() != null && !etymology.getEtymologyType().isEmpty()) {
                sb.append(etymology.getEtymologyType());
            }
            if (etymology.getTranslation() != null && !etymology.getTranslation().isEmpty()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(etymology.getTranslation());
            }
            if (sb.length() > 0) {
                translation.setText(sb.toString());
            }

            addView(row);
        }
    }
}
