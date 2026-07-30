package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.SynonymAntonymView;

import java.io.Serializable;
import java.util.List;

public class SynonymAntonymViewFragment extends Fragment {
    private List<SynonymWord> synonymWords;
    private List<AntonymWord> antonymWords;

    public SynonymAntonymViewFragment() {}

    public SynonymAntonymViewFragment(List<SynonymWord> synonymWords, List<AntonymWord> antonymWords) {
        this.synonymWords = synonymWords;
        this.antonymWords = antonymWords;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_integrated_part, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LearnPageStateViewModel stateViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageStateViewModel.class);
        Integer subFontLevel = stateViewModel.getSubFontLevel().getValue();
        if (subFontLevel == null) subFontLevel = Constants.FONT_SIZE_NORMAL;

        LinearLayout containerLayout = view.findViewById(R.id.word_fragment_container);
        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        scrollView.setFillViewport(true);

        View contentView = new SynonymAntonymView(requireContext(), this, subFontLevel, synonymWords, antonymWords);
        contentView.setLayoutParams(new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        scrollView.addView(contentView);
        containerLayout.addView(scrollView);
    }
}