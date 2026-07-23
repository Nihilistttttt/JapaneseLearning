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
    private int touchSlop;
    private float initialX;
    private float initialY;
    private String layerName = "未命名层级";
    private final NestedScrollableHostBetween3LayersManager nestedScrollableHostBetween3LayersManager = NestedScrollableHostBetween3LayersManager.getInstance();
    private int level = 2;

    public NestedScrollableHostBetween3Layers(@NonNull Context context) {
        super(context);
        init(context);
        this.TAG = "NestedScrollableHost";
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
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private ViewPager2 getParentViewPager() {
        if (level == 2) {
            return nestedScrollableHostBetween3LayersManager.getOuterViewPager2();
        } else return nestedScrollableHostBetween3LayersManager.getMiddleViewPage2();
    }

    private NestedScrollableHostBetween3Layers getMiddleHost() {
        return nestedScrollableHostBetween3LayersManager.getMiddle();
    }

    private View getChildView() {
        return getChildCount() > 0 ? getChildAt(0) : null;
    }

    private ViewPager2 getDeepestScrollableViewPager() {
        return findViewById(nestedScrollableHostBetween3LayersManager.getInnerViewPage2Id());
    }

    private boolean canAllChildScroll(int orientation, float delta) {
        int direction = (int) Math.signum(-delta);
        ViewPager2 deepestVp = getDeepestScrollableViewPager();
        if (deepestVp != null) {
            boolean canScroll = deepestVp.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL ?
                    deepestVp.canScrollHorizontally(direction) :
                    deepestVp.canScrollVertically(direction);
            if (canScroll) return true;
        }
        View child = getChildView();
        if (child == null) return false;
        return orientation == ViewPager2.ORIENTATION_HORIZONTAL ?
                child.canScrollHorizontally(direction) :
                child.canScrollVertically(direction);
    }

    private boolean canDirectChildScroll(int orientation, float delta) {
        int direction = (int) Math.signum(-delta);
        View child = getChildView();
        if (child == null) return false;
        return orientation == ViewPager2.ORIENTATION_HORIZONTAL ?
                child.canScrollHorizontally(direction) :
                child.canScrollVertically(direction);
    }

    private boolean canDeepChildScroll(int orientation, float delta) {
        int direction = (int) Math.signum(-delta);
        ViewPager2 deepestVp = getDeepestScrollableViewPager();
        if (deepestVp == null) return false;
        return deepestVp.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL ?
                deepestVp.canScrollHorizontally(direction) :
                deepestVp.canScrollVertically(direction);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        handleInterceptTouchEvent(e);
        return super.onInterceptTouchEvent(e);
    }

    private void handleInterceptTouchEvent(MotionEvent e) {
        Rect middleRect = new Rect();
        boolean isTouchInMiddleRect = false;
        if (level == 2) {
            ViewPager2 middleViewPager2 = findViewById(nestedScrollableHostBetween3LayersManager.getMiddleViewPage2Id());
            middleViewPager2.getHitRect(middleRect);
            isTouchInMiddleRect = middleRect.contains((int) e.getX(), (int) e.getY());
        }

        ViewPager2 parentViewPager = getParentViewPager();
        if (parentViewPager == null) return;

        int orientation = getEffectiveOrientation(parentViewPager);

        if (!canAllChildScroll(orientation, -1) && !canAllChildScroll(orientation, 1)) {
            return;
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = e.getX();
                initialY = e.getY();
                requestParentDisallowIntercept(true);
                break;

            case MotionEvent.ACTION_MOVE:
                handleMoveEvent(e, orientation, isTouchInMiddleRect);
                break;
        }
    }

    private int getEffectiveOrientation(ViewPager2 parentVp) {
        ViewPager2 deepestVp = getDeepestScrollableViewPager();
        return deepestVp != null ? deepestVp.getOrientation() : parentVp.getOrientation();
    }

    private void handleMoveEvent(MotionEvent e, int orientation, boolean isTouchInMiddleRect) {
        float dx = e.getX() - initialX;
        float dy = e.getY() - initialY;
        boolean isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL;

        float absDx = Math.abs(dx);
        float absDy = Math.abs(dy);

        boolean shouldHandle = isVpHorizontal ?
                (absDx > touchSlop && absDx > absDy * 0.5f) :
                (absDy > touchSlop && absDy > absDx * 0.5f);

        if (shouldHandle) {
            float delta = isVpHorizontal ? dx : dy;
            boolean canScroll = canAllChildScroll(orientation, delta);
            boolean canDirectScroll = canDirectChildScroll(orientation, delta);
            boolean canDeepScroll = canDeepChildScroll(orientation, delta);
            requestParentDisallowIntercept(canDirectScroll || (canDeepScroll && !isTouchInMiddleRect));

            if ((!canScroll) && (level == 3)) {
                NestedScrollableHostBetween3Layers middleHost = getMiddleHost();
                if (middleHost != null && middleHost.canAnyChildScroll()) {
                    middleHost.requestParentDisallowIntercept(true);
                }
            }
        }
    }

    private boolean canAnyChildScroll() {
        ViewPager2 parentVp = getParentViewPager();
        if (parentVp == null) return false;
        int orientation = parentVp.getOrientation();
        return canAllChildScroll(orientation, 1) || canAllChildScroll(orientation, -1);
    }

    private void requestParentDisallowIntercept(boolean disallow) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallow);
        }
    }
}
