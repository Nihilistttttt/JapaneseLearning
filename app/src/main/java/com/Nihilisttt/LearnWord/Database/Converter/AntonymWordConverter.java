package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.AntonymWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class AntonymWordConverter {
    private static final String TAG = "AntonymWordConverter";
    private static final AntonymWord DEFAULT = new AntonymWord.Builder()
            .antonymWordId("null").wordId("null").correspondingWordId("null")
            .kanjiComponents(Collections.singletonList("null")).kanaComponents(Collections.singletonList("null")).build();
    private AntonymWordConverter() {}

    public static AntonymWord AntonymWordEntityToAntonymWord(AntonymWordEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "AntonymWord", e ->
                new AntonymWord.Builder()
                        .antonymWordId(e.getAntonymWordId()).wordId(e.getWordId())
                        .correspondingWordId(e.getCorrespondingWordId())
                        .kanjiComponents(Convert.jsonToList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToList(e.getKanaComponents())).build(), () -> DEFAULT);
    }

    public static AntonymWordEntity AntonymWordToAntonymWordEntity(AntonymWord model) {
        return ConverterHelper.modelToEntity(model, TAG, "AntonymWord", m ->
                new AntonymWordEntity(m.getAntonymWordId(), m.getWordId(), m.getCorrespondingWordId(),
                        Convert.listToJson(m.getKanjiComponents()), Convert.listToJson(m.getKanaComponents())));
    }
}
