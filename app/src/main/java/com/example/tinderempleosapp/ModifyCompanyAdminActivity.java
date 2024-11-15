package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ModifyCompanyAdminActivity extends AppCompatActivity {

    private EditText nombreEmpresaEditText, sobreEmpresaEditText, interesadosEditText, horarioEditText;
    private Button btnModificarEmpresa, btnVolverEmpresaInfo;

    private DatabaseReference empresasRef;
    private String empresaCorreo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifi_company_admin);

        // Vinculación de elementos del layout
        nombreEmpresaEditText = findViewById(R.id.nombreEmpresaEditText);
        sobreEmpresaEditText = findViewById(R.id.aboutEmpresaEditText);
        interesadosEditText = findViewById(R.id.interesadosEditText);
        horarioEditText = findViewById(R.id.horariosEditText);
        btnModificarEmpresa = findViewById(R.id.btnModificarEmpresa);
        btnVolverEmpresaInfo = findViewById(R.id.btnVolverEmpresaInfo);

        // Recibir el correo electrónico de la empresa desde la actividad anterior
        Intent intent = getIntent();
        empresaCorreo = intent.getStringExtra("correo");

        // Referencia a la base de datos de empresas
        empresasRef = FirebaseDatabase.getInstance().getReference("empresas");

        // Listener para el botón de modificar
        btnModificarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarEmpresa();
            }
        });

        // Listener para el botón de volver
        btnVolverEmpresaInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModifyCompanyAdminActivity.this, ViewCompaniesAdminActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void modificarEmpresa() {
        // Obtener los valores de los campos
        String nombreEmpresa = nombreEmpresaEditText.getText().toString();
        String sobreEmpresa = sobreEmpresaEditText.getText().toString();
        String interesados = interesadosEditText.getText().toString();
        String horario = horarioEditText.getText().toString();

        // Validar que el correo electrónico no esté vacío
        if (empresaCorreo == null || empresaCorreo.isEmpty()) {
            Toast.makeText(this, "Correo de la empresa no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar la empresa por correo en la base de datos y actualizar sus datos
        empresasRef.orderByChild("correo").equalTo(empresaCorreo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot empresaSnapshot : dataSnapshot.getChildren()) {
                        // Actualizar los datos de la empresa
                        empresaSnapshot.getRef().child("nombre").setValue(nombreEmpresa);
                        empresaSnapshot.getRef().child("sobreEmpresa").setValue(sobreEmpresa);
                        empresaSnapshot.getRef().child("interesados").setValue(interesados);
                        empresaSnapshot.getRef().child("horario").setValue(horario);

                        Toast.makeText(ModifyCompanyAdminActivity.this, "Datos de la empresa actualizados", Toast.LENGTH_SHORT).show();
                    }

                    // Redirige a ViewCompanyAdminActivity después de la modificación
                    Intent intent = new Intent(ModifyCompanyAdminActivity.this, ViewCompaniesAdminActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ModifyCompanyAdminActivity.this, "Empresa no encontrada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ModifyCompanyAdminActivity.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
