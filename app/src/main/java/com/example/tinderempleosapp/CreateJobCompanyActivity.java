package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateJobCompanyActivity extends AppCompatActivity {

    private EditText editTextTitulo, editTextDescripcion, editTextSalario;
    private Button buttonGuardarTrabajo, btnVolverRegistro;

    private FirebaseAuth mAuth;
    private DatabaseReference companiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job_company);

        // Inicializar FirebaseAuth y obtener la referencia a la base de datos
        mAuth = FirebaseAuth.getInstance();
        companiesRef = FirebaseDatabase.getInstance().getReference("empresas");

        // Referencias a los campos de entrada y botones
        editTextTitulo = findViewById(R.id.editText_titulo);
        editTextDescripcion = findViewById(R.id.editText_descripcion);
        editTextSalario = findViewById(R.id.editText_salario);
        buttonGuardarTrabajo = findViewById(R.id.button_guardar_trabajo);
        btnVolverRegistro = findViewById(R.id.btnVolverRegistro);

        // Asignar listener al botón "Guardar Trabajo"
        buttonGuardarTrabajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarTrabajo();
            }
        });

        // Asignar listener al botón "Volver" para redirigir a activity_company_profile
        btnVolverRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirPerfilEmpresa();
            }
        });
    }

    private void guardarTrabajo() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show();
            return;
        }

        String companyId = mAuth.getCurrentUser().getUid();
        String titulo = editTextTitulo.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String salario = editTextSalario.getText().toString().trim();

        if (titulo.isEmpty() || descripcion.isEmpty() || salario.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> trabajoData = new HashMap<>();
        trabajoData.put("titulo", titulo);
        trabajoData.put("descripcion", descripcion);
        trabajoData.put("salario", salario);

        String trabajoId = companiesRef.child(companyId).child("trabajos").push().getKey();

        if (trabajoId != null) {
            companiesRef.child(companyId).child("trabajos").child(trabajoId)
                    .setValue(trabajoData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateJobCompanyActivity.this, "Trabajo guardado con éxito", Toast.LENGTH_SHORT).show();
                            abrirPerfilEmpresa(); // Redirigir después de guardar
                        } else {
                            Toast.makeText(CreateJobCompanyActivity.this, "Error al guardar el trabajo", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Error al guardar el trabajo", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirPerfilEmpresa() {
        Intent intent = new Intent(CreateJobCompanyActivity.this, CompanyProfileActivity.class);
        startActivity(intent);
        finish(); // Finalizar actividad actual para evitar regresar a esta al presionar "Atrás"
    }
}
