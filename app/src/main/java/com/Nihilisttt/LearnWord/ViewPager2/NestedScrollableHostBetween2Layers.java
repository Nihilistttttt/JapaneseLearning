package com.Nihilisttt.LearnWord.ViewPager2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

public class NestedScrollableHostBetween2Layers extends FrameLayout {

    private int touchSlop;
    private float initialX;
    private float initialY;

    public NestedScrollableHostBetween2Layers(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NestedScrollableHostBetween2Layers(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private ViewPager2 getParentViewPager() {
        ViewParent parent = getParent();
        while (parent != null) {
            if (parent instanceof ViewPager2) return (ViewPager2) parent;
            parent = parent.getParent();
        }
        return null;
    }

    private View getChildView() {
        return getChildCount() > 0 ? getChildAt(0) : null;
    }

    private boolean canChildScroll(int orientation, float delta) {
        int direction = -(int) Math.signum(delta);
        View child = getChildView();
        if (child == null) return false;

        switch (orientation) {
            case ViewPager2.ORIENTATION_HORIZONTAL:
                return child.canScrollHorizontally(direction);
            case ViewPager2.ORIENTATION_VERTICAL:
                return child.canScrollVertically(direction);
            default:
                throw new IllegalArgumentException("Invalid orientation");
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        handleInterceptTouchEvent(e);
        return super.onInterceptTouchEvent(e);
    }

    private void handleInterceptTouchEvent(MotionEvent e) {
        ViewPager2 parentViewPager = getParentViewPager();
        if (parentViewPager == null) return;

        int orientation = parentViewPager.getOrientation();

        // Check if child can't scroll in both directions
        if (!canChildScroll(orientation, -1) && !canChildScroll(orientation, 1)) {
            return;
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = e.getX();
                initialY = e.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = e.getX() - initialX;
                float dy = e.getY() - initialY;
                boolean isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL;

                // Apply scaling factors
                float scaledDx = Math.abs(dx) * (isVpHorizontal ? 0.5f : 1f);
                float scaledDy = Math.abs(dy) * (isVpHorizontal ? 1f : 0.5f);

                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    if (isVpHorizontal == (scaledDy > scaledDx)) {
                        // Allow parent intercept for perpendicular gestures
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        // Check if child can scroll in the gesture direction
                        boolean canScroll = canChildScroll(orientation, isVpHorizontal ? dx : dy);
                        getParent().requestDisallowInterceptTouchEvent(canScroll);
                    }
                }
                break;
        }
    }
}
