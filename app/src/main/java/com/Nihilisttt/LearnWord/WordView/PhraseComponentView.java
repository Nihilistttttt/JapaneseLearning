package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.UtilityClass.Judge;
import com.Nihilisttt.LearnWord.UtilityClass.Select;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class PhraseComponentView extends LinearLayout {

    private PhraseComponentView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    private static List<String> cleanKanaAnnotations(List<String> kanjiComponents, List<String> kanaComponents) {
        List<String> cleaned = new ArrayList<>();
        for (int i = 0; i < kanjiComponents.size(); i++) {
            String kanji = kanjiComponents.get(i);
            if (Judge.isKana(kanji)) {
                cleaned.add("");
            } else {
                cleaned.add(i < kanaComponents.size() ? kanaComponents.get(i) : "");
            }
        }
        return cleaned;
    }

    public static PhraseComponentView fromSingleWord(Context context, @NonNull LifecycleOwner lifecycleOwner,
                                                      int layoutType, List<String> kanjiComponents, List<String> kanaComponents) {
        PhraseComponentView view = new PhraseComponentView(context);
        Select.layoutParams lp = Select.selectLayout(layoutType);
        List<String> cleanedKana = cleanKanaAnnotations(kanjiComponents, kanaComponents);
        WordComponentView wcv = new WordComponentView(context, lifecycleOwner, lp, kanjiComponents, cleanedKana);
        wcv.setClickable(false);
        view.addView(wcv);
        return view;
    }

    public static PhraseComponentView fromSingleWord(Context context, @NonNull LifecycleOwner lifecycleOwner,
                                                      int layoutType, List<String> kanjiComponents, List<String> kanaComponents, String wordId) {
        PhraseComponentView view = new PhraseComponentView(context);
        Select.layoutParams lp = Select.selectLayout(layoutType);
        List<String> cleanedKana = cleanKanaAnnotations(kanjiComponents, kanaComponents);
        WordComponentView wcv = new WordComponentView(context, lifecycleOwner, lp, kanjiComponents, cleanedKana, wordId);
        view.addView(wcv);
        return view;
    }

    public static PhraseComponentView fromPhrase(Context context, @NonNull LifecycleOwner lifecycleOwner,
                                                  int layoutType, List<List<String>> kanjiComponentsList, List<List<String>> kanaComponentsList) {
        PhraseComponentView view = new PhraseComponentView(context);
        Select.layoutParams lp = Select.selectLayout(layoutType);
        for (int i = 0; i < kanjiComponentsList.size(); i++) {
            List<String> kanjiComp = kanjiComponentsList.get(i);
            List<String> kanaComp = kanaComponentsList.get(i);
            List<String> cleanedKana = cleanKanaAnnotations(kanjiComp, kanaComp);
            WordComponentView wcv = new WordComponentView(context, lifecycleOwner, lp, kanjiComp, cleanedKana);
            wcv.setClickable(false);
            view.addView(wcv);
        }
        return view;
    }
}
