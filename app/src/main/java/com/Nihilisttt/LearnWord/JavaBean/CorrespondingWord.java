package com.Nihilisttt.LearnWord.JavaBean;

import java.util.List;

public abstract class CorrespondingWord extends WordComponent {
    protected final String correspondingWordId;

    protected CorrespondingWord(String wordId, String correspondingWordId,
                                List<String> kanjiComponents, List<String> kanaComponents) {
        super(wordId, kanjiComponents, kanaComponents);
        this.correspondingWordId = correspondingWordId;
    }

    public String getCorrespondingWordId() { return correspondingWordId; }
}