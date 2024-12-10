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

import java.util.ArrayList;
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

        score = getIntent().getIntExtra("score", 0); // Initialize the score
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

                // Check if the player has completed the entire sequence
                if (currentIndex == sequence.size()) {
                    // Award bonus for completing the sequence
                    score += sequence.size(); // You can adjust this logic based on your game's rules
                    Intent intent = new Intent(GameActivity.this, SequenceActivity.class);
                    intent.putExtra("score", score); // Pass the score to the next sequence
                    startActivity(intent);
                    finish(); // Finish this activity so the player moves to the next one
                }
            } else {
                // If the player fails to match the correct sequence, go to the Game Over screen
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra("score", score); // Pass the score to GameOverActivity
                startActivity(intent);
                finish(); // Ensure GameActivity is removed from the stack
            }

            // Set the buffer to block further processing for 500 milliseconds
            isProcessing = true;
            new android.os.Handler().postDelayed(() -> isProcessing = false, 500);
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
