package com.example.hearway;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

public class InfoActivity extends AppCompatActivity {

    private TextView tvDescription;
    private Button btnBack;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        tvDescription = findViewById(R.id.tvDescription);
        btnBack = findViewById(R.id.btnBack);

        // 1. Preluăm textul trimis din activitatea anterioară
        String infoText = getIntent().getStringExtra("INFO_TEXT");
        if (infoText != null) {
            tvDescription.setText(infoText);
        }

        // 2. Setup Voce pentru a citi explicația orbilor
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                tts.speak("Feature not implemented yet. " + infoText, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // 3. Butonul Back
        btnBack.setOnClickListener(v -> {
            // Ne întoarcem la Dashboard și închidem toate activitățile intermediare
            Intent intent = new Intent(InfoActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}