package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.GrammarPoint;
import com.Nihilisttt.LearnWord.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class GrammarPointView extends LinearLayout {

    public GrammarPointView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<GrammarPoint> points) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        for (GrammarPoint gp : points) {
            View row = View.inflate(context, R.layout.view_integrated_part_row, null);
            LinearLayout rowContainer = row.findViewById(R.id.integrated_part_row);
            TextView translation = row.findViewById(R.id.integrated_part_translation);

            if (gp.getNameKanjiComponents() != null && !gp.getNameKanjiComponents().isEmpty()) {
                WrappedPhraseView namePhrase = new WrappedPhraseView(context, lifecycleOwner, layoutType,
                        gp.getNameKanjiComponents(), gp.getNameKanaComponents());
                rowContainer.addView(namePhrase);
            }

            if (gp.getDescKanjiComponents() != null && !gp.getDescKanjiComponents().isEmpty()) {
                WrappedPhraseView descPhrase = new WrappedPhraseView(context, lifecycleOwner, layoutType,
                        gp.getDescKanjiComponents(), gp.getDescKanaComponents());
                rowContainer.addView(descPhrase);
            }

            if (gp.getExampleKanji() != null && !gp.getExampleKanji().isEmpty()) {
                try {
                    org.json.JSONArray kjArr = new org.json.JSONArray(gp.getExampleKanji());
                    org.json.JSONArray knArr = new org.json.JSONArray(gp.getExampleKana());
                    List<String> kj = new ArrayList<>();
                    List<String> kn = new ArrayList<>();
                    for (int i = 0; i < kjArr.length(); i++) kj.add(kjArr.getString(i));
                    for (int i = 0; i < knArr.length(); i++) kn.add(knArr.getString(i));
                    WrappedPhraseView example = new WrappedPhraseView(context, lifecycleOwner, layoutType, kj, kn);
                    rowContainer.addView(example);
                } catch (Exception e) {
                    TextView fallback = new TextView(context);
                    fallback.setText(gp.getExampleKanji());
                    rowContainer.addView(fallback);
                }
            }

            if (gp.getGrammarDescription() != null && !gp.getGrammarDescription().isEmpty()) {
                translation.setText(gp.getGrammarDescription());
            }

            addView(row);
        }
    }
}
