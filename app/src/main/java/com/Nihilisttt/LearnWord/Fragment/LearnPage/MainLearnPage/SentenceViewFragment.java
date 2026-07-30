package com.Nihilisttt.LearnWord.Fragment.LearnPage.MainLearnPage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.SentenceView;

import java.util.List;

public class SentenceViewFragment extends Fragment {
    private List<WordSentence> sentences;

    public SentenceViewFragment() {}

    public SentenceViewFragment(List<WordSentence> sentences) {
        this.sentences = sentences;
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
        View contentView = new SentenceView(requireContext(), this, subFontLevel, sentences);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(params);
        containerLayout.addView(contentView);
    }
}