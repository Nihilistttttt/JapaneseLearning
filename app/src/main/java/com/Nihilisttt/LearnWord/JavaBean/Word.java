package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Word {
    private final String wordId;
    private final List<String> antonymIdList;
    private final List<String> synonymIdList;
    private final List<String> collocationIdList;
    private final List<String> meaningIdList;
    private final List<String> sentenceIdList;
    private final List<String> conjugationFormIdList;
    private final List<String> etymologyIdList;
    private final List<String> kanjiInfoIdList;
    private final List<String> usageDistinctionIdList;
    private final List<String> grammarPointIdList;
    private final List<String> idiomIdList;

    // 私有构造函数，只能通过Builder创建实例
    private Word(String wordId,
                 List<String> antonymIdList,
                 List<String> synonymIdList,
                 List<String> collocationIdList,
                 List<String> meaningIdList,
                 List<String> sentenceIdList,
                 List<String> conjugationFormIdList,
                 List<String> etymologyIdList,
                 List<String> kanjiInfoIdList,
                 List<String> usageDistinctionIdList,
                 List<String> grammarPointIdList,
                 List<String> idiomIdList) {
        this.wordId = wordId;
        this.antonymIdList = Collections.unmodifiableList(antonymIdList);
        this.synonymIdList = Collections.unmodifiableList(synonymIdList);
        this.collocationIdList = Collections.unmodifiableList(collocationIdList);
        this.meaningIdList = Collections.unmodifiableList(meaningIdList);
        this.sentenceIdList = Collections.unmodifiableList(sentenceIdList);
        this.conjugationFormIdList = Collections.unmodifiableList(conjugationFormIdList);
        this.etymologyIdList = Collections.unmodifiableList(etymologyIdList);
        this.kanjiInfoIdList = Collections.unmodifiableList(kanjiInfoIdList);
        this.usageDistinctionIdList = Collections.unmodifiableList(usageDistinctionIdList);
        this.grammarPointIdList = Collections.unmodifiableList(grammarPointIdList);
        this.idiomIdList = Collections.unmodifiableList(idiomIdList);
    }

    public static Builder builder() {
        return new Builder();
    }

    // 原getter方法保持不变
    public String getWordId() { return wordId; }
    public List<String> getAntonymIdList() { return antonymIdList; }
    public List<String> getSynonymIdList() { return synonymIdList; }
    public List<String> getCollocationIdList() { return collocationIdList; }
    public List<String> getMeaningIdList() { return meaningIdList; }
    public List<String> getSentenceIdList() { return sentenceIdList; }
    public List<String> getConjugationFormIdList() { return conjugationFormIdList; }
    public List<String> getEtymologyIdList() { return etymologyIdList; }
    public List<String> getKanjiInfoIdList() { return kanjiInfoIdList; }
    public List<String> getUsageDistinctionIdList() { return usageDistinctionIdList; }
    public List<String> getGrammarPointIdList() { return grammarPointIdList; }
    public List<String> getIdiomIdList() { return idiomIdList; }

    public static class Builder {
        private String wordId;
        private List<String> antonymIdList = new ArrayList<>();
        private List<String> synonymIdList = new ArrayList<>();
        private List<String> collocationIdList = new ArrayList<>();
        private List<String> meaningIdList = new ArrayList<>();
        private List<String> sentenceIdList = new ArrayList<>();
        private List<String> conjugationFormIdList = new ArrayList<>();
        private List<String> etymologyIdList = new ArrayList<>();
        private List<String> kanjiInfoIdList = new ArrayList<>();
        private List<String> usageDistinctionIdList = new ArrayList<>();
        private List<String> grammarPointIdList = new ArrayList<>();
        private List<String> idiomIdList = new ArrayList<>();

        public Builder addWordId(String wordId) {
            this.wordId = Objects.requireNonNull(wordId);
            return this;
        }

        // 单个添加方法
        public Builder addAntonymId(String antonymId) {
            this.antonymIdList.add(antonymId);
            return this;
        }

        // 批量添加方法
        public Builder withAntonymIdList(List<String> antonymIdList) {
            this.antonymIdList = new ArrayList<>(antonymIdList);
            return this;
        }

        public Builder addSynonymId(String synonymId) {
            this.synonymIdList.add(synonymId);
            return this;
        }

        public Builder withSynonymIdList(List<String> synonymIdList) {
            this.synonymIdList = new ArrayList<>(synonymIdList);
            return this;
        }

        public Builder addCollocationId(String collocationId) {
            this.collocationIdList.add(collocationId);
            return this;
        }

        public Builder withCollocationIdList(List<String> collocationIdList) {
            this.collocationIdList = new ArrayList<>(collocationIdList);
            return this;
        }

        public Builder addMeaningId(String meaningId) {
            this.meaningIdList.add(meaningId);
            return this;
        }

        public Builder withMeaningIdList(List<String> meaningIdList) {
            this.meaningIdList = new ArrayList<>(meaningIdList);
            return this;
        }

        public Builder addSentenceId(String sentenceId) {
            this.sentenceIdList.add(sentenceId);
            return this;
        }

        public Builder withSentenceIdList(List<String> sentenceIdList) {
            this.sentenceIdList = new ArrayList<>(sentenceIdList);
            return this;
        }

        public Builder withConjugationFormIdList(List<String> conjugationFormIdList) {
            this.conjugationFormIdList = new ArrayList<>(conjugationFormIdList);
            return this;
        }

        public Builder withEtymologyIdList(List<String> etymologyIdList) {
            this.etymologyIdList = new ArrayList<>(etymologyIdList);
            return this;
        }

        public Builder withKanjiInfoIdList(List<String> kanjiInfoIdList) {
            this.kanjiInfoIdList = new ArrayList<>(kanjiInfoIdList);
            return this;
        }

        public Builder withUsageDistinctionIdList(List<String> usageDistinctionIdList) {
            this.usageDistinctionIdList = new ArrayList<>(usageDistinctionIdList);
            return this;
        }

        public Builder withGrammarPointIdList(List<String> grammarPointIdList) {
            this.grammarPointIdList = new ArrayList<>(grammarPointIdList);
            return this;
        }

        public Builder withIdiomIdList(List<String> idiomIdList) {
            this.idiomIdList = new ArrayList<>(idiomIdList);
            return this;
        }

        public Word build() {
            // 必要参数校验
            Objects.requireNonNull(wordId, "wordId must not be null");

            // 处理可能为null的列表（防御性拷贝）
            return new Word(
                    wordId,
                    antonymIdList != null ? new ArrayList<>(antonymIdList) : new ArrayList<>(),
                    synonymIdList != null ? new ArrayList<>(synonymIdList) : new ArrayList<>(),
                    collocationIdList != null ? new ArrayList<>(collocationIdList) : new ArrayList<>(),
                    meaningIdList != null ? new ArrayList<>(meaningIdList) : new ArrayList<>(),
                    sentenceIdList != null ? new ArrayList<>(sentenceIdList) : new ArrayList<>(),
                    conjugationFormIdList != null ? new ArrayList<>(conjugationFormIdList) : new ArrayList<>(),
                    etymologyIdList != null ? new ArrayList<>(etymologyIdList) : new ArrayList<>(),
                    kanjiInfoIdList != null ? new ArrayList<>(kanjiInfoIdList) : new ArrayList<>(),
                    usageDistinctionIdList != null ? new ArrayList<>(usageDistinctionIdList) : new ArrayList<>(),
                    grammarPointIdList != null ? new ArrayList<>(grammarPointIdList) : new ArrayList<>(),
                    idiomIdList != null ? new ArrayList<>(idiomIdList) : new ArrayList<>()
            );
        }
    }

    // 其他方法如equals/hashCode可根据需要添加
}
