package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa FirebaseAuth y referencia a la base de datos
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Inicializa los elementos de la interfaz
        emailEditText = findViewById(R.id.userEditTextLogin);
        passwordEditText = findViewById(R.id.passwordEditTextLogin);
        loginButton = findViewById(R.id.loginButton);
        backButton = findViewById(R.id.backButton);

        // Configura el botón de inicio de sesión
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validación de entrada
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, ingresa tu correo y contraseña.", Toast.LENGTH_SHORT).show();
                } else {
                    signIn(email, password);
                }
            }
        });

        // Configura el botón de volver
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("AUTH", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkUserType(user, email, password);
                            }
                        } else {
                            Log.w("AUTH", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserType(FirebaseUser user, String email, String password) {
        // Verifica si las credenciales son las predefinidas para el administrador
        if ("test@sociallab.com".equals(email) && "test123".equals(password)) {
            Log.d("AUTH", "Credenciales predefinidas de administrador");
            Intent intent = new Intent(LoginActivity.this, AdminProfileActivity.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        } else {
            // Si no son las credenciales predefinidas, realiza la verificación en Firebase
            DatabaseReference adminRef = databaseReference.child("admins").child(user.getUid());
            DatabaseReference companyRef = databaseReference.child("empresas").child(user.getUid());
            DatabaseReference userRef = databaseReference.child("usuarios").child(user.getUid());

            // Verificación en la tabla "admins"
            adminRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> adminTask) {
                    if (adminTask.isSuccessful()) {
                        DataSnapshot adminSnapshot = adminTask.getResult();
                        if (adminSnapshot.exists()) {
                            // Si existe en "admins", redirige a la actividad de administrador
                            Log.d("AUTH", "Usuario encontrado en 'admins'");
                            Intent intent = new Intent(LoginActivity.this, AdminProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Si no existe en "admins", verifica en la tabla "empresas"
                            checkCompanyOrUser(userRef, companyRef);
                        }
                    } else {
                        Log.d("AUTH", "Error al verificar en 'admins': " + adminTask.getException());
                        Toast.makeText(LoginActivity.this, "Error al verificar en 'admins'.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkCompanyOrUser(DatabaseReference userRef, DatabaseReference companyRef) {
        // Verificación en la tabla "empresas"
        companyRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> companyTask) {
                if (companyTask.isSuccessful()) {
                    DataSnapshot companySnapshot = companyTask.getResult();
                    if (companySnapshot.exists()) {
                        Log.d("AUTH", "Usuario encontrado en 'empresas'");
                        Intent intent = new Intent(LoginActivity.this, CompanyProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        checkRegularUser(userRef);
                    }
                } else {
                    Log.d("AUTH", "Error al verificar en 'empresas': " + companyTask.getException());
                    Toast.makeText(LoginActivity.this, "Error al verificar en 'empresas'.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkRegularUser(DatabaseReference userRef) {
        // Verificación en la tabla "usuarios"
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> userTask) {
                if (userTask.isSuccessful()) {
                    DataSnapshot userSnapshot = userTask.getResult();
                    if (userSnapshot.exists()) {
                        Log.d("AUTH", "Usuario encontrado en 'usuarios'");
                        Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                    } else {
                        Log.d("AUTH", "Usuario no encontrado en 'usuarios'");
                        Toast.makeText(LoginActivity.this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("AUTH", "Error al verificar en 'usuarios': " + userTask.getException());
                    Toast.makeText(LoginActivity.this, "Error al verificar en 'usuarios'.", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}







