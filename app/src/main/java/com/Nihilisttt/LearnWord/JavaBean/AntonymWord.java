package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AntonymWord {
    private final String antonymWordId;
    private final String wordId;
    private final String correspondingWordId;
    private final List<String> kanjiComponents; // 汉字配件
    private final List<String> kanaComponents; // 假名配件


    public static class Builder {
        private String antonymWordId;
        private String wordId;
        private String correspondingWordId;
        private List<String> kanjiComponents;
        private List<String> kanaComponents;


        public Builder antonymWordId(String antonymWordId) {
            this.antonymWordId = antonymWordId;
            return this;
        }

        public Builder wordId(String wordId) {
            this.wordId = wordId;
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

        public Builder correspondingWordId(String correspondingWordId) {
            this.correspondingWordId = correspondingWordId;
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

        public AntonymWord build() {
            return new AntonymWord(
                    antonymWordId, wordId, correspondingWordId,
                    Collections.unmodifiableList(kanjiComponents), Collections.unmodifiableList(kanaComponents)

            );
        }
    }

    private AntonymWord(String antonymWordId, String wordId, String correspondingWordId, List<String> kanjiComponents, List<String> kanaComponents) {
        this.antonymWordId = antonymWordId;
        this.wordId = wordId;
        this.correspondingWordId = correspondingWordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
    }

    // region Getter方法
    public List<String> getKanjiComponents() {
        return kanjiComponents; // 已通过unmodifiableList包装
    }

    public List<String> getKanaComponents() {
        return kanaComponents; // 已通过unmodifiableList包装
    }

    public String getWordId() {
        return wordId;
    }

    public String getAntonymWordId() {
        return antonymWordId;
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
