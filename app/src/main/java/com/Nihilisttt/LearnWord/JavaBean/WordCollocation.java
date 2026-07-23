package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordCollocation {
    private String wordCollocationId;
    private String wordId;
    private final List<List<String>> kanjiComponents; // 汉字拆分结构
    private final List<List<String>> kanaComponents;  // 假名拆分结构
    private final List<String> wordIdList;
    /* 考虑之后构建数据库后先通过python完成词与id的对应，再把word id也传入，而两个list保持传入
    这样既可以高效构建wordComponent，也方便传入id给点击事件，点击后查询数据表获得句中单词对象，随后构建单词窗口*/
    private final String translation;
    private final String source;
    private final String audioUrl;

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
            this.wordIdList = new ArrayList<>(wordIdList); // 防御性拷贝
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
            validateComponents();
            return new WordCollocation(
                    wordCollocationId,
                    wordId,
                    Collections.unmodifiableList(kanjiComponents),
                    Collections.unmodifiableList(kanaComponents),
                    Collections.unmodifiableList(new ArrayList<>(wordIdList)),
                    translation,
                    source,
                    audioUrl
            );
        }

        private void validateComponents() {
            // 1. 基础非空校验
            if (kanjiComponents.isEmpty() && kanaComponents.isEmpty()) {
                throw new IllegalArgumentException("至少需要一组拆分组件");
            }

            // 2. 结构一致性校验
            if (!kanjiComponents.isEmpty() && !kanaComponents.isEmpty()
                    && kanjiComponents.size() != kanaComponents.size()) {
                throw new IllegalArgumentException("汉字与假名拆分结构数量不一致");
            }
        }


        private List<List<String>> deepCopy(List<List<String>> source) {
            List<List<String>> copy = new ArrayList<>();
            for (List<String> inner : source) {
                copy.add(new ArrayList<>(inner));
            }
            return copy;
        }
    }

    private WordCollocation(String wordCollocationId,
                            String wordId,
                            List<List<String>> kanjiComponents,
                            List<List<String>> kanaComponents,
                            List<String> wordIdList,
                            String translation,
                            String source,
                            String audioUrl) {
        this.wordCollocationId = wordCollocationId;
        this.wordId = wordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.wordIdList = wordIdList;
        this.translation = translation;
        this.source = source;
        this.audioUrl = audioUrl;
    }

    // region Getter方法

    public String getWordCollocationId() {
        return wordCollocationId;
    }

    public void setWordCollocationId(String wordCollocationId) {
        this.wordCollocationId = wordCollocationId;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public List<List<String>> getKanjiComponents() {
        return deepUnmodifiable(kanjiComponents);
    }

    public List<List<String>> getKanaComponents() {
        return deepUnmodifiable(kanaComponents);
    }

    public List<String> getWordIdList() {
        return Collections.unmodifiableList(wordIdList);
    }

    public String getTranslation() {
        return translation;
    }

    public String getSource() {
        return source;
    }

    public String getAudioUrl() {
        return audioUrl;
    }
    // endregion

    private List<List<String>> deepUnmodifiable(List<List<String>> list) {
        List<List<String>> copy = new ArrayList<>();
        for (List<String> inner : list) {
            copy.add(Collections.unmodifiableList(inner));
        }
        return Collections.unmodifiableList(copy);
    }
}
