package com.Nihilisttt.LearnWord.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 嵌套滑动处理容器，解决ViewPager2内嵌可滚动视图的滑动冲突
 * 功能：根据滑动方向智能分配滑动事件给子视图或父ViewPager2
 */
public class NestedScrollableHostBetween3Layers extends FrameLayout {
    private final String TAG;
    private int touchSlop; // 系统认定的最小滑动距离
    private float initialX; // 触摸起始X坐标
    private float initialY; // 触摸起始Y坐标
    private String layerName = "未命名层级";
    private final NestedScrollableHostBetween3LayersManager nestedScrollableHostBetween3LayersManager = NestedScrollableHostBetween3LayersManager.getInstance();
    private int level = 2;

    // 构造方法
    public NestedScrollableHostBetween3Layers(@NonNull Context context) {
        super(context);
        init(context);
        this.TAG = "NestedScrollableHost";
        Log.i(TAG, "构造方法1被调用");
    }

    public NestedScrollableHostBetween3Layers(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        @SuppressLint("CustomViewStyleable")
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NestedViewPagerContainer);
        layerName = a.getString(R.styleable.NestedViewPagerContainer_layerName);
        level = a.getInt(R.styleable.NestedViewPagerContainer_level, 2);
        this.TAG = "NestedScrollableHost" + "[" + layerName + "] ";
        a.recycle();
        init(context);
        Log.i(TAG, "构造方法2被调用");
    }

    // 初始化触摸参数
    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Log.d(TAG, "初始化完成，touchSlop = " + touchSlop);
    }

    // 查找最近的父ViewPager2
    private ViewPager2 getParentViewPager() {
        if (level == 2) {
            return nestedScrollableHostBetween3LayersManager.getOuterViewPager2();
        } else return nestedScrollableHostBetween3LayersManager.getMiddleViewPage2();
    }

    // 获取所有父级嵌套滑动宿主（用于多层嵌套场景）
    private NestedScrollableHostBetween3Layers getNestedScrollHostList() {
        return nestedScrollableHostBetween3LayersManager.getMiddle();
//        return findViewById(nestedScrollableHostBetween3LayersManager.getMiddleViewPage2Id());
    }

    // 获取直接子视图
    private View getChildView() {
        View child = getChildCount() > 0 ? getChildAt(0) : null;
        Log.d(TAG, "获取子视图: " + (child != null ? child.getClass().getSimpleName() : "null"));
        return child;
    }

    // 递归查找所有子ViewPager2
    private List<ViewPager2> findAllChildViewPagers( ) {
        List<ViewPager2> result = new ArrayList<>();
        if (level == 2) {
            findViewById(nestedScrollableHostBetween3LayersManager.getMiddleViewPage2Id());
        }
        findViewById(nestedScrollableHostBetween3LayersManager.getInnerViewPage2Id());
        Log.d(TAG, "共找到" + result.size() + "个子ViewPager2");
        return result;
    }

    // 获取最深层可滑动的ViewPager2
    private ViewPager2 getDeepestScrollableViewPager() {
        ViewPager2 result = findViewById(nestedScrollableHostBetween3LayersManager.getInnerViewPage2Id());
        Log.d(TAG, "最深层ViewPager2: " + (result != null ? result.getId() : "null"));
        return result;

    }

    // 判断子视图是否可滑动
    private boolean canAllChildScroll(int orientation, float delta) {
        int direction = (int) Math.signum(-delta);
        Log.d(TAG, "检查子视图滑动能力，方向：" + direction + "，滑动轴："
                + (orientation == ViewPager2.ORIENTATION_HORIZONTAL ? "水平" : "垂直"));

        // 优先检查嵌套ViewPager2
        ViewPager2 deepestVp = getDeepestScrollableViewPager();
        if (deepestVp != null) {
            boolean canScroll = deepestVp.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL ?
                    deepestVp.canScrollHorizontally(direction) :
                    deepestVp.canScrollVertically(direction);

            Log.d(TAG, "深层ViewPager2[" + deepestVp.getId() + "]可滑动: " + canScroll);
            if (canScroll) return true;
        }

        // 检查常规子视图
        View child = getChildView();
        if (child == null) {
            Log.d(TAG, "无子视图，不可滑动");
            return false;
        }

        boolean canScroll = orientation == ViewPager2.ORIENTATION_HORIZONTAL ?
                child.canScrollHorizontally(direction) :
                child.canScrollVertically(direction);

        Log.d(TAG, "子视图[" + child.getClass().getSimpleName() + "]可滑动: " + canScroll);
        return canScroll;
    }
    // 判断子视图是否可滑动
    private boolean canDirectChildScroll(int orientation, float delta) {
        int direction = (int) Math.signum(-delta);
        Log.d(TAG, "检查子视图滑动能力，方向：" + direction + "，滑动轴："
                + (orientation == ViewPager2.ORIENTATION_HORIZONTAL ? "水平" : "垂直"));

        // 检查常规子视图
        View child = getChildView();
        if (child == null) {
            Log.d(TAG, "无子视图，不可滑动");
            return false;
        }
        boolean canScroll = orientation == ViewPager2.ORIENTATION_HORIZONTAL ?
                child.canScrollHorizontally(direction) :
                child.canScrollVertically(direction);
        Log.d(TAG, "子视图[" + child.getClass().getSimpleName() + "]可滑动: " + canScroll);
        return canScroll;
    }
    // 判断子视图是否可滑动
    private boolean canDeepChildScroll(int orientation, float delta) {
        int direction = (int) Math.signum(-delta);
        Log.d(TAG, "检查子视图滑动能力，方向：" + direction + "，滑动轴："
                + (orientation == ViewPager2.ORIENTATION_HORIZONTAL ? "水平" : "垂直"));

        // 优先检查嵌套ViewPager2
        ViewPager2 deepestVp = getDeepestScrollableViewPager();

        boolean canScroll = deepestVp.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL ?
                deepestVp.canScrollHorizontally(direction) :
                deepestVp.canScrollVertically(direction);

        Log.d(TAG, "深层ViewPager2[" + deepestVp.getId() + "]可滑动: " + canScroll);
        return canScroll;
    }

    // 触摸事件拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        Log.d(TAG, "触摸事件拦截: " + MotionEvent.actionToString(e.getAction()));
        handleInterceptTouchEvent(e);
        return super.onInterceptTouchEvent(e);
    }

    // 处理触摸拦截逻辑
    private void handleInterceptTouchEvent(MotionEvent e) {
        Rect middleRect = new Rect();
        boolean isTouchInMiddleRect = false;
        if (level == 2) {
            ViewPager2 middleViewPager2 = findViewById(nestedScrollableHostBetween3LayersManager.getMiddleViewPage2Id());
            middleViewPager2.getHitRect(middleRect);
            isTouchInMiddleRect = middleRect.contains((int) e.getX(), (int) e.getY());
        }
        Log.d(TAG, "isTouchInMiddleRect: " + isTouchInMiddleRect);


        ViewPager2 parentViewPager = getParentViewPager();
        if (parentViewPager == null) return;

        int orientation = getEffectiveOrientation(parentViewPager);
        Log.d(TAG, "有效滑动方向: " + (orientation == ViewPager2.ORIENTATION_HORIZONTAL ? "水平" : "垂直"));

        if (!canAllChildScroll(orientation, -1) && !canAllChildScroll(orientation, 1)) {
            Log.d(TAG, "子视图无滑动能力，不处理事件");
            return;
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = e.getX();
                initialY = e.getY();
                Log.d(TAG, "按下事件，初始坐标 X:" + initialX + " Y:" + initialY);
                requestParentDisallowIntercept(true);
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "移动事件，当前坐标 X:" + e.getX() + " Y:" + e.getY());
                handleMoveEvent(e, orientation, isTouchInMiddleRect);
                break;
        }
    }

    // 获取有效滑动方向（优先使用深层ViewPager2的方向）
    private int getEffectiveOrientation(ViewPager2 parentVp) {
        ViewPager2 deepestVp = getDeepestScrollableViewPager();
        int orientation = deepestVp != null ? deepestVp.getOrientation() : parentVp.getOrientation();
        Log.d(TAG, "最终滑动方向: " + (orientation == ViewPager2.ORIENTATION_HORIZONTAL ? "水平" : "垂直"));
        return orientation;
    }

    // 处理移动事件
    private void handleMoveEvent(MotionEvent e, int orientation, boolean isTouchInMiddleRect) {
        float dx = e.getX() - initialX;
        float dy = e.getY() - initialY;
        boolean isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL;

        float absDx = Math.abs(dx);
        float absDy = Math.abs(dy);

        Log.d(TAG, "位移量 dx:" + dx + " dy:" + dy +
                "，阈值判断：" + (isVpHorizontal ? "水平" : "垂直"));

        boolean shouldHandle = isVpHorizontal ?
                (absDx > touchSlop && absDx > absDy * 0.5f) :
                (absDy > touchSlop && absDy > absDx * 0.5f);

        if (shouldHandle) {
            float delta = isVpHorizontal ? dx : dy;
            boolean canScroll = canAllChildScroll(orientation, delta);
            boolean canDirectScroll = canDirectChildScroll(orientation, delta);
            boolean canDeepScroll = canDeepChildScroll(orientation, delta);
            Log.d(TAG, "需要处理事件，子视图" + (canScroll ? "可" : "不可") + "滑动");
            Log.d(TAG, "需要处理事件，直接子视图" + (canDirectScroll ? "可" : "不可") + "滑动");
            Log.d(TAG, "需要处理事件，深度子视图" + (canDeepScroll ? "可" : "不可") + "滑动");
            requestParentDisallowIntercept(canDirectScroll||(canDeepScroll&&!isTouchInMiddleRect));

            if ((!canScroll) && (level == 3)) {
                Log.d(TAG, "开始处理嵌套宿主...");
                handleNestedScrollableHosts();
            }
        }
    }

    // 控制父容器是否拦截事件
    private void requestParentDisallowIntercept(boolean disallow) {
        ViewParent parent = getParent();
        if (parent != null) {
            Log.d(TAG, "设置父容器拦截：" + !disallow);
            parent.requestDisallowInterceptTouchEvent(disallow);
        }
    }

    // 处理多层嵌套宿主
    private void handleNestedScrollableHosts() {
        NestedScrollableHostBetween3Layers host = getNestedScrollHostList();
        Log.d(TAG, "host == "+host+"        handleNestedScrollableHosts++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (host.checkDeepChildScrollable()) {
            Log.d(TAG, "发现可滑动子视图，阻止父容器拦截");
            host.requestParentDisallowIntercept(true);
        }
    }

    // 深度检查子视图滑动能力
    private boolean checkDeepChildScrollable() {
        Log.d(TAG, "开始深度检查子视图...");
        List<ViewPager2> viewPagers = findAllChildViewPagers();
        for (ViewPager2 vp : viewPagers) {
            boolean canScroll = vp.canScrollHorizontally(1) || vp.canScrollHorizontally(-1) ||
                    vp.canScrollVertically(1) || vp.canScrollVertically(-1);
            Log.d(TAG, "ViewPager2[" + vp.getId() + "]可滑动: " + canScroll);
            if (canScroll) return true;
        }
        return checkChildScrollable();
    }

    // 检查直接子视图滑动能力
    private boolean checkChildScrollable() {
        ViewPager2 parentVp = getParentViewPager();
        if (parentVp == null) return false;
        int orientation = parentVp.getOrientation();
        boolean canScroll = canAllChildScroll(orientation, 1) || canAllChildScroll(orientation, -1);
        Log.d(TAG, "直接子视图滑动能力: " + canScroll);
        return canScroll;
    }
}
