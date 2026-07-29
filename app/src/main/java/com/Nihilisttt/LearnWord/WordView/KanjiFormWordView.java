package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.KanjiFormWord;

import java.util.List;

@SuppressLint("ViewConstructor")
public class KanjiFormWordView extends WordComponentListView<KanjiFormWord> {

    public KanjiFormWordView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<KanjiFormWord> kanjiFormWords) {
        super(context, lifecycleOwner, layoutType, kanjiFormWords);
    }

    @Override
    protected String getClickId(KanjiFormWord item) {
        return item.getWordId();
    }
}
