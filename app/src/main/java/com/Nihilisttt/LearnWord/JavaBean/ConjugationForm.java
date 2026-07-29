package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConjugationForm extends WordComponent {
    private final String conjugationFormId;
    private final String formName;

    public static class Builder {
        private String conjugationFormId;
        private String wordId;
        private String formName;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;

        public Builder conjugationFormId(String id) { this.conjugationFormId = id; return this; }
        public Builder wordId(String wordId) { this.wordId = wordId; return this; }
        public Builder formName(String formName) { this.formName = formName; return this; }
        public Builder kanjiComponents(List<String> components) { this.kanjiComponents = new ArrayList<>(components); return this; }
        public Builder kanaComponents(List<String> components) { this.kanaComponents = new ArrayList<>(components); return this; }

        public ConjugationForm build() {
            return new ConjugationForm(
                    conjugationFormId, wordId, formName,
                    unmodifiableListOf(kanjiComponents), unmodifiableListOf(kanaComponents)
            );
        }
    }

    private ConjugationForm(String conjugationFormId, String wordId, String formName,
                            List<String> kanjiComponents, List<String> kanaComponents) {
        super(wordId, kanjiComponents, kanaComponents);
        this.conjugationFormId = conjugationFormId;
        this.formName = formName;
    }

    public String getConjugationFormId() { return conjugationFormId; }
    public String getFormName() { return formName; }

    @Override
    public String toString() {
        return formName + ": " + getCompositeKanji() + " (" + getCompositeKana() + ")";
    }
}
