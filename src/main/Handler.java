package main;

public class Handler {

    public static String formatoHora(int hora) {
        String res = "" + hora;
        if (hora < 10) {
            res = "0" + hora;
        }
        return res + ":00";
    }

}
