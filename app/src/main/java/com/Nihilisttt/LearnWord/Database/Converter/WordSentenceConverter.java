package com.Nihilisttt.LearnWord.Database.Converter;

import android.util.Log;

import com.Nihilisttt.LearnWord.Database.Entities.WordSentenceEntity;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.google.gson.Gson;

import java.util.Collections;

public class WordSentenceConverter {
    private static final Gson gson = new Gson();
    private static final String TAG = "WordSentenceConverter"; // 统一日志标签

    // 缓存默认对象减少GC压力
    private static final WordSentence DEFAULT_WORD_SENTENCE = createDefaultWordSentence();

    private WordSentenceConverter() {
        // 防止实例化
    }

    public static WordSentence WordSentenceEntityToWordSentence(WordSentenceEntity entity) {
        if (entity == null) {
            Log.d(TAG, "输入WordSentence为null，返回默认WordSentence");
            return DEFAULT_WORD_SENTENCE;
        }

        try {
            WordSentence model = new WordSentence.Builder()
                    .wordSentenceId(entity.getWordSentenceId())
                    .wordId(entity.getWordId())
                    .wordMeaningId(entity.getWordMeaningId())
                    .kanjiComponents(Convert.jsonToNestedList(entity.getKanjiComponents()))
                    .kanaComponents(Convert.jsonToNestedList(entity.getKanaComponents()))
                    .wordIdList(Convert.jsonToList(entity.getWordIdList()))
                    .translation(entity.getTranslation())
                    .source(entity.getSource())
                    .sentenceAudioUrl(entity.getAudioUrl())
                    .build();

            Log.d(TAG, "成功转换WordSentenceEntity ID: " + entity.getWordSentenceId());
            return model;
        } catch (Exception e) {
            Log.d(TAG, "转换WordSentenceEntity失败, ID: " + entity.getWordSentenceId(), e); // 使用Log.d记录异常
            return DEFAULT_WORD_SENTENCE;
        }
    }

    public static WordSentenceEntity WordSentenceToWordSentenceEntity(WordSentence model) {
        if (model == null) {
            Log.d(TAG, "输入WordSentence为null，抛出异常");
            throw new IllegalArgumentException("WordSentence不能为null");
        }

        try {
            WordSentenceEntity entity = new WordSentenceEntity(
                    model.getWordSentenceId(),
                    model.getWordId(),
                    model.getWordMeaningId(),
                    Convert.nestedListToJson(model.getKanjiComponents()),
                    Convert.nestedListToJson(model.getKanaComponents()),
                    Convert.listToJson(model.getWordIdList()),
                    model.getTranslation(),
                    model.getSource(),
                    model.getAudioUrl()
            );
            Log.d(TAG, "成功转换WordSentence为WordSentenceEntity ID: " + model.getWordSentenceId());
            return entity;
        } catch (Exception e) {
            Log.d(TAG, "WordSentence转WordSentenceEntity失败, ID: " + model.getWordSentenceId(), e);
            throw new RuntimeException("转换失败", e);
        }
    }


    private static WordSentence createDefaultWordSentence() {
        Log.d(TAG, "创建默认WordSentence实例");
        return new WordSentence.Builder()
                .wordSentenceId("null")
                .wordId("null")
                .wordMeaningId("null")
                .kanjiComponents(Collections.singletonList(Collections.singletonList("null")))
                .kanaComponents(Collections.singletonList(Collections.singletonList("null")))
                .wordIdList(Collections.singletonList("null"))
                .sentenceAudioUrl("null")
                .translation("null")
                .source("null")
                .build();
    }
    // endregion
}
