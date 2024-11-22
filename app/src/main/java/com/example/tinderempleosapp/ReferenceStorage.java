package com.example.tinderempleosapp;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class ReferenceStorage extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar el proyecto principal (proyectoA) automáticamente
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        // Configurar y registrar el segundo proyecto (proyectoStorage) manualmente
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("loginperfilimg")  // ID del proyecto de Firebase
                .setApplicationId("1:1030729774309:android:f989dcbcc3c39bb40e500c") // ID de la aplicación móvil
                .setApiKey("AIzaSyAgfeveBQYjthGmRU8S6QacpH4LHDOxaUg") // API key (Considera usar variables de entorno o `strings.xml` para seguridad)
                .setStorageBucket("loginperfilimg.appspot.com") // Storage bucket correctamente formateado
                .build();

        // Inicializar el segundo proyecto con un nombre único para distinguirlo
        FirebaseApp.initializeApp(this, options, "proyectoStorage");
    }
}