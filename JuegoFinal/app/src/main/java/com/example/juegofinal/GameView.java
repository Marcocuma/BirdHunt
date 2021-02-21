package com.example.juegofinal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.juegofinal.objetos.Cazador;
import com.example.juegofinal.objetos.Flecha;
import com.example.juegofinal.objetos.Pajaro;
import com.example.juegofinal.objetos.Pluma;
import com.example.juegofinal.objetos.PlumaEnemiga;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameView extends SurfaceView {
    private Bitmap bmp,fondoCartel,botonReiniciar,botonSalir;
    public GameLoop gameLoopThread;
    private List<Pajaro> pajaros = new ArrayList<Pajaro>();
    private List<Pajaro> pajarosMuertos = new ArrayList<Pajaro>();
    private List<Flecha> flechas = new ArrayList<>();
    private List<Pluma> plumas = new ArrayList<>();
    private List<PlumaEnemiga> plumaEnemiga = new ArrayList<>();
    private long tiempo,segundos,tiempoMaximo,puntuacionMax;
    private SoundPool sound;
    public int puntuacion;
    private int xreiniciar,yreiniciar,xsalir,ysalir;
    private int idMuerte,idDisparo,idRespawn,idDerrota,idVictoria;
    private Cazador cazador;
    private Rect fondoBounds;
    private Paint letras,fondoP;
    public int cooldownDisparo = 0;
    public Activity main;
    public BitmapDrawable fondo;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder holder;
    public boolean pause,isPrimeraCarga,victoria,derrota;
    private int x = 0;
    private int xSpeed = 1;
    public GameView(Context context, Activity main,int tiempoMaximo,int puntuacionMax) {
        super(context);
        gameLoopThread = new GameLoop(this);
        holder = getHolder();
        pause=victoria=derrota = false;
        this.tiempoMaximo = tiempoMaximo;
        this.puntuacionMax = puntuacionMax;
        mediaPlayer = MediaPlayer.create(context, R.raw.music);
        mediaPlayer.setLooping(true);
        fondo = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper));
        puntuacion = 0;
        sound = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        isPrimeraCarga = true;
        this.main = main;
        segundos = 0;
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(gameLoopThread.getState().equals(Thread.State.TERMINATED)) {
                    gameLoopThread = new GameLoop(GameView.this);
                    mediaPlayer.start();
                }else {
                    createSprites();
                }
                    gameLoopThread.start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                sound.autoPause();
                mediaPlayer.pause();
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }

        });
    }
    private void createSprites(){
        letras = new Paint();
        letras.setTextSize(52);
        letras.setStyle(Paint.Style.FILL);
        letras.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        letras.setColor(Color.WHITE);
        fondoP = new Paint();
        fondoP.setFilterBitmap(true);
        tiempo = System.currentTimeMillis();
        mediaPlayer.start();
        fondoCartel=BitmapFactory.decodeResource(getResources(), R.drawable.menu);
        fondoCartel =Bitmap.createScaledBitmap(fondoCartel, (int) (fondoCartel.getWidth()*(getWidth()/1080.0)), (int) (fondoCartel.getHeight()*(getHeight()/2110.0)), false);
        botonReiniciar = BitmapFactory.decodeResource(getResources(), R.drawable.button_restart);
        botonReiniciar = Bitmap.createScaledBitmap(botonReiniciar
                ,fondoCartel.getWidth()/2 ,
                fondoCartel.getWidth()/2,
                false);
        botonSalir = BitmapFactory.decodeResource(getResources(), R.drawable.button_wood);
        botonSalir = Bitmap.createScaledBitmap(botonSalir
                ,fondoCartel.getWidth()/2 ,
                fondoCartel.getWidth()/2,
                false);
        idMuerte = sound.load(getContext(),R.raw.muerte, 1);
        idDisparo = sound.load(getContext(),R.raw.disparo, 1);
        idRespawn = sound.load(getContext(),R.raw.spawn, 1);
        idDerrota = sound.load(getContext(),R.raw.gameover, 1);
        idVictoria = sound.load(getContext(),R.raw.win, 1);
        cazador = new Cazador(this,BitmapFactory.decodeResource(getResources(), R.drawable.hunter));
        pajaros.add(new Pajaro(this,BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet),0));
        pajaros.add(new Pajaro(this,BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet),1));
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(!pause) {
            if(isPrimeraCarga) {
                fondoBounds = new Rect(0,0,getWidth(),getHeight());
                isPrimeraCarga = false;
            }
            canvas.drawBitmap(fondo.getBitmap(),null,fondoBounds,fondoP);
            if (cooldownDisparo > 0)
                cooldownDisparo--;
            cazador.onDraw(canvas);
            //Si hay tiempo maximo controla el tiempo, si no no
            if(tiempoMaximo>0){
                if((System.currentTimeMillis() - tiempo)/1000 >= 1){
                    segundos += 1;
                    tiempo = System.currentTimeMillis();
                }
                canvas.drawText(main.getString(R.string.time)+": " + segundos + "/"+tiempoMaximo,
                        letras.getTextSize(),
                        getHeight(),
                        letras);
                if (segundos  == tiempoMaximo) {
                    pierdes();
                }
            }
            mueveEntidades(canvas);
            compruebaColisionFlechas();
            compruebaColisionPluma();
            //Si hay puntuacion maxima, controla cuando gana
            if(puntuacionMax > 0) {
                canvas.drawText(main.getString(R.string.score)+": " + puntuacion + " /"+puntuacionMax,
                        getWidth() / 2+letras.getTextSize(),
                        getHeight(),
                        letras);
                if (puntuacion == puntuacionMax) {
                    ganas();
                }
            } else {
                canvas.drawText("Points: " + puntuacion,
                        getWidth() / 2 + (getWidth() / 4),
                        getHeight() - letras.getTextSize(),
                        letras);
            }
        } else {
            String label = "";
            if(derrota)
                label = main.getString(R.string.defeat);
            else if (victoria)
                label = main.getString(R.string.win);
            else {
                label = main.getString(R.string.pause);
            }
            canvas.drawBitmap(fondo.getBitmap(),null,fondoBounds,fondoP);
            int leftCartel = getWidth()/2-(fondoCartel.getWidth()/2);
            int topCartel = (getHeight()/4)/2;
            canvas.drawBitmap(fondoCartel,leftCartel,topCartel,null);
            canvas.drawText(label,
                    leftCartel+fondoCartel.getWidth()/4,
                    topCartel+fondoCartel.getHeight()/2-letras.getTextSize(),
                    letras);
            canvas.drawText("Puntuacion: "+puntuacion,
                    leftCartel+fondoCartel.getWidth()/4,
                    topCartel+fondoCartel.getHeight()/2+letras.getTextSize(),
                    letras);
            xreiniciar = leftCartel;
            yreiniciar = topCartel+fondoCartel.getHeight();
            canvas.drawBitmap(botonReiniciar,xreiniciar,yreiniciar,null);
            xsalir = leftCartel+botonSalir.getWidth();
            ysalir = topCartel+fondoCartel.getHeight();
            canvas.drawBitmap(botonSalir,xsalir,ysalir,null);
        }
    }
    public void cambiar(float xCambio){
        if(cazador != null)
            cazador.update(xCambio);
    }
    public void mueveEntidades(Canvas canvas){
        for (int g = 0; g < pajarosMuertos.size(); g++) {
            int random = (int) Math.round(Math.random() * 12);
            if (random == 5) {
                sound.play(idRespawn, 1, 1, 0, 0, 1);
                pajaros.add(new Pajaro(this, BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet), pajarosMuertos.get(g).altura));
                pajarosMuertos.remove(g);
            }
        }
        for (Pajaro pajaro : pajaros) {
            pajaro.onDraw(canvas);
        }
        for (int f = 0; f < plumas.size(); f++) {
            plumas.get(f).onDraw(canvas);
        }
        for (int i = 0; i < flechas.size(); i++) {
            flechas.get(i).update();
            if (flechas.get(i).perdida)
                flechas.remove(i);
            else
                flechas.get(i).onDraw(canvas);
        }
        for(int x = 0; x < plumaEnemiga.size(); x++){
            PlumaEnemiga p = plumaEnemiga.get(x);
            p.update();
            if(p.perdida)
                plumaEnemiga.remove(x);
            else
                p.onDraw(canvas);
        }
    }
    public void compruebaColisionFlechas(){
        for (int i = 0; i< flechas.size(); i++) {
            for (int j = 0; j< pajaros.size(); j++){
                if(pajaros.get(j).colisiona(flechas.get(i).x,flechas.get(i).y,flechas.get(i).height,flechas.get(i).width)){
                    flechas.remove(i);
                    sound.play(idMuerte,1, 1, 0, 0, 1);
                    plumas.add(new Pluma(plumas,this,pajaros.get(j).x,pajaros.get(j).y,BitmapFactory.decodeResource(getResources(), R.drawable.plume)));
                    pajarosMuertos.add(pajaros.get(j));
                    plumaEnemiga.add(new PlumaEnemiga(this,BitmapFactory.decodeResource(getResources(), R.drawable.pluma),pajaros.get(j).x,pajaros.get(j).y));
                    pajaros.remove(j);
                    puntuacion++;
                }
            }
        }
    }
    public void compruebaColisionPluma(){
        for (int i = 0; i< plumaEnemiga.size(); i++) {
            PlumaEnemiga p = plumaEnemiga.get(i);
            if(cazador.compruebaColision(p.x,p.y,p.height,p.width))
                pierdes();
        }
    }
    public void pierdes(){
        sound.play(idDerrota,1, 1, 0, 0, 1);
        pause = true;
        derrota = true;
        mediaPlayer.pause();
    }
    public void acabar() {
        ((MainGame) this.main).destruir();
    }
    public void ganas(){
        sound.play(idVictoria, 1, 1, 0, 0, 1);
        pause = true;
        victoria = true;
    }
    public void reiniciar(){
        segundos = 0;
        pause = false;
        victoria = false;
        derrota = false;
        pajarosMuertos.clear();
        pajaros.clear();
        plumaEnemiga.clear();
        if(puntuacionMax == 0){
            ((MainGame) this.main).guardarPuntuacion();
        }
        puntuacion = 0;
        cooldownDisparo = 0;
        tiempo = System.currentTimeMillis();
        flechas.clear();
        createSprites();
        gameLoopThread.setRunning(true);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!pause) {
            if (cooldownDisparo == 0) {
                sound.play(idDisparo, 1, 1, 0, 0, 1);
                flechas.add(new Flecha(this, BitmapFactory.decodeResource(getResources(), R.drawable.flecha), cazador.x + (cazador.width / 2), cazador.y));
                cooldownDisparo = 20;
                cazador.dipara();
            }
        } else {
            if(event.getX() >= xsalir && event.getX() <= xsalir + botonSalir.getWidth()) {
                if (event.getY() >= ysalir && event.getY() <= ysalir + botonSalir.getHeight())
                    acabar();
            }else if(event.getX() >= xreiniciar && event.getX() <= xreiniciar + botonReiniciar.getWidth())
                if (event.getY() >= yreiniciar && event.getY() <= yreiniciar + botonReiniciar.getHeight())
                    reiniciar();
        }
        return super.onTouchEvent(event);
    }

}