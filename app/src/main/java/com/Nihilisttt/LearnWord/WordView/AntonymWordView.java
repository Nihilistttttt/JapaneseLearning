package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;

import java.util.List;

@SuppressLint("ViewConstructor")
public class AntonymWordView extends WordComponentListView<AntonymWord> {

    public AntonymWordView(Context context, @NonNull LifecycleOwner lifecycleOwner, int layoutType, List<AntonymWord> antonymWords) {
        super(context, lifecycleOwner, layoutType, antonymWords);
    }

    @Override
    protected String getClickId(AntonymWord item) {
        return item.getCorrespondingWordId();
    }
}
