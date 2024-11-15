package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewJobsUserActivity extends AppCompatActivity {

    private ListView listaTrabajos;
    private Button btnPostularTrabajo;
    private ArrayAdapter<String> trabajosAdapter;
    private ArrayList<String> listaTrabajosData;
    private ArrayList<String> trabajosKeys; // Para almacenar los IDs de trabajos en Firebase

    private DatabaseReference trabajosRef;
    private DatabaseReference usuariosRef;
    private FirebaseAuth mAuth;

    private String trabajoSeleccionadoId; // Variable para almacenar el ID del trabajo seleccionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jobs_user);

        // Inicializa los elementos de la vista
        listaTrabajos = findViewById(R.id.lista_trabajos);
        btnPostularTrabajo = findViewById(R.id.btn_postular_trabajo);

        // Inicializa Firebase
        mAuth = FirebaseAuth.getInstance();
        trabajosRef = FirebaseDatabase.getInstance().getReference("empresas"); // Referencia a las empresas (trabajos)
        usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios"); // Referencia a los usuarios

        // Inicializa los adaptadores y listas
        listaTrabajosData = new ArrayList<>();
        trabajosKeys = new ArrayList<>();
        trabajosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaTrabajosData);
        listaTrabajos.setAdapter(trabajosAdapter);

        // Carga los trabajos desde Firebase
        cargarTrabajosDeFirebase();

        // Maneja el evento de clic en la lista de trabajos
        listaTrabajos.setOnItemClickListener((parent, view, position, id) -> {
            // Obtiene el ID del trabajo seleccionado
            trabajoSeleccionadoId = trabajosKeys.get(position);
            Toast.makeText(ViewJobsUserActivity.this, "Trabajo seleccionado: " + listaTrabajosData.get(position), Toast.LENGTH_SHORT).show();
        });

        // Maneja el evento de clic en el botón de postular
        btnPostularTrabajo.setOnClickListener(v -> {
            if (trabajoSeleccionadoId != null) {
                postularTrabajo(trabajoSeleccionadoId, "Pendiente"); // Estado inicial como "Pendiente"
            } else {
                Toast.makeText(ViewJobsUserActivity.this, "Selecciona un trabajo para postularte", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(ViewJobsUserActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void cargarTrabajosDeFirebase() {
        trabajosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTrabajosData.clear();
                trabajosKeys.clear();

                // Itera a través de todas las empresas
                for (DataSnapshot empresaSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot trabajosSnapshot = empresaSnapshot.child("trabajos");

                    // Verifica si la empresa tiene trabajos
                    if (!trabajosSnapshot.exists()) {
                        Log.d("ViewJobsUserActivity", "No hay trabajos disponibles en esta empresa.");
                        continue;
                    }

                    // Itera a través de los trabajos dentro de la empresa
                    for (DataSnapshot trabajoSnapshot : trabajosSnapshot.getChildren()) {
                        String tituloTrabajo = trabajoSnapshot.child("titulo").getValue(String.class);
                        String descripcionTrabajo = trabajoSnapshot.child("descripcion").getValue(String.class);
                        String salarioTrabajo = trabajoSnapshot.child("salario").getValue(String.class);
                        String trabajoId = trabajoSnapshot.getKey();

                        // Crea una cadena concatenada con título, descripción y salario
                        String trabajoInfo = "Título: " + tituloTrabajo + "\nDescripción: " + descripcionTrabajo + "\nSalario: " + salarioTrabajo;

                        // Agrega el trabajo a la lista
                        listaTrabajosData.add(trabajoInfo);
                        trabajosKeys.add(trabajoId);
                    }
                }

                // Notifica al adaptador para actualizar la lista
                trabajosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewJobsUserActivity", "Error al cargar trabajos", databaseError.toException());
            }
        });
    }

    private void postularTrabajo(String trabajoId, String estadoInicial) {
        String userId = mAuth.getCurrentUser().getUid(); // ID del usuario actual

        // Obtiene la referencia del usuario actual
        usuariosRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("nombre").getValue(String.class);
                    String correo = dataSnapshot.child("correo").getValue(String.class);
                    String sobreMi = dataSnapshot.child("sobreMi").getValue(String.class);

                    // Crea una estructura para guardar solo los datos necesarios
                    Map<String, Object> postulacionData = new HashMap<>();
                    postulacionData.put("titulo", listaTrabajosData.get(trabajosKeys.indexOf(trabajoId)).split("\n")[0].split(": ")[1]); // Título del trabajo
                    postulacionData.put("estado", estadoInicial); // Estado de la postulación
                    postulacionData.put("nombre", nombre);        // Nombre del usuario
                    postulacionData.put("correo", correo);        // Correo del usuario
                    postulacionData.put("sobreMi", sobreMi);      // Información sobre el usuario

                    // Guarda la postulación en el nodo de postulaciones de la rama "usuarios"
                    usuariosRef.child(userId).child("postulaciones").child(trabajoId).setValue(postulacionData)
                            .addOnSuccessListener(aVoid -> Toast.makeText(ViewJobsUserActivity.this, "Te has postulado al trabajo", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Log.e("ViewJobsUserActivity", "Error al postularse al trabajo", e));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewJobsUserActivity", "Error al obtener los datos del usuario", databaseError.toException());
            }
        });
    }

    private void actualizarEstadoPostulacion(String userId, String trabajoId, String nuevoEstado) {
        usuariosRef.child(userId).child("postulaciones").child(trabajoId).child("estado")
                .setValue(nuevoEstado)
                .addOnSuccessListener(aVoid -> Toast.makeText(ViewJobsUserActivity.this,
                        "Estado de la postulación actualizado a " + nuevoEstado, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("ViewJobsUserActivity", "Error al actualizar el estado de la postulación", e));
    }

    // Obtener la descripción del trabajo desde la base de datos
    private String getDescripcionTrabajo(String trabajoId) {
        return listaTrabajosData.get(trabajosKeys.indexOf(trabajoId)).split("\n")[1].split(": ")[1]; // Extrae la descripción
    }
}
