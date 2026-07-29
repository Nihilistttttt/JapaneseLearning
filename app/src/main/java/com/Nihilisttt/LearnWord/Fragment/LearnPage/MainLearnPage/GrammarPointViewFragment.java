package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.GrammarPoint;
import com.Nihilisttt.LearnWord.WordView.GrammarPointView;

import java.util.List;

public class GrammarPointViewFragment extends WordViewFragment<GrammarPoint> {
    public GrammarPointViewFragment() {}
    public GrammarPointViewFragment(List<GrammarPoint> grammarPoints) { super(grammarPoints); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<GrammarPoint> data) {
        return new GrammarPointView(context, lifecycleOwner, layoutType, data);
    }
}
