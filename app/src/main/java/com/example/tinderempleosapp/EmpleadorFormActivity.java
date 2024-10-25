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

public class EmpleadorFormActivity extends AppCompatActivity {

    private TextInputLayout nombreEmpresaInputLayout, correoEmpresaInputLayout, contrasenaEmpresaInputLayout; // Corregido el ID
    private Button btnSiguienteEmpresa, btnVolverEmpresa;
    private FirebaseAuth mAuth; // Firebase Auth
    private DatabaseReference databaseReference; // Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_form);

        // Inicializar Firebase Auth y Realtime Database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("empresas");

        // Vincular los campos del formulario
        nombreEmpresaInputLayout = findViewById(R.id.nombreEmpresaInputLayout);
        correoEmpresaInputLayout = findViewById(R.id.correoEmpresaInputLayout);
        contrasenaEmpresaInputLayout = findViewById(R.id.contrasenaEmpresaInputLayout); // Usar el ID correcto
        btnSiguienteEmpresa = findViewById(R.id.btnSiguienteEmpresa);
        btnVolverEmpresa = findViewById(R.id.btnVolverEmpresa);

        // Acción del botón "Siguiente"
        btnSiguienteEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreEmpresa = nombreEmpresaInputLayout.getEditText().getText().toString().trim();
                String correoEmpresa = correoEmpresaInputLayout.getEditText().getText().toString().trim();
                String claveEmpresa = contrasenaEmpresaInputLayout.getEditText().getText().toString().trim(); // Usar la variable corregida

                if (TextUtils.isEmpty(nombreEmpresa) || TextUtils.isEmpty(correoEmpresa) || TextUtils.isEmpty(claveEmpresa)) {
                    Toast.makeText(EmpleadorFormActivity.this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
                } else {
                    registrarEmpresa(nombreEmpresa, correoEmpresa, claveEmpresa);
                }
            }
        });

        // Acción del botón "Volver"
        btnVolverEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a SignupActivity
                Intent intent = new Intent(EmpleadorFormActivity.this, SignupActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
        });
    }

    private void registrarEmpresa(String nombreEmpresa, String correoEmpresa, String claveEmpresa) {
        mAuth.createUserWithEmailAndPassword(correoEmpresa, claveEmpresa)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Guardar los datos de la empresa en Realtime Database
                                guardarDatosEmpresa(user.getUid(), nombreEmpresa, correoEmpresa, claveEmpresa);
                            }
                        } else {
                            Toast.makeText(EmpleadorFormActivity.this, "Error al registrar empresa: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarDatosEmpresa(String userId, String nombreEmpresa, String correoEmpresa, String claveEmpresa) {
        // Crear un mapa con los datos de la empresa
        Map<String, Object> empresa = new HashMap<>();
        empresa.put("nombreEmpresa", nombreEmpresa);
        empresa.put("correoEmpresa", correoEmpresa);
        empresa.put("claveEmpresa", claveEmpresa);

        // Guardar los datos en Firebase Realtime Database bajo la tabla "empresas"
        databaseReference.child(userId).setValue(empresa)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EmpleadorFormActivity.this, "Registro exitoso.", Toast.LENGTH_SHORT).show();
                            // Ir a la siguiente actividad (Información de la empresa)
                            Intent intent = new Intent(EmpleadorFormActivity.this, CompanyInfoActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(EmpleadorFormActivity.this, "Error al guardar los datos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


