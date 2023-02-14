package clases;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import main.PrintColor;

/**
 * Clase que representa a un pasajero.
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Pasajero extends Persona implements Runnable {

    private final Reserva reserva;
    private final Aeropuerto aeropuerto;
    private final boolean quiereVerTienda;
    private final boolean quiereComprarTienda;
    private final AtomicInteger hora;

    public Pasajero(String nombre, Reserva reserva, Aeropuerto aeropuerto,
            boolean quiereVerTienda, boolean quiereComprarTienda, AtomicInteger hora) {
        super(nombre);
        this.reserva = reserva;
        this.aeropuerto = aeropuerto;
        this.quiereVerTienda = quiereVerTienda;
        this.quiereComprarTienda = quiereComprarTienda;
        this.hora = hora;
    }

    @Override
    /**
     * Este método simula la acción de un pasajero en un aeropuerto, incluyendo
     * la llegada al aeropuerto, traslado a la terminal, ver una tienda, compra
     * en la tienda, y espera de vuelo.
     */
    public void run() {
        try {
            Tren tren = aeropuerto.getTren();
            ControlTren controlTren = tren.getControlTren();
            Terminal terminal = reserva.getTerminal();
            Tienda tienda = terminal.getTienda();
            PuestoAtencion puesto = reserva.getAerolinea().getPuestoAtencion();
            int horaVuelo = reserva.getHora();

            aeropuerto.ingresarAeropuerto(this);

            puesto.entrarFilaPuestoAtencion(this);
            Thread.sleep(400);

            puesto.entrarPuestoAtencion(this);
            Thread.sleep(400);

            puesto.salirPuestoAtencion(this);
            Thread.sleep(400);

            controlTren.pasajeroSubeAlTren(this);
            tren.solicitarViajeATerminal(reserva.getTerminal().getLetra());
            controlTren.bajarDeTren(this);

            int horaActual = hora.addAndGet(0);
            if (((horaActual + 2 <= horaVuelo) || (horaActual > horaVuelo)) && quiereVerTienda) {
                System.out.println(PrintColor.ANSI_PURPLE + "[CLASE PASAJERO]" + toString() + " esta viendo la tienda ..." + PrintColor.ANSI_RESET);
                Thread.sleep(3000);
            } else {
                System.out.println(PrintColor.ANSI_PURPLE + "[CLASE PASAJERO]" + toString() + " no va a ver la tienda porque no tiene tiempo o no quiere." + PrintColor.ANSI_RESET);
            }

            if (((horaActual + 3 <= horaVuelo) || (horaActual > horaVuelo)) && (quiereComprarTienda && quiereVerTienda)) {
                ArrayList<Producto> carro = new ArrayList<>();
                Caja caja = tienda.obtenerCaja(this);

                tienda.entrarTienda(this);

                tienda.seleccionarProductos(this, carro);
                Thread.sleep(1000);

                caja.hacerFila(this);
                caja.ponerProductosCinta(carro);
                Thread.sleep(600);
                caja.procesarCompra();
                Thread.sleep(600);
                caja.salirCaja();

                tienda.salirTienda(this);
            } else {
                System.out.println(PrintColor.ANSI_PURPLE + toString() + "[CLASE PASAJERO]" + " no va a comprar en la tienda porque no tiene tiempo o no quiere." + PrintColor.ANSI_RESET);
            }

            // espera vuelo
            terminal.esperarVuelo(this);
            System.out.println( PrintColor.ANSI_PURPLE + "[CLASE PASAJERO]" + toString() + " ha tomado su vuelo. Adios! :)" + PrintColor.ANSI_RESET);
        } catch (InterruptedException ex) {
            System.out.println("Error en Pasajero.run: " + ex.getMessage());
        }
    }

    // Metodos de la clase ---------------------------------------------------------
    public Reserva getReserva() {
        return reserva;
    }
}
