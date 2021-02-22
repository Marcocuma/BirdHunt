package com.example.juegofinal.objetos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.juegofinal.GameView;

import java.util.Random;

public class Pajaro {
    private static final int BMP_ROWS = 2;
    private static final int BMP_COLUMNS = 2;
    public int x = 0;
    public int y = 0;
    private int xSpeed = 5;
    private Bitmap bmp;
    private int currentFrame = 0;
    private int width, widthView;
    private int height;
    public int altura;
    private int animacion;

    public Pajaro(GameView gameView, Bitmap bmp,int altura) {
        widthView = gameView.getWidth();
        this.bmp=Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth()*(gameView.getWidth()/1080.0)), (int) (bmp.getHeight()*(gameView.getHeight()/2110.0)), false);;
        this.width = this.bmp.getWidth() / BMP_COLUMNS;
        this.height = this.bmp.getHeight() / BMP_ROWS;
        this.y=height*altura;
        this.altura = altura;
        this.xSpeed = (int) Math.ceil(Math.random()*50);
    }
    private void update() {
        if (x+width+xSpeed >= widthView) {
            xSpeed = -xSpeed;
            animacion = 1;
        }
        if (x < 0) {
            xSpeed = -xSpeed;
            animacion = 0;
        }
        x = x + xSpeed;
        currentFrame = ++currentFrame % BMP_COLUMNS;
    }

    public void onDraw(Canvas canvas) {
        update();
        int srcX = currentFrame * width;
        int srcY = animacion * height;
        Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
        Rect dst = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }
    public boolean colisiona(float x2, float y2,int h2,int w2) {
        return (x2+w2/2 >= x && x2+w2/2 <= x+width) && (y2-h2/2/2 >= y && y2+h2/2/2 <= y + height);
    }
}
