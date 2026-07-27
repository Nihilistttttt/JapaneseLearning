package com.Nihilisttt.LearnWord.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
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
        WordRepository wordRepository = WordRepository.getInstance(holder.itemView.getContext());

        // 使用构造函数传入的lifecycleOwner
        LiveData<BasicWord> basicWordLiveData = wordRepository.getBasicWordById(wordList.get(position).getWordId());
        basicWordLiveData.observe(lifecycleOwner, basicWord -> {
            BasicWordView basicWordView = new BasicWordView(holder.itemView.getContext(),lifecycleOwner, layoutType, basicWord);

            holder.word_item_container.removeAllViews();
            holder.word_item_container.addView(basicWordView);
        });

        LiveData<List<WordMeaning>> wordDMeaningsLiveData = wordRepository.getWordMeaningsByWordMeaningIdList(wordList.get(position).getMeaningIdList());
        wordDMeaningsLiveData.observe(lifecycleOwner, wordMeanings -> {
            MeaningView meaningView = new MeaningView(holder.itemView.getContext(),lifecycleOwner, layoutType, wordMeanings,Constants.SHOW_SENTENCE_POPUP);
            holder.meaning_item_container.removeAllViews();
            holder.meaning_item_container.addView(meaningView);
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    static class WordItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout word_item_container, meaning_item_container;

        public WordItemViewHolder(@NonNull View itemView) {
            super(itemView);
            word_item_container = itemView.findViewById(R.id.word_item_container);
            meaning_item_container = itemView.findViewById(R.id.meaning_item_container);
        }
    }
}
