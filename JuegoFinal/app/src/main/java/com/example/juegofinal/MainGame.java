package com.example.juegofinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Window;

import java.util.List;

public class MainGame extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor acelerometerSensor;
    public float x;
    public int puntuacionMax;
    public GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int tiempoMaximo = getIntent().getExtras().getInt("tiempoMaximo");
        puntuacionMax = getIntent().getExtras().getInt("puntuacionMax");
        gameView = new GameView(this,this,tiempoMaximo,puntuacionMax);
        setContentView(gameView);
        //gameView.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.wallpaper,null));
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        List<Sensor> listaSensores;

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!listaSensores.isEmpty()) {
            // Cogemos el primer sensor de tipo TYPE_ACCELEROMETER que tenga el dispositivo
            acelerometerSensor = listaSensores.get(0);
            sensorManager.registerListener(this, acelerometerSensor,SensorManager.SENSOR_STATUS_ACCURACY_LOW);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch(event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    x = event.values[0];
                    if(x>=0 && x<2 )
                        gameView.cambiar(0);
                    else if(x<=0 && x>-2){
                        gameView.cambiar(0);
                    }else{
                        gameView.cambiar(x);
                    }
            }
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// TODO Auto-generated method stub
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        gameView.gameLoopThread.setRunning(false);
    }
    public void destruir(){
        if(puntuacionMax == 0){
            guardarPuntuacion();
        }
        finish();
    }
    public void guardarPuntuacion(){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        int puntuacion = sharedPreferences.getInt("score",0);
        if(gameView.puntuacion > puntuacion)
            sharedPreferences.edit().putInt("score",gameView.puntuacion).apply();
    }
    @Override
    public void onBackPressed() {
        System.out.println("Entra");
        if(!gameView.derrota && !gameView.victoria)
            gameView.pause = !gameView.pause;
    }
}