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
import androidx.cardview.widget.CardView;
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
import com.Nihilisttt.LearnWord.WordView.CollocationView;
import com.Nihilisttt.LearnWord.WordView.ConjugationFormView;
import com.Nihilisttt.LearnWord.WordView.ExtendedInfoView;
import com.Nihilisttt.LearnWord.WordView.KanjiInfoView;
import com.Nihilisttt.LearnWord.WordView.MeaningView;
import com.Nihilisttt.LearnWord.WordView.SentenceView;
import com.Nihilisttt.LearnWord.WordView.SynonymAntonymView;
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
                int statusBarH = 0;
                int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resId > 0) statusBarH = getResources().getDimensionPixelSize(resId);
                int toolbarH = (int) (Constants.TOOLBAR_HEIGHT_DP * getResources().getDisplayMetrics().density);
                int peekH = screenHeight - statusBarH - toolbarH;
                behavior.setPeekHeight(peekH);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        float density = getResources().getDisplayMetrics().density;

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        int minH = (int) (getResources().getDisplayMetrics().heightPixels * Constants.BOTTOM_SHEET_HEIGHT_RATIO);
        root.setMinimumHeight(minH);
        int rootPad = (int) (Constants.ROOT_TOP_PADDING_DP * density);
        root.setPadding(0, rootPad, 0, 0);

        LinearLayout headerPart = new LinearLayout(context);
        headerPart.setOrientation(LinearLayout.VERTICAL);
        int hPad = (int) (Constants.HEADER_PADDING_H_DP * density);
        headerPart.setPadding(hPad, 0, hPad, (int) (Constants.HEADER_PADDING_BOTTOM_DP * density));

        CardView tabCard = new CardView(context);
        tabCard.setRadius(Constants.CARD_RADIUS_DP * density);
        tabCard.setCardElevation(Constants.CARD_ELEVATION_DP * density);
        tabCard.setCardBackgroundColor(context.getResources().getColor(R.color.md_card_background));
        int contentPad = (int) (Constants.CARD_CONTENT_PADDING_DP * density);
        tabCard.setContentPadding(contentPad, contentPad, contentPad, contentPad);
        LinearLayout.LayoutParams tabCardLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        int cardMargin = (int) (Constants.CARD_MARGIN_DP * density);
        tabCardLp.setMargins(cardMargin, (int) (Constants.ROOT_TOP_PADDING_DP * density), cardMargin, cardMargin);
        tabCard.setLayoutParams(tabCardLp);

        LinearLayout tabPart = new LinearLayout(context);
        tabPart.setOrientation(LinearLayout.VERTICAL);
        tabCard.addView(tabPart);

        root.addView(headerPart);
        root.addView(tabCard);

        loadAndRender(headerPart, tabPart);
        return root;
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
            renderHeader(headerPart, basicWord, wordLevel, subFontLevel, lifecycleOwner, context, stateViewModel);
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
            if (sentencesLiveData.getValue() == null || collocationsLiveData.getValue() == null
                    || antonymsLiveData.getValue() == null || synonymsLiveData.getValue() == null
                    || conjugationsLiveData.getValue() == null || etymologiesLiveData.getValue() == null
                    || kanjiInfosLiveData.getValue() == null || usageDistinctionsLiveData.getValue() == null
                    || grammarPointsLiveData.getValue() == null || idiomsLiveData.getValue() == null) return;
            Log.d(TAG, "tryBuildTabs: all data ready");
            Boolean isScroll = stateViewModel.getIsScrollMode().getValue();
            buildContent(tabPart, subFontLevel, isScroll != null && isScroll, sentencesLiveData, collocationsLiveData,
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
        antonymsLiveData.observe(lifecycleOwner, a -> tryBuildTabs.run());
        synonymsLiveData.observe(lifecycleOwner, s -> tryBuildTabs.run());
        conjugationsLiveData.observe(lifecycleOwner, c -> tryBuildTabs.run());
        etymologiesLiveData.observe(lifecycleOwner, e -> tryBuildTabs.run());
        kanjiInfosLiveData.observe(lifecycleOwner, k -> tryBuildTabs.run());
        usageDistinctionsLiveData.observe(lifecycleOwner, u -> tryBuildTabs.run());
        grammarPointsLiveData.observe(lifecycleOwner, g -> tryBuildTabs.run());
        idiomsLiveData.observe(lifecycleOwner, i -> tryBuildTabs.run());

        stateViewModel.getIsScrollMode().observe(lifecycleOwner, isScroll -> {
            if (!tabsBuilt[0]) return;
            tabPart.removeAllViews();
            buildContent(tabPart, subFontLevel, isScroll, sentencesLiveData, collocationsLiveData,
                    antonymsLiveData, synonymsLiveData, conjugationsLiveData,
                    etymologiesLiveData, kanjiInfosLiveData, usageDistinctionsLiveData,
                    grammarPointsLiveData, idiomsLiveData, context, lifecycleOwner);
        });
    }

    private void renderHeader(LinearLayout headerPart, BasicWord basicWord, int wordLevel, int subFontLevel,
                              LifecycleOwner lifecycleOwner, Context context, LearnPageStateViewModel stateViewModel) {
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

        android.widget.ImageButton scrollToggle = new android.widget.ImageButton(context);
        scrollToggle.setImageResource(R.drawable.ic_scroll_mode);
        scrollToggle.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        int btnSize = (int) (32 * context.getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams toggleLp = new LinearLayout.LayoutParams(btnSize, btnSize);
        toggleLp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        toggleLp.topMargin = (int) (4 * context.getResources().getDisplayMetrics().density);
        scrollToggle.setLayoutParams(toggleLp);
        scrollToggle.setOnClickListener(v -> {
            Boolean current = stateViewModel.getIsScrollMode().getValue();
            stateViewModel.setScrollMode(current == null || !current);
        });
        headerPart.addView(scrollToggle);

    }

    private void renderMeanings(LinearLayout headerPart, int subFontLevel,
                                List<WordMeaning> meanings, LifecycleOwner lifecycleOwner, Context context) {
        for (int i = 0; i < headerPart.getChildCount(); i++) {
            if (headerPart.getChildAt(i) instanceof MeaningView) return;
        }
        MeaningView meaningView = new MeaningView(context, lifecycleOwner, subFontLevel, meanings, Constants.SHOW_SENTENCE_POPUP);
        int mBottom = (int) (Constants.MEANING_BOTTOM_MARGIN_DP * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mp.setMargins(0, 0, 0, mBottom);
        meaningView.setLayoutParams(mp);
        headerPart.addView(meaningView);
    }

    private void buildContent(LinearLayout tabPart, int subFontLevel, boolean isScrollMode,
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
            Log.d(TAG, "buildContent: no fragments to show");
            return;
        }

        Log.d(TAG, "buildContent: tabs=" + tabTitles + " isScroll=" + isScrollMode);

        if (isScrollMode) {
            buildScrollContent(tabPart, subFontLevel, sentences, antonyms, synonyms, conjugations,
                    etymologies, kanjiInfos, usageDistinctions, grammarPoints, validIdioms, context, lifecycleOwner);
            return;
        }

        TabLayout tabLayout = new TabLayout(context);
        tabLayout.setTabGravity(TabLayout.GRAVITY_START);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        NestedScrollableHostBetween2Layers host = new NestedScrollableHostBetween2Layers(context);
        host.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
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

        int tabMinWidth = (int) (Constants.TAB_MIN_WIDTH_DP * getResources().getDisplayMetrics().density);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.view != null) {
                tab.view.setMinimumWidth(tabMinWidth);
            }
        }
    }

    private void buildScrollContent(LinearLayout tabPart, int subFontLevel,
                                    List<WordSentence> sentences, List<AntonymWord> antonyms, List<SynonymWord> synonyms,
                                    List<ConjugationForm> conjugations, List<Etymology> etymologies, List<KanjiInfo> kanjiInfos,
                                    List<UsageDistinction> usageDistinctions, List<GrammarPoint> grammarPoints, List<Idiom> validIdioms,
                                    Context context, LifecycleOwner lifecycleOwner) {
        float density = context.getResources().getDisplayMetrics().density;

        android.widget.ScrollView scrollView = new android.widget.ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);

        if (!sentences.isEmpty()) {
            addScrollSection(container, "例句", new SentenceView(context, lifecycleOwner, subFontLevel, sentences), context, density);
        }
        if (!synonyms.isEmpty() || !antonyms.isEmpty()) {
            View synView = new SynonymAntonymView(context, lifecycleOwner, subFontLevel, synonyms, antonyms);
            synView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            container.addView(synView);
            addScrollDivider(container, context, density);
        }
        if (!conjugations.isEmpty()) {
            addScrollSection(container, "活用形", new ConjugationFormView(context, lifecycleOwner, subFontLevel, conjugations), context, density);
        }
        if (!kanjiInfos.isEmpty()) {
            addScrollSection(container, "漢字", new KanjiInfoView(context, lifecycleOwner, subFontLevel, kanjiInfos), context, density);
        }
        boolean hasExtendedInfo = !etymologies.isEmpty() || !usageDistinctions.isEmpty()
                || !grammarPoints.isEmpty() || !validIdioms.isEmpty();
        if (hasExtendedInfo) {
            addScrollSection(container, "其他", new ExtendedInfoView(context, lifecycleOwner, subFontLevel, etymologies, usageDistinctions, grammarPoints, validIdioms), context, density);
        }

        scrollView.addView(container);
        tabPart.addView(scrollView);
    }

    private void addScrollSection(LinearLayout container, String title, View content, Context context, float density) {
        TextView titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        titleView.setTextColor(context.getResources().getColor(R.color.md_detail_label));
        titleView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = (int) (Constants.SECTION_TITLE_BOTTOM_MARGIN_DP * density);
        titleView.setLayoutParams(titleParams);
        container.addView(titleView);

        content.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        container.addView(content);

        addScrollDivider(container, context, density);
    }

    private void addScrollDivider(LinearLayout container, Context context, float density) {
        View divider = new View(context);
        divider.setBackgroundColor(context.getResources().getColor(R.color.md_outline_variant));
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (Constants.SECTION_DIVIDER_HEIGHT_DP * density));
        dividerParams.topMargin = (int) (8 * density);
        divider.setLayoutParams(dividerParams);
        container.addView(divider);
    }
}
