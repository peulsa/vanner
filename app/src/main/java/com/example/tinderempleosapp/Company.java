package com.example.tinderempleosapp;

public class Company {
    private String name;
    private String correo;
    private String clave;
    private String sobreEmpresa;
    private String interes;
    private String horario;
    private String tipo;
    private String profileImageUrl; // URL de la imagen de perfil

    // Constructor que incluye todos los campos
    public Company(String name, String correo, String clave, String sobreEmpresa, String interes, String horario, String tipo, String profileImageUrl) {
        this.name = name;
        this.correo = correo;
        this.clave = clave;
        this.sobreEmpresa = sobreEmpresa;
        this.interes = interes;
        this.horario = horario;
        this.tipo = tipo;
        this.profileImageUrl = profileImageUrl; // Asignar la URL de la imagen
    }

    // Métodos getter y setter
    public String getName() {
        return name;
    }

    public String getCorreo() {
        return correo;
    }

    public String getClave() {
        return clave;
    }

    public String getSobreEmpresa() {
        return sobreEmpresa;
    }

    public String getInteres() {
        return interes;
    }

    public String getHorario() {
        return horario;
    }

    public String getTipo() {
        return tipo;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // Otros setters y getters aquí si es necesario...
}
