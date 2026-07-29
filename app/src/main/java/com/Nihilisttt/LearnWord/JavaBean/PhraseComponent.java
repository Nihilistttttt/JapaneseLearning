package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PhraseComponent {
    protected String wordId;
    protected final List<List<String>> kanjiComponents;
    protected final List<List<String>> kanaComponents;
    protected final List<String> wordIdList;
    protected final String translation;
    protected final String source;
    protected final String audioUrl;

    protected PhraseComponent(String wordId, List<List<String>> kanjiComponents,
                              List<List<String>> kanaComponents, List<String> wordIdList,
                              String translation, String source, String audioUrl) {
        this.wordId = wordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.wordIdList = wordIdList;
        this.translation = translation;
        this.source = source;
        this.audioUrl = audioUrl;
    }

    public String getWordId() { return wordId; }
    public List<List<String>> getKanjiComponents() { return deepUnmodifiable(kanjiComponents); }
    public List<List<String>> getKanaComponents() { return deepUnmodifiable(kanaComponents); }
    public List<String> getWordIdList() { return Collections.unmodifiableList(wordIdList); }
    public String getTranslation() { return translation; }
    public String getSource() { return source; }
    public String getAudioUrl() { return audioUrl; }

    protected static List<List<String>> deepCopy(List<List<String>> source) {
        List<List<String>> copy = new ArrayList<>();
        for (List<String> inner : source) {
            copy.add(new ArrayList<>(inner));
        }
        return copy;
    }

    protected static List<List<String>> deepUnmodifiable(List<List<String>> list) {
        List<List<String>> copy = new ArrayList<>();
        for (List<String> inner : list) {
            copy.add(Collections.unmodifiableList(inner));
        }
        return Collections.unmodifiableList(copy);
    }

    protected static void validatePhraseComponents(List<List<String>> kanjiComponents, List<List<String>> kanaComponents) {
        if (kanjiComponents.isEmpty() && kanaComponents.isEmpty()) {
            throw new IllegalArgumentException("至少需要一组拆分组件");
        }
        if (!kanjiComponents.isEmpty() && !kanaComponents.isEmpty()
                && kanjiComponents.size() != kanaComponents.size()) {
            throw new IllegalArgumentException("汉字与假名拆分结构数量不一致");
        }
    }
}