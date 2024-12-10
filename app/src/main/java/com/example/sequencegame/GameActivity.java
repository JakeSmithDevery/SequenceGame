package com.example.sequencegame;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float[] accelerometerValues;
    private List<String> sequence;
    private int currentIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sequence = getIntent().getStringArrayListExtra("sequence");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;

            String direction = detectTilt();
            if (direction != null && direction.equals(sequence.get(currentIndex))) {
                currentIndex++;
                if (currentIndex == sequence.size()) {
                    score += sequence.size();
                    Intent intent = new Intent(GameActivity.this, SequenceActivity.class);
                    startActivity(intent);
                }
            } else if (direction != null) {
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra("score", score);
                startActivity(intent);
            }
        }
    }

    private String detectTilt() {
        if (accelerometerValues[0] > 5) return "Red";       // Tilt left
        if (accelerometerValues[0] < -5) return "Blue";      // Tilt right
        if (accelerometerValues[1] > 5) return "Green";     // Tilt up
        if (accelerometerValues[1] < -5) return "Yellow";   // Tilt down
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
