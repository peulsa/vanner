package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private TextInputLayout aboutMeInputLayout, titulosInputLayout, edadInputLayout, ciudadInputLayout, nombreInputLayout, paisInputLayout;
    private Button btnEnviar, btnVolver;

    // Referencia a la base de datos de Firebase
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajador_info_profile);

        // Inicializar Firebase Realtime Database y obtener el UID del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish(); // Termina la actividad si no hay usuario autenticado
        }

        // Vinculando los campos del formulario de información del usuario
        aboutMeInputLayout = findViewById(R.id.aboutMeInputLayout);
        titulosInputLayout = findViewById(R.id.titulosInputLayout);
        edadInputLayout = findViewById(R.id.edadInputLayout);
        ciudadInputLayout = findViewById(R.id.ciudadInputLayout);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnVolver = findViewById(R.id.btnVolverInfo);
        paisInputLayout = findViewById(R.id.paisInputLayout);
        nombreInputLayout = findViewById(R.id.nombreInputLayout);

        // Acción del botón "Enviar"
        btnEnviar.setOnClickListener(v -> enviarInformacion());

        // Acción del botón "Volver"
        btnVolver.setOnClickListener(v -> volver());
    }

    private void enviarInformacion() {
        // Obtener los datos del formulario
        String aboutMe = aboutMeInputLayout.getEditText().getText().toString().trim();
        String titulos = titulosInputLayout.getEditText().getText().toString().trim();
        String edad = edadInputLayout.getEditText().getText().toString().trim();
        String ciudad = ciudadInputLayout.getEditText().getText().toString().trim();
        String nombre = nombreInputLayout.getEditText().getText().toString().trim();
        String pais = paisInputLayout.getEditText().getText().toString().trim();

        // Verificar que el usuario esté autenticado
        if (userId != null) {
            // Crear un mapa para las actualizaciones del perfil
            Map<String, Object> cambioPerfil = new HashMap<>();
            cambioPerfil.put("nombre", nombre);
            cambioPerfil.put("edad", edad);
            cambioPerfil.put("ciudad", ciudad);
            cambioPerfil.put("pais", pais);
            cambioPerfil.put("sobreMi", aboutMe);
            cambioPerfil.put("info", titulos);

            // Actualizar los datos en la base de datos
            databaseReference.updateChildren(cambioPerfil)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(UserInfoActivity.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                    )

                    .addOnFailureListener(e ->
                            Toast.makeText(UserInfoActivity.this, "Error al actualizar el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
            Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UserInfoActivity.this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void volver() {
        // Volver a la actividad de registro
        Intent intent = new Intent(UserInfoActivity.this, RegistrerActivity.class);
        startActivity(intent);
        finish(); // Opcional: Cierra la actividad actual
    }
}








