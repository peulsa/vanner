package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class CompanyInfoActivity extends AppCompatActivity {

    private TextInputEditText aboutEmpresaEditText, interesadosEditText, horariosEditText;
    private Button btnEnviarEmpresa, btnVolverEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info_profile);

        // Inicializar los campos
        aboutEmpresaEditText = findViewById(R.id.aboutEmpresaEditText);
        interesadosEditText = findViewById(R.id.interesadosEditText);
        horariosEditText = findViewById(R.id.horariosEditText);
        btnEnviarEmpresa = findViewById(R.id.btnEnviarEmpresa);
        btnVolverEmpresa = findViewById(R.id.btnVolverEmpresaInfo); // Asegúrate de que el ID sea correcto

        // Configurar el botón de enviar
        btnEnviarEmpresa.setOnClickListener(v -> enviarDatos());

        // Configurar el botón de volver
        btnVolverEmpresa.setOnClickListener(v -> {
            Intent intent = new Intent(CompanyInfoActivity.this, SignupActivity.class);
            startActivity(intent);
            finish(); // Cierra esta actividad si no se necesita volver
        });
    }

    private void enviarDatos() {
        // Obtener datos de los campos
        String aboutEmpresa = aboutEmpresaEditText.getText().toString().trim();
        String interesados = interesadosEditText.getText().toString().trim();
        String horarios = horariosEditText.getText().toString().trim();

        if (aboutEmpresa.isEmpty() || interesados.isEmpty() || horarios.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear referencia a la base de datos
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("infoEmpresas");

        // Crear un nuevo ID único para la empresa
        String id = databaseReference.push().getKey();

        // Crear un mapa de datos para enviar a Firebase
        Map<String, String> empresaInfo = new HashMap<>();
        empresaInfo.put("id", id);
        empresaInfo.put("about", aboutEmpresa);
        empresaInfo.put("interesados", interesados);
        empresaInfo.put("horarios", horarios);

        // Guardar datos en Firebase
        if (id != null) {
            databaseReference.child(id).setValue(empresaInfo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CompanyInfoActivity.this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
                        // Redirigir a MainActivity después de guardar los datos
                        Intent intent = new Intent(CompanyInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Cerrar esta actividad
                    })
                    .addOnFailureListener(e -> Toast.makeText(CompanyInfoActivity.this, "Error al guardar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}


