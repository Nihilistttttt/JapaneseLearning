package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.UsageDistinction;
import com.Nihilisttt.LearnWord.R;

import java.util.List;

@SuppressLint("ViewConstructor")
public class UsageDistinctionView extends LinearLayout {

    public UsageDistinctionView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<UsageDistinction> distinctions) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        for (UsageDistinction ud : distinctions) {
            View row = View.inflate(context, R.layout.view_integrated_part_row, null);
            LinearLayout rowContainer = row.findViewById(R.id.integrated_part_row);
            TextView translation = row.findViewById(R.id.integrated_part_translation);

            if (ud.getKanjiComponents() != null && !ud.getKanjiComponents().isEmpty()) {
                WrappedPhraseView phrase = new WrappedPhraseView(context, lifecycleOwner, layoutType,
                        ud.getKanjiComponents(), ud.getKanaComponents());
                rowContainer.addView(phrase);
            }

            if (ud.getDistinctionText() != null && !ud.getDistinctionText().isEmpty()) {
                translation.setText(ud.getDistinctionText());
            }

            addView(row);
        }
    }
}
