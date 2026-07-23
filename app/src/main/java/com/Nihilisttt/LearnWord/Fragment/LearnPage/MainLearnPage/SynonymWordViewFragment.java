package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.SynonymWordView;

import java.util.List;

public class SynonymWordViewFragment extends Fragment {

    private List<SynonymWord> synonymWords;

    public SynonymWordViewFragment() {
        // Required empty public constructor
    }

    public SynonymWordViewFragment(List<SynonymWord> synonymWords) {
        this.synonymWords = synonymWords;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 仅负责创建视图
        return inflater.inflate(R.layout.fragment_integrated_part, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化逻辑
        LinearLayout containerLayout = view.findViewById(R.id.word_fragment_container);
        SynonymWordView synonymWordView = new SynonymWordView(requireContext(), this, Constants.NORMAL, synonymWords);
        // 添加布局参数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        synonymWordView.setLayoutParams(params);
        // 配置布局参数并添加视图
        containerLayout.addView(synonymWordView);
    }


}