package com.Nihilisttt.LearnWord.Page.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class LearnPageStateViewModel extends AndroidViewModel {
    public enum FragmentInLearnPage {
        LearnPageFragment,
        SearchFragment
    }

    public static final int FONT_SIZE_SMALL = 0;
    public static final int FONT_SIZE_NORMAL = 1;
    public static final int FONT_SIZE_LARGE = 2;

    public LearnPageStateViewModel(@NonNull Application application) {
        super(application);
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

    private MutableLiveData<Integer> fontSizeLevel = new MutableLiveData<>(FONT_SIZE_NORMAL);

    public MutableLiveData<Integer> getFontSizeLevel() {
        return fontSizeLevel;
    }

    public void cycleFontSize() {
        Integer current = fontSizeLevel.getValue();
        if (current == null) current = FONT_SIZE_NORMAL;
        int next = (current + 1) % 3;
        fontSizeLevel.setValue(next);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
