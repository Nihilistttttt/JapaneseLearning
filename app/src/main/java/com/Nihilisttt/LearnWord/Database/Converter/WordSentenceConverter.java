package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.WordSentenceEntity;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class WordSentenceConverter {
    private static final String TAG = "WordSentenceConverter";
    private static final WordSentence DEFAULT = new WordSentence.Builder()
            .wordSentenceId("null").wordId("null").wordMeaningId("null")
            .kanjiComponents(Collections.singletonList(Collections.singletonList("null")))
            .kanaComponents(Collections.singletonList(Collections.singletonList("null")))
            .wordIdList(Collections.singletonList("null")).sentenceAudioUrl("null")
            .translation("null").source("null").build();
    private WordSentenceConverter() {}

    public static WordSentence WordSentenceEntityToWordSentence(WordSentenceEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "WordSentence", e ->
                new WordSentence.Builder()
                        .wordSentenceId(e.getWordSentenceId()).wordId(e.getWordId()).wordMeaningId(e.getWordMeaningId())
                        .kanjiComponents(Convert.jsonToNestedList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToNestedList(e.getKanaComponents()))
                        .wordIdList(Convert.jsonToList(e.getWordIdList()))
                        .translation(e.getTranslation()).source(e.getSource()).sentenceAudioUrl(e.getAudioUrl())
                        .build(), () -> DEFAULT);
    }

    public static WordSentenceEntity WordSentenceToWordSentenceEntity(WordSentence model) {
        return ConverterHelper.modelToEntity(model, TAG, "WordSentence", m ->
                new WordSentenceEntity(m.getWordSentenceId(), m.getWordId(), m.getWordMeaningId(),
                        Convert.nestedListToJson(m.getKanjiComponents()), Convert.nestedListToJson(m.getKanaComponents()),
                        Convert.listToJson(m.getWordIdList()), m.getTranslation(), m.getSource(), m.getAudioUrl()));
    }
}
