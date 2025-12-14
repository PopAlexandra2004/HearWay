package com.example.hearway;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import java.util.Locale; // Am șters importul RelativeLayout că nu mai e nevoie

public class AlertActivity extends AppCompatActivity {

    private Vibrator vibrator;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        // 1. Inițializăm Vibrația
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        triggerStrongVibration();

        // 2. Inițializăm Audio (Urgent)
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                // Mesaj imperativ
                tts.speak("GET OFF NOW! Main Square reached.", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // AM ȘTERS LINIA CU EROAREA (findViewById) PENTRU CĂ FOLOSEȘTI onTouchEvent MAI JOS
    }

    // Funcție de vibrație puternică (Puls lung)
    // Funcție de vibrație puternică (Puls lung)
    private void triggerStrongVibration() {
        if (vibrator != null) {
            long[] pattern = {0, 1000, 200, 1000}; // Așteaptă 0, vibrează 1s, pauză 0.2s, vibrează 1s

            // Verificăm versiunea de Android a telefonului
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Pentru telefoane noi (Android 8.0+) folosim metoda modernă
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            } else {
                // Pentru telefoane mai vechi folosim metoda clasică
                vibrator.vibrate(pattern, -1);
            }
        }
    }

    // Această metodă se ocupă de click-ul pe ecran
    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            // Oprește tot
            if (vibrator != null) vibrator.cancel();
            if (tts != null) tts.stop();

            // Navighează către explicația de final
            Intent intent = new Intent(AlertActivity.this, InfoActivity.class);
            String description = "Journey Completed! In the full version, users can now save this route to Favorites or set a return reminder (Task 8 & 9).";
            intent.putExtra("INFO_TEXT", description);

            startActivity(intent);
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }
}