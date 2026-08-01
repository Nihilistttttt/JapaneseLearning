package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.Database.Entities.WordEntity;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;

import java.util.Collections;

public class WordConverter {
    private static final String TAG = "WordConverter";
    private static final Word DEFAULT = new Word.Builder()
            .addWordId("null")
            .withAntonymIdList(Collections.singletonList("null")).withSynonymIdList(Collections.singletonList("null"))
            .withMeaningIdList(Collections.singletonList("null")).withCollocationIdList(Collections.singletonList("null"))
            .withSentenceIdList(Collections.singletonList("null"))
            .withConjugationFormIdList(Collections.emptyList()).withEtymologyIdList(Collections.emptyList())
            .withKanjiInfoIdList(Collections.emptyList()).withUsageDistinctionIdList(Collections.emptyList())
            .withGrammarPointIdList(Collections.emptyList()).withIdiomIdList(Collections.emptyList())
            .withDerivedWordIdList(Collections.emptyList()).withRelatedWordIdList(Collections.emptyList())
            .build();
    private WordConverter() {}

    public static Word WordEntityToWord(WordEntity entity) {
        return ConverterHelper.entityToModel(entity, TAG, "Word", e ->
                new Word.Builder()
                        .addWordId(e.getWordId())
                        .withAntonymIdList(Convert.jsonToList(e.getAntonymWordIdList()))
                        .withSynonymIdList(Convert.jsonToList(e.getSynonymWordIdList()))
                        .withCollocationIdList(Convert.jsonToList(e.getCollocationIdList()))
                        .withMeaningIdList(Convert.jsonToList(e.getMeaningIdList()))
                        .withSentenceIdList(Convert.jsonToList(e.getSentenceIdList()))
                        .withConjugationFormIdList(Convert.jsonToList(e.getConjugationFormIdList()))
                        .withEtymologyIdList(Convert.jsonToList(e.getEtymologyIdList()))
                        .withKanjiInfoIdList(Convert.jsonToList(e.getKanjiInfoIdList()))
                        .withUsageDistinctionIdList(Convert.jsonToList(e.getUsageDistinctionIdList()))
                        .withGrammarPointIdList(Convert.jsonToList(e.getGrammarPointIdList()))
                        .withIdiomIdList(Convert.jsonToList(e.getIdiomIdList()))
                        .withDerivedWordIdList(Convert.jsonToList(e.getDerivedWordIdList()))
                        .withRelatedWordIdList(Convert.jsonToList(e.getRelatedWordIdList()))
                        .build(), () -> DEFAULT);
    }

    public static WordEntity WordToWordEntity(Word model) {
        return ConverterHelper.modelToEntity(model, TAG, "Word", m ->
                new WordEntity(m.getWordId(),
                        Convert.listToJson(m.getAntonymIdList()), Convert.listToJson(m.getSynonymIdList()),
                        Convert.listToJson(m.getCollocationIdList()), Convert.listToJson(m.getMeaningIdList()),
                        Convert.listToJson(m.getSentenceIdList()),
                        Convert.listToJson(m.getConjugationFormIdList()), Convert.listToJson(m.getEtymologyIdList()),
                        Convert.listToJson(m.getKanjiInfoIdList()), Convert.listToJson(m.getUsageDistinctionIdList()),
                        Convert.listToJson(m.getGrammarPointIdList()), Convert.listToJson(m.getIdiomIdList()),
                        Convert.listToJson(m.getDerivedWordIdList()), Convert.listToJson(m.getRelatedWordIdList())));
    }
}
