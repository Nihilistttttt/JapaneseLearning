package com.Nihilisttt.LearnWord.Fragment.SearchPage;

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

import com.Nihilisttt.LearnWord.Adapter.SearchPageAdapter;
import com.Nihilisttt.LearnWord.Fragment.LearnPage.ExtendedLearnPage.ExtendedSentenceFragment;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.ViewPager2.ViewPager2Navigation;

import java.util.ArrayList;
import java.util.List;

public class SearchPageFragment extends Fragment {
    private ViewPager2 viewPager2;
    private LearnPageViewModel viewModel;
    private LearnPageStateViewModel stateViewModel;
    private WordMeaning meaning;
    private List<WordSentence> sentenceList;
    private List<Fragment> fragmentList;

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser()) { // 确保真正可见
            stateViewModel.setWhichFragmentInLearnPage(
                    LearnPageStateViewModel.FragmentInLearnPage.SearchFragment
            );
        }
    }

    private boolean isVisibleToUser() {
        return getUserVisibleHint() || isVisible();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 使用新的布局文件名
        return inflater.inflate(R.layout.fragment_search_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 初始化ViewModel
        viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageStateViewModel.class);
        // 初始化视图组件
        initViews(view);

        // 设置数据观察
        setupObservers();
    }

    private void initViews(View view) {
        viewPager2 = view.findViewById(R.id.detailed_page_container);
        View childAt = viewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        } // 取消滑动到边缘的阴影效果
    }


    private void setupObservers() {
        viewModel.getCombinedWordInfo().observe(getViewLifecycleOwner(), combinedWordInfo -> {
            List<WordSentence> sentences = combinedWordInfo.getWordSentenceList();
            fragmentList = new ArrayList<>();
            for (WordSentence sentence : sentences) {
                fragmentList.add(new ExtendedSentenceFragment(sentence));
            }

            // 创建ViewPager2所使用的适配器，FragmentStateAdapter抽象类的实现类对象
            SearchPageAdapter adapter = new SearchPageAdapter(requireActivity());

            viewPager2.setAdapter(adapter);
            ViewPager2Navigation.getInstance().setMeaningVp2(viewPager2);
            ViewPager2Navigation.getInstance().onMeaningVp2Ready();

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
    }
}
