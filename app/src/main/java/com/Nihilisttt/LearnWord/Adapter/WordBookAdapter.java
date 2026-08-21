package com.Nihilisttt.LearnWord.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Nihilisttt.LearnWord.Database.Entities.WordBookEntity;
import com.Nihilisttt.LearnWord.R;

import java.util.ArrayList;
import java.util.List;

public class WordBookAdapter extends RecyclerView.Adapter<WordBookAdapter.BookViewHolder> {
    private List<WordBookEntity> bookList = new ArrayList<>();
    private final OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(WordBookEntity book);
    }

    public WordBookAdapter(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void setBookList(List<WordBookEntity> bookList) {
        this.bookList = bookList != null ? bookList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        WordBookEntity book = bookList.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        private final View colorIndicator;
        private final TextView tvName;
        private final TextView tvDescription;
        private final TextView tvDueCount;
        private final TextView tvTotalCount;
        private final TextView tvLearnedCount;
        private final TextView tvMasteredCount;
        private final ProgressBar progressBar;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.color_indicator);
            tvName = itemView.findViewById(R.id.tv_book_name);
            tvDescription = itemView.findViewById(R.id.tv_book_description);
            tvDueCount = itemView.findViewById(R.id.tv_due_count);
            tvTotalCount = itemView.findViewById(R.id.tv_total_count);
            tvLearnedCount = itemView.findViewById(R.id.tv_learned_count);
            tvMasteredCount = itemView.findViewById(R.id.tv_mastered_count);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }

        void bind(WordBookEntity book) {
            tvName.setText(book.getName());
            tvDescription.setText(book.getDescription());

            if (book.getColor() != 0) {
                colorIndicator.setBackgroundColor(book.getColor());
            }

            int total = book.getTotalWordCount();
            int learned = book.getLearnedCount();
            int mastered = book.getMasteredCount();

            tvTotalCount.setText("总计 " + total + " 词");
            tvLearnedCount.setText("已学 " + learned);
            tvMasteredCount.setText("已掌握 " + mastered);

            if (total > 0) {
                int progress = (int) ((long) learned * 100 / total);
                progressBar.setProgress(progress);
            } else {
                progressBar.setProgress(0);
            }

            itemView.setOnClickListener(v -> listener.onBookClick(book));
        }
    }
}