package clases;

import java.util.concurrent.atomic.AtomicInteger;
import main.PrintColor;

/**
 * Clase que representa al repositor del aeropuerto
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Repositor extends Persona implements Runnable {

    private static final int HORA_REPONER = 0;

    private final Tienda[] tiendas;
    private final AtomicInteger hora;

    public Repositor(String nombre, Tienda[] tiendas, AtomicInteger hora) {
        super(nombre);
        this.tiendas = tiendas;
        this.hora = hora;
    }

    @Override
    /**
     * método ejecuta el método reponerProductosTiendas para el repositor.
     */
    public void run() {
        while (true) {
            try {
                reponerProductosTiendas();
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * método es sincronizado y repone los productos en las tiendas cuando la
     * hora es igual a la constante <HORA_REPONER>, Si la hora no es igual a
     * <HORA_REPONER>, el hilo se bloqueará esperando una notificación.
     */
    private synchronized void reponerProductosTiendas() {
        // Metodo con lock implicito donde se utiliza al objeto this como lock
        while (hora.get() != HORA_REPONER) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error en Repositor.reponerProductosTiendas: " + e.getMessage());
            }
        }
        System.out.println(PrintColor.ANSI_WHITE + "[CLASE REPOSITOR] " + toString() + " comienza a reponer productos para las tiendas" + PrintColor.ANSI_RESET);
        // Repone productos de todas las tiendas
        for (Tienda tienda : tiendas) {
            tienda.reponerProductos(this);
        }
        System.out.println(PrintColor.ANSI_WHITE + "[CLASE REPOSITOR] " + toString() + " termino de reponer productos en las tiendas" + PrintColor.ANSI_RESET);
    }

    // Metodos de Reloj ------------------------------------------------------------
    /**
     * método es sincronizado y notifica a todos los hilos bloqueados que
     * esperan una notificación.
     */
    public synchronized void notificarCambioHora() {
        // Metodo con lock implicito que despertara al hilo Repositor
        notify();
    }
}
