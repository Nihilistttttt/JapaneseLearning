package com.Nihilisttt.LearnWord.Page.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.Nihilisttt.LearnWord.UtilityClass.Constants;

public class LearnPageStateViewModel extends AndroidViewModel {
    public enum FragmentInLearnPage {
        LearnPageFragment,
        SearchFragment
    }

    private static final String PREFS_NAME = "FontSizePrefs";
    private static final String KEY_WORD_FONT_LEVEL = "word_font_level";
    private static final String KEY_SUB_FONT_LEVEL = "sub_font_level";

    private final SharedPreferences prefs;

    private final MutableLiveData<Integer> wordFontLevel;
    private final MutableLiveData<Integer> subFontLevel;

    public LearnPageStateViewModel(@NonNull Application application) {
        super(application);
        prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedWord = prefs.getInt(KEY_WORD_FONT_LEVEL, 5);
        int savedSub = prefs.getInt(KEY_SUB_FONT_LEVEL, 2);
        wordFontLevel = new MutableLiveData<>(Math.max(4, Math.min(6, savedWord)));
        subFontLevel = new MutableLiveData<>(Math.max(1, Math.min(3, savedSub)));
    }

    private MutableLiveData<Boolean> isViewPagerScrollEnabled = new MutableLiveData<>(true);

    public MutableLiveData<Boolean> getIsViewPagerScrollEnabled() {
        return isViewPagerScrollEnabled;
    }

    public void setViewPagerScrollEnabled(boolean enabled) {
        isViewPagerScrollEnabled.setValue(enabled);
    }

    private MutableLiveData<FragmentInLearnPage> whichFragmentInLearnPage = new MutableLiveData<>();

    public MutableLiveData<FragmentInLearnPage> getWhichFragmentInLearnPage() {
        return whichFragmentInLearnPage;
    }

    public void setWhichFragmentInLearnPage(FragmentInLearnPage fragment) {
        whichFragmentInLearnPage.setValue(fragment);
    }

    public MutableLiveData<Integer> getWordFontLevel() {
        return wordFontLevel;
    }

    public MutableLiveData<Integer> getSubFontLevel() {
        return subFontLevel;
    }

    public void setWordFontLevel(int level) {
        int clamped = Math.max(4, Math.min(6, level));
        wordFontLevel.setValue(clamped);
        prefs.edit().putInt(KEY_WORD_FONT_LEVEL, clamped).apply();
    }

    public void setSubFontLevel(int level) {
        int clamped = Math.max(1, Math.min(3, level));
        subFontLevel.setValue(clamped);
        prefs.edit().putInt(KEY_SUB_FONT_LEVEL, clamped).apply();
    }

    public static final int FONT_SIZE_NORMAL = Constants.FONT_SIZE_NORMAL;


    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
