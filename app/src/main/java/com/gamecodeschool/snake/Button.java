package com.gamecodeschool.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

class Button implements GameObject{
    private final int top;
    private final int left;
    private final int right;
    private final int bottom;


    Button(Point size) {
        int mScreenHeight = size.y;
        int mScreenWidth = size.x;
        int buttonWidth = mScreenWidth / 14;
        int buttonHeight = mScreenHeight / 12;

        top = mScreenWidth / 90;
        left = mScreenWidth - top - buttonWidth;
        right = mScreenWidth - top;
        bottom = top + buttonHeight;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        // Draw button
        paint.setColor(Color.argb(100, 255, 255, 255));
        canvas.drawRect(left, top, right, bottom, paint);

        // Set the colors back
        paint.setColor(Color.argb(255, 255, 255, 255));
    }

    boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        return x >= left && x <= right && y >= top && y <= bottom;
    }
}



