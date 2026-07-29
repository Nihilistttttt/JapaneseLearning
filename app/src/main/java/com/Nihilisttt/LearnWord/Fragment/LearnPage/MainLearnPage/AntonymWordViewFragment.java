package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.WordView.AntonymWordView;

import java.util.List;

public class AntonymWordViewFragment extends WordViewFragment<AntonymWord> {
    public AntonymWordViewFragment() {}
    public AntonymWordViewFragment(List<AntonymWord> antonymWords) { super(antonymWords); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<AntonymWord> data) {
        return new AntonymWordView(context, lifecycleOwner, layoutType, data);
    }
}
