package clases;

/**
 * Clase abstracta que representa a una persona.
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public abstract class Persona {

    private final String nombre;

    public Persona(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }

}
