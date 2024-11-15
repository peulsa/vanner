package com.example.tinderempleosapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewUsersAdminActivity extends AppCompatActivity {

    private ListView listaEmpleados;
    private ArrayList<String> usuariosList;
    private ArrayList<Usuario> usuariosDataList; // Lista para almacenar los objetos Usuario
    private Button btnModificarEmpleado, btnEliminarEmpleado;
    private Usuario usuarioSeleccionado; // Usuario seleccionado de la lista

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users_admin);

        listaEmpleados = findViewById(R.id.lista_empleados);
        btnModificarEmpleado = findViewById(R.id.btn_modificar_empleado);
        btnEliminarEmpleado = findViewById(R.id.btn_eliminar_empleado); // Asumimos que tienes un botón para eliminar
        usuariosList = new ArrayList<>();
        usuariosDataList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usuariosList);
        listaEmpleados.setAdapter(adapter);

        cargarUsuarios(adapter);

        // Configura el evento de clic en la lista para seleccionar el usuario
        listaEmpleados.setOnItemClickListener((parent, view, position, id) -> {
            usuarioSeleccionado = usuariosDataList.get(position); // Guarda el usuario seleccionado
            Toast.makeText(this, "Seleccionado: " + usuarioSeleccionado.nombre, Toast.LENGTH_SHORT).show();
        });

        // Configura el botón para modificar el usuario seleccionado
        btnModificarEmpleado.setOnClickListener(v -> {
            if (usuarioSeleccionado != null) {
                Intent intent = new Intent(ViewUsersAdminActivity.this, ModifyUserAdminActivity.class);
                intent.putExtra("nombre", usuarioSeleccionado.nombre);
                intent.putExtra("correo", usuarioSeleccionado.correo);
                intent.putExtra("contrasena", usuarioSeleccionado.contrasena);

                startActivity(intent);
            } else {
                Toast.makeText(this, "Por favor, selecciona un usuario.", Toast.LENGTH_SHORT).show();
            }
        });

        // Configura el botón para eliminar el usuario seleccionado
        btnEliminarEmpleado.setOnClickListener(v -> {
            if (usuarioSeleccionado != null) {
                confirmarEliminacionUsuario();
            } else {
                Toast.makeText(this, "Por favor, selecciona un usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarUsuarios(ArrayAdapter<String> adapter) {
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuariosList.clear();
                usuariosDataList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    if (usuario != null) {
                        String usuarioInfo = "Nombre: " + usuario.nombre + "\nCorreo: " + usuario.correo + "\nContrasena: " + usuario.contrasena;
                        usuariosList.add(usuarioInfo);
                        usuariosDataList.add(usuario);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewUsersAdminActivity.this, "Error al cargar los usuarios.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para mostrar el diálogo de confirmación y eliminar el usuario
    private void confirmarEliminacionUsuario() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar a " + usuarioSeleccionado.nombre + "?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarUsuarioDeFirebase())
                .setNegativeButton("No", null)
                .show();
    }

    // Método para eliminar el usuario de Firebase
    private void eliminarUsuarioDeFirebase() {
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");

        usuariosRef.orderByChild("correo").equalTo(usuarioSeleccionado.correo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                // Elimina el usuario de Firebase
                                snapshot.getRef().removeValue((databaseError, databaseReference) -> {
                                    if (databaseError == null) {
                                        Toast.makeText(ViewUsersAdminActivity.this, "Usuario eliminado correctamente.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ViewUsersAdminActivity.this, "Error al eliminar el usuario.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                        } else {
                            Toast.makeText(ViewUsersAdminActivity.this, "Usuario no encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ViewUsersAdminActivity.this, "Error al acceder a la base de datos.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Clase Usuario
    public static class Usuario {
        public String nombre, correo, contrasena;

        public Usuario() {}

        public Usuario(String nombre, String correo, String clave) {
            this.nombre = nombre;
            this.correo = correo;
            this.contrasena = clave;
        }
    }
}
