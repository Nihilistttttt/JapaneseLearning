package com.Nihilisttt.LearnWord.Page.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class LearnPageStateViewModel extends AndroidViewModel {
    // 滚动方向枚举
    public enum FragmentInLearnPage {
        LearnPageFragment,
        SearchFragment
    }
    public LearnPageStateViewModel(@NonNull Application application) {
        super(application);
    }

    // region  添加控制 ViewPager 滚动的 LiveData
    private MutableLiveData<Boolean> isViewPagerScrollEnabled = new MutableLiveData<>(true);

    public MutableLiveData<Boolean> getIsViewPagerScrollEnabled() {
        return isViewPagerScrollEnabled;
    }

    public void setViewPagerScrollEnabled(boolean enabled) {
        isViewPagerScrollEnabled.setValue(enabled);
    }
    // endregion
    // region  添加控制 是否启用blankPart 判定的 LiveData
    private MutableLiveData<FragmentInLearnPage> whichFragmentInLearnPage = new MutableLiveData<>();

    public MutableLiveData<FragmentInLearnPage> getWhichFragmentInLearnPage() {
        return whichFragmentInLearnPage;
    }

    public void setWhichFragmentInLearnPage(FragmentInLearnPage fragment) {
        whichFragmentInLearnPage.setValue(fragment);
    }
    // endregion


    @Override
    protected void onCleared() {
        super.onCleared();
        // 清理可能存在的资源
    }
}

