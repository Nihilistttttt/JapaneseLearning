package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.IdiomEntity;
import com.Nihilisttt.LearnWord.JavaBean.Idiom;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

public class IdiomConverter {
    private static final String TAG = "IdiomConverter";
    private IdiomConverter() {}

    public static Idiom entityToModel(IdiomEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "Idiom", e ->
                new Idiom.Builder()
                        .idiomId(e.getIdiomId())
                        .wordId(e.getWordId())
                        .kanjiComponents(Convert.jsonToList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToList(e.getKanaComponents()))
                        .translation(e.getTranslation())
                        .wordIdList(Convert.jsonToList(e.getWordIdList()))
                        .build(), () -> null);
    }

    public static IdiomEntity modelToEntity(Idiom model) {
        return ConverterHelper.modelToEntity(model, TAG, "Idiom", m ->
                new IdiomEntity(
                        m.getIdiomId(), m.getWordId(),
                        Convert.listToJson(m.getKanjiComponents()),
                        Convert.listToJson(m.getKanaComponents()),
                        m.getTranslation(),
                        Convert.listToJson(m.getWordIdList())));
    }
}
