package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class userFormActivity extends AppCompatActivity {

    private TextInputLayout nombreInputLayout, correoInputLayout, contrasenaInputLayout;
    private Button btnSiguiente, btnVolverRegistro;
    private FirebaseAuth mAuth; // Firebase Auth
    private DatabaseReference databaseReference; // Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajador_form);

        // Inicializar Firebase Auth y Realtime Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        // Vincular los campos del formulario
        nombreInputLayout = findViewById(R.id.nombreInputLayout);
        correoInputLayout = findViewById(R.id.correoInputLayout);
        contrasenaInputLayout = findViewById(R.id.contrasenaInputLayout);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnVolverRegistro = findViewById(R.id.btnVolverRegistro);

        // Acción del botón "Siguiente"
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = nombreInputLayout.getEditText().getText().toString().trim();
                String correo = correoInputLayout.getEditText().getText().toString().trim();
                String contrasena = contrasenaInputLayout.getEditText().getText().toString().trim();

                if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena)) {
                    Toast.makeText(userFormActivity.this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
                } else {
                    registrarUsuario(nombre, correo, contrasena);
                }
            }
        });

        // Acción del botón "Volver"
        btnVolverRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresar a la actividad de registro
                Intent intent = new Intent(userFormActivity.this, SignupActivity.class);
                startActivity(intent);
                finish(); // Cerrar esta actividad si no se necesita volver
            }
        });
    }

    private void registrarUsuario(String nombre, String correo, String contrasena) {
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Guardar los datos del usuario en Realtime Database
                                guardarDatosUsuario(user.getUid(), nombre, correo, contrasena);
                            }
                        } else {
                            Toast.makeText(userFormActivity.this, "Error al registrar usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarDatosUsuario(String userId, String nombre, String correo, String contrasena) {
        // Crear un mapa con los datos del usuario
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombreCompleto", nombre);
        usuario.put("correoElectronico", correo);
        usuario.put("clave", contrasena);

        // Guardar los datos en Firebase Realtime Database bajo la tabla "usuarios"
        databaseReference.child(userId).setValue(usuario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(userFormActivity.this, "Registro exitoso.", Toast.LENGTH_SHORT).show();
                            // Ir a la siguiente actividad (Información del usuario)
                            Intent intent = new Intent(userFormActivity.this, UserInfoActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(userFormActivity.this, "Error al guardar los datos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


