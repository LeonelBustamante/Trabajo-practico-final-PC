package clases;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ControlTren es una clase que representa un controlador de tren.
 * 
 * Se utiliza para llevar un control de la cantidad de pasajeros en el tren,
 * la cantidad de pasajeros en cada terminal, el número de viajes y otros
 * detalles relacionados con el tren.
 */
public class ControlTren {

    private static final int PRIMER_TERMINAL = 65;

    private LinkedList<String> prints;
    private AtomicInteger cantPasajerosEnTren = new AtomicInteger(0);
    private AtomicInteger numeroViaje = new AtomicInteger(1);
    private final int cantTerminales, capacidadTren;
    private final int[] cantPasajerosEnTerminal;
    private final Semaphore permisoArrancarTren, semVolverInicio, permisoSubirTren, mutex;

    public ControlTren(int capacidadMaxima, int cantidadTerminales) {
        this.prints = new LinkedList<String>();
        this.capacidadTren = capacidadMaxima;
        this.cantTerminales = cantidadTerminales;
        this.permisoSubirTren = new Semaphore(capacidadMaxima);
        this.semVolverInicio = new Semaphore(0);
        this.permisoArrancarTren = new Semaphore(0);
        this.mutex = new Semaphore(1); // mutex
        this.cantPasajerosEnTerminal = new int[cantidadTerminales];
    }

    // Metodos para Pasajero -------------------------------------------------------
    /**
     * Permite que un pasajero suba al tren.
     * 
     * @param pasajero El pasajero que desea subir al tren
     */
    public void pasajeroSubeAlTren(Pasajero pasajero) throws InterruptedException {
        permisoSubirTren.acquire();

        mutex.acquire();
        prints.add(pasajero + " subio al tren.");
        cantPasajerosEnTren.incrementAndGet();
        controlarBajadaEnEstacion(pasajero.getReserva().getTerminal().getLetra());
        mutex.release();

        permisoArrancarTren.release();
    }

    /**
     * Este metodo controla la descarga de un pasajero en una terminal especifica.
     * 
     * @param letra La letra identificadora de la terminal en la que el pasajero
     *              desea bajar.
     */
    private void controlarBajadaEnEstacion(char letra) {
        int puntero = PRIMER_TERMINAL;
        int ultimaTerminal = cantTerminales + PRIMER_TERMINAL;
        boolean seguir = true;

        while (seguir && puntero < ultimaTerminal) {
            if (letra == ((char) puntero)) {
                cantPasajerosEnTerminal[puntero - PRIMER_TERMINAL] += 1;
                seguir = false;
            }
            puntero += 1;
        }
    }

    /**
     * Este metodo permite que un pasajero baje del tren.
     * 
     * @param pasajero El pasajero que desea bajar del tren.
     */
    public void bajarDeTren(Pasajero pasajero) {
        semVolverInicio.release();
        cantPasajerosEnTren.decrementAndGet();
        prints.add(pasajero + " se bajo del tren. Quedan " + cantPasajerosEnTren.get() + " pasajeros en el tren.");
    }

    /**
     * 
     * Este método permite comenzar un viaje del tren.
     * Primero, verifica si el tren está completo o no, y si no lo está, adquiere
     * permisos suficientes para que los que aun no llegan no se suban.
     * Después, comienza el viaje y aumenta el contador del número de viajes.
     */
    public void comenzarViaje() throws InterruptedException {
        Thread.sleep(1000);
        if (cantPasajerosEnTren.get() < capacidadTren) {
            int cantPasajerosFaltantes = capacidadTren - cantPasajerosEnTren.get();
            permisoSubirTren.acquire(cantPasajerosFaltantes);
            semVolverInicio.release(cantPasajerosFaltantes);
            prints.add("====== El tren no esta completo, faltan " + cantPasajerosFaltantes
                    + " pasajeros para completar el tren. El tren comienza el viaje "
                    + numeroViaje.getAndIncrement() + ".");
        } else {
            prints.add("====== El tren esta completo, comienza el viaje " + numeroViaje.getAndIncrement() + ".");
        }
    }

    /**
     * 
     * Este metodo se encarga de procesar el arribo de una terminal especifica.
     * 
     * @param terminalArribada La terminal que ha arribado.
     * 
     * @param semaforos        Un arreglo de semaforos que controla el acceso a las
     *                         terminales.
     */
    public void arriboTerminal(char terminalArribada, Semaphore[] semaforos) throws InterruptedException {
        mutex.acquire();
        int puntero = PRIMER_TERMINAL;
        int ultimaTerminal = cantTerminales + PRIMER_TERMINAL;
        boolean seguir = true;

        while (seguir && puntero < ultimaTerminal) {
            if (terminalArribada == (char) puntero) {
                int indice = puntero - PRIMER_TERMINAL;
                int cantidad = cantPasajerosEnTerminal[indice];

                semaforos[indice].release(cantidad);
                cantPasajerosEnTerminal[indice] = 0;
                seguir = false;
            }
            puntero++;
        }
        mutex.release();
    }

    /**
     * Este metodo permite que el tren regrese a la primera parada.
     */
    public void volverPrimerParada() throws InterruptedException {
        semVolverInicio.acquire(capacidadTren);
        prints.add("******El tren regresa a la primera parada. Quedan {} pasajeros en el tren."
                + cantPasajerosEnTren.get());
    }

    /**
     * Este metodo permite que el tren llegue a la primera parada.
     */
    public void llegoOrigen() {
        permisoSubirTren.release(capacidadTren);
    }

    // Metodos de la clase ---------------------------------------------------------
    public void mostrarMensajes() {
        while (!prints.isEmpty()) {
            System.out.println(prints.poll());
        }
    }
}
