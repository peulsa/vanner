package com.example.tinderempleosapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    // Referencias a los elementos de la interfaz
    private TextView userName, useredad, userLocation, userDescription, userSkill1;
    private Button btnVerTrabajosDisponibles, btnLogout;

    // Referencias a la base de datos de Firebase
    private DatabaseReference usuariosRef;

    // Referencia al usuario autenticado
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile); // Vincula el layout de perfil

        // Inicializa los elementos de la vista
        userName = findViewById(R.id.userName);
        useredad = findViewById(R.id.userEdad);
        userLocation = findViewById(R.id.userCiudad);
        userDescription = findViewById(R.id.userDescription);
        userSkill1 = findViewById(R.id.userSkill1);
        btnVerTrabajosDisponibles = findViewById(R.id.btnVerTrabajosDispo);
        btnLogout = findViewById(R.id.btnLogout);

        // Inicializa Firebase Auth para obtener el correo del usuario autenticado
        mAuth = FirebaseAuth.getInstance();

        // Configura el evento de clic para el botón "Ver Trabajos Disponibles"
        btnVerTrabajosDisponibles.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, ViewJobsUserActivity.class);
            startActivity(intent);
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Obtiene el correo del usuario autenticado
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

        if (userEmail != null) {
            // Inicializa la referencia de Firebase Database para los usuarios
            usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

            // Llama al método para obtener datos del usuario
            getUserProfileDataByEmail(userEmail);
        } else {
            Log.e("UserProfileActivity", "No hay usuario autenticado.");
        }
    }

    // Método para obtener los datos del perfil del usuario desde Firebase usando el correo
    private void getUserProfileDataByEmail(String email) {
        usuariosRef.orderByChild("correo").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Extrae los datos necesarios del usuario encontrado
                        String nombre = userSnapshot.child("nombre").getValue(String.class);
                        String edad = userSnapshot.child("edad").getValue(String.class);
                        String ciudadPais = userSnapshot.child("ciudad").getValue(String.class);
                        String aboutMe = userSnapshot.child("sobreMi").getValue(String.class);
                        String habilidad1 = userSnapshot.child("info").getValue(String.class);

                        // Logs para depuración
                        Log.d("UserProfileActivity", "Nombre: " + nombre);
                        Log.d("UserProfileActivity", "Edad: " + edad);
                        Log.d("UserProfileActivity", "Ciudad: " + ciudadPais);
                        Log.d("UserProfileActivity", "Descripción: " + aboutMe);
                        Log.d("UserProfileActivity", "Habilidad: " + habilidad1);

                        // Muestra los datos en los TextView correspondientes
                        userName.setText(nombre);
                        useredad.setText(edad);
                        userLocation.setText(ciudadPais);
                        userDescription.setText(aboutMe);
                        userSkill1.setText(habilidad1);
                    }
                } else {
                    Log.e("UserProfileActivity", "No se encontró el usuario con el correo especificado.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja cualquier error que ocurra durante la lectura de datos
                Log.e("UserProfileActivity", "Error al leer datos de Firebase", databaseError.toException());
            }
        });
    }
}
