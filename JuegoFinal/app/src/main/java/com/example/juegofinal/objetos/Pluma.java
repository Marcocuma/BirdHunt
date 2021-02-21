package com.example.juegofinal.objetos;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.juegofinal.GameView;

import java.util.List;

public class Pluma {
    private float x;
    private float y;
    private Bitmap bmp;
    private int life = 15;
    private List<Pluma> plumas;
    public Pluma(List<Pluma> plumas, GameView gameView, float x,
                 float y, Bitmap bmp) {
        this.x = x;
        this.y = y;
        this.bmp = Bitmap.createScaledBitmap(bmp, (int) (100.0*(gameView.getWidth()/1080.0)), (int) (100.0*(gameView.getHeight()/2110.0)), false);
        this.plumas = plumas;
    }
    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(bmp, x, y, null);
    }
    private void update() {
        if (--life < 1) {
            plumas.remove(this);
        }
    }
}
