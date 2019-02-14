package com.example.project3;

import android.graphics.Point;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends AppCompatActivity {

    AsteroidView asteroidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        asteroidView = new AsteroidView(this);
        setContentView(asteroidView);
    }

    class AsteroidView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;
        boolean paused = false;
        Canvas canvas;
        Paint paint;
        int posxCircle, posYCircle;
        int posxRect;
        int dx, dy;
        int height, width;

        private long thisTimeFrame;
        public AsteroidView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();
        }

        @Override
        public void run() {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int maxX = size.x;
            int maxY = size.y;
            posxCircle = 550;
            posYCircle = maxY / 2;
            posxRect = 550;
            dx = 0;
            dy = 30;

            while (playing)
            {
                if (!paused) {
                    updateCircle();
                }
                draw();
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {

                }
            }
        }
        public void updateCircle() {
            posxCircle += dx;
            posYCircle += dy;
            if(posYCircle >= height - 20 && (posxCircle <= posxRect + 140 && posxCircle >= posxRect - 140 ))
            {
                dx = (posxCircle - posxRect) / 3;
                dy = -dy;
                System.out.println(posxCircle - posxRect);
            }
            else if ((posxCircle > width) || (posxCircle < 0))
            {
                dx = -dx;
            }
            else if(posYCircle < 0)
            {
                dy = -dy;
            }
            else if (posYCircle > height)
            {
                System.out.println("Game over!");
                pause();
            }
        }
        public void draw() {
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                width = canvas.getWidth();
                height = canvas.getHeight();

                // Draw the background color
                canvas.drawColor(Color.argb(255, 26, 128, 182));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                canvas.drawCircle(posxCircle, posYCircle, 30l, paint);

                System.out.println(width);

                //Rectangle r = new Rectangle(50, 50, 50, 50);
                canvas.drawRect(posxRect - 140, height - 20, posxRect + 140, height, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

       public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN)
                posxRect = (int)motionEvent.getX();
            return true;
        }

    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        asteroidView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        asteroidView.pause();
    }
}