package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.Etymology;
import com.Nihilisttt.LearnWord.WordView.EtymologyView;

import java.util.List;

public class EtymologyViewFragment extends WordViewFragment<Etymology> {
    public EtymologyViewFragment() {}
    public EtymologyViewFragment(List<Etymology> etymologies) { super(etymologies); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<Etymology> data) {
        return new EtymologyView(context, lifecycleOwner, layoutType, data);
    }
}