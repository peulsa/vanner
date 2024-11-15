package com.example.tinderempleosapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminCreateAdminActivity extends AppCompatActivity {

    private EditText nombreEditText, correoEditText, contrasenaEditText, cargoEditText, ciudadEditText, paisEditText, infoEditText;
    private Button btnRegistrarAdmin, btnVolver;

    // Referencia a la base de datos y autenticación de Firebase
    private DatabaseReference databaseAdmins;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_admin);

        // Inicializa FirebaseAuth y la referencia a la base de datos
        mAuth = FirebaseAuth.getInstance();
        databaseAdmins = FirebaseDatabase.getInstance().getReference("admins");

        // Inicializa los campos de entrada y botones
        nombreEditText = findViewById(R.id.nombreEditText);
        correoEditText = findViewById(R.id.correoEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        cargoEditText = findViewById(R.id.cargoEditText);
        ciudadEditText = findViewById(R.id.ciudadEditText);
        paisEditText = findViewById(R.id.paisEditText);
        infoEditText = findViewById(R.id.infoEditText);
        btnRegistrarAdmin = findViewById(R.id.btnRegistrarAdmin);

        // Configura el botón para registrar al administrador
        btnRegistrarAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtiene los datos ingresados
                String nombre = nombreEditText.getText().toString().trim();
                String correo = correoEditText.getText().toString().trim();
                String contrasena = contrasenaEditText.getText().toString().trim();
                String cargo = cargoEditText.getText().toString().trim();
                String ciudad = ciudadEditText.getText().toString().trim();
                String pais = paisEditText.getText().toString().trim();
                String info = infoEditText.getText().toString().trim();

                // Verifica que todos los campos estén llenos
                if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || cargo.isEmpty() || ciudad.isEmpty() || pais.isEmpty() || info.isEmpty()) {
                    Toast.makeText(AdminCreateAdminActivity.this, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show();
                } else {
                    // Llama a la función para registrar el administrador
                    createAdminAccount(nombre, correo, contrasena, cargo, ciudad, pais, info);
                }
            }
        });

        // Configura el botón de "Volver" para ir al perfil del administrador
    }

    private void createAdminAccount(String nombre, String correo, String contrasena, String cargo, String ciudad, String pais, String info) {
        // Registra al administrador en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si la autenticación es exitosa, guarda los datos en Realtime Database
                            String adminId = task.getResult().getUser().getUid();
                            Admin admin = new Admin(nombre, correo, contrasena, cargo, ciudad, pais, info);

                            // Guarda el administrador en la tabla "admins"
                            databaseAdmins.child(adminId).setValue(admin)
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            Toast.makeText(AdminCreateAdminActivity.this, "Administrador registrado exitosamente.", Toast.LENGTH_SHORT).show();
                                            // Redirige al perfil del administrador tras un registro exitoso
                                            Intent intent = new Intent(AdminCreateAdminActivity.this, AdminProfileActivity.class);
                                            startActivity(intent);
                                            finish(); // Finaliza la actividad actual
                                        } else {
                                            Toast.makeText(AdminCreateAdminActivity.this, "Error al registrar al administrador en la base de datos.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(AdminCreateAdminActivity.this, "Error al crear cuenta de administrador: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Clase Admin para almacenar los datos en la base de datos
    public static class Admin {
        public String nombre, correo, contrasena, cargo, ciudad, pais, info;

        public Admin() {
            // Constructor vacío necesario para Firebase
        }

        public Admin(String nombre, String correo, String clave, String cargo, String ciudad, String pais, String info) {
            this.nombre = nombre;
            this.correo = correo;
            this.contrasena = clave;
            this.cargo = cargo;
            this.ciudad = ciudad;
            this.pais = pais;
            this.info = info;
        }
    }
}



