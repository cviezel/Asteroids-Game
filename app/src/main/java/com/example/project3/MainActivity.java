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
import android.view.View;
import java.util.Random;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    boolean superHit = false;

    AsteroidView asteroidView;
    int height, width;
    int posxRect;
    ArrayList<Ball> ball_list = new ArrayList<Ball>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        asteroidView = new AsteroidView(this);
        setContentView(asteroidView);
    }

    public class Ball{
        int posxCircle, posYCircle;
        int dx;
        int dy;
        boolean phase = false;

        public Ball(int x, int y)
        {
            this.posxCircle = x;
            this.posYCircle = y;
            this.dx = 0;
            this.dy = 30;
        }

        public boolean updateCircle() {
            posxCircle += dx;
            posYCircle += dy;
            if(!phase)
            {
                if(posYCircle >= height - 20 && (posxCircle <= posxRect + 140 && posxCircle >= posxRect - 140 ))
                {
                    if(superHit)
                    {
                        dx = 0;
                        dy = dy + dx;
                        dy *= 2;
                        dy = -dy;
                        superHit = false;
                        phase = true;
                    }
                    else {
                        dx = (posxCircle - posxRect) / 3;
                        dy += 1;
                        dy = -dy;
                        //System.out.println(dx + " " + dy);
                    }
                }
                else if ((posxCircle > width) || (posxCircle < 0))
                {
                    dx = -dx;
                }
                else if(posYCircle < 0 && !phase)
                {
                    dy = -dy;
                }
                else if (posYCircle > height)
                {
                    return false;
                    //asteroidView.run();
                }
                if(posYCircle < 0 && phase)
                {
                    posYCircle = -100;
                    dy = 0;
                    dx = 0;
                }
            }
            return true;
        }
    }

    class AsteroidView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;
        boolean paused = false;
        Canvas canvas;
        Paint paint;
        Paint paintRect;

        private long thisTimeFrame;
        public AsteroidView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();
            paintRect = new Paint();
        }

        @Override
        public void run() {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int maxX = size.x;
            int maxY = size.y;
            Ball ball = new Ball(550, maxY / 2);
            ball_list.clear();
            ball_list.add(ball);

            posxRect = 550;


            while (playing)
            {
                if (!paused) {
                    Random rand = new Random();
                    int n = rand.nextInt(500);
                    if(n == 420)
                    {
                        int x, y;
                        x = rand.nextInt(1080);
                        Ball temp = new Ball(x, maxY / 4);
                        ball_list.add(temp);
                    }
                    for(Ball i : ball_list)
                    {
                        if(i.phase == true && i.posYCircle < 0)
                        {
                            ball_list.remove(i);
                        }
                        else if(!i.updateCircle())
                        {
                            run();
                        }
                    }
                }
                draw();
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {

                }
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

                if(superHit)
                {
                    paintRect.setColor(Color.argb(255, 255, 0, 0));
                }
                else
                {
                    paintRect.setColor(Color.argb(255, 255, 255, 255));
                }

                for(Ball i : ball_list)
                    canvas.drawCircle(i.posxCircle, i.posYCircle, 30l, paint);
                canvas.drawRect(posxRect - 140, height - 20, posxRect + 140, height, paintRect);

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
            if ((motionEvent.getY() > height / 2) && (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE))
                posxRect = (int)motionEvent.getX();

            if (motionEvent.getY() < height / 2 && ball_list.size() > 1)
            {
                superHit = true;
            }




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