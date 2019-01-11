package upc.fib.victor.globetrotter.Domain;

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

    public String getNombreCompletoCapital() {
        return nombre.toUpperCase() + " " + apellidos.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return numSeguidores == profile.numSeguidores &&
                numSeguidos == profile.numSeguidos &&
                numPaises == profile.numPaises &&
                uid.equals(profile.getUid()) &&
                nombre.equals(profile.getNombre()) &&
                apellidos.equals(profile.getApellidos()) &&
                descripcion.equals(profile.getDescripcion()) &&
                nacimiento.equals(profile.getNacimiento()) &&
                correo.equals(profile.getCorreo());
    }

    @Override
    public String toString() {
        return "Profile{" +
                "uid='" + uid + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", nacimiento=" + nacimiento +
                ", correo='" + correo + '\'' +
                ", numSeguidores=" + numSeguidores +
                ", numSeguidos=" + numSeguidos +
                ", numPaises=" + numPaises +
                '}';
    }
}
