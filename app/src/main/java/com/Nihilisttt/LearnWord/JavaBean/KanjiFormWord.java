package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KanjiFormWord extends WordComponent {
    private final String kanjiFormId;

    public static class Builder {
        private String kanjiFormId;
        private String wordId;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;

        public Builder kanjiFormId(String kanjiFormId) { this.kanjiFormId = kanjiFormId; return this; }
        public Builder wordId(String wordId) { this.wordId = wordId; return this; }
        public Builder kanjiComponents(List<String> components) {
            validateComponentsNotEmpty(components, "Kanji");
            this.kanjiComponents = new ArrayList<>(components);
            return this;
        }
        public Builder kanaComponents(List<String> components) {
            validateComponentsNotEmpty(components, "Kana");
            this.kanaComponents = new ArrayList<>(components);
            return this;
        }

        public KanjiFormWord build() {
            return new KanjiFormWord(kanjiFormId, wordId,
                    Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents));
        }
    }

    private KanjiFormWord(String kanjiFormId, String wordId, List<String> kanjiComponents, List<String> kanaComponents) {
        super(wordId, kanjiComponents, kanaComponents);
        this.kanjiFormId = kanjiFormId;
    }

    public String getKanjiFormId() { return kanjiFormId; }
}
