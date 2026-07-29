package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordCollocation extends PhraseComponent {
    private String wordCollocationId;

    public static class Builder {
        private String wordCollocationId;
        private String wordId;
        private List<List<String>> kanjiComponents = new ArrayList<>();
        private List<List<String>> kanaComponents = new ArrayList<>();
        private List<String> wordIdList = new ArrayList<>();
        private String translation;
        private String source = "未知来源";
        private String audioUrl;

        public Builder wordCollocationId(String wordCollocationId) {
            this.wordCollocationId = wordCollocationId;
            return this;
        }

        public Builder wordId(String wordId) {
            this.wordId = wordId;
            return this;
        }

        public Builder kanjiComponents(List<List<String>> kanji) {
            this.kanjiComponents = deepCopy(kanji);
            return this;
        }

        public Builder kanaComponents(List<List<String>> kana) {
            this.kanaComponents = deepCopy(kana);
            return this;
        }

        public Builder wordIdList(List<String> wordIdList) {
            this.wordIdList = new ArrayList<>(wordIdList);
            return this;
        }

        public Builder translation(String translation) {
            this.translation = translation;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder collocationAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
            return this;
        }

        public WordCollocation build() {
            validatePhraseComponents(kanjiComponents, kanaComponents);
            return new WordCollocation(
                    wordCollocationId, wordId,
                    kanjiComponents, kanaComponents,
                    new ArrayList<>(wordIdList),
                    translation, source, audioUrl
            );
        }
    }

    private WordCollocation(String wordCollocationId, String wordId,
                            List<List<String>> kanjiComponents, List<List<String>> kanaComponents,
                            List<String> wordIdList, String translation, String source, String audioUrl) {
        super(wordId, Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents),
                Collections.unmodifiableList(wordIdList), translation, source, audioUrl);
        this.wordCollocationId = wordCollocationId;
    }

    public String getWordCollocationId() { return wordCollocationId; }

    public void setWordCollocationId(String wordCollocationId) {
        this.wordCollocationId = wordCollocationId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }
}
