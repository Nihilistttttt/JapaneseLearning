package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;

import java.util.List;

@SuppressLint("ViewConstructor")
public class SynonymWordView extends WordComponentListView<SynonymWord> {

    public SynonymWordView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<SynonymWord> synonymWords) {
        super(context, lifecycleOwner, layoutType, synonymWords);
    }

    @Override
    protected String getClickId(SynonymWord item) {
        return item.getCorrespondingWordId();
    }
}
