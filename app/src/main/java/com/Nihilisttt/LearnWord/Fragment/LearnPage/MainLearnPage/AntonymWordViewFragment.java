package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.AntonymWordView;

import java.util.List;

public class AntonymWordViewFragment extends Fragment {
    private List<AntonymWord> antonymWords;

    public AntonymWordViewFragment() {
        // Required empty public constructor
    }

    public AntonymWordViewFragment(List<AntonymWord> antonymWords) {
        this.antonymWords = antonymWords;
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
        AntonymWordView antonymWordView = new AntonymWordView(requireContext(), this, Constants.NORMAL, antonymWords);
        // 添加布局参数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        antonymWordView.setLayoutParams(params);
        // 配置布局参数并添加视图
        containerLayout.addView(antonymWordView);
    }

}