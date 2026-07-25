package com.Nihilisttt.LearnWord.UtilityClass;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
    private int lineSpacing = 0;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLineSpacing(int pixels) {
        this.lineSpacing = pixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int totalWidth = 0;
        int totalHeight = 0;
        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > widthSize && lineWidth > 0) {
                totalWidth = Math.max(totalWidth, lineWidth);
                totalHeight += lineHeight + lineSpacing;
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }

        totalWidth = Math.max(totalWidth, lineWidth);
        totalHeight += lineHeight;

        totalWidth += getPaddingLeft() + getPaddingRight();
        totalHeight += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize : totalWidth,
                heightMode == MeasureSpec.EXACTLY ? heightSize : totalHeight
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l - getPaddingRight();
        int x = getPaddingLeft();
        int y = getPaddingTop();
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (x + lp.leftMargin + childWidth + lp.rightMargin > width && x > getPaddingLeft()) {
                x = getPaddingLeft();
                y += lineHeight + lineSpacing;
                lineHeight = 0;
            }

            int childLeft = x + lp.leftMargin;
            int childTop = y + lp.topMargin;
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            x += lp.leftMargin + childWidth + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }
}