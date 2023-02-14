package clases;

import java.util.concurrent.atomic.AtomicInteger;
import main.Handler;

import main.PrintColor;

/**
 * Clase que representa a una terminal del aeropuerto
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Terminal {

    private static char ASIGNACION_CORRELATIVA = 'A';

    private final int[] puertas;
    private final Tienda tienda;
    private final char letra;
    private final AtomicInteger hora;

    public Terminal(int[] puertas, Tienda tienda, AtomicInteger hora) {
        this.puertas = puertas;
        this.tienda = tienda;
        this.hora = hora;
        this.letra = ASIGNACION_CORRELATIVA;
        ASIGNACION_CORRELATIVA++;
    }

    // Metodos para Pasajero ---------------------------------------------------
    /**
     * método permite que un pasajero espere en la terminal hasta que llegue la
     * hora de su vuelo, notifica al pasajero cuando la hora actual coincide con
     * la hora de su vuelo.
     *
     * @param pasajero pasajero que espera el vuelo
     */
    public synchronized void esperarVuelo(Pasajero pasajero) {
        // Metodo con lock implicito donde se utiliza al objeto this como lock
        while (hora.get() != pasajero.getReserva().getHora()) {
            try {
                System.out.println(PrintColor.ANSI_CYAN_BACKGROUND + "[CLASE TERMINAL]"
                        + pasajero + " esta esperando que su vuelo de las: "
                        + Handler.formatoHora(pasajero.getReserva().getHora())
                        + PrintColor.ANSI_RESET);
                wait();
            } catch (InterruptedException ex) {
                System.out.println("Error en Terminal.esperarVuelo: " + ex.getMessage());

            }
        }
    }

    // Metodos para Reloj ------------------------------------------------------
    /**
     * método permite la notificación de todos los pasajeros esperando en la
     * terminal.
     */
    public synchronized void pasarHora() {
        // Metodo con lock implicito que despierta a todos los hilos que estan esperando
        notifyAll();
    }

    // Metodos de la clase -----------------------------------------------------
    public int[] getPuertas() {
        return puertas;
    }

    public Tienda getTienda() {
        return tienda;
    }

    public char getLetra() {
        return letra;
    }

}
