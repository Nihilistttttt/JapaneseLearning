package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.Nihilisttt.LearnWord.JavaBean.UsageDistinction;
import com.Nihilisttt.LearnWord.WordView.UsageDistinctionView;

import java.util.List;

public class UsageDistinctionViewFragment extends WordViewFragment<UsageDistinction> {
    public UsageDistinctionViewFragment() {}
    public UsageDistinctionViewFragment(List<UsageDistinction> usageDistinctions) { super(usageDistinctions); }

    @Override
    protected View createContentView(Context context, LifecycleOwner lifecycleOwner, int layoutType, List<UsageDistinction> data) {
        return new UsageDistinctionView(context, lifecycleOwner, layoutType, data);
    }
}
