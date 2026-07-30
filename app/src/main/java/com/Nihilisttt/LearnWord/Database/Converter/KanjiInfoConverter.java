package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.KanjiInfoEntity;
import com.Nihilisttt.LearnWord.JavaBean.KanjiInfo;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

public class KanjiInfoConverter {
    private static final String TAG = "KanjiInfoConverter";
    private KanjiInfoConverter() {}

    public static KanjiInfo entityToModel(KanjiInfoEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "KanjiInfo", e ->
                new KanjiInfo.Builder()
                        .kanjiInfoId(e.getKanjiInfoId())
                        .wordId(e.getWordId())
                        .kanji(e.getKanji())
                        .onyomi(Convert.jsonToList(e.getOnyomi()))
                        .kunyomi(Convert.jsonToList(e.getKunyomi()))
                        .sameKanjiWords(Convert.jsonToList(e.getSameKanjiWords()))
                        .translation(e.getTranslation())
                        .build(), () -> null);
    }

    public static KanjiInfoEntity modelToEntity(KanjiInfo model) {
        return ConverterHelper.modelToEntity(model, TAG, "KanjiInfo", m ->
                new KanjiInfoEntity(
                        m.getKanjiInfoId(), m.getWordId(), m.getKanji(),
                        Convert.listToJson(m.getOnyomi()),
                        Convert.listToJson(m.getKunyomi()),
                        Convert.listToJson(m.getSameKanjiWords()),
                        m.getTranslation()));
    }
}
