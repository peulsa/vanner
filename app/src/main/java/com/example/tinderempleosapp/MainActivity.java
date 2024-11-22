package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button signupButton;
    private Switch themeSwitch; // Switch para alternar entre modos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Vinculamos el layout con la clase Java

        // Vinculamos los botones a sus IDs
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        // Vinculamos el switch a su ID
        themeSwitch = findViewById(R.id.themeSwitch);

        // Configuramos el estado inicial del Switch según el tema actual
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        themeSwitch.setChecked(currentNightMode == AppCompatDelegate.MODE_NIGHT_YES);

        // Listener para alternar entre modo claro y oscuro
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTheme(isChecked);
        });

        // Evento clic en el botón Login
        loginButton.setOnClickListener(v -> {
            // Iniciar la actividad de Login
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Evento clic en el botón Signup
        signupButton.setOnClickListener(v -> {
            // Iniciar la actividad de Signup
            Intent intent = new Intent(MainActivity.this, RegistrerActivity.class); // Asegúrate de que el nombre sea correcto
            startActivity(intent);
        });
    }

    public void toggleTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
