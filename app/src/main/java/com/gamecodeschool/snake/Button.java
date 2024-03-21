package com.gamecodeschool.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

class Button implements GameObject{
    private int mTextFormatting;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mButtonWidth;
    private int mButtonHeight;
    private int mButtonPadding;

    Button(Point size) {
        mScreenHeight = size.y;
        mScreenWidth = size.x;
        mTextFormatting = size.x / 50;

        mButtonWidth = mScreenWidth / 14;
        mButtonHeight = mScreenHeight / 12;
        mButtonPadding = mScreenWidth / 90;
    }

    public void draw(Canvas c, Paint p) {
        // Draw button
        p.setColor(Color.argb(100, 255, 255, 255));
        c.drawRect(mScreenWidth - mButtonPadding - mButtonWidth,
                mButtonPadding,
                mScreenWidth - mButtonPadding,
                mButtonPadding + mButtonHeight, p);

        // Set the colors back
        p.setColor(Color.argb(255, 255, 255, 255));
    }

    // Method to handle touch events
    boolean handleTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (x >= mScreenWidth - mButtonPadding - mButtonWidth &&
                x <= mScreenWidth - mButtonPadding &&
                y >= mButtonPadding &&
                y <= mButtonPadding + mButtonHeight) {
            // Touch event is within the button

            return true; // return true if button has been pressed
        }

        return false; // Return false if the touch event is not within the button bounds
    }
}

