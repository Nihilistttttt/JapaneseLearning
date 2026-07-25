package com.Nihilisttt.LearnWord.JavaBean;

import com.Nihilisttt.LearnWord.UtilityClass.Constants;

import java.util.Objects;

public class WordMeaning {

    private final Constants.PartOfSpeech partOfSpeech;
    private final String originalDefinition;
    private final String translationDefinition;
    private final String definitionSummary;
    private final String wordMeaningId;
    private final String wordId;

    public static class Builder {
        private String wordMeaningId;
        private String wordId;
        private Constants.PartOfSpeech partOfSpeech = Constants.PartOfSpeech.UNKNOWN;
        private String originalDefinition;
        private String translationDefinition;
        private String definitionSummary;

        public Builder wordMeaningId(String wordMeaningId) {
            this.wordMeaningId = wordMeaningId;
            return this;
        }

        public Builder wordId(String wordId) {
            this.wordId = wordId;
            return this;
        }

        public Builder partOfSpeech(Constants.PartOfSpeech pos) {
            this.partOfSpeech = Objects.requireNonNull(pos);
            return this;
        }

        public Builder originalDefinition(String definition) {
            this.originalDefinition = validateDefinition(definition, "原文定义");
            return this;
        }

        public Builder translationDefinition(String definition) {
            this.translationDefinition = validateDefinition(definition, "翻译定义");
            return this;
        }

        public Builder definitionSummary(String summary) {
            this.definitionSummary = summary != null ? summary : "";
            return this;
        }

        private String validateDefinition(String definition, String fieldName) {
            if (definition == null) {
                throw new IllegalArgumentException(fieldName + "不能为null");
            }
            String trimmed = definition.trim();
            if (trimmed.isEmpty()) {
                throw new IllegalArgumentException(fieldName + "内容不能为空");
            }
            return trimmed;
        }

        public WordMeaning build() {
            if (definitionSummary == null || definitionSummary.isEmpty()) {
                int semiIdx = originalDefinition.indexOf(';');
                definitionSummary = semiIdx > 0 ? originalDefinition.substring(0, semiIdx) : originalDefinition;
            }
            return new WordMeaning(
                    wordMeaningId,
                    wordId,
                    originalDefinition,
                    translationDefinition,
                    definitionSummary,
                    partOfSpeech
            );
        }
    }

    private WordMeaning(String wordMeaningId, String wordId,
                        String originalDefinition, String translationDefinition,
                        String definitionSummary, Constants.PartOfSpeech partOfSpeech) {
        this.wordMeaningId = wordMeaningId;
        this.wordId = wordId;
        this.partOfSpeech = partOfSpeech;
        this.originalDefinition = originalDefinition;
        this.translationDefinition = translationDefinition;
        this.definitionSummary = definitionSummary;
    }

    public Constants.PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public String getOriginalDefinition() {
        return originalDefinition;
    }

    public String getTranslationDefinition() {
        return translationDefinition;
    }

    public String getDefinitionSummary() {
        return definitionSummary;
    }

    public String getWordMeaningId() {
        return wordMeaningId;
    }

    public String getWordId() {
        return wordId;
    }

}
