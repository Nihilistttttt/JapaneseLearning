package com.Nihilisttt.LearnWord.ViewPager2;

import androidx.viewpager2.widget.ViewPager2;

/**
 * 页面导航控制器
 *
 * 管理两个VP2的协调导航：
 * - overviewDetailVp2: 中层VP2(水平), MainLearnPage(pos0) ↔ ExtendedLearnPage(pos1)
 * - meaningVp2:        内层VP2(水平), 释义间切换
 *
 * 注意：字段命名与View层级对应，非ViewPager2Navigation内部编号。
 * LearnPage.java:50 曾设置outerViewPager2但被LearnPageFragment:79覆盖，属死代码已移除。
 */
public class ViewPager2Navigation {
    private ViewPager2 overviewDetailVp2;
    private ViewPager2 meaningVp2;
    private Integer pendingPosition = null;

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

    public void setOverviewDetailVp2(ViewPager2 vp2) {
        this.overviewDetailVp2 = vp2;
    }

    public void setMeaningVp2(ViewPager2 vp2) {
        this.meaningVp2 = vp2;
    }

    /**
     * 从概览页跳转到指定释义的详细页
     *
     * 逻辑：先切中层VP2到详细页(pos1)，再设内层VP2到目标释义。
     * 如果内层VP2尚未就绪(Fragment未创建)，暂存position等待回调。
     *
     * @param position 目标释义在内层VP2中的位置
     */
    public void turnToDetailPage(int position) {
        if (overviewDetailVp2 == null) return;
        overviewDetailVp2.setCurrentItem(1);
        if (meaningVp2 != null) {
            meaningVp2.setCurrentItem(position, false);
        } else {
            pendingPosition = position;
        }
    }

    /**
     * 内层VP2就绪回调
     *
     * 由ExtendedLearnPageFragment/SearchPageFragment在setMeaningVp2后调用。
     * 如果有待执行的导航意图，立即执行并清除。
     */
    public void onMeaningVp2Ready() {
        if (pendingPosition != null && meaningVp2 != null) {
            meaningVp2.setCurrentItem(pendingPosition, false);
            pendingPosition = null;
        }
    }

    /**
     * 清除待执行的导航意图
     *
     * 在以下场景调用：
     * - 用户点击上一个/下一个单词按钮时（切换单词后旧意图失效）
     * - Fragment销毁时（防止过期意图在重建后误执行）
     */
    public void clearPendingNavigation() {
        pendingPosition = null;
    }
}
