package com.example.tinderempleosapp;

import android.content.Intent;
import android.os.Bundle;
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

public class ViewCompaniesAdminActivity extends AppCompatActivity {

    private ListView listaEmpresas;
    private ArrayList<String> empresasList;
    private ArrayList<Empresa> empresasDataList;
    private Button btnModificarEmpresa, btnEliminarEmpresa;
    private Empresa empresaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_company_admin);

        listaEmpresas = findViewById(R.id.lista_empresas);
        btnModificarEmpresa = findViewById(R.id.btn_modificar_empresa);
        btnEliminarEmpresa = findViewById(R.id.btn_eliminar_empresa);
        empresasList = new ArrayList<>();
        empresasDataList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, empresasList);
        listaEmpresas.setAdapter(adapter);

        cargarEmpresas(adapter);

        listaEmpresas.setOnItemClickListener((parent, view, position, id) -> {
            empresaSeleccionada = empresasDataList.get(position);
            Toast.makeText(this, "Seleccionado: " + empresaSeleccionada.nombre, Toast.LENGTH_SHORT).show();
        });

        btnModificarEmpresa.setOnClickListener(v -> {
            if (empresaSeleccionada != null) {
                Intent intent = new Intent(ViewCompaniesAdminActivity.this, ModifyCompanyAdminActivity.class);
                intent.putExtra("nombre", empresaSeleccionada.nombre);
                intent.putExtra("correo", empresaSeleccionada.correo);
                intent.putExtra("contrasena", empresaSeleccionada.contrasena);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Por favor, selecciona una empresa.", Toast.LENGTH_SHORT).show();
            }
        });

        btnEliminarEmpresa.setOnClickListener(v -> {
            if (empresaSeleccionada != null) {
                mostrarDialogoConfirmacion();
            } else {
                Toast.makeText(this, "Por favor, selecciona una empresa.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarEmpresas(ArrayAdapter<String> adapter) {
        DatabaseReference empresasRef = FirebaseDatabase.getInstance().getReference("empresas");

        empresasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                empresasList.clear();
                empresasDataList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Empresa empresa = snapshot.getValue(Empresa.class);
                    if (empresa != null) {
                        String empresaInfo = "Nombre: " + empresa.nombre + "\nCorreo: " + empresa.correo + "\nContraseña: " + empresa.contrasena;
                        empresasList.add(empresaInfo);
                        empresasDataList.add(empresa);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewCompaniesAdminActivity.this, "Error al cargar las empresas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta empresa?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarEmpresa())
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarEmpresa() {
        DatabaseReference empresasRef = FirebaseDatabase.getInstance().getReference("empresas");

        empresasRef.orderByChild("correo").equalTo(empresaSeleccionada.correo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Empresa empresa = snapshot.getValue(Empresa.class);
                    if (empresa != null &&
                            empresa.nombre.equals(empresaSeleccionada.nombre) &&
                            empresa.contrasena.equals(empresaSeleccionada.contrasena)) {

                        snapshot.getRef().removeValue();
                        Toast.makeText(ViewCompaniesAdminActivity.this, "Empresa eliminada", Toast.LENGTH_SHORT).show();
                        empresaSeleccionada = null;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewCompaniesAdminActivity.this, "Error al eliminar la empresa.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Empresa {
        public String nombre, correo, contrasena;

        public Empresa() {}

        public Empresa(String nombre, String correo, String contrasena) {
            this.nombre = nombre;
            this.correo = correo;
            this.contrasena = contrasena;
        }
    }
}
