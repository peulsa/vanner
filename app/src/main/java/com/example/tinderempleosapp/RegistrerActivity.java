package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrerActivity extends AppCompatActivity {

    private TextInputEditText correoEditText, contrasenaEditText;
    private RadioGroup radioGroupTipoUsuario;
    private Button btnSiguiente, btnVolverRegistro;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrer_form);

        // Inicializar elementos de la interfaz
        correoEditText = findViewById(R.id.correoEditText);
        contrasenaEditText = findViewById(R.id.contrasenaEditText);
        radioGroupTipoUsuario = findViewById(R.id.radioGroupTipoUsuario);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnVolverRegistro = findViewById(R.id.btnVolverRegistro);

        // Inicializar Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Evento para el botón Siguiente
        btnSiguiente.setOnClickListener(v -> {
            String correo = correoEditText.getText().toString().trim();
            String contrasena = contrasenaEditText.getText().toString().trim();

            // Validar campos de entrada
            if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena)) {
                Toast.makeText(RegistrerActivity.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Redirigir dependiendo del tipo de usuario
            int selectedRadioButtonId = radioGroupTipoUsuario.getCheckedRadioButtonId();
            String tipo;

            if (selectedRadioButtonId == R.id.radioEmpresa) {
                tipo = "empresa";
                databaseReference = FirebaseDatabase.getInstance().getReference("empresas"); // Referencia para empresas
            } else if (selectedRadioButtonId == R.id.radioUsuario) {
                tipo = "usuario";
                databaseReference = FirebaseDatabase.getInstance().getReference("usuarios"); // Referencia para usuarios
            } else {
                Toast.makeText(RegistrerActivity.this, "Selecciona un tipo de usuario", Toast.LENGTH_SHORT).show();
                return; // Salir si no se seleccionó ningún tipo
            }

            // Creación de usuario en Firebase Auth
            firebaseAuth.createUserWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            guardarDatosEnFirebase(correo, contrasena, tipo);
                        } else {
                            String errorMessage = "Error en la autenticación.";
                            if (task.getException() instanceof FirebaseAuthException) {
                                errorMessage = ((FirebaseAuthException) task.getException()).getMessage();
                            }
                            Toast.makeText(RegistrerActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Evento para el botón Volver
        btnVolverRegistro.setOnClickListener(v -> finish()); // Vuelve a la actividad anterior
    }

    private void guardarDatosEnFirebase(String correo, String contrasena, String tipo) {
        String userId = firebaseAuth.getCurrentUser().getUid();

        // Map de datos según tipo de usuario
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("correo", correo);
        dataMap.put("contrasena", contrasena);

        if ("empresa".equals(tipo)) {
            dataMap.put("nombre", "");
            dataMap.put("interesados", "");
            dataMap.put("sobreEmpresa", "");
            dataMap.put("horario", "");
        } else {
            dataMap.put("edad", "");
            dataMap.put("ciudad", "");
            dataMap.put("nombre", "");
            dataMap.put("pais", "");
            dataMap.put("sobreMi", "");
            dataMap.put("tipo", tipo);
            dataMap.put("info", "");
        }

        if (userId != null) {
            databaseReference.child(userId).setValue(dataMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RegistrerActivity.this, tipo + " registrado exitosamente", Toast.LENGTH_SHORT).show();
                        redirigirSegunTipo(tipo);
                    })
                    .addOnFailureListener(e -> Toast.makeText(RegistrerActivity.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void redirigirSegunTipo(String tipo) {
        Intent intent;
        if ("empresa".equals(tipo)) {
            intent = new Intent(RegistrerActivity.this, CompanyInfoActivity.class); // Asegúrate de que CompanyInfoActivity esté en AndroidManifest.xml
        } else {
            intent = new Intent(RegistrerActivity.this, UserInfoActivity.class);
        }
        startActivity(intent);
        finish();
    }
}


