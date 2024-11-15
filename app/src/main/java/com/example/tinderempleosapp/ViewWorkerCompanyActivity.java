package com.example.tinderempleosapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewWorkerCompanyActivity extends AppCompatActivity {

    private ListView listaPostulantes;
    private ArrayAdapter<String> postulantesAdapter;
    private ArrayList<String> listaPostulantesData;
    private DatabaseReference empresasRef, usuariosRef;
    private String tituloTrabajo, postulanteSeleccionadoId;
    private Map<Integer, String> idMap; // Mapa para almacenar el ID del postulante según el índice de la lista

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_worker_company);

        listaPostulantes = findViewById(R.id.lista_empleados);
        usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        empresasRef = FirebaseDatabase.getInstance().getReference("empresas");
        listaPostulantesData = new ArrayList<>();
        postulantesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaPostulantesData);
        listaPostulantes.setAdapter(postulantesAdapter);

        idMap = new HashMap<>(); // Inicializa el mapa para almacenar los IDs de los postulantes

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            obtenerTituloTrabajoDinamicamente(currentUser.getUid());
        } else {
            Toast.makeText(this, "No estás autenticado como empresa", Toast.LENGTH_SHORT).show();
        }

        listaPostulantes.setOnItemClickListener((parent, view, position, id) -> {
            postulanteSeleccionadoId = idMap.get(position); // Obtén el ID del postulante seleccionado desde el mapa
            Toast.makeText(this, "Seleccionado: " + postulanteSeleccionadoId, Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_aceptar_empleado).setOnClickListener(v -> cambiarEstadoPostulante("aceptado"));
        findViewById(R.id.btn_rechazar_empleado).setOnClickListener(v -> cambiarEstadoPostulante("rechazado"));
    }

    private void obtenerTituloTrabajoDinamicamente(String empresaId) {
        empresasRef.child(empresaId).child("trabajos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot trabajosSnapshot) {
                if (trabajosSnapshot.exists()) {
                    for (DataSnapshot trabajoSnapshot : trabajosSnapshot.getChildren()) {
                        tituloTrabajo = trabajoSnapshot.child("titulo").getValue(String.class);
                        if (tituloTrabajo != null && !tituloTrabajo.isEmpty()) {
                            cargarPostulantesParaTrabajo(empresaId, tituloTrabajo);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(ViewWorkerCompanyActivity.this, "No hay trabajos disponibles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewWorkerCompanyActivity", "Error al obtener el título del trabajo", databaseError.toException());
            }
        });
    }

    private void cargarPostulantesParaTrabajo(String empresaId, String tituloTrabajo) {
        listaPostulantesData.clear();
        idMap.clear(); // Limpia el mapa cada vez que se cargan los postulantes

        empresasRef.child(empresaId).child("trabajos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot trabajosSnapshot) {
                boolean trabajoEncontrado = false;

                for (DataSnapshot trabajoSnapshot : trabajosSnapshot.getChildren()) {
                    String titulo = trabajoSnapshot.child("titulo").getValue(String.class);
                    if (tituloTrabajo.equals(titulo)) {
                        buscarPostulantesParaTrabajo(tituloTrabajo);
                        trabajoEncontrado = true;
                        break;
                    }
                }

                if (!trabajoEncontrado) {
                    Toast.makeText(ViewWorkerCompanyActivity.this, "Trabajo no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewWorkerCompanyActivity", "Error al cargar trabajos", databaseError.toException());
            }
        });
    }

    private void buscarPostulantesParaTrabajo(String tituloTrabajo) {
        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usuariosSnapshot) {
                boolean hayPostulantes = false;
                listaPostulantesData.clear();
                idMap.clear();

                for (DataSnapshot usuarioSnapshot : usuariosSnapshot.getChildren()) {
                    DataSnapshot postulacionesSnapshot = usuarioSnapshot.child("postulaciones");

                    for (DataSnapshot trabajoSnapshot : postulacionesSnapshot.getChildren()) {
                        String titulo = trabajoSnapshot.child("titulo").getValue(String.class);

                        if (tituloTrabajo.equals(titulo)) {
                            String nombre = trabajoSnapshot.child("nombre").getValue(String.class);
                            String sobreMi = trabajoSnapshot.child("sobreMi").getValue(String.class);
                            String estado = trabajoSnapshot.child("estado").getValue(String.class);
                            postulanteSeleccionadoId = usuarioSnapshot.getKey();

                            if (nombre != null && sobreMi != null && estado != null) {
                                String infoPostulante = "Nombre: " + nombre + "\nSobre mí: " + sobreMi + "\nEstado: " + estado;
                                listaPostulantesData.add(infoPostulante);
                                idMap.put(listaPostulantesData.size() - 1, postulanteSeleccionadoId); // Mapa de posición a ID
                                hayPostulantes = true;
                            }
                        }
                    }
                }

                postulantesAdapter.notifyDataSetChanged();

                if (!hayPostulantes) {
                    Toast.makeText(ViewWorkerCompanyActivity.this, "No hay postulantes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ViewWorkerCompanyActivity", "Error al cargar postulaciones", databaseError.toException());
            }
        });
    }

    private void cambiarEstadoPostulante(String nuevoEstado) {
        if (postulanteSeleccionadoId != null) {
            usuariosRef.child(postulanteSeleccionadoId).child("postulaciones").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot postulacionesSnapshot) {
                    for (DataSnapshot trabajoSnapshot : postulacionesSnapshot.getChildren()) {
                        String titulo = trabajoSnapshot.child("titulo").getValue(String.class);
                        if (titulo != null && titulo.equals(tituloTrabajo)) {
                            trabajoSnapshot.getRef().child("estado").setValue(nuevoEstado);
                            Toast.makeText(ViewWorkerCompanyActivity.this, "Estado actualizado a " + nuevoEstado, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ViewWorkerCompanyActivity", "Error al actualizar estado", databaseError.toException());
                }
            });
        } else {
            Toast.makeText(this, "Selecciona un postulante", Toast.LENGTH_SHORT).show();
        }
    }
}
