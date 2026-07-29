package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.WordView.SynonymWordView;

import java.util.List;

public class SynonymWordViewFragment extends WordViewFragment<SynonymWord> {
    public SynonymWordViewFragment() {}
    public SynonymWordViewFragment(List<SynonymWord> synonymWords) { super(synonymWords); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<SynonymWord> data) {
        return new SynonymWordView(context, lifecycleOwner, layoutType, data);
    }
}
