package com.Nihilisttt.LearnWord.UtilityClass;

import android.graphics.Canvas;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PartialSelectorDrawable extends Drawable {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean isPressed = false;
    private final int startPixel;
    private final int endPixel;
    private final int pressedColor;

    public PartialSelectorDrawable(int startPixel, int endPixel, int pressedColor) {
        this.startPixel = Math.max(startPixel, 0);
        this.endPixel = Math.max(endPixel, 0);
        this.pressedColor = pressedColor;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (!isPressed) return; // 仅在按压时绘制背景

        Rect bounds = getBounds();
        float height = bounds.height();
        float width = bounds.width();
        float topPosition = height * 0.3f;

        paint.setColor(pressedColor);
        float drawLeft = startPixel;
        float drawRight = width - endPixel;

        if (drawRight > drawLeft) {
            canvas.drawRect(
                    drawLeft,
                    topPosition,
                    drawRight,
                    height,
                    paint
            );
        }
    }

    @Override
    public void setAlpha(int alpha) { paint.setAlpha(alpha); }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() { return PixelFormat.TRANSLUCENT; }

    @Override
    public boolean isStateful() { return true; }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean prevPressed = isPressed;
        isPressed = false;
        for (int s : state) {
            if (s == android.R.attr.state_pressed) {
                isPressed = true;
                break;
            }
        }
        if (prevPressed != isPressed) {
            invalidateSelf();
            return true;
        }
        return false;
    }
}