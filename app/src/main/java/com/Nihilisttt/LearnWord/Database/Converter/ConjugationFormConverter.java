package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.ConjugationFormEntity;
import com.Nihilisttt.LearnWord.JavaBean.ConjugationForm;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

public class ConjugationFormConverter {
    private static final String TAG = "ConjugationFormConverter";
    private ConjugationFormConverter() {}

    public static ConjugationForm entityToModel(ConjugationFormEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "ConjugationForm", e ->
                new ConjugationForm.Builder()
                        .conjugationFormId(e.getConjugationFormId())
                        .wordId(e.getWordId())
                        .formName(e.getFormName())
                        .kanjiComponents(Convert.jsonToList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToList(e.getKanaComponents()))
                        .build(), () -> null);
    }

    public static ConjugationFormEntity modelToEntity(ConjugationForm model) {
        return ConverterHelper.modelToEntity(model, TAG, "ConjugationForm", m ->
                new ConjugationFormEntity(
                        m.getConjugationFormId(), m.getWordId(), m.getFormName(),
                        Convert.listToJson(m.getKanjiComponents()),
                        Convert.listToJson(m.getKanaComponents())));
    }
}
