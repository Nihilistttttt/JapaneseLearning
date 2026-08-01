package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DerivedWord extends CorrespondingWord {
    private final String derivedWordId;

    public static class Builder {
        private String derivedWordId;
        private String wordId;
        private String correspondingWordId;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;

        public Builder derivedWordId(String derivedWordId) {
            this.derivedWordId = derivedWordId;
            return this;
        }

        public Builder wordId(String wordId) {
            this.wordId = wordId;
            return this;
        }

        public Builder correspondingWordId(String correspondingWordId) {
            this.correspondingWordId = correspondingWordId;
            return this;
        }

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

        public DerivedWord build() {
            return new DerivedWord(
                    this.derivedWordId, this.wordId, this.correspondingWordId,
                    Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents)
            );
        }
    }

    private DerivedWord(String derivedWordId, String wordId, String correspondingWordId,
                        List<String> kanjiComponents, List<String> kanaComponents) {
        super(wordId, correspondingWordId, kanjiComponents, kanaComponents);
        this.derivedWordId = derivedWordId;
    }

    public String getDerivedWordId() { return derivedWordId; }

    @Override
    public String toString() {
        return getCompositeKanji() + " (" + getCompositeKana() + ") - id = " + getCorrespondingWordId();
    }
}