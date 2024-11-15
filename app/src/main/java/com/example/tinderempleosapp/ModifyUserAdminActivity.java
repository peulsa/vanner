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

public class ModifyUserAdminActivity extends AppCompatActivity {

    private EditText nombreEditText, edadEditText, ciudadEditText, paisEditText, aboutMeEditText, titulosEditText;
    private Button btnModificarUser, btnVolverInfo;

    private DatabaseReference userRef;
    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_admin);

        // Vinculación de elementos del layout
        nombreEditText = findViewById(R.id.nombreEditText);
        edadEditText = findViewById(R.id.edadEditText);
        ciudadEditText = findViewById(R.id.ciudadEditText);
        paisEditText = findViewById(R.id.paisEditText);
        aboutMeEditText = findViewById(R.id.aboutMeEditText);
        titulosEditText = findViewById(R.id.titulosEditText);
        btnModificarUser = findViewById(R.id.btnModificarUser);
        btnVolverInfo = findViewById(R.id.btnVolverInfo);

        // Recibir el correo electrónico del usuario seleccionado desde ViewUsersAdminActivity
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("correo");

        // Referencia a la base de datos
        userRef = FirebaseDatabase.getInstance().getReference("usuarios");

        // Listener para el botón de modificar
        btnModificarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarUsuario();
            }
        });

        // Listener para el botón de volver
        btnVolverInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModifyUserAdminActivity.this, ViewUsersAdminActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void modificarUsuario() {
        // Obtiene los valores de los campos
        String nombre = nombreEditText.getText().toString();
        String edad = edadEditText.getText().toString();
        String ciudad = ciudadEditText.getText().toString();
        String pais = paisEditText.getText().toString();
        String aboutMe = aboutMeEditText.getText().toString();
        String titulos = titulosEditText.getText().toString();

        // Validar que el correo electrónico no esté vacío
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Correo de usuario no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar el usuario por correo en la base de datos y actualizar sus datos
        userRef.orderByChild("correo").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Actualizar los datos del usuario
                        userSnapshot.getRef().child("nombre").setValue(nombre);
                        userSnapshot.getRef().child("edad").setValue(edad);
                        userSnapshot.getRef().child("ciudad").setValue(ciudad);
                        userSnapshot.getRef().child("pais").setValue(pais);
                        userSnapshot.getRef().child("sobreMi").setValue(aboutMe);
                        userSnapshot.getRef().child("info").setValue(titulos);

                        Toast.makeText(ModifyUserAdminActivity.this, "Datos del usuario actualizados", Toast.LENGTH_SHORT).show();
                    }

                    // Redirige a ViewUsersAdminActivity después de la modificación
                    Intent intent = new Intent(ModifyUserAdminActivity.this, ViewUsersAdminActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ModifyUserAdminActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ModifyUserAdminActivity.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
