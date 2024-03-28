package com.gamecodeschool.snake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class SnakeGame extends SurfaceView implements Runnable, GameObject{

    private Thread mThread = null;
    private long mNextFrameTime;
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;
    private final int NUM_BLOCKS_WIDE = 40;
    private final int mNumBlocksHigh;
    private int mScore;
    private static Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private static Paint mPaint;

    private Snake mSnake;
    // And an apple
    private Apple mApple;
    private Button mButton;
    private volatile boolean buttonTouch = false;
    private SoundEngine mSoundEngine;
    private Bitmap mBackground;



    public SnakeGame(Context context, Point size) {
        super(context);
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;
        initializeObj(context, size);
        initializeGameObjects(context, size);

    }
        private void initializeObj(Context context, Point size) {
            mSurfaceHolder = getHolder();
            mPaint = new Paint();
            mCanvas = new Canvas();
            mSoundEngine = new SoundEngine(context);
            mBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.dog);
            mBackground = Bitmap.createScaledBitmap(mBackground, size.x, size.y, true);
        }
        private void initializeGameObjects(Context context, Point size) {
            int blockSize = size.x / NUM_BLOCKS_WIDE;

            mApple = new Apple(context,
                    new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                    blockSize);

            mSnake = new Snake(context,
                    new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh),
                    blockSize);

            mButton = new Button(size);
        }
        public void draw(GameObject gameObject){
        gameObject.draw(mCanvas,mPaint);
    }
    // Called to start a new game
    private void newGame() {

        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        mApple.move();
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

    private boolean updateRequired() {


        final long TARGET_FPS = 10;

        final long MILLIS_PER_SECOND = 1000;

        if(mNextFrameTime <= System.currentTimeMillis()){
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            return true;
        }

        return false;
    }

    // Update all the game objects
    private void update() {
        mSnake.move();

        if (mSnake.checkDinner(mApple.getLocation())) {
            mApple.move();
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
            drawAllObj();
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }
    private void drawAllObj() {
        font();
        mCanvas.drawBitmap(mBackground, 0, 0, null);

        GameObject[] gameObjects = {mApple, mSnake, mButton};
        for (GameObject gameObject : gameObjects) {
            draw(gameObject);
        }

        AllText();
    }
    private void AllText(){
        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setTextSize(130);
        setScoreText();
        setNameText();
        if(mPaused){
            setPlayText();
        }
        if(buttonTouch){
            setPauseText();
        }

    }
    private void setNameText(){
        mCanvas.drawText("Mitchell K, Rajesh S", 1200, 120, mPaint);
    }
    private void setScoreText(){
        mCanvas.drawText("" + mScore, 20, 120, mPaint);
    }
    private void setPauseText(){
        mCanvas.drawText("PAUSED",200,700,mPaint);
    }

    private void setPlayText(){
        mCanvas.drawText(getResources().getString(R.string.tap_to_play),200,700,mPaint);
    }

    private void font() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Typeface typeface = getResources().getFont(R.font.groovy);
            mPaint.setTypeface(typeface);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mPaused) {
                startingGame();
            }
            else if (mButton.onTouchEvent(motionEvent)) {
                pausingGame();
            }
            else if (!buttonTouch) {
                mSnake.switchHeading(motionEvent);
            }
        }
        return true;
    }
    private void startingGame() {
        mPaused = false;
        newGame();
    }
    private void pausingGame() {
        if (buttonTouch) {
            buttonTouch = false;
            resume();
        } else {
            buttonTouch = true;
            pause();
        }
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