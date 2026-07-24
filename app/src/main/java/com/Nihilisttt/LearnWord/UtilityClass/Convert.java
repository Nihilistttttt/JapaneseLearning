package com.Nihilisttt.LearnWord.UtilityClass;

import android.content.Context;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Convert {
    public static int dpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int spToPx(Context context, float sp) {
        return (int) (sp * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    @TypeConverter
    public static List<String> jsonToList(String value) {
        if (value == null || value.isEmpty()) return java.util.Collections.emptyList();
        List<String> result = new Gson().fromJson(value, new TypeToken<List<String>>(){}.getType());
        return result != null ? result : java.util.Collections.emptyList();
    }

    @TypeConverter
    public static String listToJson(List<String> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<List<String>> jsonToNestedList(String value) {
        if (value == null || value.isEmpty()) return java.util.Collections.emptyList();
        List<List<String>> result = new Gson().fromJson(value, new TypeToken<List<List<String>>>(){}.getType());
        return result != null ? result : java.util.Collections.emptyList();
    }

    @TypeConverter
    public static String nestedListToJson(List<List<String>> list) {
        return new Gson().toJson(list);
    }
    @TypeConverter
    public static String partOfSpeechToJson(Constants.PartOfSpeech pos) {
        return pos == null ? null : pos.name();
    }

    @TypeConverter
    public static Constants.PartOfSpeech jsonToPartOfSpeech(String value) {
        if (value == null) return Constants.PartOfSpeech.UNKNOWN;

        try {
            return Constants.PartOfSpeech.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return Constants.PartOfSpeech.UNKNOWN; // 处理未知枚举值
        }
    }
}
