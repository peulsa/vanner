package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProfileActivity extends AppCompatActivity {

    private Button btnAdminCreate, btnUserList, btnLogout, btnEmpresaslist;
    private TextView adminProfileName, adminProfilePosition, adminProfileGmail;

    private DatabaseReference adminRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Inicializa los botones con los IDs correctos
        btnAdminCreate = findViewById(R.id.btnAdminCreate);
        btnUserList = findViewById(R.id.btnUserList);
        btnLogout = findViewById(R.id.btnLogout);
        btnEmpresaslist = findViewById(R.id.btn_empresas);
        adminProfileName = findViewById(R.id.adminProfileName);
        adminProfilePosition = findViewById(R.id.adminProfilePosition);
        adminProfileGmail = findViewById(R.id.adminProfileGmail);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            adminRef = FirebaseDatabase.getInstance().getReference("admins").child(currentUser.getUid());

            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Admin admin = dataSnapshot.getValue(Admin.class);
                        if (admin != null) {
                            adminProfileName.setText(admin.nombre);
                            adminProfilePosition.setText("Cargo: " + admin.cargo);
                            adminProfileGmail.setText("Correo: " + admin.correo);
                        }
                    } else {
                        Toast.makeText(AdminProfileActivity.this, "No se encontraron datos del administrador.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                    Toast.makeText(AdminProfileActivity.this, "Error al cargar los datos.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Configura el botón "Crear Administrador"
        btnAdminCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfileActivity.this, AdminCreateAdminActivity.class);
                startActivity(intent);
            }
        });

        // Configura el botón "Ver lista de usuarios"
        btnUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfileActivity.this, ViewUsersAdminActivity.class);
                startActivity(intent);
            }
        });

        // Configura el botón "Cerrar sesión"
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Configura el botón "Ver lista de empresas"
        btnEmpresaslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminProfileActivity.this, ViewCompaniesAdminActivity.class);
                startActivity(intent);
            }
        });
    }

    public static class Admin {
        public String nombre, correo, cargo;

        public Admin() {}

        public Admin(String nombre, String correo, String cargo) {
            this.nombre = nombre;
            this.correo = correo;
            this.cargo = cargo;
        }
    }
}
