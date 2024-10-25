package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserInfoActivity extends AppCompatActivity {

    private TextInputLayout aboutMeInputLayout, titulosInputLayout, edadInputLayout, ciudadPaisInputLayout;
    private Button btnEnviar;

    // Referencia a la base de datos de Firebase
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajador_info_profile);

        // Inicializar Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("infoUsuarios");

        // Vinculando los campos del formulario de información del usuario
        aboutMeInputLayout = findViewById(R.id.aboutMeInputLayout);
        titulosInputLayout = findViewById(R.id.titulosInputLayout);
        edadInputLayout = findViewById(R.id.edadInputLayout);  // Nuevo campo edad
        ciudadPaisInputLayout = findViewById(R.id.ciudadPaisInputLayout);  // Nuevo campo ciudad-pais
        btnEnviar = findViewById(R.id.btnEnviar);

        // Acción del botón "Enviar"
        btnEnviar.setOnClickListener(v -> enviarInformacion());
    }

    private void enviarInformacion() {
        // Obtener los datos ingresados por el usuario
        String aboutMe = aboutMeInputLayout.getEditText().getText().toString().trim();
        String titulos = titulosInputLayout.getEditText().getText().toString().trim();
        String edad = edadInputLayout.getEditText().getText().toString().trim();  // Capturar el valor de edad
        String ciudadPais = ciudadPaisInputLayout.getEditText().getText().toString().trim();  // Capturar el valor de ciudad-pais

        // Validar que los campos no estén vacíos
        if (aboutMe.isEmpty() || titulos.isEmpty() || edad.isEmpty() || ciudadPais.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un objeto con la información del usuario
        UserInfo userInfo = new UserInfo(aboutMe, titulos, edad, ciudadPais);

        // Generar una clave única para el usuario en la tabla "infoUsuarios"
        String userId = databaseReference.push().getKey();

        // Guardar la información del usuario en Firebase
        if (userId != null) {
            databaseReference.child(userId).setValue(userInfo)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserInfoActivity.this, "Información enviada exitosamente", Toast.LENGTH_SHORT).show();

                            // Redirigir a MainActivity
                            Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Finaliza la actividad actual para que no se pueda volver a ella con el botón "Atrás"
                        } else {
                            Toast.makeText(UserInfoActivity.this, "Error al enviar la información", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Clase modelo para los datos del usuario
    public static class UserInfo {
        public String aboutMe;
        public String titulos;
        public String edad;         // Nuevo campo edad
        public String ciudadPais;   // Nuevo campo ciudad-pais

        // Constructor vacío requerido por Firebase
        public UserInfo() {
        }

        public UserInfo(String aboutMe, String titulos, String edad, String ciudadPais) {
            this.aboutMe = aboutMe;
            this.titulos = titulos;
            this.edad = edad;
            this.ciudadPais = ciudadPais;
        }
    }
}



