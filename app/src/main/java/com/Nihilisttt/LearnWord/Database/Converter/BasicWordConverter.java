package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class BasicWordConverter {
    private static final String TAG = "BasicWordConverter";
    private static final BasicWord DEFAULT = new BasicWord.Builder()
            .wordId("null").kanjiComponents(Collections.singletonList("null")).kanaComponents(Collections.singletonList("null"))
            .audioUrl("null").accentMark("null").mnemonic("null").jlptLevel(0).wordFrequency(0).build();
    private BasicWordConverter() {}

    public static BasicWord BasicWordEntityToBasicWord(BasicWordEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "BasicWord", e ->
                new BasicWord.Builder()
                        .wordId(e.getWordId())
                        .kanjiComponents(Convert.jsonToList(e.getKanjiComponents()))
                        .kanaComponents(Convert.jsonToList(e.getKanaComponents()))
                        .audioUrl(e.getAudioUrl()).accentMark(e.getAccentMark()).mnemonic(e.getMnemonic())
                        .jlptLevel(e.getJlptLevel()).wordFrequency(e.getWordFrequency())
                        .build(), () -> DEFAULT);
    }

    public static BasicWordEntity BasicWordToBasicWordEntity(BasicWord model) {
        return ConverterHelper.modelToEntity(model, TAG, "BasicWord", m ->
                new BasicWordEntity(m.getWordId(),
                        Convert.listToJson(m.getKanjiComponents()), Convert.listToJson(m.getKanaComponents()),
                        m.getAudioUrl(), m.getAccentMark(), m.getMnemonic(), m.getJlptLevel(), m.getWordFrequency()));
    }
}
