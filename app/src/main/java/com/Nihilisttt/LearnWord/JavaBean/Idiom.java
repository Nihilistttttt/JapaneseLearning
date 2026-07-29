package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Idiom extends WordComponent {
    private final String idiomId;
    private final String translation;
    private final List<String> wordIdList;

    public static class Builder {
        private String idiomId;
        private String wordId;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;
        private String translation;
        private List<String> wordIdList;

        public Builder idiomId(String id) { this.idiomId = id; return this; }
        public Builder wordId(String wordId) { this.wordId = wordId; return this; }
        public Builder kanjiComponents(List<String> components) { this.kanjiComponents = new ArrayList<>(components); return this; }
        public Builder kanaComponents(List<String> components) { this.kanaComponents = new ArrayList<>(components); return this; }
        public Builder translation(String translation) { this.translation = translation; return this; }
        public Builder wordIdList(List<String> ids) { this.wordIdList = new ArrayList<>(ids); return this; }

        public Idiom build() {
            return new Idiom(
                    idiomId, wordId,
                    unmodifiableListOf(kanjiComponents), unmodifiableListOf(kanaComponents),
                    translation,
                    wordIdList != null ? Collections.unmodifiableList(wordIdList) : Collections.emptyList()
            );
        }
    }

    private Idiom(String idiomId, String wordId, List<String> kanjiComponents,
                  List<String> kanaComponents, String translation, List<String> wordIdList) {
        super(wordId, kanjiComponents, kanaComponents);
        this.idiomId = idiomId;
        this.translation = translation;
        this.wordIdList = wordIdList;
    }

    public String getIdiomId() { return idiomId; }
    public String getTranslation() { return translation; }
    public List<String> getWordIdList() { return wordIdList; }

    @Override
    public String toString() {
        return getCompositeKanji() + " (" + getCompositeKana() + "): " + translation;
    }
}
