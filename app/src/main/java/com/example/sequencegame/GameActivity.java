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
import java.util.Random;

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

        score = getIntent().getIntExtra("score", 0);
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
                return;//skip if no tilt
            }

            Log.d("GameActivity", "Detected direction: " + direction);
            Log.d("GameActivity", "Expected direction: " + sequence.get(currentIndex));

            if (direction.equals(sequence.get(currentIndex))) {
                currentIndex++;
                //if the player has completed the entire sequence
                if (currentIndex == sequence.size()) {
                    // award points for completing the sequence
                    score += sequence.size();

                    // pass sequence to the next activity
                    Intent intent = new Intent(GameActivity.this, SequenceActivity.class);
                    intent.putStringArrayListExtra("sequence", (ArrayList<String>) sequence); // pass updated sequence
                    intent.putExtra("score", score); // pass updated score
                    startActivity(intent);
                    finish(); // finish this activity
                }
            } else {
                // if the player fails go to the game over screen
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra("score", score); // pass score to GameOverActivity
                startActivity(intent);
                finish(); // ensure GameActivity is removed
            }


            // set the buffer
            isProcessing = true;
            new android.os.Handler().postDelayed(() -> isProcessing = false, 500);
        }
    }

    private String detectTilt() {
        if (accelerometerValues[0] > 5) return "Blue";  // Tilt down
        if (accelerometerValues[0] < -5) return "Red";  // Tilt up
        if (accelerometerValues[1] > 5) return "Yellow";// Tilt right
        if (accelerometerValues[1] < -5) return "Green";// Tilt left
        return null;
    }

    private void extendSequence(int length) {
        String[] colors = {"Red", "Blue", "Green", "Yellow"};
        Random random = new Random();

        // add length new colors
        for (int i = 0; i < length; i++) {
            sequence.add(colors[random.nextInt(colors.length)]); // add random color to the sequence
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
