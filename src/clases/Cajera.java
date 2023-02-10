package clases;

import main.PrintColor;

/**
 * Clase que representa una cajera de una tienda.
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Cajera extends Persona implements Runnable {

    private final Caja caja;

    public Cajera(String nombre, Caja caja) {
        super(nombre);
        this.caja = caja;
    }

    @Override
    public void run() {
        // Metodo que indica la accion que realiza la cajera
        while (true) {
            try {
                Producto producto = caja.cobrarProducto(this);
                System.out.println(PrintColor.ANSI_CYAN + "[CLASE CAJERA] "
                        + "La cajera: " + toString() + " de la caja: " + caja.getId()
                        + " esta cobrando el producto " + producto.getId()
                        + " al cliente: " + caja.getClienteActual()+ PrintColor.ANSI_RESET);
                Thread.sleep(100);
                producto.restarStock();
            } catch (InterruptedException ex) {
                System.out.println("Error en Cajera.run " + ex.getMessage());
            }
        }
    }

    // Metodos de Caja ---------------------------------------------------------
    public Caja getCaja() {
        return caja;
    }

}
