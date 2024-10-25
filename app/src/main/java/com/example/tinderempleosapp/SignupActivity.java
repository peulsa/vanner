package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private Button btnEmpleador;
    private Button btnTrabajador;
    private Button btnAdministrador; // Botón de Administrador
    private Button btnVolverInfo; // Botón "Volver"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Vinculamos los botones con sus IDs en el layout
        btnEmpleador = findViewById(R.id.btnEmpleador);
        btnTrabajador = findViewById(R.id.btnTrabajador);
        btnAdministrador = findViewById(R.id.btnAdministrador);
        btnVolverInfo = findViewById(R.id.btnVolverInfo); // Vincular botón "Volver"

        // Al hacer clic en el botón "Soy Empleador"
        btnEmpleador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir al formulario de empleador
                Intent intent = new Intent(SignupActivity.this, EmpleadorFormActivity.class);
                startActivity(intent);
            }
        });

        // Al hacer clic en el botón "Soy Trabajador"
        btnTrabajador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir al formulario de trabajador
                Intent intent = new Intent(SignupActivity.this, userFormActivity.class);
                startActivity(intent);
            }
        });

        // Al hacer clic en el botón "Soy Administrador"
        btnAdministrador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir al formulario de administrador
                Intent intent = new Intent(SignupActivity.this, AdminSignupActivity.class);
                startActivity(intent);
            }
        });

        // Al hacer clic en el botón "Volver"
        btnVolverInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad principal (MainActivity)
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}