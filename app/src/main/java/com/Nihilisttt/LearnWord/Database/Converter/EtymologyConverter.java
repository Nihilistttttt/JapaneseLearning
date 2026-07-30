package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.EtymologyEntity;
import com.Nihilisttt.LearnWord.JavaBean.Etymology;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EtymologyConverter {
    private static final String TAG = "EtymologyConverter";
    private static final Etymology DEFAULT = new Etymology.Builder()
            .etymologyId("null").wordId("null").etymologyType("")
            .kanjiComponents(Collections.emptyList()).kanaComponents(Collections.emptyList())
            .wordIdList(Collections.emptyList()).translation("").build();
    private EtymologyConverter() {}

    public static Etymology EtymologyEntityToEtymology(EtymologyEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "Etymology", e ->
                new Etymology.Builder()
                        .etymologyId(e.getEtymologyId())
                        .wordId(e.getWordId())
                        .etymologyType(e.getEtymologyType())
                        .kanjiComponents(Convert.jsonToNestedList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToNestedList(e.getKanaComponents()))
                        .wordIdList(Convert.jsonToList(e.getWordIdList()))
                        .translation(e.getTranslation())
                        .build(), () -> DEFAULT);
    }

    public static EtymologyEntity EtymologyToEtymologyEntity(Etymology model) {
        return ConverterHelper.modelToEntity(model, TAG, "Etymology", m ->
                new EtymologyEntity(m.getEtymologyId(), m.getWordId(), m.getEtymologyType(),
                        Convert.nestedListToJson(m.getKanjiComponents()),
                        Convert.nestedListToJson(m.getKanaComponents()),
                        Convert.listToJson(m.getWordIdList()), m.getTranslation()));
    }
}