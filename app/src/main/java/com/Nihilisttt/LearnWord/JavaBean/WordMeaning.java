package com.Nihilisttt.LearnWord.JavaBean;

import com.Nihilisttt.LearnWord.UtilityClass.Constants;

import java.util.Objects;

public class WordMeaning {


    private final Constants.PartOfSpeech partOfSpeech;
    private final String originalDefinition;
    private final String translationDefinition;
    private final String wordMeaningId;
    private final String wordId;

    // 主建造者
    public static class Builder {
        private String wordMeaningId;
        private String wordId;
        private Constants.PartOfSpeech partOfSpeech = Constants.PartOfSpeech.UNKNOWN;
        private String originalDefinition;
        private String translationDefinition;

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
            return new WordMeaning(
                    wordMeaningId,
                    wordId,
                    originalDefinition,
                    translationDefinition,
                    partOfSpeech
            );
        }
    }

    // 例句建造者


    // 私有构造函数
    private WordMeaning(String wordMeaningId, String wordId,
                        String originalDefinition, String translationDefinition, Constants.PartOfSpeech partOfSpeech) {
        this.wordMeaningId = wordMeaningId;
        this.wordId = wordId;
        this.partOfSpeech = partOfSpeech;
        this.originalDefinition = originalDefinition;
        this.translationDefinition = translationDefinition;
    }

    // Getter方法
    public Constants.PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public String getOriginalDefinition() {
        return originalDefinition;
    }

    public String getWordMeaningId() {
        return wordMeaningId;
    }

    public String getWordId() {
        return wordId;
    }

    public String getTranslationDefinition() {
        return translationDefinition;
    }

}
