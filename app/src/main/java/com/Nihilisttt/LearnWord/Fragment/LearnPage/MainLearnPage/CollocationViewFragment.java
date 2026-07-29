package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.Nihilisttt.LearnWord.WordView.CollocationView;

import java.util.List;

public class CollocationViewFragment extends WordViewFragment<WordCollocation> {
    public CollocationViewFragment() {}
    public CollocationViewFragment(List<WordCollocation> collocations) { super(collocations); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<WordCollocation> data) {
        return new CollocationView(context, lifecycleOwner, layoutType, data);
    }
}
