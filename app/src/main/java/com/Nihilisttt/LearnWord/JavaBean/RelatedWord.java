package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RelatedWord extends CorrespondingWord {
    private final String relatedWordId;

    public static class Builder {
        private String relatedWordId;
        private String wordId;
        private String correspondingWordId;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;

        public Builder relatedWordId(String relatedWordId) {
            this.relatedWordId = relatedWordId;
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

        public RelatedWord build() {
            return new RelatedWord(
                    this.relatedWordId, this.wordId, this.correspondingWordId,
                    Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents)
            );
        }
    }

    private RelatedWord(String relatedWordId, String wordId, String correspondingWordId,
                        List<String> kanjiComponents, List<String> kanaComponents) {
        super(wordId, correspondingWordId, kanjiComponents, kanaComponents);
        this.relatedWordId = relatedWordId;
    }

    public String getRelatedWordId() { return relatedWordId; }

    @Override
    public String toString() {
        return getCompositeKanji() + " (" + getCompositeKana() + ") - id = " + getCorrespondingWordId();
    }
}