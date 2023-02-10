package clases;

/**
 * Clase que representa a un recepcionista
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Recepcionista extends Persona implements Runnable {

    private PuestoAtencion puesto;

    public Recepcionista(String nombre) {
        super(nombre);
    }

    @Override
    public void run() {
        while (true) {
            try {
                puesto.hacerPasarPasajero();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Error en Recepcionista.run: " + ex.getMessage());
            }
        }
    }

    // Metodos de la clase -----------------------------------------------------
    public PuestoAtencion getPuesto() {
        return puesto;
    }

    public void setPuesto(PuestoAtencion puesto) {
        this.puesto = puesto;
    }

}
