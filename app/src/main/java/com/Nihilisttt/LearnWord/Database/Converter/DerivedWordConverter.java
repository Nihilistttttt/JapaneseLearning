package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.DerivedWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.DerivedWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class DerivedWordConverter {
    private static final String TAG = "DerivedWordConverter";
    private static final DerivedWord DEFAULT = new DerivedWord.Builder()
            .derivedWordId("null").wordId("null").correspondingWordId("null")
            .kanjiComponents(Collections.singletonList("null")).kanaComponents(Collections.singletonList("null")).build();
    private DerivedWordConverter() {}

    public static DerivedWord DerivedWordEntityToDerivedWord(DerivedWordEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "DerivedWord", e ->
                new DerivedWord.Builder()
                        .derivedWordId(e.getDerivedWordId()).wordId(e.getWordId())
                        .correspondingWordId(e.getCorrespondingWordId())
                        .kanjiComponents(Convert.jsonToList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToList(e.getKanaComponents())).build(), () -> DEFAULT);
    }

    public static DerivedWordEntity DerivedWordToDerivedWordEntity(DerivedWord model) {
        return ConverterHelper.modelToEntity(model, TAG, "DerivedWord", m ->
                new DerivedWordEntity(m.getDerivedWordId(), m.getWordId(), m.getCorrespondingWordId(),
                        Convert.listToJson(m.getKanjiComponents()), Convert.listToJson(m.getKanaComponents())));
    }
}