package clases;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import main.PrintColor;

/**
 * Clase que representa a un puesto de atencion
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class PuestoAtencion {

    private static final int MAX_PUESTO_ATENCION = 2;

    private int cantFila, cantHall;
    private final ArrayList<Pasajero> filaPuestoAtencion;
    private final Lock lock;
    private final Condition esperaHall;
    private final Condition pasajerosEnFila;
    private final Condition activarRecepcionista;
    private final Recepcionista recepcionista;

    public PuestoAtencion(Recepcionista recepcionista) {
        this.recepcionista = recepcionista;
        this.filaPuestoAtencion = new ArrayList<>();
        this.cantFila = 0;
        this.cantHall = 0;
        this.lock = new ReentrantLock();
        this.esperaHall = lock.newCondition();
        this.pasajerosEnFila = lock.newCondition();
        this.activarRecepcionista = lock.newCondition();
    }

    // Metodos para Pasajero -------------------------------------------------------
    public void entrarFilaPuestoAtencion(Pasajero pasajero) {
        // Se utiliza un lock explicito con una condicion que indicara si el pasajero debe esperar en la sala de espera o no
        lock.lock();
        try {
            cantHall++;
            while (cantFila == MAX_PUESTO_ATENCION) {
                try {
                    System.out.println(PrintColor.ANSI_RED + "[CLASE PUESTOATENCION] " + pasajero + " debe esperar en la sala de espera." + PrintColor.ANSI_RESET);
                    esperaHall.await();
                } catch (InterruptedException ex) {
                }
            }
            System.out.println(PrintColor.ANSI_RED + "[CLASE PUESTOATENCION] " + pasajero + " ingreso a la fila del puesto de atencion." + PrintColor.ANSI_RESET);
            filaPuestoAtencion.add(pasajero);
            cantHall--;
            cantFila++;
        } finally {
            lock.unlock();
        }
    }

    public void entrarPuestoAtencion(Pasajero pasajero) {
        // Se utiliza un lock explicito con una condicion que indicara si el pasajero debe esperar en la fila o no
        lock.lock();
        try {
            while (filaPuestoAtencion.get(0) != pasajero) {
                pasajerosEnFila.await();
            }
            System.out.println(PrintColor.ANSI_RED + "[CLASE PUESTOATENCION] " + pasajero + " ingreso al puesto de atencion." + PrintColor.ANSI_RESET);
        } catch (InterruptedException ex) {
            System.out.println("Error en PuestoAtencion.entrarPuestoAtencion: " + ex.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void salirPuestoAtencion(Pasajero pasajero) {
        // Se utiliza un lock explicito y se notifica a los pasajeros en la fila y al recepcionista para que se activen
        lock.lock();
        try {
            System.out.println(PrintColor.ANSI_RED + "[CLASE PUESTOATENCION] " + pasajero + " salio del puesto de atencion." + PrintColor.ANSI_RESET);
            filaPuestoAtencion.remove(0);
            cantFila--;
            pasajerosEnFila.signal();
            activarRecepcionista.signal();
        } finally {
            lock.unlock();
        }
    }

    // Metodos para Recepcionista --------------------------------------------------
    public void hacerPasarPasajero() {
        // Se utiliza un lock explicito con una condicion que lo hara esperar si no hay pasajeros en la sala de espera o si la fila esta llena, luego 
        // notifica a los pasajeros en sala de espera para que se activen
        lock.lock();
        try {
            while (cantHall == 0 || cantFila == MAX_PUESTO_ATENCION) {
                try {
                    activarRecepcionista.await();
                } catch (InterruptedException ex) {
                    System.out.println("Error en PuestoAtencion.hacerPasarPasajero: " + ex.getMessage());
                }
            }
            System.out.println(PrintColor.ANSI_RED + "[CLASE PUESTOATENCION] " + recepcionista + " hizo pasar a " + filaPuestoAtencion.get(0) + " al puesto de atencion." + PrintColor.ANSI_RESET);
            esperaHall.signal();
        } finally {
            lock.unlock();
        }
    }

}
