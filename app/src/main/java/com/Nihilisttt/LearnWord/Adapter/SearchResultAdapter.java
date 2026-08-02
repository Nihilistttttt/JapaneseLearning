package com.Nihilisttt.LearnWord.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Nihilisttt.LearnWord.Page.ViewModel.SearchPageViewModel;
import com.Nihilisttt.LearnWord.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<SearchPageViewModel.SearchResult> results = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchPageViewModel.SearchResult result);
    }

    public SearchResultAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setResults(List<SearchPageViewModel.SearchResult> results) {
        this.results = results != null ? results : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchPageViewModel.SearchResult result = results.get(position);
        holder.kanjiText.setText(result.kanji);
        if (!result.kana.equals(result.kanji)) {
            holder.kanaText.setText(result.kana);
            holder.kanaText.setVisibility(View.VISIBLE);
        } else {
            holder.kanaText.setVisibility(View.GONE);
        }
        holder.meaningText.setText(result.firstMeaning);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(result));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView kanjiText;
        final TextView kanaText;
        final TextView meaningText;

        ViewHolder(View view) {
            super(view);
            kanjiText = view.findViewById(R.id.search_result_kanji);
            kanaText = view.findViewById(R.id.search_result_kana);
            meaningText = view.findViewById(R.id.search_result_meaning);
        }
    }
}