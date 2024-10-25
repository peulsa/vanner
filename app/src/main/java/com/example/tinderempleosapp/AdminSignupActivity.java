package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminSignupActivity extends AppCompatActivity {

    private EditText editTextAdminNombre, editTextAdminRUT, editTextAdminPuesto;
    private Button btnEnviarAdmin, btnVolverRegistro;
    private DatabaseReference databaseReference; // Referencia a la base de datos de Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_signup);

        // Inicializa la referencia a la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("admins");

        // Vinculando los campos del formulario
        editTextAdminNombre = findViewById(R.id.editTextAdminNombre);
        editTextAdminRUT = findViewById(R.id.editTextAdminRUT);
        editTextAdminPuesto = findViewById(R.id.editTextAdminPuesto);
        btnEnviarAdmin = findViewById(R.id.btnEnviarAdmin);
        btnVolverRegistro = findViewById(R.id.btnVolverRegistro);

        // Acción del botón "Registrar Administrador"
        btnEnviarAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarAdministrador();
            }
        });

        // Acción del botón "Volver"
        btnVolverRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSignupActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registrarAdministrador() {
        String nombre = editTextAdminNombre.getText().toString().trim();
        String rut = editTextAdminRUT.getText().toString().trim();
        String puesto = editTextAdminPuesto.getText().toString().trim();

        // Validar campos
        if (nombre.isEmpty() || rut.isEmpty() || puesto.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un nuevo administrador y guardarlo en la base de datos
        String id = databaseReference.push().getKey(); // Genera una clave única para el nuevo registro
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("nombre", nombre);
        adminData.put("rut", rut);
        adminData.put("puesto", puesto);

        // Guardar datos en Firebase
        if (id != null) {
            databaseReference.child(id).setValue(adminData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminSignupActivity.this, "Administrador registrado exitosamente", Toast.LENGTH_SHORT).show();
                    // Redirigir a la actividad principal
                    Intent intent = new Intent(AdminSignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AdminSignupActivity.this, "Error al registrar el administrador", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}


