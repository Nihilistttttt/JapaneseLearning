package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.WordMeaningEntity;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

public class WordMeaningConverter {
    private static final String TAG = "WordMeaningConverter";
    private static final WordMeaning DEFAULT = new WordMeaning.Builder()
            .wordMeaningId("null").wordId("null").originalDefinition("null")
            .translationDefinition("null").partOfSpeech(Constants.PartOfSpeech.UNKNOWN).build();
    private WordMeaningConverter() {}

    public static WordMeaning WordMeaningEntityToWordMeaning(WordMeaningEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "WordMeaning", e ->
                new WordMeaning.Builder()
                        .wordMeaningId(e.getWordMeaningId())
                        .partOfSpeech(Convert.jsonToPartOfSpeech(e.getPartOfSpeech()))
                        .originalDefinition(e.getOriginalDefinition())
                        .translationDefinition(e.getTranslationDefinition())
                        .build(), () -> DEFAULT);
    }

    public static WordMeaningEntity WordMeaningToWordMeaningEntity(WordMeaning model) {
        return ConverterHelper.modelToEntity(model, TAG, "WordMeaning", m ->
                new WordMeaningEntity(m.getWordMeaningId(), m.getWordId(),
                        m.getOriginalDefinition(), m.getTranslationDefinition(),
                        Convert.partOfSpeechToJson(m.getPartOfSpeech())));
    }
}
