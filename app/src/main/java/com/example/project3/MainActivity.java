package com.example.project3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Random;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    boolean superHit = false;
    AsteroidView asteroidView;
    int height, width;
    int posxRect;
    ArrayList<Ball> ball_list = new ArrayList<Ball>();
    Boss boss;
    int lives = 3;
    Rect button;
    boolean pauseFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        asteroidView = new AsteroidView(this);
        setContentView(asteroidView);
    }

    public class Boss{
        int posx, posy;
        int dx;
        int dy;
        int health;
        public Boss(int x, int y, int h)
        {
            this.posx = x;
            this.posy = y;
            this.health = h;
        }
        public void hit(int dmg)
        {
            System.out.println("Hit for " + dmg + " damage!");
            health -= dmg;
        }
        public void move()
        {
            posx += dx;
            posy += dy;
            int speed = 4000/health;
            int ySpeed = 15;
            Random r = new Random();
            if(dx == 0 && dy == 0)
            {
                dx = r.nextInt(speed + 1);
                dy = r.nextInt(ySpeed + 1);
            }
            else if(posx > width)
            {
                dx = - Math.abs(r.nextInt(speed + 1));
            }
            else if(posx < 0)
            {
                dx = Math.abs(r.nextInt(speed + 1));
            }
            else if(posy > height / 5)
            {
                dy = - Math.abs(r.nextInt(ySpeed + 1));
                if(dx > 0)
                    dx = r.nextInt(speed + 1);
                else
                    dx = -r.nextInt(speed + 1);
            }
            else if(posy < 0)
            {
                dy = Math.abs(r.nextInt(ySpeed + 1));
                if(dx > 0)
                    dx = r.nextInt(speed + 1);
                else
                    dx = -r.nextInt(speed + 1);
            }
        }
    }
    public class Ball{
        int posx, posy;
        int dx;
        int dy;
        boolean phase = false;
        boolean hit = false;
        int damage = 1;

        public Ball(int x, int y)
        {
            this.posx = x;
            this.posy = y;
            this.dx = 0;
            this.dy = 30;
        }

        public boolean updateCircle() {
            posx += dx;
            posy += dy;
            if(!hit)
            {
                if(posx <= boss.posx + boss.health && posx >= boss.posx - boss.health && posy <= boss.posy + boss.health && posy >= boss.posy - boss.health)
                {
                    if(dy < 0)
                    {
                        hit = true;
                        if(phase)
                        {
                            if(2*damage >= 20 && lives < 3)
                                lives++;
                            boss.hit(2 * damage);
                        }
                        else
                            boss.hit(damage);
                        damage++;
                    }
                }
            }
            if(!phase)
            {
                if(posy >= height - 20 && (posx <= posxRect + 140 && posx >= posxRect - 140 ))
                {
                    hit = false;
                    if(superHit)
                    {
                        dx = 0;
                        dy = Math.abs(dy * 3);
                        dy = -dy;
                        superHit = false;
                        phase = true;
                    }
                    else {
                        dx = (posx - posxRect) / 4;
                        dy = Math.abs(dy) + 1;
                        dy = -dy;
                        //System.out.println(dx + " " + dy);
                    }
                }
                else if ((posx > width) || (posx < 0))
                {
                    dx = -dx;
                }
                else if(posy < 0 && !phase)
                {
                    dy = -dy;
                }
                else if (posy > height)
                {
                    return false;
                    //asteroidView.run();
                }
                if(posy < 0 && phase)
                {
                    posy = -100;
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
        Paint paintBoss;
        Paint paintLives;
        Paint paintBg;

        private long thisTimeFrame;
        public AsteroidView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();
            paintBoss = new Paint();
            paintRect = new Paint();
            paintLives = new Paint();
            paintBg = new Paint();
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
            boss = new Boss(550, 100, 400);
            posxRect = 550;
            lives = 3;
            superHit = false;

            while (playing)
            {
                if (!paused) {
                    boss.move();
                    Random rand = new Random();
                    int n = rand.nextInt(350);
                    //System.out.println(n);
                    if(n == 13)
                    {
                        int x;
                        x = rand.nextInt(1080);
                        Ball temp = new Ball(x, maxY / 4);
                        ball_list.add(temp);
                    }
                    int index = 0;
                    int j = 0;
                    boolean delFlag = false;
                    for(Ball i : ball_list)
                    {
                        if(i.phase == true && i.posy < 0)
                        {
                            index = j;
                            delFlag = true;
                            //ball_list.remove(i);
                        }
                        if(i.posy < -100)
                        {
                            index = j;
                            delFlag = true;
                        }
                        else if(!i.updateCircle()) {
                            if (ball_list.size() == 1 || lives == 1) {
                                //pause();
                                //System.out.println(lives);
                                startActivity(new Intent(MainActivity.this, LoseScreenActivity.class));
                                //run();
                            } else {
                                lives--;
                                //System.out.println(lives);
                                index = j;
                                delFlag = true;
                            }
                        }
                        j++;
                    }
                    if(delFlag)
                        ball_list.remove(index);
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

                paintBoss.setColor(Color.argb(255, 0, 0, 0));
                canvas.drawCircle(boss.posx, boss.posy, boss.health, paintBoss);
                paint.setTextSize(70);
                canvas.drawText(Integer.toString(boss.health), boss.posx - 50, boss.posy + 10, paint);

                for(Ball i : ball_list) {
                    int bg = Math.max(0, 255 - 8*i.damage);
                    paint.setColor(Color.argb(255, 255, bg, bg));
                    //if(i.damage >= 10)
                    //    paint.setColor(Color.YELLOW);
                    canvas.drawCircle(i.posx, i.posy, 30l, paint);
                }
                canvas.drawRect(posxRect - 140, height - 20, posxRect + 140, height, paintRect);

                paintLives.setColor(Color.GREEN);
                if(lives == 1 || ball_list.size() == 1)
                    paintLives.setColor(Color.RED);

                int cx, cy;
                cx = 40;
                cy = 30;
                if(lives == 1)
                {
                    canvas.drawCircle(cx, cy, 20, paintLives);
                }
                if(lives == 2)
                {
                    canvas.drawCircle(cx, cy, 20, paintLives);
                    canvas.drawCircle(cx + 100, cy, 20, paintLives);
                }
                if(lives == 3)
                {
                    canvas.drawCircle(cx, cy, 20, paintLives);
                    canvas.drawCircle(cx + 100, cy, 20, paintLives);
                    canvas.drawCircle(cx + 200, cy, 20, paintLives);
                }
                paintBg.setColor(Color.WHITE);
                paintBg.setTextSize(70);
                canvas.drawText("Lives", 40, 140, paintBg);

                Paint butPaint = new Paint();
                butPaint.setColor(Color.WHITE);
                button = new Rect(width - 150, 50, width- 50,  150);
                canvas.drawRect(button, butPaint);

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
            if(motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN && button.contains((int)motionEvent.getX(), (int)motionEvent.getY()))
            {
                if(!paused)
                {
                    paused = true;
                }
                else
                {
                    paused = false;
                }
                return true;
            }
            else if ((motionEvent.getY() > height / 2) && (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE))
                posxRect = (int)motionEvent.getX();

            else if (motionEvent.getY() < height / 2 && ball_list.size() > 1)
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