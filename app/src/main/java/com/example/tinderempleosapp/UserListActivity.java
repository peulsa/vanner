package com.example.tinderempleosapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Crear lista de usuarios (esto es un ejemplo, podrías cargar datos reales aquí)
        userList = new ArrayList<>();
        userList.add(new User("Juan Pérez", "juan.perez@ejemplo.com"));
        userList.add(new User("Ana Gómez", "ana.gomez@ejemplo.com"));
        // Agregar más usuarios aquí...

        // Configurar el adaptador
        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setAdapter(userAdapter);
    }
}
