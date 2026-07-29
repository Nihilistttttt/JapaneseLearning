package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.KanjiInfo;
import com.Nihilisttt.LearnWord.WordView.KanjiInfoView;

import java.util.List;

public class KanjiInfoViewFragment extends WordViewFragment<KanjiInfo> {
    public KanjiInfoViewFragment() {}
    public KanjiInfoViewFragment(List<KanjiInfo> kanjiInfos) { super(kanjiInfos); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<KanjiInfo> data) {
        return new KanjiInfoView(context, lifecycleOwner, layoutType, data);
    }
}
