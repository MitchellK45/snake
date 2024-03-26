package com.gamecodeschool.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

class Pause_Button implements GameObject{
    private int mScreenHeight;
    private int mScreenWidth;
    private int buttonWidth;
    private int buttonHeight;
    private int buttonPadding;

    Pause_Button(Point size) {
        mScreenHeight = size.y;
        mScreenWidth = size.x;

        buttonWidth = mScreenWidth / 14;
        buttonHeight = mScreenHeight / 12;
        buttonPadding = mScreenWidth / 90;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        // Draw button
        paint.setColor(Color.argb(100, 255, 255, 255));
        canvas.drawRect(
                mScreenWidth - buttonPadding - buttonWidth,
                    buttonPadding,
                mScreenWidth - buttonPadding,
                buttonPadding + buttonHeight, paint);

        // Set the colors back
        paint.setColor(Color.argb(255, 255, 255, 255));
    }

    // Method to handle touch events
    boolean handleTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (x >= mScreenWidth - buttonPadding - buttonWidth &&
                x <= mScreenWidth - buttonPadding &&
                y >= buttonPadding &&
                y <= buttonPadding + buttonHeight) {
            // Touch event is within the button

            return true; // return true if button has been pressed
        }

        return false; // Return false if the touch event is not within the button bounds
    }
}



