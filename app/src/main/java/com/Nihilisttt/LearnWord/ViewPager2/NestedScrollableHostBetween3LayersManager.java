package com.Nihilisttt.LearnWord.ViewPager2;


import androidx.viewpager2.widget.ViewPager2;

public class NestedScrollableHostBetween3LayersManager {
    private static volatile NestedScrollableHostBetween3LayersManager instance;
    private NestedScrollableHostBetween3Layers middle;
    private NestedScrollableHostBetween3Layers inner;
    private ViewPager2 outerViewPager2,middleViewPage2,innerViewPage2;
    private int outerViewPager2Id,middleViewPage2Id,innerViewPage2Id;

    public int getOuterViewPager2Id() {
        return outerViewPager2Id;
    }

    public void setOuterViewPager2Id(int outerViewPager2Id) {
        this.outerViewPager2Id = outerViewPager2Id;
    }

    public int getMiddleViewPage2Id() {
        return middleViewPage2Id;
    }

    public void setMiddleViewPage2Id(int middleViewPage2Id) {
        this.middleViewPage2Id = middleViewPage2Id;
    }

    public int getInnerViewPage2Id() {
        return innerViewPage2Id;
    }

    public void setInnerViewPage2Id(int innerViewPage2Id) {
        this.innerViewPage2Id = innerViewPage2Id;
    }

    public ViewPager2 getOuterViewPager2() {
        return outerViewPager2;
    }

    public void setOuterViewPager2(ViewPager2 outerViewPager2) {
        this.outerViewPager2 = outerViewPager2;
    }

    public ViewPager2 getMiddleViewPage2() {
        return middleViewPage2;
    }

    public void setMiddleViewPage2(ViewPager2 middleViewPage2) {
        this.middleViewPage2 = middleViewPage2;
    }

    public ViewPager2 getInnerViewPage2() {
        return innerViewPage2;
    }

    public void setInnerViewPage2(ViewPager2 innerViewPage2) {
        this.innerViewPage2 = innerViewPage2;
    }

    public static NestedScrollableHostBetween3LayersManager getInstance() {
        if (instance == null) {
            synchronized (NestedScrollableHostBetween3LayersManager.class) {
                if (instance == null) {
                    instance = new NestedScrollableHostBetween3LayersManager();
                }
            }
        }
        return instance;
    }


    public NestedScrollableHostBetween3Layers getMiddle() {
        return middle;
    }

    public void setMiddle(NestedScrollableHostBetween3Layers middle) {
        this.middle = middle;
    }

    public NestedScrollableHostBetween3Layers getInner() {
        return inner;
    }

    public void setInner(NestedScrollableHostBetween3Layers inner) {
        this.inner = inner;
    }
}
