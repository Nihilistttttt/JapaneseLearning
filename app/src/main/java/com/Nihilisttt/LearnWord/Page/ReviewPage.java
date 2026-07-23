package com.Nihilisttt.LearnWord.Page;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.Nihilisttt.LearnWord.Adapter.WordListAdapter;
import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.R;

import java.util.List;

public class ReviewPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WordListAdapter wordListAdapter;
    private WordRepository wordRepository;
    private LiveData<List<Word>> wordLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_page);

        // 初始化仓库和组件
        wordRepository = WordRepository.getInstance(this);
        wordLiveData = wordRepository.getAllWords();

        // 设置RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 关键修改：传入当前Activity作为LifecycleOwner
        wordListAdapter = new WordListAdapter(this);
        recyclerView.setAdapter(wordListAdapter);

        // 观察数据变化
        wordLiveData.observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                wordListAdapter.setWordList(words);
                // 注意：已移除notifyDataSetChanged()，因其在Adapter的setWordList中调用
            }
        });
    }
}
