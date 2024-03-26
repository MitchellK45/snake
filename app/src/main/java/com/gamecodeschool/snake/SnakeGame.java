package com.gamecodeschool.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


class SnakeGame extends SurfaceView implements Runnable, GameObject{

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;


    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;
    private Pause_Button mPauseButton;
    private volatile boolean buttonTouched = false;
    private SoundEngine mSoundEngine;


    public SnakeGame(Context context, Point size) {
        super(context);

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;


        // Initialize the drawing objects and sound
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mCanvas = new Canvas();
        mSoundEngine = new SoundEngine(context);

        // Call the constructors of our game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mPauseButton = new Pause_Button(size);
    }


    // Called to start a new game
    public void newGame() {

        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        mApple.spawn();
        mScore = 0;

        mNextFrameTime = System.currentTimeMillis();
    }


    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                if (updateRequired()) {
                    update();
                }
            }
            draw(mCanvas,mPaint);
        }
    }

    public boolean updateRequired() {


        final long TARGET_FPS = 10;

        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }

    // Update all the game objects
    public void update() {
        mSnake.move();

        if (mSnake.checkDinner(mApple.getLocation())) {
            mApple.spawn();
            mScore++;
            mSoundEngine.playEat();
        }

        if (mSnake.detectDeath()) {
            mSoundEngine.playCrash();
            mPaused = true;
        }
    }


    // Do all the drawing
    @Override
    public void draw(Canvas canvas,Paint mPaint) {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            // Fill the screen with a color
            canvas.drawColor(Color.argb(255, 26, 128, 128 ));

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);

            // Draw the score
            canvas.drawText("" + mScore, 20, 120, mPaint);

            // Draw the apple,snake,and button
            mApple.draw(canvas, mPaint);
            mSnake.draw(canvas, mPaint);
            mPauseButton.draw(canvas, mPaint);

            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(60);

            mCanvas.drawText("Mitchell, Rajesh ", 1400, 60, mPaint);
            Typeface typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC);
            mPaint.setTypeface(typeface);


            if(mPaused){
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(130);

                mCanvas.drawText(getResources().getString(R.string.tap_to_play),200,700,mPaint);

            }

            if(buttonTouched){
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(130);
                mCanvas.drawText("PAUSED",200,700,mPaint);
            }
            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (mPaused) {
                    mPaused = false;
                    newGame();

                    // Don't want to process snake direction for this tap
                    return true;
                }
                else if(mPauseButton.handleTouchEvent(motionEvent)){
                    if (buttonTouched) {
                        buttonTouched = false;
                        resume();
                    } else {
                        buttonTouched = true;
                        pause();
                    }
                    return true;
                } else if(!buttonTouched) {
                    // Let the Snake class handle the input
                    mSnake.switchHeading(motionEvent);
                }
                break;

            default:
                break;
        }
        return true;
    }
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}