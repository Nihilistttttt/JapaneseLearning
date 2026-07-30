package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Etymology {
    private final String etymologyId;
    private final String wordId;
    private final String etymologyType;
    private final List<List<String>> kanjiComponents;
    private final List<List<String>> kanaComponents;
    private final List<String> wordIdList;
    private final String translation;

    private Etymology(String etymologyId, String wordId, String etymologyType,
                      List<List<String>> kanjiComponents, List<List<String>> kanaComponents,
                      List<String> wordIdList, String translation) {
        this.etymologyId = etymologyId;
        this.wordId = wordId;
        this.etymologyType = etymologyType;
        this.kanjiComponents = Collections.unmodifiableList(kanjiComponents);
        this.kanaComponents = Collections.unmodifiableList(kanaComponents);
        this.wordIdList = Collections.unmodifiableList(wordIdList);
        this.translation = translation;
    }

    public String getEtymologyId() { return etymologyId; }
    public String getWordId() { return wordId; }
    public String getEtymologyType() { return etymologyType; }
    public List<List<String>> getKanjiComponents() { return kanjiComponents; }
    public List<List<String>> getKanaComponents() { return kanaComponents; }
    public List<String> getWordIdList() { return wordIdList; }
    public String getTranslation() { return translation; }

    public static class Builder {
        private String etymologyId;
        private String wordId;
        private String etymologyType;
        private List<List<String>> kanjiComponents = new ArrayList<>();
        private List<List<String>> kanaComponents = new ArrayList<>();
        private List<String> wordIdList = new ArrayList<>();
        private String translation = "";

        public Builder etymologyId(String etymologyId) { this.etymologyId = etymologyId; return this; }
        public Builder wordId(String wordId) { this.wordId = wordId; return this; }
        public Builder etymologyType(String etymologyType) { this.etymologyType = etymologyType; return this; }
        public Builder kanjiComponents(List<List<String>> kanjiComponents) { this.kanjiComponents = new ArrayList<>(kanjiComponents); return this; }
        public Builder kanaComponents(List<List<String>> kanaComponents) { this.kanaComponents = new ArrayList<>(kanaComponents); return this; }
        public Builder wordIdList(List<String> wordIdList) { this.wordIdList = new ArrayList<>(wordIdList); return this; }
        public Builder translation(String translation) { this.translation = translation; return this; }

        public Etymology build() {
            Objects.requireNonNull(etymologyId, "etymologyId must not be null");
            return new Etymology(etymologyId, wordId, etymologyType,
                    kanjiComponents != null ? new ArrayList<>(kanjiComponents) : new ArrayList<>(),
                    kanaComponents != null ? new ArrayList<>(kanaComponents) : new ArrayList<>(),
                    wordIdList != null ? new ArrayList<>(wordIdList) : new ArrayList<>(),
                    translation != null ? translation : "");
        }
    }
}