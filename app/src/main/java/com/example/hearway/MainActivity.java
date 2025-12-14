package com.example.hearway; // Asigură-te că pachetul tău e corect aici!

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech; // Import pentru voce
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Definim variabila pentru Text-to-Speech
    private TextToSpeech tts;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Legăm butonul din XML de codul Java
        btnLogin = findViewById(R.id.btnLogin);

        // 1. Inițializăm motorul Text-to-Speech (TTS)
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Setăm limba engleză
                    int result = tts.setLanguage(Locale.US);

                    if (result != TextToSpeech.LANG_MISSING_DATA
                            && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        // 2. Când e gata, spunem mesajul de bun venit
                        speak("Welcome to HearWay!");
                    }
                }
            }
        });

        // 3. Ce se întâmplă când apăsăm Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Feedback tactil (Vibrație) ar veni aici
                // Feedback audio
                speak("Logging in. Please wait.");
                // Așteptăm 1 secundă să termine de vorbit (simulat) și schimbăm pagina
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish(); // Închide Login-ul ca să nu te poți întoarce cu Back
                    }
                }, 1000);

                // Mesaj vizual de confirmare (Toast)
                Toast.makeText(MainActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();

                // Aici vom pune mai târziu codul care deschide pagina următoare (Dashboard)
            }
        });
    }

    // Funcție ajutătoare pentru a vorbi
    private void speak(String text) {
        if (tts != null) {
            // QUEUE_FLUSH înseamnă că întrerupe orice alt sunet curent
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        // Oprim vocea când închidem aplicația ca să eliberăm memoria
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}