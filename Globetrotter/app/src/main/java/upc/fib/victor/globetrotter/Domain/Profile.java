package upc.fib.victor.globetrotter.Domain;

import java.util.Date;

public class Profile {

    private String uid;
    private String nombre;
    private String apellidos;
    private Date nacimiento;

    public Profile() {
    }

    public Profile(String uid, String nombre, String apellidos, Date nacimiento) {
        this.uid = uid;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nacimiento = nacimiento;
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

    public String nombreCompleto() {
        return nombre + " " + apellidos;
    }
}
