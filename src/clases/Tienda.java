package clases;

import java.util.ArrayList;
import java.util.Random;
import main.PrintColor;

/**
 * Clase que representa la tienda del aeropuerto
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Tienda {

    private static final int MAX_PERSONAS = 3;
    private static final int MAX_PRODUCTOS = 10;

    private final Caja[] cajas;
    private final Producto[] productos;
    private int personasAdentroTienda;

    public Tienda(Caja[] cajas) {
        this.cajas = cajas;
        this.productos = new Producto[MAX_PRODUCTOS];
        this.personasAdentroTienda = 0;
        crearProductos();
    }

    private void crearProductos() {
        int id, stock, precio;
        for (int i = 0; i < MAX_PRODUCTOS; i++) {
            // Creamos los productos con un id, stock y precio aleatorio
            id = i;
            stock = new Random().nextInt(10);
            precio = new Random().nextInt(100);
            this.productos[i] = new Producto(id, stock, precio);
        }
    }

    // Metodos para Pasajeros ------------------------------------------------------
    /**
     * método permite obtener una caja de las disponibles en el aeropuerto de
     * forma aleatoria y retorna la caja seleccionada
     *
     * @param pasajero El pasajero que quiere obtener una caja
     * @return La caja seleccionada
     */
    public Caja obtenerCaja(Pasajero pasajero) {
        Caja caja = cajas[new Random().nextInt(cajas.length)];
        System.out.println(PrintColor.ANSI_GREEN_BACKGROUND + "[CLASE TIENDA] " + pasajero + " eligio la caja " + caja.getId() + PrintColor.ANSI_RESET);
        return caja;
    }

    /**
     * Este método permite a un pasajero entrar a la tienda, sin embargo, si ya
     * hay una cantidad máxima de personas en la tienda (definida por
     * MAX_PERSONAS), el pasajero tendrá que esperar hasta que se libere un
     * lugar.
     *
     * @param pasajero El pasajero que quiere entrar a la tienda
     */
    public synchronized void entrarTienda(Pasajero pasajero) {
        // Metodo con lock implicito donde se utiliza al objeto this como lock
        while (personasAdentroTienda >= MAX_PERSONAS) {
            try {
                System.out.println(PrintColor.ANSI_GREEN_BACKGROUND + "[CLASE TIENDA] " + pasajero + " esta esperando a que se libere un lugar en la tienda" + PrintColor.ANSI_RESET);
                wait();
            } catch (InterruptedException ex) {
                System.out.println("Error en Tienda.entrarTienda: " + ex.getMessage());
            }
        }
        personasAdentroTienda++;
        System.out.println(PrintColor.ANSI_GREEN_BACKGROUND + "[CLASE TIENDA] " + pasajero + " entro a la tienda" + PrintColor.ANSI_RESET);
    }

    /**
     * método permite a un pasajero seleccionar una cantidad aleatoria de
     * productos (dentro del rango definido por cantProductos) y agregarlos a su
     * carrito.
     *
     * @param pasajero El pasajero que quiere seleccionar productos
     * @param carrito  El carrito del pasajero
     */
    public void seleccionarProductos(Pasajero pasajero, ArrayList<Producto> carrito) {
        Producto producto;
        int cantProductosCarritoCliente = new Random().nextInt(5);

        System.out.println(PrintColor.ANSI_GREEN_BACKGROUND + "[CLASE TIENDA] " + pasajero + " esta seleccionando productos" + PrintColor.ANSI_RESET);
        for (int i = 0; i < cantProductosCarritoCliente; i++) {
            producto = this.productos[new Random().nextInt(MAX_PRODUCTOS)];
            synchronized (producto) {
                // Bloque sincronizado que se ejecuta sobre el objeto producto para evitar que dos hilos consulten el stock al mismo tiempo
                if (producto.getStock() > 0) {
                    System.out.println(PrintColor.ANSI_GREEN_BACKGROUND + "[CLASE TIENDA] " + pasajero + " agrego producto " + producto.getId() + " al carrito" + PrintColor.ANSI_RESET);
                    carrito.add(producto);
                } else {
                    System.out.println(PrintColor.ANSI_GREEN_BACKGROUND + "[CLASE TIENDA] " + pasajero + " no pudo agregar un producto al carrito porque no hay stock" + PrintColor.ANSI_RESET);
                }
            }
        }
    }

    /**
     * método permite a un pasajero salir de la tienda y notifica a otros
     * posibles hilos que esperan para entrar a la tienda.
     *
     * @param pasajero El pasajero que quiere salir de la tienda
     */
    public synchronized void salirTienda(Pasajero pasajero) {
        // Metodo con lock implicito que despertara al pasajero que estaba esperando
        System.out.println(PrintColor.ANSI_GREEN_BACKGROUND + "[CLASE TIENDA] " + pasajero + " salio de la tienda" + PrintColor.ANSI_RESET);
        personasAdentroTienda--;
        notify();
    }

    // Metodo para Repositor ------------------------------------------------------
    /**
     * método permite a un repositor reponer todos los productos disponibles en
     * la tienda.
     *
     * @param repositor El repositor que quiere reponer los productos
     */
    public void reponerProductos(Repositor repositor) {
        for (int i = 0; i < productos.length; i++) {
            this.productos[i].agregarStock(20);
        }
    }
}
