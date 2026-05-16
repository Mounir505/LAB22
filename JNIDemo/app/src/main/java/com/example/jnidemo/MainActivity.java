package com.example.jnidemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Chargement statique immuable de la bibliothèque compilée par CMake
    static {
        try {
            System.loadLibrary("native-lib");
        } catch (UnsatisfiedLinkError e) {
            android.util.Log.e("JNI_JAVA", "Erreur critique de chargement .so", e);
        }
    }

    // Déclaration des méthodes natives avec signatures propres (liées via RegisterNatives)
    public native String helloFromJNI();
    public native long factorial(int n);
    public native String reverseString(String s);
    public native int sumArray(int[] values);
    public native long intenseCalculationNative(int iterations); // Extension C

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ====================================================
        // GESTION SECTION 1 : HELLO WORLD
        // ====================================================
        Button btnTriggerHello = findViewById(R.id.btnTriggerHello);
        TextView tvHelloResult = findViewById(R.id.tvHelloResult);
        btnTriggerHello.setOnClickListener(v -> tvHelloResult.setText(helloFromJNI()));

        // ====================================================
        // GESTION SECTION 2 : FACTORIEL DYNAMIQUE
        // ====================================================
        EditText etFactorielInput = findViewById(R.id.etFactorielInput);
        Button btnCalculerFact = findViewById(R.id.btnCalculerFact);
        TextView tvFactResult = findViewById(R.id.tvFactResult);

        btnCalculerFact.setOnClickListener(v -> {
            String txtInput = etFactorielInput.getText().toString().trim();
            if (TextUtils.isEmpty(txtInput)) {
                etFactorielInput.setError(getString(R.string.error_empty_field));
                return;
            }

            int nombreSaisi = Integer.parseInt(txtInput);
            long resultatNatif = factorial(nombreSaisi);

            if (resultatNatif == -1) {
                tvFactResult.setText(getString(R.string.factorial_error_negative));
            } else if (resultatNatif == -2) {
                tvFactResult.setText(getString(R.string.factorial_error_overflow));
            } else {
                tvFactResult.setText(getString(R.string.dynamic_factorial_success, nombreSaisi, resultatNatif));
            }
        });

        // ====================================================
        // GESTION SECTION 3 : INVERSION CHAINE DYNAMIQUE
        // ====================================================
        EditText etStringInput = findViewById(R.id.etStringInput);
        Button btnInverserString = findViewById(R.id.btnInverserString);
        TextView tvReverseResult = findViewById(R.id.tvReverseResult);

        btnInverserString.setOnClickListener(v -> {
            String texteSaisi = etStringInput.getText().toString();
            if (TextUtils.isEmpty(texteSaisi)) {
                etStringInput.setError(getString(R.string.error_empty_field));
                return;
            }

            String texteInverse = reverseString(texteSaisi);
            tvReverseResult.setText(getString(R.string.dynamic_reverse_success, texteInverse));
        });

        // ====================================================
        // GESTION SECTION 4 : SOMME TABLEAU INT[] DYNAMIQUE
        // ====================================================
        EditText etArrayInput = findViewById(R.id.etArrayInput);
        Button btnCalculerSomme = findViewById(R.id.btnCalculerSomme);
        TextView tvArrayResult = findViewById(R.id.tvArrayResult);

        btnCalculerSomme.setOnClickListener(v -> {
            String chaineNombres = etArrayInput.getText().toString().trim();
            if (TextUtils.isEmpty(chaineNombres)) {
                etArrayInput.setError(getString(R.string.error_empty_field));
                return;
            }

            try {
                String[] morceaux = chaineNombres.split(",");
                int[] tableauEntiers = new int[morceaux.length];

                for (int i = 0; i < morceaux.length; i++) {
                    tableauEntiers[i] = Integer.parseInt(morceaux[i].trim());
                }

                int sommeCalculee = sumArray(tableauEntiers);

                if (sommeCalculee == -1) {
                    tvArrayResult.setText(getString(R.string.array_error_null));
                } else if (sommeCalculee == -3) {
                    tvArrayResult.setText(getString(R.string.array_error_overflow));
                } else {
                    tvArrayResult.setText(getString(R.string.dynamic_array_success, tableauEntiers.length, sommeCalculee));
                }

            } catch (NumberFormatException e) {
                etArrayInput.setError(getString(R.string.error_invalid_array));
            }
        });

        // ====================================================
        // GESTION SECTION 5 : BENCHMARK (EXTENSION C & D)
        // ====================================================
        Button btnRunBenchmark = findViewById(R.id.btnRunBenchmark);
        TextView tvBenchmarkResult = findViewById(R.id.tvBenchmarkResult);

        btnRunBenchmark.setOnClickListener(v -> {
            int cycles = 10_000_000;
            tvBenchmarkResult.setText(getString(R.string.benchmark_running));

            // Exécution & Chronométrage C++ Natif
            long startNative = System.nanoTime();
            intenseCalculationNative(cycles);
            long endNative = System.nanoTime();
            long durationNativeMs = (endNative - startNative) / 1_000_000;

            // Exécution & Chronométrage Java Managé
            long startJava = System.nanoTime();
            intenseCalculationJava(cycles);
            long endJava = System.nanoTime();
            long durationJavaMs = (endJava - startJava) / 1_000_000;

            // Calcul du facteur multiplicateur d'accélération brute
            double ratio = (double) durationJavaMs / (durationNativeMs == 0 ? 1 : durationNativeMs);

            tvBenchmarkResult.setText(getString(R.string.benchmark_success, durationJavaMs, durationNativeMs, ratio));
        });
    }

    /**
     * Algorithme miroir de calcul pour le benchmark (Java Pur)
     */
    private long intenseCalculationJava(int iterations) {
        long count = 0;
        for (int i = 0; i < iterations; ++i) {
            count += (i % 3 == 0) ? (long) i * 2 : (long) i / 2;
        }
        return count;
    }
}