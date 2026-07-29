package com.Nihilisttt.LearnWord.UtilityClass;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Judge {
    // 小假名字符串数组
    private static final Set<Character> SMALL_KANA_SET;

    static {
        // 使用不可变集合确保线程安全
        Set<Character> set = new HashSet<>();
        // 添加所有小假名的字符
        char[] kanaArray = {
                'ぁ', 'ぃ', 'ぅ', 'ぇ', 'ぉ',
                'ゃ', 'ゅ', 'ょ', 'っ',
                'ァ', 'ィ', 'ゥ', 'ェ', 'ォ',
                'ャ', 'ュ', 'ョ', 'ッ',
                'ヵ', 'ヶ'
        };
        for (char c : kanaArray) {
            set.add(c);
        }
        SMALL_KANA_SET = Collections.unmodifiableSet(set);
    }

    public static boolean isKana(char c) {
        return (c >= '\u3040' && c <= '\u309F') || (c >= '\u30A0' && c <= '\u30FF');
    }

    public static boolean isKana(String s) {
        return s != null && s.length() == 1 && isKana(s.charAt(0));
    }

    // 字符串版本直接复用字符判断逻辑
    public static boolean isSmallKana(String s) {
        return s != null &&
                s.length() == 1 &&
                isSmallKana(s.charAt(0));
    }

    // 字符版本使用 HashSet 实现 O(1) 查询
    public static boolean isSmallKana(char c) {
        return SMALL_KANA_SET.contains(c);
    }

}
