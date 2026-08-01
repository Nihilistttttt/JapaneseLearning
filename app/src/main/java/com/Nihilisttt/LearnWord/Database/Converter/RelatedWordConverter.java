package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.RelatedWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.RelatedWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class RelatedWordConverter {
    private static final String TAG = "RelatedWordConverter";
    private static final RelatedWord DEFAULT = new RelatedWord.Builder()
            .relatedWordId("null").wordId("null").correspondingWordId("null")
            .kanjiComponents(Collections.singletonList("null")).kanaComponents(Collections.singletonList("null")).build();
    private RelatedWordConverter() {}

    public static RelatedWord RelatedWordEntityToRelatedWord(RelatedWordEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "RelatedWord", e ->
                new RelatedWord.Builder()
                        .relatedWordId(e.getRelatedWordId()).wordId(e.getWordId())
                        .correspondingWordId(e.getCorrespondingWordId())
                        .kanjiComponents(Convert.jsonToList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToList(e.getKanaComponents())).build(), () -> DEFAULT);
    }

    public static RelatedWordEntity RelatedWordToRelatedWordEntity(RelatedWord model) {
        return ConverterHelper.modelToEntity(model, TAG, "RelatedWord", m ->
                new RelatedWordEntity(m.getRelatedWordId(), m.getWordId(), m.getCorrespondingWordId(),
                        Convert.listToJson(m.getKanjiComponents()), Convert.listToJson(m.getKanaComponents())));
    }
}