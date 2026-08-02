package com.Nihilisttt.LearnWord.Page.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.Nihilisttt.LearnWord.Database.Database.WordDatabase;
import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordMeaningEntity;
import com.Nihilisttt.LearnWord.Database.Repository.WordRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchPageViewModel extends AndroidViewModel {
    private final WordRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<SearchResult>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<String> currentQuery = new MutableLiveData<>("");

    public static class SearchResult {
        public final String wordId;
        public final String kanji;
        public final String kana;
        public final String firstMeaning;

        public SearchResult(String wordId, String kanji, String kana, String firstMeaning) {
            this.wordId = wordId;
            this.kanji = kanji;
            this.kana = kana;
            this.firstMeaning = firstMeaning;
        }
    }

    public SearchPageViewModel(@NonNull Application application) {
        super(application);
        repository = WordRepository.getInstance(application);
    }

    public MutableLiveData<List<SearchResult>> getSearchResults() {
        return searchResults;
    }

    public MutableLiveData<String> getCurrentQuery() {
        return currentQuery;
    }

    public void search(String query) {
        currentQuery.setValue(query);
        if (query == null || query.trim().isEmpty()) {
            searchResults.setValue(Collections.emptyList());
            return;
        }
        String trimmed = query.trim();
        executor.execute(() -> {
            List<BasicWordEntity> basicWords = repository.searchBasicWordsSync(trimmed, 50);
            if (basicWords.isEmpty()) {
                searchResults.postValue(Collections.emptyList());
                return;
            }
            List<String> wordIds = new ArrayList<>();
            for (BasicWordEntity bw : basicWords) {
                wordIds.add(bw.getWordId());
            }
            List<WordMeaningEntity> meanings = repository.getWordMeaningsByWordIdListSync(wordIds);
            java.util.Map<String, String> firstMeaningMap = new java.util.HashMap<>();
            for (WordMeaningEntity m : meanings) {
                if (!firstMeaningMap.containsKey(m.getWordId())) {
                    String pos = m.getPartOfSpeech();
                    String trans = m.getTranslationDefinition();
                    StringBuilder sb = new StringBuilder();
                    if (pos != null && !pos.isEmpty()) {
                        sb.append(pos).append(" ");
                    }
                    if (trans != null && !trans.isEmpty()) {
                        sb.append(trans);
                    }
                    firstMeaningMap.put(m.getWordId(), sb.toString());
                }
            }
            List<SearchResult> results = new ArrayList<>();
            for (BasicWordEntity bw : basicWords) {
                String kanji = flattenJsonArray(bw.getKanjiComponents());
                String kana = flattenJsonArray(bw.getKanaComponents());
                String meaning = firstMeaningMap.getOrDefault(bw.getWordId(), "");
                results.add(new SearchResult(bw.getWordId(), kanji, kana, meaning));
            }
            results = deduplicateResults(results);
            searchResults.postValue(results);
        });
    }

    private List<SearchResult> deduplicateResults(List<SearchResult> results) {
        java.util.LinkedHashMap<String, SearchResult> best = new java.util.LinkedHashMap<>();
        for (SearchResult r : results) {
            String key = r.kanji + "|" + r.kana;
            SearchResult existing = best.get(key);
            if (existing == null) {
                best.put(key, r);
            } else if (existing.firstMeaning.isEmpty() && !r.firstMeaning.isEmpty()) {
                best.put(key, r);
            }
        }
        return new ArrayList<>(best.values());
    }

    private String flattenJsonArray(String json) {
        if (json == null || json.isEmpty()) return "";
        com.google.gson.Gson gson = new com.google.gson.Gson();
        try {
            List<String> list = gson.fromJson(json, new com.google.gson.reflect.TypeToken<List<String>>(){}.getType());
            if (list != null) {
                StringBuilder sb = new StringBuilder();
                for (String s : list) sb.append(s);
                return sb.toString();
            }
        } catch (Exception ignored) {}
        try {
            List<List<String>> nestedList = gson.fromJson(json, new com.google.gson.reflect.TypeToken<List<List<String>>>(){}.getType());
            if (nestedList != null) {
                StringBuilder sb = new StringBuilder();
                for (List<String> inner : nestedList) {
                    if (inner != null) {
                        for (String s : inner) sb.append(s);
                    }
                }
                return sb.toString();
            }
        } catch (Exception ignored) {}
        return json;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }
}