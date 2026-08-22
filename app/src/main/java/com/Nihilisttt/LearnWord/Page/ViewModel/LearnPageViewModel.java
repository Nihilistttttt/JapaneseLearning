package com.Nihilisttt.LearnWord.Page.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.Nihilisttt.LearnWord.Database.Database.WordDatabase;
import com.Nihilisttt.LearnWord.Database.Repository.WordBookRepository;
import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;
import com.Nihilisttt.LearnWord.Database.Entities.WordMeaningEntity;
import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordReviewEntity;
import com.Nihilisttt.LearnWord.Algorithm.StudyQueueEntry;
import com.Nihilisttt.LearnWord.Algorithm.StudyStage;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class LearnPageViewModel extends AndroidViewModel {
    private static final String PREFS_NAME = "WordProgressPrefs";
    private static final String KEY_CURRENT_ID = "current_word_id";
    private static final String DEFAULT_WORD_ID = "";

    private final SharedPreferences prefs;
    private final WordRepository repository;
    private final WordBookRepository bookRepository;

    private String bookId = null;
    private boolean isSRSMode = false;
    private boolean isReviewMode = false;
    private final List<StudyQueueEntry> studyQueue = new ArrayList<>();
    private int currentQueueIndex = 0;
    private final Map<String, Integer> wordCorrectCount = new HashMap<>();
    private final Random random = new Random();
    private static final int SESSION_WORD_COUNT = 20;
    private final List<String> sessionWords = new ArrayList<>();
    private int sessionNewCount = 0;
    private int sessionReviewCount = 0;

    private final List<String> reviewSessionWords = new ArrayList<>();
    private final Map<String, String> reviewWordBookIds = new HashMap<>();
    private int reviewSessionIndex = 0;
    private int reviewCompletedCount = 0;
    private String reviewBookId = null;
    private final Set<String> relearnWords = new HashSet<>();
    private boolean inRelearnMode = false;

    private final MutableLiveData<String> currentWordId = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> dailyNewCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> dailyReviewCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> queueRemaining = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> sessionComplete = new MutableLiveData<>(false);
    private final MutableLiveData<StudyStage> currentStage = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentCorrectCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> sessionTotal = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> sessionProgress = new MutableLiveData<>(0);
    private final MutableLiveData<List<ChoiceOption>> multipleChoiceOptions = new MutableLiveData<>();
    private final MutableLiveData<Integer> correctOptionIndex = new MutableLiveData<>(0);
    private final MutableLiveData<String> correctWordText = new MutableLiveData<>();

    private final MutableLiveData<ReviewStage> reviewStage = new MutableLiveData<>();
    private final MutableLiveData<Integer> reviewSessionTotal = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> reviewSessionProgress = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> reviewSessionComplete = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> reviewAnswerRevealed = new MutableLiveData<>(false);

    public static class ChoiceOption {
        public final String meaningText;
        public final String wordText;
        public ChoiceOption(String meaningText, String wordText) {
            this.meaningText = meaningText;
            this.wordText = wordText;
        }
    }

    public enum SrsButtonMode { HIDDEN, CHOICE, SUBMIT, SUBMIT_PASS_ONLY, SUBMIT_FAIL_ONLY }

    public enum ReviewStage {
        SHOW_WORD,
        REVEAL_RECOGNIZE,
        REVEAL_FUZZY,
        REVEAL_FAIL
    }
    private final MutableLiveData<SrsButtonMode> srsButtonMode = new MutableLiveData<>(SrsButtonMode.HIDDEN);
    private final MutableLiveData<Boolean> revealRequested = new MutableLiveData<>(false);
    private boolean passPreviewed = false;
    private boolean failPreviewed = false;
    private SrsButtonMode pendingRevealMode = SrsButtonMode.SUBMIT;
    private int entryVersion = 0;
    private final MutableLiveData<Integer> entryLoaded = new MutableLiveData<>(0);
    private final AtomicInteger wordGeneration = new AtomicInteger(0);
    private final int[] sourceGenerations = new int[14];

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
        bookRepository = WordBookRepository.getInstance(application);

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

        combinedWordInfo.addSource(basicWordLiveData, value -> { sourceGenerations[0] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(wordMeaningListLiveData, value -> { sourceGenerations[1] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(wordSentenceListLiveData, value -> { sourceGenerations[2] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(wordCollocationListLiveData, value -> { sourceGenerations[3] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(antonymWordListLiveData, value -> { sourceGenerations[4] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(synonymWordListLiveData, value -> { sourceGenerations[5] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(conjugationFormListLiveData, value -> { sourceGenerations[6] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(etymologyListLiveData, value -> { sourceGenerations[7] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(kanjiInfoListLiveData, value -> { sourceGenerations[8] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(usageDistinctionListLiveData, value -> { sourceGenerations[9] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(grammarPointListLiveData, value -> { sourceGenerations[10] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(idiomListLiveData, value -> { sourceGenerations[11] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(derivedWordListLiveData, value -> { sourceGenerations[12] = wordGeneration.get(); updateCombined(); });
        combinedWordInfo.addSource(relatedWordListLiveData, value -> { sourceGenerations[13] = wordGeneration.get(); updateCombined(); });
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
    public LiveData<Integer> getDailyNewCount() { return dailyNewCount; }
    public LiveData<Integer> getDailyReviewCount() { return dailyReviewCount; }
    public LiveData<Integer> getQueueRemaining() { return queueRemaining; }
    public LiveData<Boolean> getSessionComplete() { return sessionComplete; }
    public LiveData<StudyStage> getCurrentStage() { return currentStage; }
    public LiveData<Integer> getCurrentCorrectCount() { return currentCorrectCount; }
    public LiveData<Integer> getSessionTotal() { return sessionTotal; }
    public LiveData<Integer> getSessionProgress() { return sessionProgress; }
    public LiveData<List<ChoiceOption>> getMultipleChoiceOptions() { return multipleChoiceOptions; }
    public LiveData<Integer> getCorrectOptionIndex() { return correctOptionIndex; }
    public LiveData<String> getCorrectWordText() { return correctWordText; }
    public LiveData<SrsButtonMode> getSrsButtonMode() { return srsButtonMode; }
    public void setSrsButtonMode(SrsButtonMode mode) { srsButtonMode.setValue(mode); }
    public LiveData<Boolean> getRevealRequested() { return revealRequested; }
    public void requestReveal(SrsButtonMode mode) { pendingRevealMode = mode; revealRequested.setValue(true); }
    public SrsButtonMode getPendingRevealMode() { return pendingRevealMode; }
    public void consumeRevealRequest() { revealRequested.setValue(false); }
    public LiveData<Integer> getEntryLoaded() { return entryLoaded; }
    public boolean isSRSMode() { return isSRSMode; }
    public boolean isReviewMode() { return isReviewMode; }
    public boolean isRelearningCurrentWord() { return inRelearnMode; }
    public String getBookId() { return bookId; }
    public LiveData<ReviewStage> getReviewStage() { return reviewStage; }
    public LiveData<Integer> getReviewSessionTotal() { return reviewSessionTotal; }
    public LiveData<Integer> getReviewSessionProgress() { return reviewSessionProgress; }
    public LiveData<Boolean> getReviewSessionComplete() { return reviewSessionComplete; }
    public LiveData<Boolean> getReviewAnswerRevealed() { return reviewAnswerRevealed; }
    // endregion

    // region 导航控制
    public void setCurrentWordId(String wordId) {
        wordGeneration.incrementAndGet();
        currentWordId.setValue(wordId);
        saveCurrentId(wordId);
    }


    public String getCurrentWordId() {
        return currentWordId.getValue();
    }

    public void navigatePrevious() {
        if (isSRSMode || isReviewMode) {
            toastMessage.setValue("学习模式不支持返回上一个");
            return;
        }
        String currentId = currentWordId.getValue();
        if (currentId != null) checkAndNavigatePrevious(currentId);
    }

    public void navigateNext() {
        if (isSRSMode) {
            loadNextFromQueue();
            return;
        }
        if (isReviewMode) {
            return;
        }
        String currentId = currentWordId.getValue();
        if (currentId != null) checkAndNavigateNext(currentId);
    }

    private void checkAndNavigatePrevious(String currentId) {
        WordDatabase.databaseExecutor.execute(() -> {
            String prevId = repository.getPreviousWordId(currentId);
            if (prevId != null) {
                wordGeneration.incrementAndGet();
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
                wordGeneration.incrementAndGet();
                currentWordId.postValue(nextId);
                saveCurrentId(nextId);
            } else {
                toastMessage.postValue("已达词库末尾");
            }
        });
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
        this.isSRSMode = bookId != null && !bookId.isEmpty();
        if (isSRSMode) {
            initDailySession();
        }
    }

    public void startNewSession() {
        if (!isSRSMode || bookId == null) return;
        initDailySession();
    }

    public int getSessionNewCount() { return sessionNewCount; }
    public int getSessionReviewCount() { return sessionReviewCount; }
    public int getSessionMasteredCount() {
        int mastered = 0;
        for (String wordId : sessionWords) {
            if (wordCorrectCount.getOrDefault(wordId, 0) >= 3) mastered++;
        }
        return mastered;
    }

    private void initDailySession() {
        WordDatabase.databaseExecutor.execute(() -> {
            studyQueue.clear();
            currentQueueIndex = 0;
            wordCorrectCount.clear();
            sessionWords.clear();
            sessionNewCount = 0;
            sessionReviewCount = 0;

            List<WordReviewEntity> dueReviews = bookRepository.getDueReviews(bookId, 50);
            for (WordReviewEntity review : dueReviews) {
                if (!sessionWords.contains(review.getWordId())) {
                    sessionWords.add(review.getWordId());
                }
            }
            sessionReviewCount = sessionWords.size();

            int newRemaining = SESSION_WORD_COUNT - sessionWords.size();
            if (newRemaining > 0) {
                List<String> newWords = bookRepository.getTodayNewWords(bookId, newRemaining);
                for (String wordId : newWords) {
                    if (!sessionWords.contains(wordId)) {
                        sessionWords.add(wordId);
                    }
                }
                sessionNewCount = sessionWords.size() - sessionReviewCount;
            }

            if (sessionWords.isEmpty()) {
                sessionTotal.postValue(0);
                sessionComplete.postValue(true);
                toastMessage.postValue("今日学习已完成");
                return;
            }

            buildInterleavedQueue(sessionWords);

            dailyNewCount.postValue(sessionNewCount);
            dailyReviewCount.postValue(sessionReviewCount);
            sessionTotal.postValue(sessionWords.size());
            sessionComplete.postValue(false);

            loadCurrentEntry();
        });
    }

    private void buildInterleavedQueue(List<String> sessionWords) {
        Map<String, Integer> wordStagePointer = new HashMap<>();
        for (String wordId : sessionWords) {
            wordStagePointer.put(wordId, 0);
            wordCorrectCount.put(wordId, 0);
        }

        StudyStage[] stages = {StudyStage.NEW, StudyStage.REVIEW, StudyStage.FINAL};

        while (true) {
            List<String> available = new ArrayList<>();
            for (String wordId : sessionWords) {
                int ptr = wordStagePointer.get(wordId);
                if (ptr < 3) {
                    available.add(wordId);
                }
            }
            if (available.isEmpty()) break;

            String chosen = available.get(random.nextInt(available.size()));
            int ptr = wordStagePointer.get(chosen);
            studyQueue.add(new StudyQueueEntry(chosen, stages[ptr]));
            wordStagePointer.put(chosen, ptr + 1);
        }
        Log.d("SRS", "buildInterleavedQueue: queueSize=" + studyQueue.size() + " wordCount=" + sessionWords.size());
    }

    private StudyStage computeStageForWord(String wordId) {
        int count = wordCorrectCount.getOrDefault(wordId, 0);
        if (count >= 3) return null;
        if (count == 0) return StudyStage.NEW;
        if (count == 1) return StudyStage.REVIEW;
        return StudyStage.FINAL;
    }

    private void postSessionProgress() {
        int mastered = 0;
        for (String wid : sessionWords) {
            if (wordCorrectCount.getOrDefault(wid, 0) >= 3) mastered++;
        }
        sessionProgress.postValue(mastered);
    }

    private void loadCurrentEntry() {
        while (currentQueueIndex < studyQueue.size()) {
            StudyQueueEntry entry = studyQueue.get(currentQueueIndex);
            String wordId = entry.getWordId();
            StudyStage stage = computeStageForWord(wordId);
            if (stage == null) {
                Log.d("SRS", "loadCurrentEntry: SKIP mastered word=" + wordId + " count=" + wordCorrectCount.getOrDefault(wordId, 0) + " index=" + currentQueueIndex);
                entry.setCompleted(true);
                currentQueueIndex++;
                continue;
            }

            Log.d("SRS", "loadCurrentEntry: word=" + wordId + " stage=" + stage + " count=" + wordCorrectCount.getOrDefault(wordId, 0) + " index=" + currentQueueIndex + "/" + studyQueue.size());
            currentStage.postValue(stage);
            currentCorrectCount.postValue(wordCorrectCount.getOrDefault(wordId, 0));

            postSessionProgress();
            queueRemaining.postValue(studyQueue.size() - currentQueueIndex);

            if (stage == StudyStage.NEW) {
                generateMultipleChoiceOptions(wordId);
            } else {
                multipleChoiceOptions.postValue(null);
            }

            wordGeneration.incrementAndGet();
            currentWordId.postValue(wordId);
            saveCurrentId(wordId);

            if (stage == StudyStage.NEW) {
                bookRepository.startNewWordStudy(wordId, bookId);
            }

            entryVersion++;
            entryLoaded.postValue(entryVersion);
            return;
        }

        Log.d("SRS", "loadCurrentEntry: SESSION COMPLETE! index=" + currentQueueIndex + " queueSize=" + studyQueue.size());
        for (String wid : wordCorrectCount.keySet()) {
            Log.d("SRS", "  word=" + wid + " finalCount=" + wordCorrectCount.get(wid));
        }
        sessionComplete.postValue(true);
        toastMessage.postValue("今日学习已完成");
        queueRemaining.postValue(0);
    }

    private void generateMultipleChoiceOptions(String wordId) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordMeaningEntity correctMeaning = repository.getFirstMeaningByWordIdSync(wordId);
            if (correctMeaning == null || correctMeaning.getTranslationDefinition() == null
                    || correctMeaning.getTranslationDefinition().isEmpty()) {
                multipleChoiceOptions.postValue(null);
                return;
            }

            String correctText = correctMeaning.getTranslationDefinition();

            BasicWordEntity correctBasicWord = repository.getBasicWordEntityByIdSync(wordId);
            String wordDisplayText = "";
            if (correctBasicWord != null) {
                wordDisplayText = parseComponents(correctBasicWord.getKanjiComponents());
                if (wordDisplayText.isEmpty()) {
                    wordDisplayText = parseComponents(correctBasicWord.getKanaComponents());
                }
            }
            correctWordText.postValue(wordDisplayText);

            List<String> distractorIds = bookRepository.getDistractorWordIds(wordId, 3);
            List<ChoiceOption> options = new ArrayList<>();
            List<String> usedMeanings = new ArrayList<>();
            options.add(new ChoiceOption(correctText, wordDisplayText));
            usedMeanings.add(correctText);

            for (String distId : distractorIds) {
                WordMeaningEntity distMeaning = repository.getFirstMeaningByWordIdSync(distId);
                if (distMeaning != null && distMeaning.getTranslationDefinition() != null
                        && !distMeaning.getTranslationDefinition().isEmpty()
                        && !usedMeanings.contains(distMeaning.getTranslationDefinition())) {
                    String distWordText = "";
                    BasicWordEntity distBasicWord = repository.getBasicWordEntityByIdSync(distId);
                    if (distBasicWord != null) {
                        distWordText = parseComponents(distBasicWord.getKanjiComponents());
                        if (distWordText.isEmpty()) {
                            distWordText = parseComponents(distBasicWord.getKanaComponents());
                        }
                    }
                    options.add(new ChoiceOption(distMeaning.getTranslationDefinition(), distWordText));
                    usedMeanings.add(distMeaning.getTranslationDefinition());
                }
            }

            if (options.size() < 4) {
                List<WordMeaningEntity> randomMeanings = repository.getRandomMeaningsSync(wordId, 10);
                for (WordMeaningEntity rm : randomMeanings) {
                    if (options.size() >= 4) break;
                    if (rm.getTranslationDefinition() != null
                            && !rm.getTranslationDefinition().isEmpty()
                            && !usedMeanings.contains(rm.getTranslationDefinition())) {
                        String rmWordText = "";
                        BasicWordEntity rmBasicWord = repository.getBasicWordEntityByIdSync(rm.getWordId());
                        if (rmBasicWord != null) {
                            rmWordText = parseComponents(rmBasicWord.getKanjiComponents());
                            if (rmWordText.isEmpty()) {
                                rmWordText = parseComponents(rmBasicWord.getKanaComponents());
                            }
                        }
                        options.add(new ChoiceOption(rm.getTranslationDefinition(), rmWordText));
                        usedMeanings.add(rm.getTranslationDefinition());
                    }
                }
            }

            if (options.size() < 2) {
                multipleChoiceOptions.postValue(null);
                return;
            }

            Collections.shuffle(options, random);
            int correctIdx = 0;
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).meaningText.equals(correctText)) {
                    correctIdx = i;
                    break;
                }
            }

            multipleChoiceOptions.postValue(options);
            correctOptionIndex.postValue(correctIdx);
        });
    }

    private static String parseComponents(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        try {
            org.json.JSONArray arr = new org.json.JSONArray(raw);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length(); i++) {
                sb.append(arr.optString(i, ""));
            }
            return sb.toString();
        } catch (Exception e) {
            return raw.replace("[", "").replace("]", "").replace("\"", "").replace(",", "").replace(" ", "");
        }
    }

    private void loadNextFromQueue() {
        currentQueueIndex++;
        loadCurrentEntry();
    }

    public void previewPass() {
        if (!isSRSMode || bookId == null) return;
        String currentId = currentWordId.getValue();
        if (currentId == null) return;
        int oldCount = wordCorrectCount.getOrDefault(currentId, 0);
        int count = oldCount + 1;
        wordCorrectCount.put(currentId, count);
        currentCorrectCount.postValue(count);
        postSessionProgress();
        passPreviewed = true;
        Log.d("SRS", "previewPass: word=" + currentId + " count " + oldCount + "->" + count);
    }

    public void previewFail() {
        if (!isSRSMode || bookId == null) return;
        String currentId = currentWordId.getValue();
        if (currentId == null) return;
        int oldCount = wordCorrectCount.getOrDefault(currentId, 0);
        int count = oldCount - 1;
        if (count < 0) count = 0;
        wordCorrectCount.put(currentId, count);
        postSessionProgress();
        currentCorrectCount.postValue(count);
        failPreviewed = true;
        Log.d("SRS", "previewFail: word=" + currentId + " count " + oldCount + "->" + count);
    }

    public void debugForcePass() {
        if (!isSRSMode || bookId == null) return;
        String currentId = currentWordId.getValue();
        if (currentId == null) return;
        wordCorrectCount.put(currentId, 3);
        currentCorrectCount.postValue(3);
        postSessionProgress();
        passPreviewed = true;
        advanceToNext();
    }

    public void advanceToNext() {
        if (!isSRSMode || bookId == null) return;
        String currentId = currentWordId.getValue();
        if (currentId == null) return;

        int currentCount = wordCorrectCount.getOrDefault(currentId, 0);

        if (isReviewMode) {
            if (passPreviewed && currentCount >= 3) {
                String bid = reviewWordBookIds.get(currentId);
                if (bid != null) bookRepository.pass(currentId, bid);
                relearnWords.remove(currentId);
                reviewCompletedCount++;
                reviewSessionProgress.postValue(reviewCompletedCount);
            } else if (passPreviewed) {
                insertRelearnEntry(currentId);
            } else {
                wordCorrectCount.put(currentId, 0);
                currentCorrectCount.postValue(0);
                insertRelearnEntry(currentId);
            }
            passPreviewed = false;
            failPreviewed = false;
            inRelearnMode = false;
            isSRSMode = false;
            bookId = null;
            srsButtonMode.setValue(SrsButtonMode.HIDDEN);
            reviewSessionIndex++;
            loadReviewWord();
            return;
        }

        StudyQueueEntry entry = studyQueue.get(currentQueueIndex);
        entry.setCompleted(true);

        Log.d("SRS", "advanceToNext: word=" + currentId + " count=" + currentCount + " passPreviewed=" + passPreviewed + " failPreviewed=" + failPreviewed + " index=" + currentQueueIndex + "/" + studyQueue.size());

        if (passPreviewed && currentCount >= 3) {
            bookRepository.pass(currentId, bookId);
        }

        if (currentCount < 3 && !hasPendingEntryForWord(currentId)) {
            int reinsertPos = currentQueueIndex + 3 + random.nextInt(5);
            reinsertPos = Math.min(reinsertPos, studyQueue.size());
            studyQueue.add(reinsertPos, new StudyQueueEntry(currentId, StudyStage.NEW));
            sessionTotal.postValue(studyQueue.size());
            Log.d("SRS", "advanceToNext: REINSERT (count<3, no pending) at " + reinsertPos + " newQueueSize=" + studyQueue.size());
        }

        passPreviewed = false;
        failPreviewed = false;
        srsButtonMode.setValue(SrsButtonMode.HIDDEN);
        loadNextFromQueue();
    }

    private StudyStage computeRelearnStage(int correctCount) {
        if (correctCount == 0) return StudyStage.NEW;
        if (correctCount == 1) return StudyStage.REVIEW;
        if (correctCount == 2) return StudyStage.FINAL;
        return null;
    }

    private void insertRelearnEntry(String wordId) {
        int range = reviewSessionWords.size() - reviewSessionIndex;
        if (range <= 0) {
            reviewSessionWords.add(wordId);
        } else {
            int pos = reviewSessionIndex + 1 + random.nextInt(range);
            reviewSessionWords.add(pos, wordId);
        }
    }

    private boolean hasPendingEntryForWord(String wordId) {
        for (int i = currentQueueIndex + 1; i < studyQueue.size(); i++) {
            StudyQueueEntry e = studyQueue.get(i);
            if (e.getWordId().equals(wordId) && !e.isCompleted()) {
                return true;
            }
        }
        return false;
    }

    public void submitPass() {
        if (!isSRSMode || bookId == null) return;
        String currentId = currentWordId.getValue();
        if (currentId == null) return;

        if (isReviewMode) {
            return;
        }

        StudyQueueEntry entry = studyQueue.get(currentQueueIndex);
        entry.setCompleted(true);

        int count = wordCorrectCount.getOrDefault(currentId, 0) + 1;
        wordCorrectCount.put(currentId, count);

        Log.d("SRS", "submitPass: word=" + currentId + " count=" + count + " index=" + currentQueueIndex + "/" + studyQueue.size());

        if (count >= 3) {
            bookRepository.pass(currentId, bookId);
        }

        passPreviewed = false;
        failPreviewed = false;
        srsButtonMode.setValue(SrsButtonMode.HIDDEN);
        loadNextFromQueue();
    }

    public void submitFail() {
        if (!isSRSMode || bookId == null) return;
        String currentId = currentWordId.getValue();
        if (currentId == null) return;

        if (isReviewMode) {
            wordCorrectCount.put(currentId, 0);
            currentCorrectCount.postValue(0);
            insertRelearnEntry(currentId);
            passPreviewed = false;
            failPreviewed = false;
            inRelearnMode = false;
            isSRSMode = false;
            bookId = null;
            srsButtonMode.setValue(SrsButtonMode.HIDDEN);
            reviewSessionIndex++;
            loadReviewWord();
            return;
        }

        StudyQueueEntry entry = studyQueue.get(currentQueueIndex);
        entry.setCompleted(true);

        if (passPreviewed) {
            int count = wordCorrectCount.getOrDefault(currentId, 0) - 1;
            if (count < 0) count = 0;
            wordCorrectCount.put(currentId, count);
        }
        passPreviewed = false;
        failPreviewed = false;

        int currentCount = wordCorrectCount.getOrDefault(currentId, 0);
        Log.d("SRS", "submitFail: word=" + currentId + " count=" + currentCount + " index=" + currentQueueIndex + "/" + studyQueue.size());

        StudyStage failedStage = computeStageForWord(currentId);
        if (failedStage == null) failedStage = StudyStage.FINAL;

        if (failedStage == StudyStage.FINAL) {
            bookRepository.fail(currentId, bookId);
        }

        if (currentCount < 3 && !hasPendingEntryForWord(currentId)) {
            int reinsertPos = currentQueueIndex + 3 + random.nextInt(5);
            reinsertPos = Math.min(reinsertPos, studyQueue.size());
            studyQueue.add(reinsertPos, new StudyQueueEntry(currentId, StudyStage.NEW));
            sessionTotal.postValue(studyQueue.size());
            Log.d("SRS", "submitFail: REINSERT (count<3, no pending) at " + reinsertPos + " newQueueSize=" + studyQueue.size());
        }

        srsButtonMode.setValue(SrsButtonMode.HIDDEN);
        loadNextFromQueue();
    }

    public void submitDelete() {
        if (!isSRSMode || bookId == null) return;
        String currentId = currentWordId.getValue();
        if (currentId == null) return;
        bookRepository.deleteWord(currentId, bookId);
        loadNextFromQueue();
    }

    public void startNewWordIfNeeded() {
    }

    public void setReviewMode(boolean reviewMode, String bookId) {
        this.isReviewMode = reviewMode;
        this.reviewBookId = bookId;
        if (reviewMode) {
            initReviewSession();
        }
    }

    public void startNewReviewSession() {
        if (!isReviewMode) return;
        initReviewSession();
    }

    private void initReviewSession() {
        WordDatabase.databaseExecutor.execute(() -> {
            reviewSessionWords.clear();
            reviewWordBookIds.clear();
            reviewSessionIndex = 0;
            reviewCompletedCount = 0;
            relearnWords.clear();
            inRelearnMode = false;

            List<WordReviewEntity> dueReviews;
            if (reviewBookId != null && !reviewBookId.isEmpty()) {
                dueReviews = bookRepository.getDueReviews(reviewBookId, SESSION_WORD_COUNT);
            } else {
                dueReviews = bookRepository.getDueReviewsAllBooks(SESSION_WORD_COUNT);
            }
            for (WordReviewEntity review : dueReviews) {
                if (!reviewSessionWords.contains(review.getWordId())) {
                    reviewSessionWords.add(review.getWordId());
                    reviewWordBookIds.put(review.getWordId(), review.getBookId());
                }
            }

            if (reviewSessionWords.isEmpty()) {
                reviewSessionTotal.postValue(0);
                reviewSessionProgress.postValue(0);
                reviewSessionComplete.postValue(true);
                toastMessage.postValue("暂无到期复习单词");
                return;
            }

            reviewSessionTotal.postValue(reviewSessionWords.size());
            reviewSessionProgress.postValue(0);
            reviewSessionComplete.postValue(false);
            loadReviewWord();
        });
    }

    private void loadReviewWord() {
        if (reviewSessionIndex >= reviewSessionWords.size()) {
            reviewSessionComplete.postValue(true);
            toastMessage.postValue("本组复习完成");
            return;
        }

        String wordId = reviewSessionWords.get(reviewSessionIndex);
        wordGeneration.incrementAndGet();
        currentWordId.postValue(wordId);
        saveCurrentId(wordId);

        if (relearnWords.contains(wordId)) {
            inRelearnMode = true;
            isSRSMode = true;
            bookId = reviewWordBookIds.get(wordId);
            int count = wordCorrectCount.getOrDefault(wordId, 0);
            StudyStage stage = computeRelearnStage(count);
            if (stage == null) {
                relearnWords.remove(wordId);
                inRelearnMode = false;
                isSRSMode = false;
                bookId = null;
                reviewSessionIndex++;
                loadReviewWord();
                return;
            }
            currentCorrectCount.postValue(count);
            currentStage.postValue(stage);
            reviewStage.postValue(null);
            srsButtonMode.postValue(SrsButtonMode.HIDDEN);
            if (stage == StudyStage.NEW) {
                generateMultipleChoiceOptions(wordId);
            } else {
                multipleChoiceOptions.postValue(null);
            }
        } else {
            inRelearnMode = false;
            isSRSMode = false;
            reviewStage.postValue(ReviewStage.SHOW_WORD);
            reviewAnswerRevealed.postValue(false);
            srsButtonMode.postValue(SrsButtonMode.HIDDEN);
        }

        entryVersion++;
        entryLoaded.postValue(entryVersion);
    }

    public void reviewRecognize() {
        if (!isReviewMode) return;
        reviewStage.setValue(ReviewStage.REVEAL_RECOGNIZE);
        reviewAnswerRevealed.setValue(true);
    }

    public void reviewFuzzy() {
        if (!isReviewMode) return;
        reviewStage.setValue(ReviewStage.REVEAL_FUZZY);
        reviewAnswerRevealed.setValue(true);
    }

    public void reviewForget() {
        if (!isReviewMode) return;
        reviewStage.setValue(ReviewStage.REVEAL_FAIL);
        reviewAnswerRevealed.setValue(true);
    }

    public void reviewMarkWrong() {
        if (!isReviewMode) return;
        ReviewStage current = reviewStage.getValue();
        if (current == ReviewStage.REVEAL_RECOGNIZE || current == ReviewStage.REVEAL_FUZZY) {
            reviewStage.setValue(ReviewStage.REVEAL_FAIL);
        }
    }

    public void reviewAdvance() {
        if (!isReviewMode) return;
        String currentId = currentWordId.getValue();
        if (currentId == null) return;

        ReviewStage stage = reviewStage.getValue();
        String bid = reviewWordBookIds.get(currentId);

        if (stage == ReviewStage.REVEAL_RECOGNIZE) {
            if (bid != null) bookRepository.pass(currentId, bid);
            reviewCompletedCount++;
            reviewSessionProgress.postValue(reviewCompletedCount);
        } else if (stage == ReviewStage.REVEAL_FUZZY) {
            if (bid != null) bookRepository.fuzzyPass(currentId, bid);
        } else if (stage == ReviewStage.REVEAL_FAIL) {
            if (bid != null) bookRepository.fail(currentId, bid);
            relearnWords.add(currentId);
            wordCorrectCount.put(currentId, 0);
            insertRelearnEntry(currentId);
        }

        reviewSessionIndex++;
        reviewStage.postValue(ReviewStage.SHOW_WORD);
        reviewAnswerRevealed.postValue(false);
        srsButtonMode.postValue(SrsButtonMode.HIDDEN);
        loadReviewWord();
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
        int gen = wordGeneration.get();
        for (int g : sourceGenerations) {
            if (g != gen) return;
        }

        BasicWord basicWord = basicWordLiveData.getValue();
        if (basicWord == null) return;
        if (wordMeaningListLiveData.getValue() == null) return;
        if (wordSentenceListLiveData.getValue() == null) return;
        if (wordCollocationListLiveData.getValue() == null) return;
        if (antonymWordListLiveData.getValue() == null) return;
        if (synonymWordListLiveData.getValue() == null) return;
        if (conjugationFormListLiveData.getValue() == null) return;
        if (etymologyListLiveData.getValue() == null) return;
        if (kanjiInfoListLiveData.getValue() == null) return;
        if (usageDistinctionListLiveData.getValue() == null) return;
        if (grammarPointListLiveData.getValue() == null) return;
        if (idiomListLiveData.getValue() == null) return;
        if (derivedWordListLiveData.getValue() == null) return;
        if (relatedWordListLiveData.getValue() == null) return;


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
