package com.Nihilisttt.LearnWord.Fragment.LearnPage.ExtendedLearnPage;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween3Layers;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween3LayersManager;
import com.Nihilisttt.LearnWord.ViewPager2.Vp2IndicatorView;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;

public class ExtendedMeaningViewFragment extends Fragment {

    private static final int MAX_SENTENCES_PER_PAGE = 6;

    private WordMeaning meaning;
    private List<WordSentence> sentenceList;
    private List<Fragment> fragmentList;

    public ExtendedMeaningViewFragment() {
    }

    public ExtendedMeaningViewFragment(WordMeaning meaning, List<WordSentence> sentenceList) {
        this.meaning = meaning;
        this.sentenceList = sentenceList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_extended_meaning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentList = new ArrayList<>();
        int limit = Math.min(sentenceList.size(), MAX_SENTENCES_PER_PAGE);
        for (int i = 0; i < limit; i++) {
            fragmentList.add(new ExtendedSentenceFragment(sentenceList.get(i)));
        }
        ViewPager2 viewPager2 = view.findViewById(R.id.detailed_meaning_viewpager);
        View childAt = viewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView){
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        NestedScrollableHostBetween3LayersManager nestedScrollableHostBetween3LayersManager = NestedScrollableHostBetween3LayersManager.getInstance();
        NestedScrollableHostBetween3Layers nestedScrollableHost=view.findViewById(R.id.detailed_meaning_vp2_container);
        nestedScrollableHostBetween3LayersManager.setInner(nestedScrollableHost);
        nestedScrollableHostBetween3LayersManager.setInnerViewPage2Id(viewPager2.getId());
        FragmentStateAdapter adapter = new FragmentStateAdapter((FragmentActivity) requireContext()) {
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

        Vp2IndicatorView vp2IndicatorView = view.findViewById(R.id.vp2_indicator);
        vp2IndicatorView.attachToViewPager2(viewPager2);

        LearnPageStateViewModel stateViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageStateViewModel.class);
        Integer subFontLevel = stateViewModel.getSubFontLevel().getValue();
        if (subFontLevel == null) subFontLevel = Constants.FONT_SIZE_NORMAL;
        float definitionSize = Constants.getSubDefinitionSize(subFontLevel);

        TextView pos = view.findViewById(R.id.part_of_speech);
        pos.setText(meaning.getPartOfSpeech().getAbbreviation());
        pos.setTextSize(TypedValue.COMPLEX_UNIT_SP, definitionSize);

        TextView originalDefinition = view.findViewById(R.id.original_definition);
        originalDefinition.setText(meaning.getOriginalDefinition());
        originalDefinition.setTextSize(TypedValue.COMPLEX_UNIT_SP, definitionSize);

        TextView translationDefinition = view.findViewById(R.id.translation_definition);
        translationDefinition.setText(meaning.getTranslationDefinition());
        translationDefinition.setTextSize(TypedValue.COMPLEX_UNIT_SP, definitionSize);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NestedScrollableHostBetween3LayersManager.getInstance().setInner(null);
    }
}
