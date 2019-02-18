package com.example.asteroids;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;


import java.util.Timer;
import java.util.TimerTask;

public class CustomView extends View {

    private Rect rectangle;
    private Paint paint;
    TimerTask t1;
    Timer timer;
    final Handler handler;

    public CustomView(Context context) {
        super(context);
        int x = 0;
        int y = 0;
        int sideLength = 200;

        // create a rectangle that we'll draw later

        rectangle = new Rect(x, y, sideLength, sideLength);
        timer = new Timer();
        handler = new Handler();

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);
    }


    @Override
    protected void onDraw(final Canvas canvas) {
        //canvas.drawColor(Color.BLUE);
        int maxX = getWidth();
        int maxY = getHeight();
        rectangle.offsetTo(maxX/2 - 100, 300);
        canvas.drawRect(rectangle, paint);


        t1 = new TimerTask() {
            public void run(){
                handler.post(new Runnable() {
                    public void run(){
                        rectangle.offsetTo(0,-10);
                        canvas.drawRect(rectangle,paint);
                    }
                });
            }
        };
        timer.schedule(t1, 1000, 1000);

    }
}