package clases;

import java.util.concurrent.atomic.AtomicInteger;

import main.Handler;
import main.PrintColor;

/**
 * Clase que representa al reloj del aeropuerto
 *
 * @author Alexis Leonel Bustamante Hecht - FAI2355
 */
public class Reloj implements Runnable {

    private static final int INICIO_ATENCION = 6;
    private static final int FIN_ATENCION = 22;
    private static final int NUEVO_DIA = 24;

    private final AtomicInteger hora;
    private final Aeropuerto aeropuerto;

    public Reloj(AtomicInteger hora, Aeropuerto aeropuerto) {
        this.hora = hora;
        this.aeropuerto = aeropuerto;
    }

    @Override
    /**
     * método representa la ejecución del hilo reloj, el cual controla el avance
     * del tiempo y notifica a las terminales y el repositor de los cambios en
     * la hora actual. Además, realiza acciones específicas según la hora
     * actual, como el inicio y fin del horario de atención del aeropuerto y el
     * comienzo de un nuevo día.
     */
    public void run() {
        Terminal[] terminales = aeropuerto.getTerminales();
        Repositor repositor = aeropuerto.getRepositor();

        while (true) {
            try {
                Thread.sleep(4000);
                hora.addAndGet(1);
                System.out.println(PrintColor.ANSI_YELLOW + "[CLASE RELOJ] Hora actual: "
                        + Handler.formatoHora(hora.get()) + " hs"+ PrintColor.ANSI_RESET);

                switch (hora.get()) {
                    case INICIO_ATENCION -> {
                        aeropuerto.comenzarHorarioAtencion();
                        Thread.sleep(1000);
                    }
                    case FIN_ATENCION -> {
                        aeropuerto.terminarHorarioAtencion();
                        Thread.sleep(1000);
                    }
                    case NUEVO_DIA -> {
                        hora.set(0);
                        System.out.println(PrintColor.ANSI_YELLOW + "[CLASE RELOJ] Comienza un nuevo día"+ PrintColor.ANSI_RESET);
                    }
                    default -> {
                    }
                }

                notificarTerminalesHora(terminales);
                repositor.notificarCambioHora();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Error en Reloj.run: " + ex.getMessage());
            }
        }
    }

    /**
     * método recibe un arreglo de terminales y les notifica que han pasado una
     * hora, invocando su método correspondiente "pasarHora".
     *
     * @param terminales terminales a las cuales notificar
     */
    private void notificarTerminalesHora(Terminal[] terminales) {
        for (Terminal terminal : terminales) {
            terminal.pasarHora();
        }
    }
}
