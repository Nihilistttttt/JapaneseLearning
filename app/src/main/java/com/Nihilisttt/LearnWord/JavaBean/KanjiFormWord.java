package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KanjiFormWord {
    private final String kanjiFormId;
    private final String wordId;
    private final List<String> kanjiComponents;
    private final List<String> kanaComponents;

    public static class Builder {
        private String kanjiFormId;
        private String wordId;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;

        public Builder kanjiFormId(String kanjiFormId) { this.kanjiFormId = kanjiFormId; return this; }
        public Builder wordId(String wordId) { this.wordId = wordId; return this; }
        public Builder kanjiComponents(List<String> components) {
            if (components == null || components.isEmpty()) throw new IllegalArgumentException("Kanji组件至少需要1个元素");
            this.kanjiComponents = new ArrayList<>(components);
            return this;
        }
        public Builder kanaComponents(List<String> components) {
            if (components == null || components.isEmpty()) throw new IllegalArgumentException("Kana组件至少需要1个元素");
            this.kanaComponents = new ArrayList<>(components);
            return this;
        }

        public KanjiFormWord build() {
            return new KanjiFormWord(kanjiFormId, wordId,
                    Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents));
        }
    }

    private KanjiFormWord(String kanjiFormId, String wordId, List<String> kanjiComponents, List<String> kanaComponents) {
        this.kanjiFormId = kanjiFormId;
        this.wordId = wordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
    }

    public String getKanjiFormId() { return kanjiFormId; }
    public String getWordId() { return wordId; }
    public List<String> getKanjiComponents() { return kanjiComponents; }
    public List<String> getKanaComponents() { return kanaComponents; }
    public String getCompositeKanji() { return String.join("", kanjiComponents); }
    public String getCompositeKana() { return String.join("", kanaComponents); }
}