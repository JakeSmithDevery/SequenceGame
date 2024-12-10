package com.example.sequencegame;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
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
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
            if (isProcessing) {
                // If we are in the buffer period, ignore the event
                return;
            }

            accelerometerValues = event.values;
            String direction = detectTilt();
            if (direction == null) {
                return; // Skip if no tilt is detected
            }

            Log.d("GameActivity", "Detected direction: " + direction);
            Log.d("GameActivity", "Expected direction: " + sequence.get(currentIndex));

            if (direction.equals(sequence.get(currentIndex))) {
                currentIndex++;
                score++;
                if (currentIndex == sequence.size()) {
                    score += sequence.size();
                    Intent intent = new Intent(GameActivity.this, SequenceActivity.class);
                    //intent.putExtra("SequenceLength", sequence.get(currentIndex) + 2);
                    //intent.putExtra("score", score); // Pass the score
                    startActivity(intent);

                }
            } else {
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra("score", score);
                Log.d("GameActivity", "Player lost. Final score: " + score);
                startActivity(intent);
            }

            // Set the buffer to block further processing for 2 seconds
            isProcessing = true;
            new android.os.Handler().postDelayed(() -> isProcessing = false, 500); // 2-second delay
        }
    }

    private String detectTilt() {
        if (accelerometerValues[0] > 5) return "Blue";       // Tilt down
        if (accelerometerValues[0] < -5) return "Red";      // Tilt up
        if (accelerometerValues[1] > 5) return "Yellow";     // Tilt right
        if (accelerometerValues[1] < -5) return "Green";   // Tilt left
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
