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
import com.Nihilisttt.LearnWord.JavaBean.ConjugationForm;
import com.Nihilisttt.LearnWord.JavaBean.DerivedWord;
import com.Nihilisttt.LearnWord.JavaBean.Etymology;
import com.Nihilisttt.LearnWord.JavaBean.GrammarPoint;
import com.Nihilisttt.LearnWord.JavaBean.Idiom;
import com.Nihilisttt.LearnWord.JavaBean.KanjiInfo;
import com.Nihilisttt.LearnWord.JavaBean.RelatedWord;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.JavaBean.UsageDistinction;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

public class LearnPageViewModel extends AndroidViewModel {
    private static final String PREFS_NAME = "WordProgressPrefs";
    private static final String KEY_CURRENT_ID = "current_word_id";
    private static final String DEFAULT_WORD_ID = "";

    private final SharedPreferences prefs;
    private final WordRepository repository;

    private final MutableLiveData<String> currentWordId = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    private final LiveData<Word> currentWord;
    private final LiveData<BasicWord> basicWordLiveData;
    private final LiveData<List<WordMeaning>> wordMeaningListLiveData;
    private final LiveData<List<WordCollocation>> wordCollocationListLiveData;
    private final LiveData<List<WordSentence>> wordSentenceListLiveData;
    private final LiveData<List<AntonymWord>> antonymWordListLiveData;
    private final LiveData<List<SynonymWord>> synonymWordListLiveData;
    private final LiveData<List<ConjugationForm>> conjugationFormListLiveData;
    private final LiveData<List<Etymology>> etymologyListLiveData;
    private final LiveData<List<KanjiInfo>> kanjiInfoListLiveData;
    private final LiveData<List<UsageDistinction>> usageDistinctionListLiveData;
    private final LiveData<List<GrammarPoint>> grammarPointListLiveData;
    private final LiveData<List<Idiom>> idiomListLiveData;
    private final LiveData<List<DerivedWord>> derivedWordListLiveData;
    private final LiveData<List<RelatedWord>> relatedWordListLiveData;
    private final MediatorLiveData<CombinedWordInfo> combinedWordInfo = new MediatorLiveData<>();

    public LearnPageViewModel(@NonNull Application application) {
        super(application);

        prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        repository = WordRepository.getInstance(application);

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

        conjugationFormListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getConjugationFormsByConjugationFormIdList(word.getConjugationFormIdList()));

        etymologyListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getEtymologiesByEtymologyIdList(word.getEtymologyIdList()));

        kanjiInfoListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getKanjiInfosByKanjiInfoIdList(word.getKanjiInfoIdList()));

        usageDistinctionListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getUsageDistinctionsByUsageDistinctionIdList(word.getUsageDistinctionIdList()));

        grammarPointListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getGrammarPointsByGrammarPointIdList(word.getGrammarPointIdList()));

        idiomListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getIdiomsByIdiomIdList(word.getIdiomIdList()));

        derivedWordListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getDerivedWordsByDerivedWordsIdList(word.getDerivedWordIdList()));

        relatedWordListLiveData = Transformations.switchMap(currentWord, word ->
                repository.getRelatedWordsByRelatedWordsIdList(word.getRelatedWordIdList()));

        combinedWordInfo.addSource(basicWordLiveData, value -> updateCombined());
        combinedWordInfo.addSource(wordMeaningListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(wordSentenceListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(wordCollocationListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(antonymWordListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(synonymWordListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(conjugationFormListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(etymologyListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(kanjiInfoListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(usageDistinctionListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(grammarPointListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(idiomListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(derivedWordListLiveData, value -> updateCombined());
        combinedWordInfo.addSource(relatedWordListLiveData, value -> updateCombined());
    }

    // region 公开的LiveData访问方法
    public LiveData<Word> getCurrentWord() { return currentWord; }
    public LiveData<BasicWord> getBasicWord() { return basicWordLiveData; }
    public LiveData<List<WordMeaning>> getWordMeaningListLiveData() { return wordMeaningListLiveData; }
    public LiveData<List<WordCollocation>> getWordCollocationListLiveData() { return wordCollocationListLiveData; }
    public LiveData<List<AntonymWord>> getAntonymWordListLiveData() { return antonymWordListLiveData; }
    public LiveData<List<SynonymWord>> getSynonymWordListLiveData() { return synonymWordListLiveData; }
    public LiveData<List<ConjugationForm>> getConjugationFormListLiveData() { return conjugationFormListLiveData; }
    public LiveData<List<Etymology>> getEtymologyListLiveData() { return etymologyListLiveData; }
    public LiveData<List<KanjiInfo>> getKanjiInfoListLiveData() { return kanjiInfoListLiveData; }
    public LiveData<List<UsageDistinction>> getUsageDistinctionListLiveData() { return usageDistinctionListLiveData; }
    public LiveData<List<GrammarPoint>> getGrammarPointListLiveData() { return grammarPointListLiveData; }
    public LiveData<List<Idiom>> getIdiomListLiveData() { return idiomListLiveData; }
    public LiveData<List<DerivedWord>> getDerivedWordListLiveData() { return derivedWordListLiveData; }
    public LiveData<List<RelatedWord>> getRelatedWordListLiveData() { return relatedWordListLiveData; }
    public LiveData<String> getToastMessage() { return toastMessage; }
    public LiveData<CombinedWordInfo> getCombinedWordInfo() { return combinedWordInfo; }
    // endregion

    // region 导航控制
    public void navigatePrevious() {
        String currentId = currentWordId.getValue();
        if (currentId != null) checkAndNavigatePrevious(currentId);
    }

    public void navigateNext() {
        String currentId = currentWordId.getValue();
        if (currentId != null) checkAndNavigateNext(currentId);
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
        private final List<ConjugationForm> conjugationFormList;
        private final List<Etymology> etymologyList;
        private final List<KanjiInfo> kanjiInfoList;
        private final List<UsageDistinction> usageDistinctionList;
        private final List<GrammarPoint> grammarPointList;
        private final List<Idiom> idiomList;
        private final List<DerivedWord> derivedWordList;
        private final List<RelatedWord> relatedWordList;

        public CombinedWordInfo(BasicWord basicWord,
                                List<WordMeaning> wordMeaningList,
                                List<WordSentence> wordSentenceList,
                                List<WordCollocation> wordCollocationList,
                                List<AntonymWord> antonymWordList,
                                List<SynonymWord> synonymWordList,
                                List<ConjugationForm> conjugationFormList,
                                List<Etymology> etymologyList,
                                List<KanjiInfo> kanjiInfoList,
                                List<UsageDistinction> usageDistinctionList,
                                 List<GrammarPoint> grammarPointList,
                                 List<Idiom> idiomList,
                                 List<DerivedWord> derivedWordList,
                                 List<RelatedWord> relatedWordList) {
            this.basicWord = basicWord;
            this.wordMeaningList = wordMeaningList != null ? wordMeaningList : Collections.emptyList();
            this.wordSentenceList = wordSentenceList != null ? wordSentenceList : Collections.emptyList();
            this.wordCollocationList = wordCollocationList != null ? wordCollocationList : Collections.emptyList();
            this.antonymWordList = antonymWordList != null ? antonymWordList : Collections.emptyList();
            this.synonymWordList = synonymWordList != null ? synonymWordList : Collections.emptyList();
            this.conjugationFormList = conjugationFormList != null ? conjugationFormList : Collections.emptyList();
            this.etymologyList = etymologyList != null ? etymologyList : Collections.emptyList();
            this.kanjiInfoList = kanjiInfoList != null ? kanjiInfoList : Collections.emptyList();
            this.usageDistinctionList = usageDistinctionList != null ? usageDistinctionList : Collections.emptyList();
            this.grammarPointList = grammarPointList != null ? grammarPointList : Collections.emptyList();
            this.idiomList = idiomList != null ? idiomList : Collections.emptyList();
            this.derivedWordList = derivedWordList != null ? derivedWordList : Collections.emptyList();
            this.relatedWordList = relatedWordList != null ? relatedWordList : Collections.emptyList();
        }

        public BasicWord getBasicWord() { return basicWord; }
        public List<WordMeaning> getWordMeaningList() { return wordMeaningList; }
        public List<WordSentence> getWordSentenceList() { return wordSentenceList; }
        public List<WordCollocation> getWordCollocationList() { return wordCollocationList; }
        public List<AntonymWord> getAntonymWordList() { return antonymWordList; }
        public List<SynonymWord> getSynonymWordList() { return synonymWordList; }
        public List<ConjugationForm> getConjugationFormList() { return conjugationFormList; }
        public List<Etymology> getEtymologyList() { return etymologyList; }
        public List<KanjiInfo> getKanjiInfoList() { return kanjiInfoList; }
        public List<UsageDistinction> getUsageDistinctionList() { return usageDistinctionList; }
        public List<GrammarPoint> getGrammarPointList() { return grammarPointList; }
        public List<Idiom> getIdiomList() { return idiomList; }
        public List<DerivedWord> getDerivedWordList() { return derivedWordList; }
        public List<RelatedWord> getRelatedWordList() { return relatedWordList; }
    }

    private void updateCombined() {
        BasicWord basicWord = basicWordLiveData.getValue();
        if (basicWord == null) return;


        combinedWordInfo.setValue(new CombinedWordInfo(
                basicWord,
                wordMeaningListLiveData.getValue(),
                wordSentenceListLiveData.getValue(),
                wordCollocationListLiveData.getValue(),
                antonymWordListLiveData.getValue(),
                synonymWordListLiveData.getValue(),
                conjugationFormListLiveData.getValue(),
                etymologyListLiveData.getValue(),
                kanjiInfoListLiveData.getValue(),
                usageDistinctionListLiveData.getValue(),
                grammarPointListLiveData.getValue(),
                idiomListLiveData.getValue(),
                derivedWordListLiveData.getValue(),
                relatedWordListLiveData.getValue()
        ));
    }
    // endregion

    // region 持久化方法
    private void saveCurrentId(String id) {
        prefs.edit().putString(KEY_CURRENT_ID, id).apply();
    }
    // endregion

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
