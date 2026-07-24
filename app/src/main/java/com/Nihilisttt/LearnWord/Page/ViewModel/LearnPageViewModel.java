package com.Nihilisttt.LearnWord.Page.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.Nihilisttt.LearnWord.Database.Database.WordDatabase;
import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;
import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public class LearnPageViewModel extends AndroidViewModel {
    // 配置常量
    private static final String PREFS_NAME = "WordProgressPrefs";
    private static final String KEY_CURRENT_ID = "current_word_id";
    private static final String DEFAULT_WORD_ID = "";

    // 数据存储
    private final SharedPreferences prefs;
    private final WordRepository repository;

    // LiveData控制
    private final MutableLiveData<String> currentWordId = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    // 数据转换链
    private final LiveData<Word> currentWord;
    private final LiveData<BasicWord> basicWordLiveData;
    private final LiveData<List<WordMeaning>> wordMeaningListLiveData;
    private final LiveData<List<WordCollocation>> wordCollocationListLiveData;
    private final LiveData<List<WordSentence>> wordSentenceListLiveData;
    private final LiveData<List<AntonymWord>> antonymWordListLiveData;
    private final LiveData<List<SynonymWord>> synonymWordListLiveData;
    private final MediatorLiveData<CombinedWordInfo> combinedWordInfo = new MediatorLiveData<>();

    public LearnPageViewModel(@NonNull Application application) {
        super(application);

        // 初始化组件
        prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        repository = WordRepository.getInstance(application);

        // 加载保存的ID或使用默认值
        String savedId;
        try {
            savedId = prefs.getString(KEY_CURRENT_ID, DEFAULT_WORD_ID);
        } catch (ClassCastException e) {
            savedId = DEFAULT_WORD_ID;
            prefs.edit().remove(KEY_CURRENT_ID).apply();
        }
        if (savedId.isEmpty()) {
            try {
                String firstId = WordDatabase.databaseExecutor.submit(
                        () -> repository.getFirstWordId()).get();
                savedId = firstId != null ? firstId : "0";
            } catch (Exception e) {
                savedId = "0";
            }
        }
        currentWordId.setValue(savedId);

        // 构建数据管道
        currentWord = Transformations.switchMap(currentWordId, id ->
                repository.getWordById(id));

        basicWordLiveData = Transformations.switchMap(currentWord, word ->
                repository.getBasicWordById(word.getWordId()));

        wordMeaningListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getWordMeaningsByWordMeaningIdList(word.getMeaningIdList()));

        wordSentenceListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getWordSentencesBySentencesIdList(word.getSentenceIdList()));

        wordCollocationListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getWordCollocationsByWordCollocationIdList(word.getCollocationIdList()));

        antonymWordListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getAntonymWordsByAntonymWordsIdList(word.getAntonymIdList()));

        synonymWordListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getSynonymWordsBySynonymWordsIdList(word.getSynonymIdList()));

        combinedWordInfo.addSource(basicWordLiveData, value -> updateCombined());
        combinedWordInfo.addSource(wordMeaningListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(wordSentenceListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(wordCollocationListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(antonymWordListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(synonymWordListLiveData, value -> updateCombined());
    }

    // region 公开的LiveData访问方法
    public LiveData<Word> getCurrentWord() {
        return currentWord;
    }

    public LiveData<BasicWord> getBasicWord() {
        return basicWordLiveData;
    }

    public LiveData<List<WordMeaning>> getWordMeaningListLiveData() {
        return wordMeaningListLiveData;
    }

    public LiveData<List<WordCollocation>> getWordCollocationListLiveData() {
        return wordCollocationListLiveData;
    }

    public LiveData<List<AntonymWord>> getAntonymWordListLiveData() {
        return antonymWordListLiveData;
    }

    public LiveData<List<SynonymWord>> getSynonymWordListLiveData() {
        return synonymWordListLiveData;
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<CombinedWordInfo> getCombinedWordInfo() {
        return combinedWordInfo;
    }
    // endregion

    // region 导航控制
    public void navigatePrevious() {
        String currentId = currentWordId.getValue();
        if (currentId != null) {
            checkAndNavigatePrevious(currentId);
        }
    }

    public void navigateNext() {
        String currentId = currentWordId.getValue();
        if (currentId != null) {
            checkAndNavigateNext(currentId);
        }
    }

    private void checkAndNavigatePrevious(String currentId) {
        WordDatabase.databaseExecutor.execute(() -> {
            String prevId = repository.getPreviousWordId(currentId);
            if (prevId != null) {
                currentWordId.postValue(prevId);
                saveCurrentId(prevId);
            } else {
                toastMessage.postValue("已经是第一个单词");
            }
        });
    }

    private void checkAndNavigateNext(String currentId) {
        WordDatabase.databaseExecutor.execute(() -> {
            String nextId = repository.getNextWordId(currentId);
            if (nextId != null) {
                currentWordId.postValue(nextId);
                saveCurrentId(nextId);
            } else {
                toastMessage.postValue("已达词库末尾");
            }
        });
    }
    // endregion

    // region 内部类
    public static class CombinedWordInfo {
        private final BasicWord basicWord;
        private final List<WordMeaning> wordMeaningList;
        private final List<WordSentence> wordSentenceList;
        private final List<WordCollocation> wordCollocationList;
        private final List<AntonymWord> antonymWordList;
        private final List<SynonymWord> synonymWordList;

        public CombinedWordInfo(BasicWord basicWord,
                                List<WordMeaning> wordMeaningList,
                                List<WordSentence> wordSentenceList,
                                List<WordCollocation> wordCollocationList,
                                List<AntonymWord> antonymWordList,
                                List<SynonymWord> synonymWordList) {
            this.basicWord = basicWord;
            this.wordMeaningList = wordMeaningList != null ? wordMeaningList : Collections.emptyList();
            this.wordSentenceList = wordSentenceList != null ? wordSentenceList : Collections.emptyList();
            this.wordCollocationList = wordCollocationList != null ? wordCollocationList : Collections.emptyList();
            this.antonymWordList = antonymWordList != null ? antonymWordList : Collections.emptyList();
            this.synonymWordList = synonymWordList != null ? synonymWordList : Collections.emptyList();
        }

        // Getter 方法
        public BasicWord getBasicWord() {
            return basicWord;
        }

        public List<WordMeaning> getWordMeaningList() {
            return wordMeaningList;
        }

        public List<WordSentence> getWordSentenceList() {
            return wordSentenceList;
        }

        public List<WordCollocation> getWordCollocationList() {
            return wordCollocationList;
        }

        public List<AntonymWord> getAntonymWordList() {
            return antonymWordList;
        }

        public List<SynonymWord> getSynonymWordList() {
            return synonymWordList;
        }


    }

    // 添加私有方法
    private void updateCombined() {
        BasicWord basicWord = basicWordLiveData.getValue();
        List<WordMeaning> meanings = wordMeaningListLiveData.getValue();
        List<WordSentence> sentences = wordSentenceListLiveData.getValue();
        List<WordCollocation> collocations = wordCollocationListLiveData.getValue();
        List<AntonymWord> antonyms = antonymWordListLiveData.getValue();
        List<SynonymWord> synonyms = synonymWordListLiveData.getValue();
        if (basicWord == null) return;

        combinedWordInfo.setValue(new CombinedWordInfo(
                basicWord,
                meanings,
                sentences,
                collocations,
                antonyms,
                synonyms
        ));
    }
    // endregion

    // region 持久化方法
    private void saveCurrentId(String id) {
        prefs.edit()
                .putString(KEY_CURRENT_ID, id)
                .apply();
    }
    // endregion

    @Override
    protected void onCleared() {
        super.onCleared();
        // 清理可能存在的资源
    }
}

