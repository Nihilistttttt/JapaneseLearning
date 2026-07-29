package com.Nihilisttt.LearnWord.JavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class WordComponent {
    protected final String wordId;
    protected final List<String> kanjiComponents;
    protected final List<String> kanaComponents;

    protected WordComponent(String wordId, List<String> kanjiComponents, List<String> kanaComponents) {
        this.wordId = wordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
    }

    public String getWordId() { return wordId; }
    public List<String> getKanjiComponents() { return kanjiComponents; }
    public List<String> getKanaComponents() { return kanaComponents; }
    public String getCompositeKanji() { return String.join("", kanjiComponents); }
    public String getCompositeKana() { return String.join("", kanaComponents); }
    public boolean hasComponentCountMatch() { return kanjiComponents.size() == kanaComponents.size(); }

    protected static List<String> unmodifiableListOf(List<String> source) {
        return source != null ? Collections.unmodifiableList(new ArrayList<>(source)) : Collections.emptyList();
    }

    protected static void validateComponentsNotEmpty(List<String> components, String type) {
        if (components == null) throw new IllegalArgumentException(type + "组件列表不能为null");
        if (components.isEmpty()) throw new IllegalArgumentException(type + "组件至少需要1个元素");
    }
}