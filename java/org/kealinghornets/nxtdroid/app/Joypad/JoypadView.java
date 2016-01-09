package org.kealinghornets.nxtdroid.app.Joypad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by jychuah on 11/13/13.
 */
public class JoypadView extends SurfaceView implements View.OnTouchListener, SurfaceHolder.Callback {
    static Paint black = new Paint();
    static double[] lines = { 0, 0.5, 1, 0.5,
                            0.5, 0, 0.5, 1,
                            0, 0, 0.2, 0,
                            0, 0, 0, 0.2,
                            0.8, 0, 1, 0,
                            1, 0, 1, 0.2,
                            0, 0.8, 0, 1,
                            0, 1, 0.2, 1,
                            0.8, 1, 1, 1,
                            1, 0.8, 1, 1 };
    static float[] scaledLines;
    int size = 0;
    int bgcolor = 0xFFF7F7F7;
    boolean firstDraw = true;
    JoyPadThread jpThread;

    public JoypadView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        black.setStrokeWidth(1);
        black.setStyle(Paint.Style.FILL_AND_STROKE);
        setOnTouchListener(this);
        setFocusable(true);
        getHolder().addCallback(this);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int min = Math.min(canvas.getWidth(), canvas.getHeight());
        if (size != min) {
            size = min;
            firstDraw = true;
            scaledLines = new float[lines.length];
            for (int i = 0; i < lines.length; i++) {
                scaledLines[i] = (float)(lines[i] * size);
                if (scaledLines[i] == size) {
                    scaledLines[i]--;
                }
            }
        }
        if (firstDraw) {
            firstDraw = false;
            x = size / 2;
            y = size / 2;
        }

        x = Math.max(0, x);
        x = Math.min(size, x);
        y = Math.max(0, y);
        y = Math.min(size, y);

        canvas.drawLines(scaledLines, black);
        canvas.clipRect(0, 0, size, size);
        canvas.drawCircle(x, y, (float)(size * 0.2), black);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        firstDraw = true;
        jpThread = new JoyPadThread(getHolder(), this);
        jpThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    private boolean killed = false;

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        killed = false;
        jpThread.running = false;
        while (!killed) {
            try {
                jpThread.join();
                killed = true;
            } catch (InterruptedException e) {

            }
        }
    }

    MotionEvent lastEvent = null;
    private float x = 0;
    private float y = 0;

    public double getJoypadX() {
        return (x - size / 2) / (size / 2);

    }

    public double getJoypadY() {
        return -(y - size / 2) / (size / 2);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        x = size / 2;
        y = size / 2;
        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            x = motionEvent.getX();
            y = motionEvent.getY();
        }
        return true;
    }


    class JoyPadThread extends Thread {
        boolean running = true;
        SurfaceHolder surfaceHolder;
        JoypadView jpView;
        public JoyPadThread(SurfaceHolder surfaceHolder, JoypadView jpView) {
            this.surfaceHolder = surfaceHolder;
            this.jpView = jpView;
        }

        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    c = surfaceHolder.lockCanvas();
                    synchronized(surfaceHolder) {
                        if (c != null) {
                            c.drawColor(bgcolor);
                            jpView.onDraw(c);
                        } else {
                            running = false;
                        }
                    }
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
}
