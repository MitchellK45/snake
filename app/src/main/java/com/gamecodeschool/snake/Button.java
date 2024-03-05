package com.gamecodeschool.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

class Button {
    private int mTextFormatting;
    private int mScreenHeight;
    private int mScreenWidth;


    Button(Point size) {
        mScreenHeight = size.y;
        mScreenWidth = size.x;
        mTextFormatting = size.x / 50;


    }
    void drawControls(Canvas c, Paint p) {
        int buttonWidth = mScreenWidth / 14;
        int buttonHeight = mScreenHeight / 12;
        int buttonPadding = mScreenWidth / 90;
        // Draw buttons
        p.setColor(Color.argb(100, 255, 255, 255));
    c.drawRect(mScreenWidth - buttonPadding - buttonWidth,
            buttonPadding,
            mScreenWidth - buttonPadding,
            buttonPadding + buttonHeight,p);

        // Set the colors back
        p.setColor(Color.argb(255, 255, 255, 255));
    }
}
