package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SynonymWord {
    private final String synonymWordId;
    private final String wordId;
    private final String correspondingWordId;
    private final List<String> kanjiComponents; // 汉字配件
    private final List<String> kanaComponents; // 假名配件


    public static class Builder {
        private String synonymWordId;
        private String wordId;
        private String correspondingWordId;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;

        public Builder synonymWordId(String synonymWordId) {
            this.synonymWordId = synonymWordId;
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
            validateComponents(components, "Kanji");
            this.kanjiComponents = new ArrayList<>(components); // 防御性拷贝
            return this;
        }

        public Builder kanaComponents(List<String> components) {
            validateComponents(components, "Kana");
            this.kanaComponents = new ArrayList<>(components); // 防御性拷贝
            return this;
        }


        private void validateComponents(List<String> components, String type) {
            if (components == null) {
                throw new IllegalArgumentException(type + "组件列表不能为null");
            }
            if (components.isEmpty()) {
                throw new IllegalArgumentException(type + "组件至少需要1个元素");
            }
        }

        public SynonymWord build() {
            return new SynonymWord(
                    this.synonymWordId, this.wordId, this.correspondingWordId,
                    Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents)
            );
        }
    }

    private SynonymWord(String synonymWordId, String wordId, String correspondingWordId,
                        List<String> kanjiComponents, List<String> kanaComponents) {
        this.synonymWordId = synonymWordId;
        this.wordId = wordId;
        this.correspondingWordId = correspondingWordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;

    }

    // region Getter方法

    public String getSynonymWordId() {
        return synonymWordId;
    }

    public String getWordId() {
        return wordId;
    }

    public List<String> getKanjiComponents() {
        return kanjiComponents; // 已通过unmodifiableList包装
    }

    public List<String> getKanaComponents() {
        return kanaComponents; // 已通过unmodifiableList包装
    }

    public String getCorrespondingWordId() {
        return correspondingWordId;
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
        return getCompositeKanji() + " (" + getCompositeKana() + ") - id = " + getCorrespondingWordId();
    }
}
