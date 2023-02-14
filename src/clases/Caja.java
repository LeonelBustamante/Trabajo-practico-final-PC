package clases;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import main.PrintColor;

/**
 * Clase que representa una caja de una tienda.
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Caja {

    private static int ASIGNADOR_ID = 0;

    private final int id;
    private final Semaphore caja;
    private int cantActualCinta;
    private final Lock lock;
    private final Condition conditionCajera;
    private final Condition conditionPasajero;
    private final ArrayList<Producto> cinta;
    private Pasajero clienteActual;

    public Caja() {
        this.id = ASIGNADOR_ID++;
        this.caja = new Semaphore(1, true);
        this.cinta = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.conditionCajera = lock.newCondition();
        this.conditionPasajero = lock.newCondition();
        this.cantActualCinta = 0;
    }

    // Metodos para Pasajeros ------------------------------------------------------
    public void hacerFila(Pasajero pasajero) {
        // Se utiliza un semaforo para controlar que haya 1 solo pasajero en la caja
        try {
            caja.acquire();
            clienteActual = pasajero;
            System.out.println(PrintColor.ANSI_BLUE + "[CLASE CAJA]: " + clienteActual + " ingreso a la caja " + id + "." + PrintColor.ANSI_RESET);
        } catch (InterruptedException ex) {
            System.out.println("Error en Caja.hacerFila " + ex.getMessage());
        }
    }

    public void ponerProductosCinta(ArrayList<Producto> carrito) {
        // Se utiliza un lock explicito para controlar los accesos a la cinta
        lock.lock();
        try {
            int tamanioCarrito = carrito.size();
            cantActualCinta = tamanioCarrito;
            for (int i = 0; i < tamanioCarrito; i++) {
                cinta.add((carrito.get(i)));
            }
            System.out.println(PrintColor.ANSI_BLUE + "[CLASE CAJA]: " + clienteActual + " puso " + tamanioCarrito + " productos en la cinta " + id + "." + PrintColor.ANSI_RESET);
            carrito.clear();
            conditionCajera.signal();
        } finally {
            lock.unlock();
        }
    }

    public void procesarCompra() {
        // Se utiliza un una condicion para notificar a la cajera que el cliente ya puso todos los productos en la cinta
        lock.lock();
        try {
            while (cantActualCinta > 0) {
                conditionPasajero.await();
            }
        } catch (InterruptedException ex) {
            System.out.println("Error en Caja.procesarCompra " + ex.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void salirCaja() {
        // Se libera el acceso a la caja
        System.out.println(PrintColor.ANSI_BLUE + "[CLASE CAJA]: " + clienteActual + " termino de pagar en la caja " + id + "." + PrintColor.ANSI_RESET);
        clienteActual = null;
        caja.release();
    }

    // Metodos para Cajera ---------------------------------------------------------
    public Producto cobrarProducto(Cajera cajera) {
        // Se utiliza un una condicion para notificar al cliente que la cajera ya cobro un producto
        lock.lock();
        Producto producto = null;
        try {
            while (cantActualCinta == 0) {
                conditionCajera.await();
            }
            producto = cinta.get(0);
            cantActualCinta--;
            Thread.sleep(1000);
            cinta.remove(0);
            if (cantActualCinta == 0) {
                conditionPasajero.signal();
            }
        } catch (InterruptedException ex) {
            System.out.println("Error en Caja.obtenerProductoCinta " + ex.getMessage());
        } finally {
            lock.unlock();
        }
        return producto;
    }

    // Metodos de la clase ---------------------------------------------------------
    public int getId() {
        return id;
    }

    public int getCantActualCinta() {
        return cantActualCinta;
    }

    public Pasajero getClienteActual() {
        return clienteActual;
    }
}
