package com.example.juegofinal;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
public class GameLoop extends Thread {
    static final long FPS = 15;
    private GameView view;
    private boolean running = false;

    public GameLoop(GameView view) {
        this.view = view;
    }
    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public synchronized void start() {
        super.start();
        running = true;
    }

    @SuppressLint("WrongCall")

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.onDraw(c);
                }
            }
            finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }
    public boolean getRunning(){
        return running;
    }
}