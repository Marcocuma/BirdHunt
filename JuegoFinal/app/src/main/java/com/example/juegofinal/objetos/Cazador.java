package com.example.juegofinal.objetos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.juegofinal.GameView;
import com.example.juegofinal.R;

public class Cazador {
    private static final int BMP_ROWS = 6;
    private static final int BMP_COLUMNS = 7;
    public int x = 0;
    public int y = 0;
    public boolean disparando = false;
    private int xSpeed;
    private GameView gameView;
    private Bitmap bmp;
    private int currentFrame = 0;
    public int width;
    private int height;
    private int animacion;

    public Cazador(GameView gameView, Bitmap bmp) {
        this.gameView=gameView;
        this.bmp=Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth()*(gameView.getWidth()/1080.0)), (int) (bmp.getHeight()*(gameView.getHeight()/2110.0)), false);
        this.width =this.bmp.getWidth() / BMP_COLUMNS;
        this.height = this.bmp.getHeight() / BMP_ROWS;
        this.y = gameView.getHeight() - height;
    }
    public void update(float xCambio) {
        if(!disparando) {
            if (xCambio > 0 && xCambio < 5) {
                animacion = 3;
                xSpeed = -20;
            } else if (xCambio >= 5) {
                animacion = 3;
                xSpeed = -30;
            } else if (xCambio == 0) {
                if (xSpeed > 0)
                    animacion = 0;
                else if (xSpeed < 0)
                    animacion = 1;
                xSpeed = 0;
            } else if (xCambio < 0 && xCambio > -5) {
                animacion = 2;
                xSpeed = 20;
            } else {
                animacion = 2;
                xSpeed = 30;
            }
        }
        currentFrame = ++currentFrame % BMP_COLUMNS;
    }

    public void onDraw(Canvas canvas) {
        if(!disparando) {
            if (x + xSpeed <= 0) {
                x = 0;
            } else if ((x + width) + xSpeed >= canvas.getWidth()) {
                x = canvas.getWidth() - width;
            } else {
                x = x + xSpeed;
            }
        } else if(currentFrame >= 5){
            disparando = false;
            if(animacion == 4){
                animacion=0;
            } else {
                animacion = 1;
            }
        }
        int srcX = currentFrame * width;
        int srcY = animacion * height;
        Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
        Rect dst = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }
    public void dipara(){
        disparando = true;
        if (animacion == 0 || animacion == 2)
            animacion = 4;
        else
            animacion = 5;
        xSpeed = 0;
        currentFrame = 0;
    }
    public boolean compruebaColision(float x2, float y2,int h2,int w2){
        return (x2+w2/2 >= x && x2+w2/2 <= x+width) && (y2-h2/2/2 >= y && y2+h2/2/2 <= y + height);
    }
}
