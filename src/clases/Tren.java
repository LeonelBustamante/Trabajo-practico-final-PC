package clases;

import main.PrintColor;
import java.util.concurrent.Semaphore;

/**
 * Clase que representa al tren
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Tren implements Runnable {

    private static final int PRIMER_TERMINAL = 65;

    private final ControlTren controlTren;
    private final Terminal[] terminales;
    private final Semaphore[] semaforosTerminal;

    public Tren(Terminal[] terminales, ControlTren controlTren) {
        this.terminales = terminales;
        this.controlTren = controlTren;
        this.semaforosTerminal = new Semaphore[terminales.length];
        inicializarSemaforos();
    }

    private void inicializarSemaforos() {
        // Se inicializan los semaforos en 0 para que el tren no pueda pasar
        for (int i = 0; i < semaforosTerminal.length; i++) {
            this.semaforosTerminal[i] = new Semaphore(0);
        }
    }

    @Override
    /**
     * método representa el funcionamiento del tre, Contiene un bucle infinito
     * que simula el recorrido del tren entre las terminales y la primer parada.
     * En cada terminal, el tren muestra un mensaje de llegada, baja a los
     * pasajeros y espera un tiempo antes de continuar con su recorrido.
     * Finalmente, el tren llega a la primer parada y muestra un mensaje.
     */
    public void run() {
        // Funcionamiento del tren
        while (true) {
            char letraTerminal;
            try {
                controlTren.comenzarViaje();
                Thread.sleep(1000);
                for (Terminal terminal : terminales) {
                    letraTerminal = terminal.getLetra();
                    System.out.println(PrintColor.ANSI_PURPLE_BACKGROUND + "[CLASE TREN]  Llego a la terminal " + letraTerminal + "." + PrintColor.ANSI_RESET);
                    controlTren.arriboTerminal(letraTerminal, semaforosTerminal);
                    Thread.sleep(2500);
                }
                controlTren.volverPrimerParada();
                Thread.sleep(terminales.length * 2000);
                controlTren.llegoOrigen();
                System.out.println(PrintColor.ANSI_PURPLE_BACKGROUND + "[CLASE TREN] Llego a la primer terminal." + PrintColor.ANSI_RESET);
                controlTren.mostrarMensajes();
            } catch (InterruptedException ex) {
                System.out.println("Error en Tren.run: " + ex.getMessage());
            }
        }
    }

    // Metodos para pasajero ---------------------------------------------------
    /**
     * método permite a un pasajero solicitar traslado a una terminal
     * específica, Recibe como parámetro la letra de la terminal seleccionada y
     * verifica si corresponde a una de las terminales por recorrer. Si
     * corresponde, se libera el semáforo de la terminal para permitir al tren
     * detenerse y bajar a los pasajeros.
     *
     * @param terminalPasajero terminal solicitada por el pasajero
     */
    public void solicitarViajeATerminal(char terminalPasajero) {
        // metodo que utiliza el seemaforo para que se bajen los pasajeros en la terminal que corresponda
        int cantTotal = terminales.length + PRIMER_TERMINAL;
        int indice;
        for (int terminalActual = PRIMER_TERMINAL; terminalActual < cantTotal; terminalActual++) {
            if (terminalPasajero == ((char) terminalActual)) {
                try {
                    indice = terminalActual - PRIMER_TERMINAL;
                    semaforosTerminal[indice].acquire();
                } catch (InterruptedException ex) {
                    System.out.println("Error en Tren.solicitarViajeATerminal: " + ex.getMessage());
                }
            }
        }
    }

    // Metodos de la clase -----------------------------------------------------
    public ControlTren getControlTren() {
        return controlTren;
    }
}
