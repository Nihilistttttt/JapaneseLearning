package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.UsageDistinctionEntity;
import com.Nihilisttt.LearnWord.JavaBean.UsageDistinction;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

public class UsageDistinctionConverter {
    private static final String TAG = "UsageDistinctionConverter";
    private UsageDistinctionConverter() {}

    public static UsageDistinction entityToModel(UsageDistinctionEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "UsageDistinction", e ->
                new UsageDistinction.Builder()
                        .usageDistinctionId(e.getUsageDistinctionId())
                        .wordId(e.getWordId())
                        .distinctionText(e.getDistinctionText())
                        .comparedWordIds(Convert.jsonToList(e.getComparedWordIds()))
                        .kanjiComponents(Convert.jsonToList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToList(e.getKanaComponents()))
                        .build(), () -> null);
    }

    public static UsageDistinctionEntity modelToEntity(UsageDistinction model) {
        return ConverterHelper.modelToEntity(model, TAG, "UsageDistinction", m ->
                new UsageDistinctionEntity(
                        m.getUsageDistinctionId(), m.getWordId(), m.getDistinctionText(),
                        Convert.listToJson(m.getComparedWordIds()),
                        Convert.listToJson(m.getKanjiComponents()),
                        Convert.listToJson(m.getKanaComponents())));
    }
}
