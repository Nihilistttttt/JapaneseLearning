package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicWord {
    private final List<String> kanjiComponents; // 汉字配件
    private final List<String> kanaComponents; // 假名配件
    private final String accentMark; // 音调
    private final String audioUrl; // 语音
    private final String mnemonic; // 助记
    private final String wordId; // 单词id


    public static class Builder {
        private List<String> kanjiComponents; // 汉字配件
        private List<String> kanaComponents; // 假名配件
        private String accentMark; // 音调
        private String audioUrl; // 语音
        private String mnemonic; // 助记
        private String wordId; // 单词id

        public Builder kanjiComponents(List<String> components) {
            this.kanjiComponents = new ArrayList<>(components); // 防御性拷贝
            return this;
        }

        public Builder kanaComponents(List<String> components) {
            this.kanaComponents = new ArrayList<>(components); // 防御性拷贝
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
                    this.accentMark, this.audioUrl,this.mnemonic, this.wordId
            );
        }
    }

    private BasicWord(List<String> kanjiComponents, List<String> kanaComponents,
                      String accentMark, String audioUrl, String mnemonic, String wordId) {
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.accentMark = accentMark;
        this.audioUrl = audioUrl;
        this.mnemonic = mnemonic;
        this.wordId = wordId;
    }

    // region Getter方法


    public List<String> getKanjiComponents() {
        return kanjiComponents; // 已通过unmodifiableList包装
    }

    public List<String> getKanaComponents() {
        return kanaComponents; // 已通过unmodifiableList包装
    }

    public String getAccentMark() {
        return accentMark;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getWordId() {
        return wordId;
    }


    // endregion

    // region 实用方法
    public String getCompositeKanji() {
        return String.join("", kanjiComponents);
    }

    public String getCompositeKana() {
        return String.join("", kanaComponents);
    }

    public boolean hasComponentCountMatch() {
        return kanjiComponents.size() == kanaComponents.size();
    }
    // endregion

    @Override
    public String toString() {
        return getCompositeKanji() + " (" + getCompositeKana() + ") - correspondingWordId = " + getWordId();
    }
}
