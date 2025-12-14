package com.example.hearway;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ProgressBar;
import java.util.Locale;

public class ActiveTripActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button btnSimulate;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_trip);

        progressBar = findViewById(R.id.progressBarTrip);
        btnSimulate = findViewById(R.id.btnSimulate);

        // TTS Setup
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                tts.speak("Navigation started. Next stop: Main Square.", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // Logica "Magică" pentru Profesor
        btnSimulate.setOnClickListener(v -> {
            // 1. Schimbăm bara în ROȘU (Alertă vizuală iminentă) - conform PDF Pag 18
            progressBar.setProgress(100);
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));

            // 2. Feedback Audio
            if(tts != null) tts.speak("Arriving at Main Square. Prepare to exit.", TextToSpeech.QUEUE_FLUSH, null, null);

            // 3. Așteptăm 2 secunde și declanșăm alerta MAXIMĂ (Ecranul Verde)
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(ActiveTripActivity.this, AlertActivity.class);
                startActivity(intent);
                finish();
            }, 2500);
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}