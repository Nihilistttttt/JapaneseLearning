package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.KanjiFormWord;
import com.Nihilisttt.LearnWord.WordView.KanjiFormWordView;

import java.util.List;

public class KanjiFormViewFragment extends WordViewFragment<KanjiFormWord> {
    public KanjiFormViewFragment() {}
    public KanjiFormViewFragment(List<KanjiFormWord> kanjiFormWords) { super(kanjiFormWords); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<KanjiFormWord> data) {
        return new KanjiFormWordView(context, lifecycleOwner, layoutType, data);
    }
}
