package com.example.tinderempleosapp;

public class User {
    private String name;
    private String email;
    private String clave;
    private String ciudad;
    private String edad;
    private String info;
    private String pais;
    private String sobremi;
    private String tipo;
    private String profileImageUrl; // URL de la imagen de perfil

    // Constructor que incluye todos los campos
    public User(String name, String email, String clave, String ciudad, String edad, String info, String pais, String sobremi, String tipo, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.clave = clave;
        this.ciudad = ciudad;
        this.edad = edad;
        this.info = info;
        this.pais = pais;
        this.sobremi = sobremi;
        this.tipo = tipo;
        this.profileImageUrl = profileImageUrl; // Asignar la URL de la imagen
    }

    // Métodos getter
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getClave() {
        return clave;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getEdad() {
        return edad;
    }

    public String getInfo() {
        return info;
    }

    public String getPais() {
        return pais;
    }

    public String getSobremi() {
        return sobremi;
    }

    public String getTipo() {
        return tipo;
    }

    public String getProfileImageUrl() {
        return profileImageUrl; // Obtener la URL de la imagen
    }

    // Métodos setter
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setSobremi(String sobremi) {
        this.sobremi = sobremi;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl; // Establecer la URL de la imagen
    }
}
