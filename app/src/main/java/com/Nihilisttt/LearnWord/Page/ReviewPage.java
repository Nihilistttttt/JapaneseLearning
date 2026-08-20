package com.Nihilisttt.LearnWord.Page;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.Nihilisttt.LearnWord.Adapter.WordListAdapter;
import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.databinding.ActivityReviewPageBinding;

import java.util.List;

public class ReviewPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WordListAdapter wordListAdapter;
    private WordRepository wordRepository;
    private LiveData<List<Word>> wordLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityReviewPageBinding binding = ActivityReviewPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wordRepository = WordRepository.getInstance(this);
        wordLiveData = wordRepository.getAllWords();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 关键修改：传入当前Activity作为LifecycleOwner
        LearnPageStateViewModel stateViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(LearnPageStateViewModel.class);
        Integer subFontLevel = stateViewModel.getSubFontLevel().getValue();
        if (subFontLevel == null) subFontLevel = Constants.FONT_SIZE_NORMAL;

        wordListAdapter = new WordListAdapter(this, subFontLevel);
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
