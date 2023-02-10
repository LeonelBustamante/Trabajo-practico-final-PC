package clases;

/**
 * Clase que representa a una aerolinea
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Aerolinea {

    private final String nombre;
    private final PuestoAtencion puestoAtencion;

    public Aerolinea(String nombre, PuestoAtencion puestoAtencion) {
        this.nombre = nombre;
        this.puestoAtencion = puestoAtencion;
    }

    // Metodos de la clase -----------------------------------------------------
    public String getNombre() {
        return nombre;
    }

    public PuestoAtencion getPuestoAtencion() {
        return puestoAtencion;
    }

    @Override
    public String toString() {
        return "Aerolinea: " + nombre;
    }

}
