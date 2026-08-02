package com.Nihilisttt.LearnWord.Page.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

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

public class SearchDetailViewModel extends AndroidViewModel {
    private final WordRepository repository;

    private final MutableLiveData<String> currentWordId = new MutableLiveData<>();

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
    private final MediatorLiveData<LearnPageViewModel.CombinedWordInfo> combinedWordInfo = new MediatorLiveData<>();

    public SearchDetailViewModel(@NonNull Application application) {
        super(application);
        repository = WordRepository.getInstance(application);

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

    public void setCurrentWordId(String wordId) {
        currentWordId.setValue(wordId);
    }

    public LiveData<LearnPageViewModel.CombinedWordInfo> getCombinedWordInfo() {
        return combinedWordInfo;
    }

    private void updateCombined() {
        BasicWord basicWord = basicWordLiveData.getValue();
        if (basicWord == null) return;

        combinedWordInfo.setValue(new LearnPageViewModel.CombinedWordInfo(
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
}