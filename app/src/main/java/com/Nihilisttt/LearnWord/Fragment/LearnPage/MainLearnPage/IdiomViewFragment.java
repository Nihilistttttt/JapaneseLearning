package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.Idiom;
import com.Nihilisttt.LearnWord.WordView.IdiomView;

import java.util.List;

public class IdiomViewFragment extends WordViewFragment<Idiom> {
    public IdiomViewFragment() {}
    public IdiomViewFragment(List<Idiom> idioms) { super(idioms); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<Idiom> data) {
        return new IdiomView(context, lifecycleOwner, layoutType, data);
    }
}
