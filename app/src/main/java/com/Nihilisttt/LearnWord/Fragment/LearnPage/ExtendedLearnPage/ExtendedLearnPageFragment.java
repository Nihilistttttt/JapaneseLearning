package com.Nihilisttt.LearnWord.Fragment.LearnPage.ExtendedLearnPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.Adapter.ExtendedLearnPageAdapter;

import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween3Layers;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween3LayersManager;
import com.Nihilisttt.LearnWord.ViewPager2.ViewPager2Navigation;
import com.Nihilisttt.LearnWord.ViewPager2.Vp2IndicatorView;

import java.util.ArrayList;
import java.util.List;

public class ExtendedLearnPageFragment extends Fragment {
    private ViewPager2 viewPager2;
    private Vp2IndicatorView vp2IndicatorView;
    private List<Fragment> fragmentList;

    private LearnPageViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_extended_learn_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageViewModel.class);

        initViews(view);
        setupObservers();
    }

    private void initViews(View view) {
        viewPager2 = view.findViewById(R.id.detailed_learn_page_view_pager);
        View childAt = viewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView){
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        NestedScrollableHostBetween3LayersManager nestedScrollableHostBetween3LayersManager = NestedScrollableHostBetween3LayersManager.getInstance();
        NestedScrollableHostBetween3Layers nestedScrollableHost = view.findViewById(R.id.detailed_learn_page_vp2_container);
        nestedScrollableHostBetween3LayersManager.setMiddle(nestedScrollableHost);
        nestedScrollableHostBetween3LayersManager.setMiddleViewPage2(viewPager2);
        nestedScrollableHostBetween3LayersManager.setMiddleViewPage2Id(viewPager2.getId());
        vp2IndicatorView = view.findViewById(R.id.vp2_indicator);
    }

    private void setupObservers() {
        viewModel.getCombinedWordInfo().observe(getViewLifecycleOwner(), combinedWordInfo -> {
            List<WordMeaning> meanings = combinedWordInfo.getWordMeaningList();
            List<WordSentence> sentences = combinedWordInfo.getWordSentenceList();
            fragmentList = new ArrayList<>();

            // Pass 1: match sentences to meanings by wordMeaningId
            java.util.Set<String> matchedSentenceIds = new java.util.HashSet<>();
            java.util.Map<String, List<WordSentence>> meaningToSentences = new java.util.HashMap<>();

            for (WordMeaning meaning : meanings) {
                List<WordSentence> matched = new ArrayList<>();
                String meaningId = meaning.getWordMeaningId();
                if (meaningId != null && !meaningId.isEmpty()) {
                    for (WordSentence sentence : sentences) {
                        String sentMeaningId = sentence.getWordMeaningId();
                        if (meaningId.equals(sentMeaningId)) {
                            matched.add(sentence);
                            matchedSentenceIds.add(sentence.getWordSentenceId());
                        }
                    }
                }
                meaningToSentences.put(meaningId, matched);
            }

            // Pass 2: for meanings with no matched sentences, fallback to wordId matching
            // but exclude sentences already matched to other meanings
            List<WordSentence> unmatchedSentences = new ArrayList<>();
            for (WordSentence sentence : sentences) {
                if (!matchedSentenceIds.contains(sentence.getWordSentenceId())) {
                    unmatchedSentences.add(sentence);
                }
            }

            for (WordMeaning meaning : meanings) {
                String meaningId = meaning.getWordMeaningId();
                List<WordSentence> matched = meaningToSentences.get(meaningId);
                if (matched == null || matched.isEmpty()) {
                    // Fallback: assign all unmatched sentences for this wordId
                    matched = new ArrayList<>();
                    String meaningWordId = meaning.getWordId();
                    if (meaningWordId != null) {
                        for (WordSentence sentence : unmatchedSentences) {
                            if (meaningWordId.equals(sentence.getWordId())) {
                                matched.add(sentence);
                            }
                        }
                    }
                }
                fragmentList.add(new ExtendedMeaningViewFragment(meaning, matched));
            }

            ExtendedLearnPageAdapter adapter = new ExtendedLearnPageAdapter(requireActivity(), fragmentList);

            viewPager2.setAdapter(adapter);
            ViewPager2Navigation.getInstance().setMeaningVp2(viewPager2);
            ViewPager2Navigation.getInstance().onMeaningVp2Ready();

            vp2IndicatorView.attachToViewPager2(viewPager2);
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewPager2Navigation.getInstance().setMeaningVp2(null);
        ViewPager2Navigation.getInstance().clearPendingNavigation();
        NestedScrollableHostBetween3LayersManager.getInstance().setMiddle(null);
        NestedScrollableHostBetween3LayersManager.getInstance().setMiddleViewPage2(null);
        viewModel.getWordMeaningListLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.getWordCollocationListLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.getToastMessage().removeObservers(getViewLifecycleOwner());
    }
}
