package com.Nihilisttt.LearnWord.UtilityClass;

import android.content.Context;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Convert {
    private static final Gson GSON = new Gson();
    private static final Type TYPE_LIST_STRING = new TypeToken<List<String>>(){}.getType();
    private static final Type TYPE_LIST_LIST_STRING = new TypeToken<List<List<String>>>(){}.getType();

    public static int dpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int spToPx(Context context, float sp) {
        return (int) (sp * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    @TypeConverter
    public static List<String> jsonToList(String value) {
        if (value == null || value.isEmpty()) return java.util.Collections.emptyList();
        try {
            List<String> result = GSON.fromJson(value, TYPE_LIST_STRING);
            if (result != null) return result;
        } catch (Exception ignored) {}
        try {
            List<List<String>> nested = GSON.fromJson(value, TYPE_LIST_LIST_STRING);
            if (nested != null) {
                java.util.List<String> flat = new java.util.ArrayList<>();
                for (List<String> inner : nested) {
                    if (inner != null) flat.addAll(inner);
                }
                return flat;
            }
        } catch (Exception ignored) {}
        return java.util.Collections.emptyList();
    }

    @TypeConverter
    public static String listToJson(List<String> list) {
        return GSON.toJson(list);
    }

    @TypeConverter
    public static List<List<String>> jsonToNestedList(String value) {
        if (value == null || value.isEmpty()) return java.util.Collections.emptyList();
        List<List<String>> result = GSON.fromJson(value, TYPE_LIST_LIST_STRING);
        return result != null ? result : java.util.Collections.emptyList();
    }

    @TypeConverter
    public static String nestedListToJson(List<List<String>> list) {
        return GSON.toJson(list);
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
