package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.ConjugationForm;
import com.Nihilisttt.LearnWord.WordView.ConjugationFormView;

import java.util.List;

public class ConjugationFormViewFragment extends WordViewFragment<ConjugationForm> {
    public ConjugationFormViewFragment() {}
    public ConjugationFormViewFragment(List<ConjugationForm> conjugationForms) { super(conjugationForms); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<ConjugationForm> data) {
        return new ConjugationFormView(context, lifecycleOwner, layoutType, data);
    }
}
