package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordSentence extends PhraseComponent {
    private String wordSentenceId;
    private String wordMeaningId;

    public static class Builder {
        private String wordSentenceId;
        private String wordId;
        private String wordMeaningId;
        private List<List<String>> kanjiComponents = new ArrayList<>();
        private List<List<String>> kanaComponents = new ArrayList<>();
        private List<String> wordIdList = new ArrayList<>();
        private String translation;
        private String source = "未知来源";
        private String audioUrl;

        public Builder wordSentenceId(String wordSentenceId) {
            this.wordSentenceId = wordSentenceId;
            return this;
        }

        public Builder wordId(String wordId) {
            this.wordId = wordId;
            return this;
        }

        public Builder wordMeaningId(String wordMeaningId) {
            this.wordMeaningId = wordMeaningId;
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

        public Builder sentenceAudioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
            return this;
        }

        public WordSentence build() {
            validatePhraseComponents(kanjiComponents, kanaComponents);
            return new WordSentence(
                    wordSentenceId, wordId, wordMeaningId,
                    kanjiComponents, kanaComponents,
                    new ArrayList<>(wordIdList),
                    translation, source, audioUrl
            );
        }
    }

    private WordSentence(String wordSentenceId, String wordId, String wordMeaningId,
                         List<List<String>> kanjiComponents, List<List<String>> kanaComponents,
                         List<String> wordIdList, String translation, String source, String audioUrl) {
        super(wordId, Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents),
                Collections.unmodifiableList(wordIdList), translation, source, audioUrl);
        this.wordSentenceId = wordSentenceId;
        this.wordMeaningId = wordMeaningId;
    }

    public String getWordSentenceId() { return wordSentenceId; }
    public String getWordMeaningId() { return wordMeaningId; }
}
