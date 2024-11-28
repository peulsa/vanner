package com.example.tinderempleosapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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

public class ChatUserActivity extends AppCompatActivity {

    private ListView listaMensajes;
    private ArrayAdapter<String> mensajesAdapter;
    private ArrayList<String> mensajesList;
    private DatabaseReference empresasRef, usuariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user); // Ajusta esto al nombre correcto del layout

        // Inicializamos la lista de mensajes
        listaMensajes = findViewById(R.id.lista_mensajes);
        mensajesList = new ArrayList<>();
        mensajesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mensajesList);
        listaMensajes.setAdapter(mensajesAdapter);

        // Referencias a Firebase
        empresasRef = FirebaseDatabase.getInstance().getReference("empresas");
        usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

        // Cargar el estado del trabajador
        cargarEstadoTrabajador();
    }

    private void cargarEstadoTrabajador() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usuariosRef.child(userId).child("postulaciones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot postulacionesSnapshot) {
                mensajesList.clear();

                for (DataSnapshot trabajoSnapshot : postulacionesSnapshot.getChildren()) {
                    String estado = trabajoSnapshot.child("estado").getValue(String.class);
                    String tituloTrabajo = trabajoSnapshot.child("titulo").getValue(String.class);
                    String trabajoId = trabajoSnapshot.getKey(); // ID del trabajo

                    if (estado != null) {
                        if (estado.equals("aceptado") && trabajoId != null) {
                            obtenerEmpresaPorTrabajo(trabajoId, tituloTrabajo);
                        } else if (estado.equals("rechazado")) {
                            mensajesList.add("Lo sentimos mucho, fuiste rechazado del trabajo: " + tituloTrabajo);
                        }
                    }
                }

                mensajesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatUserActivity.this, "Error al cargar las postulaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerEmpresaPorTrabajo(String trabajoId, String tituloTrabajo) {
        empresasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot empresasSnapshot) {
                for (DataSnapshot empresaSnapshot : empresasSnapshot.getChildren()) {
                    DataSnapshot trabajosSnapshot = empresaSnapshot.child("trabajos");

                    if (trabajosSnapshot.hasChild(trabajoId)) {
                        String correoEmpresa = empresaSnapshot.child("correo").getValue(String.class);

                        if (correoEmpresa != null) {
                            mensajesList.add("¡Felicidades! Fuiste aceptado en el trabajo: " + tituloTrabajo +
                                    ". Comunícate al siguiente correo: " + correoEmpresa);
                        } else {
                            mensajesList.add("¡Felicidades! Fuiste aceptado en el trabajo: " + tituloTrabajo +
                                    ", pero no se encontró el correo de la empresa.");
                        }

                        break; // Salir del loop cuando encuentres la empresa
                    }
                }

                mensajesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatUserActivity.this, "Error al obtener la información de la empresa", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
