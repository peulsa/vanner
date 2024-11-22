package com.example.tinderempleosapp;

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
import com.squareup.picasso.Picasso; // Asegúrate de añadir esta librería en tu build.gradle

import java.io.IOException;

public class CompanyProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;  // Código para la solicitud de imagen

    private ImageView companyImage, companyBackgroundImage;
    private TextView companyName, companyDescription, categoriesTextView, horariosTextView;
    private Button buttonCrearTrabajo, buttonVerTrabajadores, btnLogOut;

    private DatabaseReference companiesRef;
    private FirebaseAuth mAuth;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri imageUri;

    private Company currentCompany;  // Agregar un objeto de tipo Company para manejar los datos de la empresa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        // Inicialización de vistas
        companyImage = findViewById(R.id.companyImagePlaceholder);
        companyBackgroundImage = findViewById(R.id.backgroundImage);
        companyName = findViewById(R.id.companyName);
        companyDescription = findViewById(R.id.companyDescription);
        categoriesTextView = findViewById(R.id.interestsTextView);
        horariosTextView = findViewById(R.id.horariosInfo);
        btnLogOut = findViewById(R.id.btnLogout);

        buttonCrearTrabajo = findViewById(R.id.buttonCrearTrabajo);
        buttonVerTrabajadores = findViewById(R.id.buttonVerTrabajadores);

        // Acciones de los botones
        buttonCrearTrabajo.setOnClickListener(view -> {
            Intent intent = new Intent(CompanyProfileActivity.this, CreateJobCompanyActivity.class);
            startActivity(intent);
        });

        buttonVerTrabajadores.setOnClickListener(view -> {
            Intent intent = new Intent(CompanyProfileActivity.this, ViewWorkerCompanyActivity.class);
            startActivity(intent);
        });

        btnLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(CompanyProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Obtener la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

        // Cargar los datos del perfil de la empresa
        if (userEmail != null) {
            companiesRef = FirebaseDatabase.getInstance().getReference("empresas");
            getCompanyProfileDataByEmail(userEmail);
        } else {
            Log.e("CompanyProfileActivity", "No hay usuario autenticado.");
        }

        // Usar Firebase Storage de un segundo proyecto
        FirebaseApp storageApp = FirebaseApp.getInstance("proyectoStorage");
        firebaseStorage = FirebaseStorage.getInstance(storageApp);
        storageReference = firebaseStorage.getReference("grupoOlivares");

        // Acción para seleccionar una nueva imagen de perfil
        companyImage.setOnClickListener(v -> openImageChooser());
    }

    // Método para abrir el selector de imágenes
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
                companyImage.setImageBitmap(bitmap);
                uploadImageToFirebase();  // Subir la imagen a Firebase después de seleccionarla
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
                        Toast.makeText(CompanyProfileActivity.this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Guardar la URL de la imagen en Firebase Database
                            companiesRef.child(mAuth.getCurrentUser().getUid()).child("profileImage").setValue(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CompanyProfileActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Cargar la imagen de perfil desde Firebase Storage
    private void loadProfileImageFromFirebase(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(companyImage);  // Usando Picasso para cargar la imagen
        }
    }

    // Obtener los datos del perfil de la empresa por correo
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
                        String profileImageUrl = companySnapshot.child("profileImage").getValue(String.class);

                        // Instanciar un objeto Company con los datos obtenidos
                        String clave = "";  // Valor predeterminado, puedes modificarlo según sea necesario
                        String tipo = "";   // Valor predeterminado, puedes modificarlo según sea necesario
                        currentCompany = new Company(name, email, clave, description, interests, workHours, tipo, profileImageUrl);

                        // Actualizar la UI con los datos del objeto Company
                        companyName.setText(currentCompany.getName());
                        companyDescription.setText(currentCompany.getSobreEmpresa());
                        categoriesTextView.setText(currentCompany.getInteres());
                        horariosTextView.setText(currentCompany.getHorario());

                        // Cargar la imagen de perfil
                        loadProfileImageFromFirebase(profileImageUrl);
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