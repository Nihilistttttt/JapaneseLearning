package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrammarPoint {
    private final String grammarPointId;
    private final String wordId;
    private final String grammarName;
    private final String grammarDescription;
    private final String exampleKanji;
    private final String exampleKana;
    private final List<String> nameKanjiComponents;
    private final List<String> nameKanaComponents;
    private final List<String> descKanjiComponents;
    private final List<String> descKanaComponents;

    public static class Builder {
        private String grammarPointId;
        private String wordId;
        private String grammarName;
        private String grammarDescription;
        private String exampleKanji;
        private String exampleKana;
        private List<String> nameKanjiComponents;
        private List<String> nameKanaComponents;
        private List<String> descKanjiComponents;
        private List<String> descKanaComponents;

        public Builder grammarPointId(String id) { this.grammarPointId = id; return this; }
        public Builder wordId(String wordId) { this.wordId = wordId; return this; }
        public Builder grammarName(String name) { this.grammarName = name; return this; }
        public Builder grammarDescription(String desc) { this.grammarDescription = desc; return this; }
        public Builder exampleKanji(String kanji) { this.exampleKanji = kanji; return this; }
        public Builder exampleKana(String kana) { this.exampleKana = kana; return this; }
        public Builder nameKanjiComponents(List<String> components) { this.nameKanjiComponents = new ArrayList<>(components); return this; }
        public Builder nameKanaComponents(List<String> components) { this.nameKanaComponents = new ArrayList<>(components); return this; }
        public Builder descKanjiComponents(List<String> components) { this.descKanjiComponents = new ArrayList<>(components); return this; }
        public Builder descKanaComponents(List<String> components) { this.descKanaComponents = new ArrayList<>(components); return this; }

        public GrammarPoint build() {
            return new GrammarPoint(grammarPointId, wordId, grammarName, grammarDescription, exampleKanji, exampleKana,
                    nameKanjiComponents != null ? Collections.unmodifiableList(nameKanjiComponents) : Collections.emptyList(),
                    nameKanaComponents != null ? Collections.unmodifiableList(nameKanaComponents) : Collections.emptyList(),
                    descKanjiComponents != null ? Collections.unmodifiableList(descKanjiComponents) : Collections.emptyList(),
                    descKanaComponents != null ? Collections.unmodifiableList(descKanaComponents) : Collections.emptyList()
            );
        }
    }

    private GrammarPoint(String grammarPointId, String wordId, String grammarName,
                         String grammarDescription, String exampleKanji, String exampleKana,
                         List<String> nameKanjiComponents, List<String> nameKanaComponents,
                         List<String> descKanjiComponents, List<String> descKanaComponents) {
        this.grammarPointId = grammarPointId;
        this.wordId = wordId;
        this.grammarName = grammarName;
        this.grammarDescription = grammarDescription;
        this.exampleKanji = exampleKanji;
        this.exampleKana = exampleKana;
        this.nameKanjiComponents = nameKanjiComponents;
        this.nameKanaComponents = nameKanaComponents;
        this.descKanjiComponents = descKanjiComponents;
        this.descKanaComponents = descKanaComponents;
    }

    public String getGrammarPointId() { return grammarPointId; }
    public String getWordId() { return wordId; }
    public String getGrammarName() { return grammarName; }
    public String getGrammarDescription() { return grammarDescription; }
    public String getExampleKanji() { return exampleKanji; }
    public String getExampleKana() { return exampleKana; }
    public List<String> getNameKanjiComponents() { return nameKanjiComponents; }
    public List<String> getNameKanaComponents() { return nameKanaComponents; }
    public List<String> getDescKanjiComponents() { return descKanjiComponents; }
    public List<String> getDescKanaComponents() { return descKanaComponents; }

    @Override
    public String toString() {
        return grammarName + ": " + grammarDescription;
    }
}
