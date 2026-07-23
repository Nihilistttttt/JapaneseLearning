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
    // 界面组件
    private ViewPager2 viewPager2;
    private Vp2IndicatorView vp2IndicatorView;
    private List<Fragment> fragmentList;

    // ViewModel
    private LearnPageViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_extended_learn_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 初始化ViewModel
        viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageViewModel.class);

        // 初始化视图组件
        initViews(view);

        // 设置数据观察
        setupObservers();
    }

    private void initViews(View view) {
        viewPager2 = view.findViewById(R.id.detailed_learn_page_view_pager);
        View childAt = viewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView){
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        } // 取消滑动到边缘的阴影效果
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
            for (WordMeaning meaning : meanings) {
                List<WordSentence> correspondingSentenceList = new ArrayList<>();
                for (WordSentence sentence : sentences) {
                    if (meaning.getWordMeaningId().equals(sentence.getWordMeaningId()))
                        correspondingSentenceList.add(sentence);
                }
                fragmentList.add(new ExtendedMeaningViewFragment(meaning, correspondingSentenceList));
            }

            // 创建ViewPager2所使用的适配器，FragmentStateAdapter抽象类的实现类对象
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
        viewModel.getWordMeaningListLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.getWordCollocationListLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.getToastMessage().removeObservers(getViewLifecycleOwner());
    }
}
