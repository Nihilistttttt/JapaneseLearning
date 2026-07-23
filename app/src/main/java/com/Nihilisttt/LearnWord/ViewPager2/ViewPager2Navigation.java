package com.Nihilisttt.LearnWord.ViewPager2;

import androidx.viewpager2.widget.ViewPager2;

public class ViewPager2Navigation {
    private ViewPager2 outerViewPager2, middleViewPage2;
    private String TAG ="ViewPager2Navigation";
    private static volatile ViewPager2Navigation instance;
    public static ViewPager2Navigation getInstance() {
        if (instance == null) {
            synchronized (ViewPager2Navigation.class) {
                if (instance == null) {
                    instance = new ViewPager2Navigation();
                }
            }
        }
        return instance;
    }
    public void setOuterViewPager2(ViewPager2 outerViewPager2) {
        this.outerViewPager2 = outerViewPager2;
    }

    public void setMiddleViewPage2(ViewPager2 middleViewPage2) {
        this.middleViewPage2 = middleViewPage2;
    }


    public void turnToDetailPage(int position) {
        if (outerViewPager2 != null) {
            outerViewPager2.setCurrentItem(1);
            // 使用 post 循环检查 middleViewPage2 是否就绪
            if (middleViewPage2 != null) {
                middleViewPage2.setCurrentItem(position,false); // smoothScroll:是否使用动画（保持false，否则无法直接跳转）
            } else {
                outerViewPager2.post(new Runnable() {
                    @Override
                    public void run() {
                        if (middleViewPage2 != null) {
                            middleViewPage2.setCurrentItem(position,false);
                        } else {
                            // 如果未就绪，继续投递自身直到成功
                            outerViewPager2.post(this);
                        }
                    }
                });
            }
        }
    }
}
