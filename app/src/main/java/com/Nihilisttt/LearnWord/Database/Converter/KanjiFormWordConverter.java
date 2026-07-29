package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.KanjiFormWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.KanjiFormWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class KanjiFormWordConverter {
    private static final String TAG = "KanjiFormWordConverter";
    private static final KanjiFormWord DEFAULT = new KanjiFormWord.Builder()
            .kanjiFormId("null").wordId("null")
            .kanjiComponents(Collections.singletonList("null")).kanaComponents(Collections.singletonList("null")).build();
    private KanjiFormWordConverter() {}

    public static KanjiFormWord KanjiFormWordEntityToKanjiFormWord(KanjiFormWordEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "KanjiFormWord", e ->
                new KanjiFormWord.Builder()
                        .kanjiFormId(e.getKanjiFormId()).wordId(e.getWordId())
                        .kanjiComponents(Convert.jsonToList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToList(e.getKanaComponents())).build(), () -> DEFAULT);
    }

    public static KanjiFormWordEntity KanjiFormWordToKanjiFormWordEntity(KanjiFormWord model) {
        return ConverterHelper.modelToEntity(model, TAG, "KanjiFormWord", m ->
                new KanjiFormWordEntity(m.getKanjiFormId(), m.getWordId(),
                        Convert.listToJson(m.getKanjiComponents()), Convert.listToJson(m.getKanaComponents())));
    }
}
