package com.Nihilisttt.LearnWord.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;
import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.R;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.WordView.MeaningView;
import com.Nihilisttt.LearnWord.WordView.BasicWordView;

import java.util.ArrayList;
import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordItemViewHolder> {
    private final LifecycleOwner lifecycleOwner;
    private final int layoutType;
    private List<Word> wordList = new ArrayList<>();
    private WordRepository wordRepository;

    public WordListAdapter(LifecycleOwner lifecycleOwner, int layoutType) {
        this.lifecycleOwner = lifecycleOwner;
        this.layoutType = layoutType;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setWordList(List<Word> wordList) {
        this.wordList = wordList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.word_list_item, parent, false);
        return new WordItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordItemViewHolder holder, int position) {
        if (wordRepository == null) {
            wordRepository = WordRepository.getInstance(holder.itemView.getContext());
        }

        String wordId = wordList.get(position).getWordId();
        BasicWord basicWord = wordRepository.getBasicWordByIdSync(wordId);
        if (basicWord != null) {
            BasicWordView basicWordView = new BasicWordView(holder.itemView.getContext(), lifecycleOwner, layoutType, basicWord);
            holder.wordItemContainer.removeAllViews();
            holder.wordItemContainer.addView(basicWordView);
        }

        List<String> meaningIds = wordList.get(position).getMeaningIdList();
        List<WordMeaning> wordMeanings = wordRepository.getWordMeaningsByWordMeaningIdListSync(meaningIds);
        if (wordMeanings != null && !wordMeanings.isEmpty()) {
            MeaningView meaningView = new MeaningView(holder.itemView.getContext(), lifecycleOwner, layoutType, wordMeanings, Constants.SHOW_SENTENCE_POPUP);
            holder.meaningItemContainer.removeAllViews();
            holder.meaningItemContainer.addView(meaningView);
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    static class WordItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout wordItemContainer, meaningItemContainer;

        public WordItemViewHolder(@NonNull View itemView) {
            super(itemView);
            wordItemContainer = itemView.findViewById(R.id.word_item_container);
            meaningItemContainer = itemView.findViewById(R.id.meaning_item_container);
        }
    }
}
