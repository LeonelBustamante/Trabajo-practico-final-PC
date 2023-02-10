package main;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import clases.*;

public class Main {

    // Constantes
    private static final int CANTIDAD_AEROLINEAS = 3;
    private static final int CANTIDAD_CAJAS_POR_TIENDA = 2;
    private static final int CANTIDAD_TERMINALES = 3;
    private static final int CANTIDAD_PUERTA_EMBARQUE_POR_TERMINAL = 4;
    private static final int CAPACIDAD_TREN = 10;

    // Variables
    private static final Aerolinea[] AEROLINEAS = new Aerolinea[CANTIDAD_AEROLINEAS];
    private static final Terminal[] TERMINALES = new Terminal[CANTIDAD_TERMINALES];
    private static final Tienda[] TIENDAS = new Tienda[CANTIDAD_TERMINALES];
    private static final AtomicInteger HORA = new AtomicInteger(5);
    // Se genera un multiplo de la capacidad del tren para que no se bloquee
    private static final int CANTIDAD_PASAJEROS = 25;

    public static void crearTerminales() {
        int numeroPuertaEmbarque = 1;

        for (int i = 0; i < CANTIDAD_TERMINALES; i++) {
            // Creación puertas de embarque
            int[] puertasEmbarque = new int[CANTIDAD_PUERTA_EMBARQUE_POR_TERMINAL];
            for (int j = 0; j < CANTIDAD_PUERTA_EMBARQUE_POR_TERMINAL; j++) {
                puertasEmbarque[j] = numeroPuertaEmbarque;
                numeroPuertaEmbarque += 1;
            }

            // Creación de la tienda
            TIENDAS[i] = crearTienda();

            // Creación de la terminal
            TERMINALES[i] = new Terminal(puertasEmbarque, TIENDAS[i], HORA);
        }
    }

    public static Tienda crearTienda() {
        Caja[] cajas = new Caja[CANTIDAD_CAJAS_POR_TIENDA];

        for (int i = 0; i < CANTIDAD_CAJAS_POR_TIENDA; i++) {
            // Creación de las cajas
            cajas[i] = new Caja();
            // Creación e inicio de las cajeras
            Cajera cajera = new Cajera("CAJERA-" + i, cajas[i]);
            new Thread(cajera).start();
        }
        return new Tienda(cajas);
    }

    public static void crearAerolineas() {
        for (int i = 0; i < CANTIDAD_AEROLINEAS; i++) {
            // Se crea un puesto de atención para cada aerolínea
            Recepcionista recepcionista = new Recepcionista("RECEPCIONISTA-" + i);
            PuestoAtencion puesto = new PuestoAtencion(recepcionista);
            recepcionista.setPuesto(puesto);
            new Thread(recepcionista).start();
            AEROLINEAS[i] = new Aerolinea("AEREOLINEA-" + i, puesto);
        }
    }

    public static void crearPasajeros(Aeropuerto aeropuerto) {
        // Se crea e inicia al pasajero y su reserva
        int hora, puerta;
        Aerolinea aereolinea;
        Terminal terminal;
        Boolean quiereVerTienda, quiereComprarTienda;

        for (int i = 0; i < CANTIDAD_PASAJEROS; i++) {
            try {
                hora = new Random().nextInt(24);
                aereolinea = AEROLINEAS[new Random().nextInt(CANTIDAD_AEROLINEAS)];
                terminal = TERMINALES[new Random().nextInt(CANTIDAD_TERMINALES)];
                puerta = (terminal.getPuertas())[new Random().nextInt(CANTIDAD_PUERTA_EMBARQUE_POR_TERMINAL)];
                quiereVerTienda = new Random().nextBoolean();
                quiereComprarTienda = new Random().nextBoolean();
                Reserva reserva = new Reserva(aereolinea, hora, terminal, puerta);
                Pasajero pasajero = new Pasajero("PASAJERO-" + i, reserva, aeropuerto, quiereVerTienda, quiereComprarTienda, HORA);
                Thread.sleep(new Random().nextInt(1000));
                new Thread(pasajero).start();
            } catch (InterruptedException ex) {
                System.out.println("Error en Main.crearPasajeros " + ex.getMessage() + ".");
            }
        }
    }

    public static void main(String[] args) {
        crearTerminales();
        crearAerolineas();

        ControlTren controlTren = new ControlTren(CAPACIDAD_TREN, CANTIDAD_TERMINALES);
        Tren tren = new Tren(TERMINALES, controlTren);
        Repositor repositor = new Repositor("REPOSITOR", TIENDAS, HORA);
        Aeropuerto aeropuerto = new Aeropuerto(TERMINALES, AEROLINEAS, tren, repositor);

        new Thread(tren).start();
        new Thread(repositor).start();
        new Thread(new Reloj(HORA, aeropuerto)).start();

        crearPasajeros(aeropuerto);
    }
}
