package com.example.tinderempleosapp;

public class Company {
    private String name;
    private String correo;
    private String clave;
    private String sobreEmpresa;
    private String interes;
    private String horario;
    private String tipo; // Puedes agregar otros atributos según sea necesario

    // Constructor que incluye todos los campos
    public Company(String name, String correo, String clave, String sobreEmpresa, String interes, String horario, String tipo) {
        this.name = name;
        this.correo = correo;
        this.clave = clave;
        this.sobreEmpresa = sobreEmpresa;
        this.interes = interes;
        this.horario = horario;
        this.tipo = tipo; // Puedes establecer el tipo de empresa si es necesario
    }

    // Constructor de ejemplo
    public Company(String nombreEmpresa, String email, String password, String descripcion, String intereses, String horarioLaboral) {
        this.name = nombreEmpresa;
        this.correo = email;
        this.clave = password;
        this.sobreEmpresa = descripcion;
        this.interes = intereses;
        this.horario = horarioLaboral;
    }

    // Métodos getter
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

    // Métodos setter
    public void setName(String name) {
        this.name = name;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void setSobreEmpresa(String sobreEmpresa) {
        this.sobreEmpresa = sobreEmpresa;
    }

    public void setInteres(String interes) {
        this.interes = interes;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

