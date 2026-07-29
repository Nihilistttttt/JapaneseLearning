package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SynonymWord extends CorrespondingWord {
    private final String synonymWordId;

    public static class Builder {
        private String synonymWordId;
        private String wordId;
        private String correspondingWordId;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;

        public Builder synonymWordId(String synonymWordId) {
            this.synonymWordId = synonymWordId;
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

        public SynonymWord build() {
            return new SynonymWord(
                    this.synonymWordId, this.wordId, this.correspondingWordId,
                    Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents)
            );
        }
    }

    private SynonymWord(String synonymWordId, String wordId, String correspondingWordId,
                        List<String> kanjiComponents, List<String> kanaComponents) {
        super(wordId, correspondingWordId, kanjiComponents, kanaComponents);
        this.synonymWordId = synonymWordId;
    }

    public String getSynonymWordId() { return synonymWordId; }

    @Override
    public String toString() {
        return getCompositeKanji() + " (" + getCompositeKana() + ") - id = " + getCorrespondingWordId();
    }
}
