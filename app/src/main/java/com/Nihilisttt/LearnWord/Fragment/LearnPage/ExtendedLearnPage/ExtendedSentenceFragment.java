package com.Nihilisttt.LearnWord.Fragment.LearnPage.ExtendedLearnPage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.SentenceView;

import androidx.lifecycle.ViewModelProvider;

@SuppressLint("ViewConstructor")
public class ExtendedSentenceFragment extends Fragment {
    private WordSentence sentence;

    public ExtendedSentenceFragment() {

    }

    public ExtendedSentenceFragment(WordSentence sentence) {
        this.sentence = sentence;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 仅负责创建视图
        return inflater.inflate(R.layout.fragment_extended_sentence, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化逻辑
        LearnPageStateViewModel stateViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageStateViewModel.class);
        Integer subFontLevel = stateViewModel.getSubFontLevel().getValue();
        if (subFontLevel == null) subFontLevel = Constants.FONT_SIZE_NORMAL;

        LinearLayout containerLayout = view.findViewById(R.id.sentence_fragment_container);
        SentenceView sentenceView = new SentenceView(requireContext(), this, subFontLevel, sentence);

        // 添加布局参数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        sentenceView.setLayoutParams(params);

        // 添加到容器
        containerLayout.addView(sentenceView);
    }
}
