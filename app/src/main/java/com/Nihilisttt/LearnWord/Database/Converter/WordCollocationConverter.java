package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.WordCollocationEntity;
import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class WordCollocationConverter {
    private static final String TAG = "WordCollocationConverter";
    private static final WordCollocation DEFAULT = new WordCollocation.Builder()
            .wordCollocationId("null").wordId("null")
            .kanjiComponents(Collections.singletonList(Collections.singletonList("null")))
            .kanaComponents(Collections.singletonList(Collections.singletonList("null")))
            .wordIdList(Collections.singletonList("null")).collocationAudioUrl("null")
            .translation("null").source("null").build();
    private WordCollocationConverter() {}

    public static WordCollocation WordCollocationEntityToWordCollocation(WordCollocationEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "WordCollocation", e ->
                new WordCollocation.Builder()
                        .wordCollocationId(e.getWordCollocationId()).wordId(e.getWordId())
                        .kanjiComponents(Convert.jsonToNestedList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToNestedList(e.getKanaComponents()))
                        .wordIdList(Convert.jsonToList(e.getWordIdList()))
                        .translation(e.getTranslation()).source(e.getSource()).collocationAudioUrl(e.getAudioUrl())
                        .build(), () -> DEFAULT);
    }

    public static WordCollocationEntity WordCollocationToWordCollocationEntity(WordCollocation model) {
        return ConverterHelper.modelToEntity(model, TAG, "WordCollocation", m ->
                new WordCollocationEntity(m.getWordCollocationId(), m.getWordId(),
                        Convert.nestedListToJson(m.getKanjiComponents()), Convert.nestedListToJson(m.getKanaComponents()),
                        Convert.listToJson(m.getWordIdList()), m.getTranslation(), m.getSource(), m.getAudioUrl()));
    }
}
