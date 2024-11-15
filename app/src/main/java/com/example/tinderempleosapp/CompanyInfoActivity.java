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

public class CompanyInfoActivity extends AppCompatActivity {

    private TextInputLayout nombreInputLayout, aboutEmpresaInputLayout, interesadosInputLayout, horariosInputLayout;
    private Button btnEnviarEmpresa, btnVolverEmpresa;

    // Referencia a la base de datos de Firebase
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info_profile);

        // Inicializar Firebase Realtime Database y obtener el UID del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("empresas").child(userId); // Referencia a "empresas"
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish(); // Termina la actividad si no hay usuario autenticado
        }

        // Vinculando los campos del formulario de información de la empresa
        nombreInputLayout = findViewById(R.id.nombreEmpresaInputLayout); // Inicializa el campo de nombre de la empresa
        aboutEmpresaInputLayout = findViewById(R.id.aboutEmpresaInputLayout);
        interesadosInputLayout = findViewById(R.id.interesadosInputLayout);
        horariosInputLayout = findViewById(R.id.horariosInputLayout);
        btnEnviarEmpresa = findViewById(R.id.btnEnviarEmpresa);
        btnVolverEmpresa = findViewById(R.id.btnVolverEmpresaInfo);

        // Acción del botón "Enviar"
        btnEnviarEmpresa.setOnClickListener(v -> enviarInformacion());

        // Acción del botón "Volver"
        btnVolverEmpresa.setOnClickListener(v -> volver());
    }

    private void enviarInformacion() {
        // Obtener los datos del formulario
        String nombreEmpresa = nombreInputLayout.getEditText().getText().toString().trim(); // Obtiene el nombre de la empresa
        String aboutEmpresa = aboutEmpresaInputLayout.getEditText().getText().toString().trim();
        String interesados = interesadosInputLayout.getEditText().getText().toString().trim();
        String horarios = horariosInputLayout.getEditText().getText().toString().trim();

        // Verificar que el usuario esté autenticado
        if (userId != null) {
            // Crear un mapa para las actualizaciones del perfil
            Map<String, Object> cambioPerfil = new HashMap<>();
            cambioPerfil.put("nombre", nombreEmpresa); // Agrega el nombre de la empresa
            cambioPerfil.put("sobreEmpresa", aboutEmpresa);
            cambioPerfil.put("interesados", interesados);
            cambioPerfil.put("horario", horarios);

            // Actualizar los datos en la base de datos
            databaseReference.updateChildren(cambioPerfil)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(CompanyInfoActivity.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(CompanyInfoActivity.this, "Error al actualizar el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
            Intent intent = new Intent(CompanyInfoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(CompanyInfoActivity.this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void volver() {
        // Volver a la actividad de registro
        Intent intent = new Intent(CompanyInfoActivity.this, RegistrerActivity.class);
        startActivity(intent);
        finish(); // Opcional: Cierra la actividad actual
    }
}





