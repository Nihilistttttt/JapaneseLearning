package com.Nihilisttt.LearnWord.Fragment.LearnPage.ExtendedLearnPage;

import android.os.Bundle;
import android.util.Log;
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
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween3Layers;
import com.Nihilisttt.LearnWord.ViewPager2.NestedScrollableHostBetween3LayersManager;
import com.Nihilisttt.LearnWord.ViewPager2.Vp2IndicatorView;

import java.util.ArrayList;
import java.util.List;

public class ExtendedMeaningViewFragment extends Fragment {

    private WordMeaning meaning;
    private List<WordSentence> sentenceList;
    private List<Fragment> fragmentList;

    public ExtendedMeaningViewFragment() {
        // Required empty public constructor
    }

    public ExtendedMeaningViewFragment(WordMeaning meaning, List<WordSentence> sentenceList) {
        this.meaning = meaning;
        this.sentenceList = sentenceList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 仅负责创建视图
        return inflater.inflate(R.layout.fragment_extended_meaning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentList = new ArrayList<>();
        for (WordSentence sentence : sentenceList) {
            Log.d("666666666", "sentence: "+sentence);
            fragmentList.add(new ExtendedSentenceFragment(sentence));
        }
        ViewPager2 viewPager2 = view.findViewById(R.id.detailed_meaning_viewpager);
        View childAt = viewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView){
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        } // 取消滑动到边缘的阴影效果
        NestedScrollableHostBetween3LayersManager nestedScrollableHostBetween3LayersManager = NestedScrollableHostBetween3LayersManager.getInstance();
        NestedScrollableHostBetween3Layers nestedScrollableHost=view.findViewById(R.id.detailed_meaning_vp2_container);
        nestedScrollableHostBetween3LayersManager.setInner(nestedScrollableHost);
//        nestedScrollableHostBetween3LayersManager.setInnerViewPage2(viewPager2);
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
        viewPager2.setAdapter(adapter); // 给ViewPager2设置适配器

        Vp2IndicatorView vp2IndicatorView = view.findViewById(R.id.vp2_indicator);
        vp2IndicatorView.attachToViewPager2(viewPager2);

        // 词性
        TextView pos = view.findViewById(R.id.part_of_speech);
        pos.setText(String.valueOf(meaning.getPartOfSpeech()));


        // 日文翻译
        TextView originalDefinition = view.findViewById(R.id.original_definition);
        originalDefinition.setText(String.format("日: %s", meaning.getOriginalDefinition()));


        // 日文翻译
        TextView translationDefinition = view.findViewById(R.id.translation_definition);
        translationDefinition.setText(String.format("中: %s", meaning.getTranslationDefinition()));

    }


}