package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompanyProfileActivity extends AppCompatActivity {

    // Referencias a los elementos de la interfaz
    private ImageView companyImage, companyBackgroundImage;
    private TextView companyName, companyDescription, categoriesTextView, horariosTextView;
    private Button buttonCrearTrabajo, buttonVerTrabajadores, btnLogOut; // Declara los botones

    // Referencias a la base de datos de Firebase
    private DatabaseReference companiesRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile); // Vincula el layout de perfil de la empresa

        // Inicializa los elementos de la vista
        companyImage = findViewById(R.id.companyImagePlaceholder);
        companyBackgroundImage = findViewById(R.id.backgroundImage);
        companyName = findViewById(R.id.companyName);
        companyDescription = findViewById(R.id.companyDescription);
        categoriesTextView = findViewById(R.id.interestsTextView);
        horariosTextView = findViewById(R.id.horariosInfo);
        btnLogOut = findViewById(R.id.btnLogout); // Corregido aquí el nombre del botón

        // Inicializa los botones
        buttonCrearTrabajo = findViewById(R.id.buttonCrearTrabajo);
        buttonVerTrabajadores = findViewById(R.id.buttonVerTrabajadores);

        // Establece el listener para el botón "Crear Trabajo"
        buttonCrearTrabajo.setOnClickListener(view -> {
            Intent intent = new Intent(CompanyProfileActivity.this, CreateJobCompanyActivity.class);
            startActivity(intent);
        });

        // Establece el listener para el botón "Ver Trabajadores"
        buttonVerTrabajadores.setOnClickListener(view -> {
            Intent intent = new Intent(CompanyProfileActivity.this, ViewWorkerCompanyActivity.class);
            startActivity(intent);
        });

        // Establece el listener para el botón "Log Out"
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CompanyProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Inicializa Firebase Auth para obtener el correo del usuario autenticado
        mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

        if (userEmail != null) {
            companiesRef = FirebaseDatabase.getInstance().getReference("empresas");
            getCompanyProfileDataByEmail(userEmail);
        } else {
            Log.e("CompanyProfileActivity", "No hay usuario autenticado.");
        }
    }

    // Método para obtener los datos del perfil de la empresa desde Firebase usando el correo
    private void getCompanyProfileDataByEmail(String email) {
        companiesRef.orderByChild("correo").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot companySnapshot : dataSnapshot.getChildren()) {
                        String name = companySnapshot.child("nombre").getValue(String.class);
                        String description = companySnapshot.child("sobreEmpresa").getValue(String.class);
                        String interests = companySnapshot.child("interesados").getValue(String.class);
                        String workHours = companySnapshot.child("horario").getValue(String.class);

                        // Muestra los datos en los TextView correspondientes
                        companyName.setText(name);
                        companyDescription.setText(description);
                        categoriesTextView.setText(interests);
                        horariosTextView.setText(workHours);
                    }
                } else {
                    Log.e("CompanyProfileActivity", "No se encontró la empresa con el correo especificado.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CompanyProfileActivity", "Error al leer datos de Firebase", databaseError.toException());
            }
        });
    }
}
