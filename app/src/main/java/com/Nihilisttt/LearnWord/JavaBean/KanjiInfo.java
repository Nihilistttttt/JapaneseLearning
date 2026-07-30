package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KanjiInfo {
    private final String kanjiInfoId;
    private final String wordId;
    private final String kanji;
    private final List<String> onyomi;
    private final List<String> kunyomi;
    private final List<String> sameKanjiWords;
    private final String translation;

    public static class Builder {
        private String kanjiInfoId;
        private String wordId;
        private String kanji;
        private List<String> onyomi;
        private List<String> kunyomi;
        private List<String> sameKanjiWords;
        private String translation;

        public Builder kanjiInfoId(String id) { this.kanjiInfoId = id; return this; }
        public Builder wordId(String wordId) { this.wordId = wordId; return this; }
        public Builder kanji(String kanji) { this.kanji = kanji; return this; }
        public Builder onyomi(List<String> onyomi) { this.onyomi = new ArrayList<>(onyomi); return this; }
        public Builder kunyomi(List<String> kunyomi) { this.kunyomi = new ArrayList<>(kunyomi); return this; }
        public Builder sameKanjiWords(List<String> words) { this.sameKanjiWords = new ArrayList<>(words); return this; }
        public Builder translation(String translation) { this.translation = translation; return this; }

        public KanjiInfo build() {
            return new KanjiInfo(
                    kanjiInfoId, wordId, kanji,
                    onyomi != null ? Collections.unmodifiableList(onyomi) : Collections.emptyList(),
                    kunyomi != null ? Collections.unmodifiableList(kunyomi) : Collections.emptyList(),
                    sameKanjiWords != null ? Collections.unmodifiableList(sameKanjiWords) : Collections.emptyList(),
                    translation != null ? translation : ""
            );
        }
    }

    private KanjiInfo(String kanjiInfoId, String wordId, String kanji,
                      List<String> onyomi, List<String> kunyomi, List<String> sameKanjiWords, String translation) {
        this.kanjiInfoId = kanjiInfoId;
        this.wordId = wordId;
        this.kanji = kanji;
        this.onyomi = onyomi;
        this.kunyomi = kunyomi;
        this.sameKanjiWords = sameKanjiWords;
        this.translation = translation;
    }

    public String getKanjiInfoId() { return kanjiInfoId; }
    public String getWordId() { return wordId; }
    public String getKanji() { return kanji; }
    public List<String> getOnyomi() { return onyomi; }
    public List<String> getKunyomi() { return kunyomi; }
    public List<String> getSameKanjiWords() { return sameKanjiWords; }
    public String getTranslation() { return translation; }

    @Override
    public String toString() {
        return kanji + " 音読み:" + onyomi + " 訓読み:" + kunyomi;
    }
}