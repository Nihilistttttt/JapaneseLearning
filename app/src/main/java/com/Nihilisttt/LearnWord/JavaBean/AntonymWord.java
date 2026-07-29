package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AntonymWord extends CorrespondingWord {
    private final String antonymWordId;

    public static class Builder {
        private String antonymWordId;
        private String wordId;
        private String correspondingWordId;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;

        public Builder antonymWordId(String antonymWordId) {
            this.antonymWordId = antonymWordId;
            return this;
        }

        public Builder wordId(String wordId) {
            this.wordId = wordId;
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

        public Builder correspondingWordId(String correspondingWordId) {
            this.correspondingWordId = correspondingWordId;
            return this;
        }

        public AntonymWord build() {
            return new AntonymWord(
                    antonymWordId, wordId, correspondingWordId,
                    Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents)
            );
        }
    }

    private AntonymWord(String antonymWordId, String wordId, String correspondingWordId,
                        List<String> kanjiComponents, List<String> kanaComponents) {
        super(wordId, correspondingWordId, kanjiComponents, kanaComponents);
        this.antonymWordId = antonymWordId;
    }

    public String getAntonymWordId() { return antonymWordId; }

    @Override
    public String toString() {
        return getCompositeKanji() + " (" + getCompositeKana() + ") - id = " + getCorrespondingWordId();
    }
}
