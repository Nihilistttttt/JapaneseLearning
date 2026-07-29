package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween2Layers;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage.AntonymWordViewFragment;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage.CollocationViewFragment;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage.SynonymWordViewFragment;
import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class IntegratedPartView extends LinearLayout {
    private List<Fragment> fragmentList;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabLayoutMediator mediator;
    private NestedScrollableHostBetween2Layers host;

    public IntegratedPartView(Context context, List<WordCollocation> collocations,
                              List<AntonymWord> antonymWords, List<SynonymWord> synonymWords) {
        super(context);
        setOrientation(VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        setLayoutParams(params);
        initContainers();
        update(collocations, antonymWords, synonymWords);
    }

    private void initContainers() {
        tabLayout = new TabLayout(getContext());
        tabLayout.setTabGravity(TabLayout.GRAVITY_START);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        host = new NestedScrollableHostBetween2Layers(getContext());
        host.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));
        viewPager2 = new ViewPager2(getContext());
        View childAt = viewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        host.addView(viewPager2);
        addView(host);
        addView(tabLayout);
    }

    public void update(List<WordCollocation> collocations,
                       List<AntonymWord> antonymWords, List<SynonymWord> synonymWords) {
        if (mediator != null) {
            mediator.detach();
        }
        fragmentList = new ArrayList<>();
        List<String> tabTitles = new ArrayList<>();

        if (!collocations.isEmpty()) {
            tabTitles.add("词组搭配");
            fragmentList.add(new CollocationViewFragment(collocations));
        }
        if (!antonymWords.isEmpty()) {
            tabTitles.add("反义词");
            fragmentList.add(new AntonymWordViewFragment(antonymWords));
        }
        if (!synonymWords.isEmpty()) {
            tabTitles.add("近义词");
            fragmentList.add(new SynonymWordViewFragment(synonymWords));
        }

        FragmentStateAdapter adapter = new FragmentStateAdapter((FragmentActivity) getContext()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return fragmentList.size();
            }
        };
        viewPager2.setAdapter(adapter);

        mediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(tabTitles.get(position));
            View tabView = tab.view;
            int minWidth = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    40,
                    getResources().getDisplayMetrics()
            );
            tabView.setMinimumWidth(minWidth);
        });
        mediator.attach();
    }


}



