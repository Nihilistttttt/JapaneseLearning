package com.Nihilisttt.LearnWord.UtilityClass;

import android.content.Context;

public class BookSelectionManager {
    private static final String PREFS_NAME = "book_selection_prefs";
    private static final String KEY_BOOK_ID = "selected_book_id";
    private static final String KEY_BOOK_NAME = "selected_book_name";

    public static void setSelectedBook(Context context, String bookId, String bookName) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_BOOK_ID, bookId)
                .putString(KEY_BOOK_NAME, bookName)
                .apply();
    }

    public static String getSelectedBookId(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_BOOK_ID, null);
    }

    public static String getSelectedBookName(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_BOOK_NAME, null);
    }

    public static boolean hasSelectedBook(Context context) {
        String id = getSelectedBookId(context);
        return id != null && !id.isEmpty();
    }
}