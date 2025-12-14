package com.example.hearway;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    // Definim variabilele pentru TTS și Butoane
    private TextToSpeech tts;
    private Button btnNavigate, btnEmergency, btnCommunicate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Legături cu elementele din XML (prin ID-urile definite în activity_dashboard.xml)
        btnNavigate = findViewById(R.id.btnNavigate);
        btnEmergency = findViewById(R.id.btnEmergency);
        btnCommunicate = findViewById(R.id.btnCommunicate); // Am adăugat și butonul de Comunicare

        // 1. Inițializare Text-to-Speech (Voce)
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                // Anunțăm utilizatorul că a ajuns în meniul principal și ce opțiuni are
                speak("Main Menu. Buttons available: Navigation, Communicate, and Emergency.");
            }
        });

        // 2. Butonul NAVIGARE (Task 1 & 3) -> Deschide simularea autobuzului
        btnNavigate.setOnClickListener(v -> {
            speak("Opening Navigation");
            // Deschide ecranul ActiveTripActivity (Simularea cu bara de progres)
            Intent intent = new Intent(DashboardActivity.this, ActiveTripActivity.class);
            startActivity(intent);
        });

        // 3. Butonul COMUNICARE (Task 7) -> Deschide pagina explicativă (InfoActivity)
        // Aceasta este logica "Placeholder" pentru funcționalități viitoare
        btnCommunicate.setOnClickListener(v -> {
            speak("Opening Communication Helper");
            Intent intent = new Intent(DashboardActivity.this, InfoActivity.class);
            // Trimitem textul explicativ către pagina generică
            intent.putExtra("INFO_TEXT", "This feature (Task 7) uses the camera for a transparent background and live subtitles to help Deaf users communicate with drivers.");
            startActivity(intent);
        });

        // 4. Butonul URGENȚĂ (Task 6) - Apăsare Lungă (Stealth Mode)
        btnEmergency.setOnLongClickListener(v -> {
            // Confirmare audio și vizuală
            speak("Emergency Alert Initiated");
            // Aici simulăm trimiterea alarmei
            Toast.makeText(DashboardActivity.this, "EMERGENCY SENT! (Stealth Mode)", Toast.LENGTH_LONG).show();
            // Return true înseamnă că evenimentul a fost consumat și nu se mai execută click-ul scurt
            return true;
        });

        // Butonul URGENȚĂ - Apăsare Scurtă (Prevenire greșeli)
        btnEmergency.setOnClickListener(v -> {
            speak("Long press to activate Emergency Alert");
            Toast.makeText(DashboardActivity.this, "Long press to activate SOS", Toast.LENGTH_SHORT).show();
        });
    }

    // Funcție helper pentru a vorbi (scurtează codul)
    private void speak(String text) {
        if (tts != null) {
            // QUEUE_FLUSH oprește orice alt discurs curent pentru a spune acest mesaj urgent
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        // Oprim motorul TTS când închidem activitatea pentru a elibera resurse
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}