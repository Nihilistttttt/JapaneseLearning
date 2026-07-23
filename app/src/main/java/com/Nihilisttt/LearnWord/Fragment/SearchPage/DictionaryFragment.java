package com.Nihilisttt.LearnWord.Fragment.SearchPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.BasicWordView;


public class DictionaryFragment extends Fragment {
    private LearnPageViewModel viewModel;
    private LinearLayout basicWordContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 使用新的布局文件名
        return inflater.inflate(R.layout.fragment_dictionary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // 初始化ViewModel
        viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageViewModel.class);
        initViews(view);

        // 设置数据观察
        setupObservers();
    }

    private void initViews(View view) {
        basicWordContainer = view.findViewById(R.id.basic_word_container);
    }


    private void setupObservers() {
        viewModel.getCombinedWordInfo().observe(getViewLifecycleOwner(), combinedWordInfo -> {
            BasicWord basicWord = combinedWordInfo.getBasicWord();
            BasicWordView basicWordView = new BasicWordView(getContext(), this, Constants.NORMAL, basicWord);
            basicWordContainer.addView(basicWordView);
        });

    }
}
