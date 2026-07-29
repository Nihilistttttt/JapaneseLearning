package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.SynonymWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class SynonymWordConverter {
    private static final String TAG = "SynonymWordConverter";
    private static final SynonymWord DEFAULT = new SynonymWord.Builder()
            .synonymWordId("null").wordId("null").correspondingWordId("null")
            .kanjiComponents(Collections.singletonList("null")).kanaComponents(Collections.singletonList("null")).build();
    private SynonymWordConverter() {}

    public static SynonymWord SynonymWordEntityToSynonymWord(SynonymWordEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "SynonymWord", e ->
                new SynonymWord.Builder()
                        .synonymWordId(e.getSynonymWordId()).wordId(e.getWordId())
                        .correspondingWordId(e.getCorrespondingWordId())
                        .kanjiComponents(Convert.jsonToList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToList(e.getKanaComponents())).build(), () -> DEFAULT);
    }

    public static SynonymWordEntity SynonymWordToSynonymWordEntity(SynonymWord model) {
        return ConverterHelper.modelToEntity(model, TAG, "SynonymWord", m ->
                new SynonymWordEntity(m.getSynonymWordId(), m.getWordId(), m.getCorrespondingWordId(),
                        Convert.listToJson(m.getKanjiComponents()), Convert.listToJson(m.getKanaComponents())));
    }
}
