package com.example.tinderempleosapp;

public class Admin {
    private String nombre;
    private String correo;
    private String clave;
    private String cargo;
    private String ciudad;  // Corregí el error tipográfico (cuidad -> ciudad)
    private String pais;
    private String infoAdicional;

    // Constructor vacío necesario para Firebase
    public Admin() {
        // Firebase necesita un constructor vacío
    }

    // Constructor con todos los atributos
    public Admin(String nombre, String correo, String clave, String cargo, String ciudad, String pais, String infoAdicional) {
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
        this.cargo = cargo;
        this.ciudad = ciudad;
        this.pais = pais;
        this.infoAdicional = infoAdicional;
    }

    // Métodos getters y setters para cada atributo
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(String infoAdicional) {
        this.infoAdicional = infoAdicional;
    }
}

