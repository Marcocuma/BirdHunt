package com.example.juegofinal.objetos;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.juegofinal.GameView;

public class PlumaEnemiga {
    public int x = 0;
    public int y = 0;
    public int height;
    public int width;
    private int xSpeed;
    private GameView gameView;
    private Bitmap bmp;
    public Boolean perdida = false;

    public PlumaEnemiga(GameView gameView, Bitmap bmp,int x, int y) {
        this.gameView=gameView;
        this.bmp=Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth()*(gameView.getWidth()/1080.0)), (int) (bmp.getHeight()*(gameView.getHeight()/2110.0)), false);
        this.x = x;
        this.y = y;
        this.height = this.bmp.getHeight();
        this.width = this.bmp.getWidth();
    }
    public void update() {
        xSpeed = 100;
        this.y = this.y+this.xSpeed;
        if(this.y > gameView.getHeight()){
            perdida = true;
        }
    }

    public void onDraw(Canvas canvas) {
        if(!perdida)
            canvas.drawBitmap(bmp, x, y, null);
    }
}
