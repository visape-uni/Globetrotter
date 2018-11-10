package upc.fib.victor.globetrotter.Domain;

import java.util.ArrayList;
import java.util.Date;

public class Profile {

    private String uid;
    private String nombre;
    private String apellidos;
    private String descripcion;
    private Date nacimiento;
    private String correo;
    private int numSeguidores;
    private int numSeguidos;
    private int numPaises;

    public Profile() {
    }

    public Profile(String uid, String nombre, String apellidos, Date nacimiento, String descripcion) {
        this.uid = uid;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nacimiento = nacimiento;
        this.descripcion = descripcion;
    }

    public Profile(String uid, String nombre, String apellidos, Date nacimiento, String correo, int numSeguidores, int numSeguidos, int numPaises, String descripcion) {
        this.uid = uid;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nacimiento = nacimiento;
        this.correo = correo;
        this.numSeguidores = numSeguidores;
        this.numSeguidos = numSeguidos;
        this.numPaises = numPaises;
        this.descripcion = descripcion;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(Date nacimiento) {
        this.nacimiento = nacimiento;
    }

    public int getNumSeguidores() {
        return numSeguidores;
    }

    public void setNumSeguidores(int numSeguidores) {
        this.numSeguidores = numSeguidores;
    }

    public int getNumSeguidos() {
        return numSeguidos;
    }

    public void setNumSeguidos(int numSeguidos) {
        this.numSeguidos = numSeguidos;
    }

    public int getNumPaises() {
        return numPaises;
    }

    public void setNumPaises(int numPaises) {
        this.numPaises = numPaises;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}
