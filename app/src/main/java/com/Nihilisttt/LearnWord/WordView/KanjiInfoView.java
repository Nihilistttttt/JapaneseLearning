package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.KanjiInfo;
import com.Nihilisttt.LearnWord.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("ViewConstructor")
public class KanjiInfoView extends LinearLayout {

    public KanjiInfoView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<KanjiInfo> infos) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        for (KanjiInfo info : infos) {
            List<String> allReadings = new ArrayList<>();
            List<String> allLabels = new ArrayList<>();
            for (String on : info.getOnyomi()) {
                allReadings.add(on.replace(".", ""));
                allLabels.add("音");
            }
            for (String kun : info.getKunyomi()) {
                allReadings.add(kun.replace(".", ""));
                allLabels.add("訓");
            }

            int numCols = allReadings.size();
            if (numCols > 0) {
                View readingsRow = View.inflate(context, R.layout.view_integrated_part_row, null);
                LinearLayout readingsContainer = readingsRow.findViewById(R.id.integrated_part_row);
                TextView readingsTranslation = readingsRow.findViewById(R.id.integrated_part_translation);

                LinearLayout colsRow = new LinearLayout(context);
                colsRow.setOrientation(HORIZONTAL);
                colsRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                for (int i = 0; i < numCols; i++) {
                    LinearLayout col = new LinearLayout(context);
                    col.setOrientation(VERTICAL);
                    col.setGravity(Gravity.CENTER_HORIZONTAL);
                    col.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

                    PhraseComponentView reading = PhraseComponentView.fromSingleWord(context, lifecycleOwner, layoutType,
                            Collections.singletonList(info.getKanji()), Collections.singletonList(allReadings.get(i)));
                    col.addView(reading);

                    TextView label = new TextView(context);
                    label.setText(allLabels.get(i));
                    label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                    label.setTextColor(0xFF4488CC);
                    label.setTypeface(null, Typeface.BOLD);
                    label.setGravity(Gravity.CENTER_HORIZONTAL);
                    col.addView(label);

                    colsRow.addView(col);
                }
                readingsContainer.addView(colsRow);
                readingsTranslation.setText("");
                addView(readingsRow);
            }

            if (info.getSameKanjiWords() != null && !info.getSameKanjiWords().isEmpty()) {
                View wordsRow = View.inflate(context, R.layout.view_integrated_part_row, null);
                LinearLayout wordsContainer = wordsRow.findViewById(R.id.integrated_part_row);
                TextView wordsTranslation = wordsRow.findViewById(R.id.integrated_part_translation);

                LinearLayout wordsColsRow = new LinearLayout(context);
                wordsColsRow.setOrientation(HORIZONTAL);
                wordsColsRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                int colCount = Math.min(info.getSameKanjiWords().size(), 3);
                for (int i = 0; i < info.getSameKanjiWords().size(); i++) {
                    if (i > 0 && i % colCount == 0) {
                        wordsContainer.addView(wordsColsRow);
                        wordsColsRow = new LinearLayout(context);
                        wordsColsRow.setOrientation(HORIZONTAL);
                        wordsColsRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    }
                    String wordJson = info.getSameKanjiWords().get(i);
                    LinearLayout wordCell = new LinearLayout(context);
                    wordCell.setOrientation(VERTICAL);
                    wordCell.setGravity(Gravity.CENTER_HORIZONTAL);
                    wordCell.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f / colCount));

                    try {
                        org.json.JSONObject obj = new org.json.JSONObject(wordJson);
                        org.json.JSONArray kjArr = obj.getJSONArray("kj");
                        org.json.JSONArray knArr = obj.getJSONArray("kn");
                        List<String> kj = new ArrayList<>();
                        List<String> kn = new ArrayList<>();
                        for (int j = 0; j < kjArr.length(); j++) kj.add(kjArr.getString(j));
                        for (int j = 0; j < knArr.length(); j++) kn.add(knArr.getString(j));
                        String wid = obj.optString("wid", "0");
                        PhraseComponentView wordView;
                        if (!wid.equals("0")) {
                            wordView = PhraseComponentView.fromSingleWord(context, lifecycleOwner, layoutType, kj, kn, wid);
                        } else {
                            wordView = PhraseComponentView.fromSingleWord(context, lifecycleOwner, layoutType, kj, kn);
                        }
                        wordCell.addView(wordView);
                    } catch (Exception e) {
                        TextView fallback = new TextView(context);
                        fallback.setText(wordJson);
                        wordCell.addView(fallback);
                    }
                    wordsColsRow.addView(wordCell);
                }
                int remaining = info.getSameKanjiWords().size() % colCount;
                if (remaining != 0) {
                    for (int s = 0; s < colCount - remaining; s++) {
                        View spacer = new View(context);
                        spacer.setLayoutParams(new LayoutParams(0, 1, 1f / colCount));
                        wordsColsRow.addView(spacer);
                    }
                }
                wordsContainer.addView(wordsColsRow);
                wordsTranslation.setText("");
                addView(wordsRow);
            }
        }
    }
}
