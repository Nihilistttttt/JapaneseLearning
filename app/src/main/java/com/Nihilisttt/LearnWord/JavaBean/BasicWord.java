package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicWord extends WordComponent {
    private final String accentMark;
    private final String audioUrl;
    private final String mnemonic;

    public static class Builder {
        private List<String> kanjiComponents;
        private List<String> kanaComponents;
        private String accentMark;
        private String audioUrl;
        private String mnemonic;
        private String wordId;

        public Builder kanjiComponents(List<String> components) {
            this.kanjiComponents = new ArrayList<>(components);
            return this;
        }

        public Builder kanaComponents(List<String> components) {
            this.kanaComponents = new ArrayList<>(components);
            return this;
        }

        public Builder accentMark(String accentMark) {
            this.accentMark = accentMark;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
            return this;
        }

        public Builder audioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
            return this;
        }

        public Builder wordId(String wordId) {
            this.wordId = wordId;
            return this;
        }

        public BasicWord build() {
            if (kanjiComponents == null || kanaComponents == null) {
                throw new IllegalStateException("Kanji/Kana components must be set");
            }
            return new BasicWord(
                    Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents),
                    this.accentMark, this.audioUrl, this.mnemonic, this.wordId
            );
        }
    }

    private BasicWord(List<String> kanjiComponents, List<String> kanaComponents,
                      String accentMark, String audioUrl, String mnemonic, String wordId) {
        super(wordId, kanjiComponents, kanaComponents);
        this.accentMark = accentMark;
        this.audioUrl = audioUrl;
        this.mnemonic = mnemonic;
    }

    public String getAccentMark() { return accentMark; }
    public String getAudioUrl() { return audioUrl; }
    public String getMnemonic() { return mnemonic; }

    @Override
    public String toString() {
        return getCompositeKanji() + " (" + getCompositeKana() + ") - correspondingWordId = " + getWordId();
    }
}
