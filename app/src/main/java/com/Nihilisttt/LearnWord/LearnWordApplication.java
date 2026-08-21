package com.Nihilisttt.LearnWord;

import android.app.Application;

import com.Nihilisttt.LearnWord.Database.Repository.PresetBookInitializer;

public class LearnWordApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PresetBookInitializer.initIfNeeded(this);
    }
}