package com.example.tinderempleosapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;  // Importa Picasso

import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userName, userEdad, userLocation, userDescription, userSkill1;
    private ImageView profileImage;
    private Button btnVerTrabajosDisponibles, btnLogout, btnChats;

    private DatabaseReference usuariosRef;
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Inicializa los elementos de la vista
        userName = findViewById(R.id.userName);
        userEdad = findViewById(R.id.userEdad);
        userLocation = findViewById(R.id.userCiudad);
        userDescription = findViewById(R.id.userDescription);
        userSkill1 = findViewById(R.id.userSkill1);
        profileImage = findViewById(R.id.profileImage);
        btnVerTrabajosDisponibles = findViewById(R.id.btnVerTrabajosDispo);
        btnLogout = findViewById(R.id.btnLogout);
        btnChats = findViewById(R.id.btnChats);

        // Inicializa Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inicializa Firebase Storage con una instancia personalizada
        FirebaseApp storageApp = FirebaseApp.getInstance("proyectoStorage");
        firebaseStorage = FirebaseStorage.getInstance(storageApp);
        storageReference = firebaseStorage.getReference("grupoOlivares");

        // Configura el evento de clic para el botón "Ver Trabajos Disponibles"
        btnVerTrabajosDisponibles.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, ViewJobsUserActivity.class);
            startActivity(intent);
        });

        // Configura el evento de clic para el botón de cierre de sesión
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut(); // Esto cierra la sesión del usuario
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnChats.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, ChatUserActivity.class);
            startActivity(intent);
        });

        // Obtiene el correo del usuario autenticado
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

        if (userEmail != null) {
            // Inicializa la referencia de Firebase Database para los usuarios
            usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

            // Llama al metodo para obtener datos del usuario
            getUserProfileDataByEmail(userEmail);
        } else {
            Log.e("UserProfileActivity", "No hay usuario autenticado.");
        }

        // Acción para seleccionar una nueva imagen de perfil
        profileImage.setOnClickListener(v -> openImageChooser());
    }

    // Metodo para obtener los datos del perfil del usuario desde Firebase usando el correo
    private void getUserProfileDataByEmail(String email) {
        usuariosRef.orderByChild("correo").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String nombre = userSnapshot.child("nombre").getValue(String.class);
                        String edad = userSnapshot.child("edad").getValue(String.class);
                        String ciudadPais = userSnapshot.child("ciudad").getValue(String.class);
                        String aboutMe = userSnapshot.child("sobreMi").getValue(String.class);
                        String habilidad1 = userSnapshot.child("info").getValue(String.class);
                        String imagenPerfil = userSnapshot.child("imagenPerfil").getValue(String.class);

                        // Muestra los datos en los TextView correspondientes
                        userName.setText(nombre);
                        userEdad.setText(edad);
                        userLocation.setText(ciudadPais);
                        userDescription.setText(aboutMe);
                        userSkill1.setText(habilidad1);

                        // Carga la imagen de perfil usando Picasso
                        loadProfileImage(imagenPerfil);
                    }
                } else {
                    Log.e("UserProfileActivity", "No se encontró el usuario con el correo especificado.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserProfileActivity", "Error al leer datos de Firebase", databaseError.toException());
            }
        });
    }

    // Metodo para cargar la imagen de perfil desde Firebase Storage usando Picasso
    private void loadProfileImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Picasso.get()
                    .load(imagePath)
                    .placeholder(R.drawable.usuario)  // Muestra una imagen de marcador de posición mientras carga
                    .error(R.drawable.usuario)       // Muestra una imagen de error si algo sale mal
                    .into(profileImage);             // Carga la imagen en el ImageView
        } else {
            Log.e("UserProfileActivity", "Ruta de imagen no válida.");
            profileImage.setImageResource(R.drawable.usuario);  // Muestra una imagen predeterminada
        }
    }

    // Metodo para abrir el selector de imágenes
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Manejo de la selección de la imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Subir la imagen seleccionada a Firebase Storage
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("profileImages").child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(UserProfileActivity.this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            usuariosRef.child(mAuth.getCurrentUser().getUid()).child("imagenPerfil").setValue(uri.toString());
                            loadProfileImage(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserProfileActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
