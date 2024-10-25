package com.example.tinderempleosapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    // Referencias a los elementos de la interfaz
    private TextView userName, userLocation, userDescription, userSkill1, userSkill2;

    // Referencias a la base de datos de Firebase
    private DatabaseReference infoUsuariosRef;
    private DatabaseReference usuariosRef;

    // ID del usuario (esto debería ser dinámico según tu lógica de aplicación)
    private String userId = "userID_123"; // Reemplaza esto con el ID del usuario actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);  // Vincula el layout de perfil

        // Inicializa los elementos de la vista
        userName = findViewById(R.id.userName);
        userLocation = findViewById(R.id.userLocation);
        userDescription = findViewById(R.id.userDescription);
        userSkill1 = findViewById(R.id.userSkill1);
        userSkill2 = findViewById(R.id.userSkill2);

        // Inicializa las referencias de Firebase Database
        infoUsuariosRef = FirebaseDatabase.getInstance().getReference("infoUsuarios");
        usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

        // Llama al método para obtener datos del usuario
        getUserProfileData();
    }

    // Método para obtener los datos del perfil del usuario desde Firebase
    private void getUserProfileData() {
        // Escucha los cambios en la base de datos de infoUsuarios
        infoUsuariosRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extrae los datos de Firebase
                    String aboutMe = dataSnapshot.child("aboutMe").getValue(String.class);
                    String titulos = dataSnapshot.child("titulos").getValue(String.class);
                    String edad = dataSnapshot.child("edad").getValue(String.class);
                    String ciudadPais = dataSnapshot.child("ciudadPais").getValue(String.class);

                    // Muestra los datos en los TextView correspondientes
                    userDescription.setText(aboutMe);
                    userSkill1.setText(titulos);
                    userSkill2.setText(edad);  // Muestra la edad en el segundo campo de habilidades
                    userLocation.setText(ciudadPais);

                    // Llama al método para obtener el nombre del usuario de la tabla usuarios
                    getUserName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja cualquier error que ocurra durante la lectura de datos
                Log.e("UserProfileActivity", "Error al leer datos de Firebase", databaseError.toException());
            }
        });
    }

    // Método para obtener el nombre del usuario desde la tabla usuarios
    private void getUserName() {
        usuariosRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extrae el campo nombre
                    String nombre = dataSnapshot.child("nombre").getValue(String.class);
                    // Muestra el nombre en el TextView correspondiente
                    userName.setText(nombre);
                } else {
                    Log.e("UserProfileActivity", "El usuario no existe en la tabla usuarios");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja cualquier error que ocurra durante la lectura de datos
                Log.e("UserProfileActivity", "Error al leer el nombre del usuario", databaseError.toException());
            }
        });
    }
}




