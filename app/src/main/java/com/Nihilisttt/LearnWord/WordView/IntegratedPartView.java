package com.Nihilisttt.LearnWord.WordView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.R;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween2Layers;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage.CollocationViewFragment;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage.SynonymAntonymViewFragment;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage.ConjugationFormViewFragment;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage.ExtendedInfoViewFragment;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage.KanjiInfoViewFragment;
import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.JavaBean.ConjugationForm;
import com.Nihilisttt.LearnWord.JavaBean.Etymology;
import com.Nihilisttt.LearnWord.JavaBean.GrammarPoint;
import com.Nihilisttt.LearnWord.JavaBean.Idiom;
import com.Nihilisttt.LearnWord.JavaBean.KanjiInfo;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.JavaBean.UsageDistinction;
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

    private List<WordCollocation> collocations;
    private List<AntonymWord> antonymWords;
    private List<SynonymWord> synonymWords;
    private List<ConjugationForm> conjugationForms;
    private List<Etymology> etymologies;
    private List<KanjiInfo> kanjiInfos;
    private List<UsageDistinction> usageDistinctions;
    private List<GrammarPoint> grammarPoints;
    private List<Idiom> idioms;

    private boolean isScrollMode = false;
    private int subFontLevel = Constants.FONT_SIZE_NORMAL;

    public IntegratedPartView(Context context,
                              List<WordCollocation> collocations,
                              List<AntonymWord> antonymWords,
                              List<SynonymWord> synonymWords,
                              List<ConjugationForm> conjugationForms,
                              List<Etymology> etymologies,
                              List<KanjiInfo> kanjiInfos,
                              List<UsageDistinction> usageDistinctions,
                              List<GrammarPoint> grammarPoints,
                              List<Idiom> idioms) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        saveData(collocations, antonymWords, synonymWords, conjugationForms, etymologies, kanjiInfos, usageDistinctions, grammarPoints, idioms);
        initContainers();
        update();
    }

    private void saveData(List<WordCollocation> collocations,
                          List<AntonymWord> antonymWords,
                          List<SynonymWord> synonymWords,
                          List<ConjugationForm> conjugationForms,
                          List<Etymology> etymologies,
                          List<KanjiInfo> kanjiInfos,
                          List<UsageDistinction> usageDistinctions,
                          List<GrammarPoint> grammarPoints,
                          List<Idiom> idioms) {
        this.collocations = collocations;
        this.antonymWords = antonymWords;
        this.synonymWords = synonymWords;
        this.conjugationForms = conjugationForms;
        this.etymologies = etymologies;
        this.kanjiInfos = kanjiInfos;
        this.usageDistinctions = usageDistinctions;
        this.grammarPoints = grammarPoints;
        this.idioms = idioms;
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
    }

    public void setScrollMode(boolean scroll, int subFontLevel) {
        if (this.isScrollMode == scroll) return;
        this.isScrollMode = scroll;
        this.subFontLevel = subFontLevel;
        rebuild();
    }

    public void updateData(List<WordCollocation> collocations,
                           List<AntonymWord> antonymWords,
                           List<SynonymWord> synonymWords,
                           List<ConjugationForm> conjugationForms,
                           List<Etymology> etymologies,
                           List<KanjiInfo> kanjiInfos,
                           List<UsageDistinction> usageDistinctions,
                           List<GrammarPoint> grammarPoints,
                           List<Idiom> idioms) {
        saveData(collocations, antonymWords, synonymWords, conjugationForms, etymologies, kanjiInfos, usageDistinctions, grammarPoints, idioms);
        rebuild();
    }

    private void rebuild() {
        removeAllViews();
        if (isScrollMode) {
            buildScrollMode();
        } else {
            buildTabMode();
        }
    }

    private void buildTabMode() {
        addView(host);
        addView(tabLayout);
        update();
    }

    private void buildScrollMode() {
        Context context = getContext();
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(VERTICAL);

        List<Idiom> validIdioms = new ArrayList<>();
        for (Idiom idiom : idioms) {
            if (idiom != null) validIdioms.add(idiom);
        }

        LifecycleOwner lifecycleOwner = (LifecycleOwner) context;
        if (!collocations.isEmpty()) {
            addSection(container, "词组", new CollocationView(context, lifecycleOwner, subFontLevel, collocations));
        }
        if (!synonymWords.isEmpty() || !antonymWords.isEmpty()) {
            View synView = new SynonymAntonymView(context, lifecycleOwner, subFontLevel, synonymWords, antonymWords);
            synView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            container.addView(synView);
            addDivider(container);
        }
        if (!conjugationForms.isEmpty()) {
            addSection(container, "活用形", new ConjugationFormView(context, lifecycleOwner, subFontLevel, conjugationForms));
        }
        if (!kanjiInfos.isEmpty()) {
            addSection(container, "漢字", new KanjiInfoView(context, lifecycleOwner, subFontLevel, kanjiInfos));
        }
        boolean hasExtendedInfo = !etymologies.isEmpty() || !usageDistinctions.isEmpty()
                || !grammarPoints.isEmpty() || !validIdioms.isEmpty();
        if (hasExtendedInfo) {
            addSection(container, "其他", new ExtendedInfoView(context, lifecycleOwner, subFontLevel, etymologies, usageDistinctions, grammarPoints, validIdioms));
        }

        scrollView.addView(container);
        addView(scrollView);
    }

    private void addSection(LinearLayout container, String title, View content) {
        Context context = getContext();
        float density = context.getResources().getDisplayMetrics().density;

        TextView titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setTextColor(context.getResources().getColor(R.color.md_detail_label));
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = (int) (Constants.SECTION_TITLE_BOTTOM_MARGIN_DP * density);
        titleView.setLayoutParams(titleParams);
        container.addView(titleView);

        content.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        container.addView(content);

        addDivider(container);
    }

    private void addDivider(LinearLayout container) {
        Context context = getContext();
        float density = context.getResources().getDisplayMetrics().density;
        View divider = new View(context);
        divider.setBackgroundColor(context.getResources().getColor(R.color.md_outline_variant));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (Constants.SECTION_DIVIDER_HEIGHT_DP * density));
        dividerParams.topMargin = (int) (8 * density);
        divider.setLayoutParams(dividerParams);
        container.addView(divider);
    }

    private void update() {
        if (mediator != null) {
            mediator.detach();
        }
        fragmentList = new ArrayList<>();
        List<String> tabTitles = new ArrayList<>();

        List<Idiom> validIdioms = new ArrayList<>();
        for (Idiom idiom : idioms) {
            if (idiom != null) validIdioms.add(idiom);
        }

        if (!collocations.isEmpty()) {
            tabTitles.add("词组");
            fragmentList.add(new CollocationViewFragment(collocations));
        }
        if (!synonymWords.isEmpty() || !antonymWords.isEmpty()) {
            tabTitles.add("近义词");
            fragmentList.add(new SynonymAntonymViewFragment(synonymWords, antonymWords));
        }
        if (!conjugationForms.isEmpty()) {
            tabTitles.add("活用形");
            fragmentList.add(new ConjugationFormViewFragment(conjugationForms));
        }
        if (!kanjiInfos.isEmpty()) {
            tabTitles.add("漢字");
            fragmentList.add(new KanjiInfoViewFragment(kanjiInfos));
        }
        boolean hasExtendedInfo = !etymologies.isEmpty() || !usageDistinctions.isEmpty()
                || !grammarPoints.isEmpty() || !validIdioms.isEmpty();
        if (hasExtendedInfo) {
            tabTitles.add("其他");
            fragmentList.add(new ExtendedInfoViewFragment(etymologies, usageDistinctions, grammarPoints, validIdioms));
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
                    Constants.TAB_MIN_WIDTH_DP,
                    getResources().getDisplayMetrics()
            );
            tabView.setMinimumWidth(minWidth);
        });
        mediator.attach();
    }
}
