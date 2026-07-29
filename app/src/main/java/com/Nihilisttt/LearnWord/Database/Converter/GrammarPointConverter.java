package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.GrammarPointEntity;
import com.Nihilisttt.LearnWord.JavaBean.GrammarPoint;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

public class GrammarPointConverter {
    private static final String TAG = "GrammarPointConverter";
    private GrammarPointConverter() {}

    public static GrammarPoint entityToModel(GrammarPointEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "GrammarPoint", e ->
                new GrammarPoint.Builder()
                        .grammarPointId(e.getGrammarPointId())
                        .wordId(e.getWordId())
                        .grammarName(e.getGrammarName())
                        .grammarDescription(e.getGrammarDescription())
                        .exampleKanji(e.getExampleKanji())
                        .exampleKana(e.getExampleKana())
                        .nameKanjiComponents(Convert.jsonToList(e.getNameKanjiComponents()))
                        .nameKanaComponents(Convert.jsonToList(e.getNameKanaComponents()))
                        .descKanjiComponents(Convert.jsonToList(e.getDescKanjiComponents()))
                        .descKanaComponents(Convert.jsonToList(e.getDescKanaComponents()))
                        .build(), () -> null);
    }

    public static GrammarPointEntity modelToEntity(GrammarPoint model) {
        return ConverterHelper.modelToEntity(model, TAG, "GrammarPoint", m ->
                new GrammarPointEntity(
                        m.getGrammarPointId(), m.getWordId(), m.getGrammarName(),
                        m.getGrammarDescription(), m.getExampleKanji(), m.getExampleKana(),
                        Convert.listToJson(m.getNameKanjiComponents()),
                        Convert.listToJson(m.getNameKanaComponents()),
                        Convert.listToJson(m.getDescKanjiComponents()),
                        Convert.listToJson(m.getDescKanaComponents())));
    }
}
