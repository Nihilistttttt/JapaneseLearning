package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.WordEntity;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class WordConverter {
    private static final String TAG = "WordConverter";
    private static final Word DEFAULT = new Word.Builder()
            .addWordId("null")
            .withAntonymIdList(Collections.singletonList("null")).withSynonymIdList(Collections.singletonList("null"))
            .withMeaningIdList(Collections.singletonList("null")).withCollocationIdList(Collections.singletonList("null"))
            .withSentenceIdList(Collections.singletonList("null")).build();
    private WordConverter() {}

    public static Word WordEntityToWord(WordEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "Word", e ->
                new Word.Builder()
                        .addWordId(e.getWordId())
                        .withAntonymIdList(Convert.jsonToList(e.getAntonymWordIdList()))
                        .withSynonymIdList(Convert.jsonToList(e.getSynonymWordIdList()))
                        .withCollocationIdList(Convert.jsonToList(e.getCollocationIdList()))
                        .withMeaningIdList(Convert.jsonToList(e.getMeaningIdList()))
                        .withSentenceIdList(Convert.jsonToList(e.getSentenceIdList()))
                        .build(), () -> DEFAULT);
    }

    public static WordEntity WordToWordEntity(Word model) {
        return ConverterHelper.modelToEntity(model, TAG, "Word", m ->
                new WordEntity(m.getWordId(),
                        Convert.listToJson(m.getAntonymIdList()), Convert.listToJson(m.getSynonymIdList()),
                        Convert.listToJson(m.getCollocationIdList()), Convert.listToJson(m.getMeaningIdList()),
                        Convert.listToJson(m.getSentenceIdList())));
    }
}
