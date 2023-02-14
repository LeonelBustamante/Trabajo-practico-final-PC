package clases;

import main.PrintColor;

/**
 * Clase que representa a un aeropuerto
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Aeropuerto {

    private final Aerolinea[] aerolineas;
    private final Terminal[] terminales;
    private final Tren tren;
    private final Repositor repositor;
    private boolean estaAbierto;

    public Aeropuerto(Terminal[] terminales, Aerolinea[] aerolineas, Tren tren, Repositor repositor) {
        this.terminales = terminales;
        this.aerolineas = aerolineas;
        this.tren = tren;
        this.estaAbierto = false;
        this.repositor = repositor;
    }

    // Metodos para Pasajero ---------------------------------------------------
    /**
     * método sincroniza el ingreso de los pasajeros al aeropuerto y los hace
     * esperar hasta que el reloj notifique que el aeropuerto abrió. Si el
     * aeropuerto está abierto, el pasajero se dirige al puesto de atención.
     *
     * @param pasajero pasajero que desea ingresar
     */
    public synchronized void ingresarAeropuerto(Pasajero pasajero) {
        // Se utiliza un lock implicito para el objeto this, esto para que
        // los pasajeros no puedan ingresar al aeropuerto mientras el reloj
        // no notifique que el aeropuerto abrió.
        try {
            while (!estaAbierto) {
                System.out.println(PrintColor.ANSI_BLACK + "[CLASE AEROPUERTO]: " + pasajero + " espera a que el aeropuerto abra." + PrintColor.ANSI_RESET);
                wait();
            }
            System.out.println(PrintColor.ANSI_BLACK + "[CLASE AEROPUERTO]" + pasajero + " se dirige al puesto de atencion." + PrintColor.ANSI_RESET);
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            System.out.println("Error en Aeropuerto.ingresarAeropuerto " + ex.getMessage());
        }
    }

    // Metodos para Reloj ------------------------------------------------------
    /**
     * Metodo sincronizado para que los pasajeros ingresen al aeropuerto y
     * esperen a que el reloj los notifique que el aeropuerto abrio
     */
    public synchronized void comenzarHorarioAtencion() {
        // Se liberan los pasajeros que estaban esperando a que el aeropuerto abriera
        System.out.println( PrintColor.ANSI_BLACK + "[CLASE AEROPUERTO] Comenzando horario de atencion." + PrintColor.ANSI_RESET);
        estaAbierto = true;
        notifyAll();
    }

    /**
     * Método sincronizado que notifica a todos los pasajeros que el aeropuerto
     * cerró y cambia el estado del aeropuerto a cerrado.
     */
    public synchronized void terminarHorarioAtencion() {
        estaAbierto = false;
        System.out.println(PrintColor.ANSI_BLACK + "[CLASE AEROPUERTO] Termino horario de atencion." + PrintColor.ANSI_RESET);
    }

    // Metodos de la clase -----------------------------------------------------
    public Aerolinea[] getAerolineas() {
        return aerolineas;
    }

    public Terminal[] getTerminales() {
        return terminales;
    }

    public Tren getTren() {
        return tren;
    }

    public Repositor getRepositor() {
        return repositor;
    }

    public boolean isEstaAbierto() {
        return estaAbierto;
    }

}
