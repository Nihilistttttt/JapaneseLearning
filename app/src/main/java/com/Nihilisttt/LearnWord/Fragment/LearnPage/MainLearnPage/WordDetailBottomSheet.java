package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;
import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.JavaBean.ConjugationForm;
import com.Nihilisttt.LearnWord.JavaBean.Etymology;
import com.Nihilisttt.LearnWord.JavaBean.GrammarPoint;
import com.Nihilisttt.LearnWord.JavaBean.Idiom;
import com.Nihilisttt.LearnWord.JavaBean.KanjiInfo;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.JavaBean.UsageDistinction;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween2Layers;
import com.Nihilisttt.LearnWord.WordView.BasicWordView;
import com.Nihilisttt.LearnWord.WordView.MeaningView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordDetailBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "WordDetailBS";
    private String wordId;

    public WordDetailBottomSheet() {}

    public WordDetailBottomSheet(String wordId) {
        this.wordId = wordId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setDimAmount(0.3f);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        View bottomSheet = requireDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            BottomSheetBehavior<?> behavior = (BottomSheetBehavior<?>) params.getBehavior();
            if (behavior != null) {
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                behavior.setPeekHeight((int) (screenHeight * 0.9));
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                Log.d(TAG, "onStart: peekH=" + behavior.getPeekHeight() + " screenHeight=" + screenHeight);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setFillViewport(true);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, (int) (8 * getResources().getDisplayMetrics().density), 0, 0);
        int minH = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
        root.setMinimumHeight(minH);

        LinearLayout headerPart = new LinearLayout(requireContext());
        headerPart.setOrientation(LinearLayout.VERTICAL);
        int hPadding = (int) (16 * getResources().getDisplayMetrics().density);
        headerPart.setPadding(hPadding, 0, hPadding, 0);

        LinearLayout tabPart = new LinearLayout(requireContext());
        tabPart.setOrientation(LinearLayout.VERTICAL);
        int tabPaddingH = (int) (20 * getResources().getDisplayMetrics().density);
        int tabPaddingV = (int) (8 * getResources().getDisplayMetrics().density);
        tabPart.setPadding(tabPaddingH, tabPaddingV, tabPaddingH, tabPaddingV);

        root.addView(headerPart);
        root.addView(tabPart);

        scrollView.addView(root);
        loadAndRender(headerPart, tabPart);

        scrollView.post(() -> {
            Log.d(TAG, "onCreateView post: scrollView w=" + scrollView.getWidth() + " h=" + scrollView.getHeight()
                    + " root w=" + root.getWidth() + " h=" + root.getHeight()
                    + " header h=" + headerPart.getHeight() + " tab h=" + tabPart.getHeight());
            View bs = requireDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bs != null) {
                Log.d(TAG, "onCreateView post: bottomSheet w=" + bs.getWidth() + " h=" + bs.getHeight());
                CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) bs.getLayoutParams();
                BottomSheetBehavior<?> b = (BottomSheetBehavior<?>) p.getBehavior();
                Log.d(TAG, "onCreateView post: behavior state=" + b.getState() + " peekH=" + b.getPeekHeight());
            }
        });

        return scrollView;
    }

    private void loadAndRender(LinearLayout headerPart, LinearLayout tabPart) {
        Context context = requireContext();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        LearnPageStateViewModel stateViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageStateViewModel.class);
        Integer subFontLevelRaw = stateViewModel.getSubFontLevel().getValue();
        final int subFontLevel = subFontLevelRaw != null ? subFontLevelRaw : Constants.FONT_SIZE_NORMAL;
        Integer wordLevelRaw = stateViewModel.getWordFontLevel().getValue();
        final int wordLevel = wordLevelRaw != null ? wordLevelRaw : Constants.FONT_SIZE_NORMAL;

        WordRepository repository = WordRepository.getInstance(context);

        LiveData<BasicWord> basicWordLiveData = repository.getBasicWordById(wordId);
        LiveData<List<WordMeaning>> meaningsLiveData = repository.getWordMeaningsByWordId(wordId);
        LiveData<Word> wordLiveData = repository.getWordById(wordId);

        LiveData<List<WordSentence>> sentencesLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getWordSentencesBySentencesIdList(word.getSentenceIdList()) : new androidx.lifecycle.MutableLiveData<>());
        LiveData<List<WordCollocation>> collocationsLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getWordCollocationsByWordCollocationIdList(word.getCollocationIdList()) : new androidx.lifecycle.MutableLiveData<>());
        LiveData<List<AntonymWord>> antonymsLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getAntonymWordsByAntonymWordsIdList(word.getAntonymIdList()) : new androidx.lifecycle.MutableLiveData<>());
        LiveData<List<SynonymWord>> synonymsLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getSynonymWordsBySynonymWordsIdList(word.getSynonymIdList()) : new androidx.lifecycle.MutableLiveData<>());
        LiveData<List<ConjugationForm>> conjugationsLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getConjugationFormsByConjugationFormIdList(word.getConjugationFormIdList()) : new androidx.lifecycle.MutableLiveData<>());
        LiveData<List<Etymology>> etymologiesLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getEtymologiesByEtymologyIdList(word.getEtymologyIdList()) : new androidx.lifecycle.MutableLiveData<>());
        LiveData<List<KanjiInfo>> kanjiInfosLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getKanjiInfosByKanjiInfoIdList(word.getKanjiInfoIdList()) : new androidx.lifecycle.MutableLiveData<>());
        LiveData<List<UsageDistinction>> usageDistinctionsLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getUsageDistinctionsByUsageDistinctionIdList(word.getUsageDistinctionIdList()) : new androidx.lifecycle.MutableLiveData<>());
        LiveData<List<GrammarPoint>> grammarPointsLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getGrammarPointsByGrammarPointIdList(word.getGrammarPointIdList()) : new androidx.lifecycle.MutableLiveData<>());
        LiveData<List<Idiom>> idiomsLiveData = Transformations.switchMap(wordLiveData,
                word -> word != null ? repository.getIdiomsByIdiomIdList(word.getIdiomIdList()) : new androidx.lifecycle.MutableLiveData<>());

        final boolean[] headerRendered = {false};
        final boolean[] tabsBuilt = {false};
        final boolean[] wordReady = {false};

        final Observer<BasicWord> basicObserver = basicWord -> {
            if (basicWord == null || basicWord.getWordId().equals("null")) return;
            Log.d(TAG, "basicObserver: wordId=" + basicWord.getWordId());
            renderHeader(headerPart, basicWord, wordLevel, subFontLevel, lifecycleOwner, context);
            headerRendered[0] = true;
        };

        final Observer<List<WordMeaning>> meaningsObserver = meanings -> {
            if (meanings == null || meanings.isEmpty()) return;
            if (!headerRendered[0]) return;
            renderMeanings(headerPart, subFontLevel, meanings, lifecycleOwner, context);
        };

        final Observer<Word> wordObserver = word -> {
            if (word == null) return;
            Log.d(TAG, "wordObserver: wordId=" + word.getWordId()
                    + " sentenceIds=" + (word.getSentenceIdList() != null ? word.getSentenceIdList().size() : "null")
                    + " collocationIds=" + (word.getCollocationIdList() != null ? word.getCollocationIdList().size() : "null"));
            wordReady[0] = true;
        };

        final Runnable tryBuildTabs = () -> {
            if (tabsBuilt[0] || !headerRendered[0] || !wordReady[0]) return;
            List<WordSentence> sv = sentencesLiveData.getValue();
            List<WordCollocation> cv = collocationsLiveData.getValue();
            Log.d(TAG, "tryBuildTabs: sentences=" + (sv != null ? sv.size() : "null")
                    + " collocations=" + (cv != null ? cv.size() : "null"));
            if (sv == null || cv == null) return;
            buildTabs(tabPart, subFontLevel, sentencesLiveData, collocationsLiveData,
                    antonymsLiveData, synonymsLiveData, conjugationsLiveData,
                    etymologiesLiveData, kanjiInfosLiveData, usageDistinctionsLiveData,
                    grammarPointsLiveData, idiomsLiveData, context, lifecycleOwner);
            tabsBuilt[0] = true;
        };

        basicWordLiveData.observe(lifecycleOwner, basicObserver);
        meaningsLiveData.observe(lifecycleOwner, meaningsObserver);
        wordLiveData.observe(lifecycleOwner, wordObserver);
        sentencesLiveData.observe(lifecycleOwner, s -> tryBuildTabs.run());
        collocationsLiveData.observe(lifecycleOwner, c -> tryBuildTabs.run());
    }

    private void renderHeader(LinearLayout headerPart, BasicWord basicWord, int wordLevel, int subFontLevel,
                              LifecycleOwner lifecycleOwner, Context context) {
        headerPart.removeAllViews();

        BasicWordView basicWordView = new BasicWordView(context, lifecycleOwner, wordLevel, basicWord);
        headerPart.addView(basicWordView);
        basicWordView.setVisibility(View.INVISIBLE);
        basicWordView.post(() -> {
            int parentWidth = headerPart.getWidth();
            int viewWidth = basicWordView.getWidth();
            int accentWidth = basicWordView.getAccentMarkWidth();
            int wordOnlyWidth = viewWidth - accentWidth;
            int contentWidth = parentWidth - headerPart.getPaddingLeft() - headerPart.getPaddingRight();
            int marginStart = Math.max(0, (contentWidth - wordOnlyWidth) / 2);
            Log.d(TAG, "center: parentW=" + parentWidth + " contentW=" + contentWidth + " viewW=" + viewWidth
                    + " accentW=" + accentWidth + " wordOnlyW=" + wordOnlyWidth
                    + " margin=" + marginStart);
            if (parentWidth <= 0) { basicWordView.setVisibility(View.VISIBLE); return; }
            if (viewWidth <= 0) { basicWordView.setVisibility(View.VISIBLE); return; }
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) basicWordView.getLayoutParams();
            mlp.setMarginStart(marginStart);
            basicWordView.setLayoutParams(mlp);
            basicWordView.setVisibility(View.VISIBLE);
        });

        int jlptLevel = basicWord.getJlptLevel();
        int wordFrequency = basicWord.getWordFrequency();
        if (jlptLevel > 0 || wordFrequency > 0) {
            LinearLayout infoRow = new LinearLayout(context);
            infoRow.setOrientation(LinearLayout.HORIZONTAL);
            infoRow.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            int primaryColor = context.getResources().getColor(R.color.md_primary);
            int variantColor = context.getResources().getColor(R.color.md_on_surface_variant);
            if (jlptLevel > 0) {
                int start = ssb.length();
                ssb.append("N" + jlptLevel);
                ssb.setSpan(new ForegroundColorSpan(primaryColor), start, ssb.length(), 0);
            }
            if (jlptLevel > 0 && wordFrequency > 0) {
                int start = ssb.length();
                ssb.append(" · ");
                ssb.setSpan(new ForegroundColorSpan(variantColor), start, ssb.length(), 0);
            }
            if (wordFrequency > 0) {
                int start = ssb.length();
                ssb.append(String.valueOf(wordFrequency));
                ssb.setSpan(new ForegroundColorSpan(variantColor), start, ssb.length(), 0);
            }
            TextView infoText = new TextView(context);
            infoText.setText(ssb);
            infoText.setTextSize(13);
            infoRow.addView(infoText);
            headerPart.addView(infoRow);
        }

    }

    private void renderMeanings(LinearLayout headerPart, int subFontLevel,
                                List<WordMeaning> meanings, LifecycleOwner lifecycleOwner, Context context) {
        for (int i = 0; i < headerPart.getChildCount(); i++) {
            if (headerPart.getChildAt(i) instanceof MeaningView) return;
        }
        MeaningView meaningView = new MeaningView(context, lifecycleOwner, subFontLevel, meanings, Constants.SHOW_SENTENCE_POPUP);
        int mBottom = (int) (8 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mp.setMargins(0, 0, 0, mBottom);
        meaningView.setLayoutParams(mp);
        headerPart.addView(meaningView);
    }

    private void buildTabs(LinearLayout tabPart, int subFontLevel,
                           LiveData<List<WordSentence>> sentencesLiveData,
                           LiveData<List<WordCollocation>> collocationsLiveData,
                           LiveData<List<AntonymWord>> antonymsLiveData,
                           LiveData<List<SynonymWord>> synonymsLiveData,
                           LiveData<List<ConjugationForm>> conjugationsLiveData,
                           LiveData<List<Etymology>> etymologiesLiveData,
                           LiveData<List<KanjiInfo>> kanjiInfosLiveData,
                           LiveData<List<UsageDistinction>> usageDistinctionsLiveData,
                           LiveData<List<GrammarPoint>> grammarPointsLiveData,
                           LiveData<List<Idiom>> idiomsLiveData,
                           Context context, LifecycleOwner lifecycleOwner) {
        if (tabPart.getChildCount() > 0) return;

        List<WordSentence> sentences = sentencesLiveData.getValue() != null ? sentencesLiveData.getValue() : Collections.emptyList();
        List<WordCollocation> collocations = collocationsLiveData.getValue() != null ? collocationsLiveData.getValue() : Collections.emptyList();
        List<AntonymWord> antonyms = antonymsLiveData.getValue() != null ? antonymsLiveData.getValue() : Collections.emptyList();
        List<SynonymWord> synonyms = synonymsLiveData.getValue() != null ? synonymsLiveData.getValue() : Collections.emptyList();
        List<ConjugationForm> conjugations = conjugationsLiveData.getValue() != null ? conjugationsLiveData.getValue() : Collections.emptyList();
        List<Etymology> etymologies = etymologiesLiveData.getValue() != null ? etymologiesLiveData.getValue() : Collections.emptyList();
        List<KanjiInfo> kanjiInfos = kanjiInfosLiveData.getValue() != null ? kanjiInfosLiveData.getValue() : Collections.emptyList();
        List<UsageDistinction> usageDistinctions = usageDistinctionsLiveData.getValue() != null ? usageDistinctionsLiveData.getValue() : Collections.emptyList();
        List<GrammarPoint> grammarPoints = grammarPointsLiveData.getValue() != null ? grammarPointsLiveData.getValue() : Collections.emptyList();
        List<Idiom> idioms = idiomsLiveData.getValue() != null ? idiomsLiveData.getValue() : Collections.emptyList();

        List<Idiom> validIdioms = new ArrayList<>();
        for (Idiom idiom : idioms) {
            if (idiom != null) validIdioms.add(idiom);
        }

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> tabTitles = new ArrayList<>();

        if (!sentences.isEmpty()) {
            tabTitles.add("例句");
            fragmentList.add(new SentenceViewFragment(sentences));
        }
        if (!collocations.isEmpty()) {
            tabTitles.add("词组");
            fragmentList.add(new CollocationViewFragment(collocations));
        }
        if (!synonyms.isEmpty() || !antonyms.isEmpty()) {
            tabTitles.add("近义词");
            fragmentList.add(new SynonymAntonymViewFragment(synonyms, antonyms));
        }
        if (!conjugations.isEmpty()) {
            tabTitles.add("活用形");
            fragmentList.add(new ConjugationFormViewFragment(conjugations));
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

        if (fragmentList.isEmpty()) {
            Log.d(TAG, "buildTabs: no fragments to show");
            return;
        }

        Log.d(TAG, "buildTabs: tabs=" + tabTitles + " sentences=" + sentences.size()
                + " collocations=" + collocations.size() + " antonyms=" + antonyms.size()
                + " synonyms=" + synonyms.size() + " conjugations=" + conjugations.size()
                + " kanjiInfos=" + kanjiInfos.size() + " etymologies=" + etymologies.size()
                + " usageDist=" + usageDistinctions.size() + " grammar=" + grammarPoints.size()
                + " idioms=" + validIdioms.size());

        TabLayout tabLayout = new TabLayout(context);
        tabLayout.setTabGravity(TabLayout.GRAVITY_START);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        NestedScrollableHostBetween2Layers host = new NestedScrollableHostBetween2Layers(context);
        host.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ViewPager2 viewPager2 = new ViewPager2(context);
        View childAt = viewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        host.addView(viewPager2);
        tabPart.addView(tabLayout);
        tabPart.addView(host);

        FragmentStateAdapter adapter = new FragmentStateAdapter(getChildFragmentManager(), getViewLifecycleOwner().getLifecycle()) {
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

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(tabTitles.get(position));
        });
        mediator.attach();
    }
}
