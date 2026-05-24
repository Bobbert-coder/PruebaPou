package com.example.poupirata2;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.ArrayList;

public class LogrosManager {

    private static final String PREFS_LOGROS = "logros_poupirata";

    public static final String PRIMER_ALIMENTO = "primer_alimento";
    public static final String PRIMER_JUEGO = "primer_juego";
    public static final String RICO = "rico";
    public static final String MASCOTA_FELIZ = "mascota_feliz";
    public static final String MASCOTA_LLENA = "mascota_llena";
    public static final String ENERGIA_ALTA = "energia_alta";
    public static final String BUEN_PUNTAJE = "buen_puntaje";

    public static final String COMELON = "comelon";

    public static ArrayList<Logro> obtenerLogros(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_LOGROS, Context.MODE_PRIVATE);
        SharedPreferences stats = context.getSharedPreferences("estadisticas", Context.MODE_PRIVATE);
        int vecesComidas = stats.getInt("veces_comidas", 0);

        ArrayList<Logro> logros = new ArrayList<>();

        logros.add(new Logro(
                PRIMER_ALIMENTO,
                "Primer alimento",
                "Alimenta a tu mascota por primera vez.",
                prefs.getBoolean(PRIMER_ALIMENTO, false),
                prefs.getBoolean(PRIMER_ALIMENTO, false) ? 1:0,
                1
        ));

        logros.add(new Logro(
                PRIMER_JUEGO,
                "Jugador principiante",
                "Juega el minijuego por primera vez.",
                prefs.getBoolean(PRIMER_JUEGO, false),
                prefs.getBoolean(PRIMER_JUEGO, false) ? 1:0,
                1
        ));

        logros.add(new Logro(
                RICO,
                "Rico",
                "Consigue 50 monedas.",
                prefs.getBoolean(RICO, false),
                prefs.getBoolean(RICO, false) ? 1:0,
                1
        ));

        logros.add(new Logro(
                MASCOTA_FELIZ,
                "Mascota feliz",
                "Sube la felicidad a 800 o más.",
                prefs.getBoolean(MASCOTA_FELIZ, false),
                prefs.getBoolean(MASCOTA_FELIZ, false) ? 1:0,
                1
        ));

        logros.add(new Logro(
                MASCOTA_LLENA,
                "Mascota llena",
                "Sube el hambre a 900 o más.",
                prefs.getBoolean(MASCOTA_LLENA, false),
                prefs.getBoolean(MASCOTA_LLENA, false) ? 1:0,
                1
        ));

        logros.add(new Logro(
                COMELON,
            "Comelon",
            "Alimenta a tu mascota 50 veces",
            prefs.getBoolean(COMELON, false),
                vecesComidas,
                50
        ));

        logros.add(new Logro(
                ENERGIA_ALTA,
                "Energía al máximo",
                "Sube la energía a 800 o más.",
                prefs.getBoolean(ENERGIA_ALTA, false),
                prefs.getBoolean(ENERGIA_ALTA, false) ? 1:0,
                1
        ));

        logros.add(new Logro(
                BUEN_PUNTAJE,
                "Buen puntaje",
                "Consigue 20 puntos en el minijuego.",
                prefs.getBoolean(BUEN_PUNTAJE, false),
                prefs.getBoolean(BUEN_PUNTAJE, false) ? 1:0,
                1
        ));

        return logros;
    }

    public static void desbloquear(Context context, String idLogro) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_LOGROS, Context.MODE_PRIVATE);

        boolean yaDesbloqueado = prefs.getBoolean(idLogro, false);

        if (!yaDesbloqueado) {
            prefs.edit().putBoolean(idLogro, true).apply();

            String titulo = obtenerTituloPorId(idLogro);

            Toast.makeText(
                    context,
                    "🏆 Logro desbloqueado: " + titulo,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public static void verificarLogros(Context context, Mascota mascota) {
        if (GameData.monedas >= 50) {
            desbloquear(context, RICO);
        }

        if (mascota.getFelicidad() >= 800) {
            desbloquear(context, MASCOTA_FELIZ);
        }

        if (mascota.getHambre() >= 900) {
            desbloquear(context, MASCOTA_LLENA);
        }

        if (mascota.getEnergia() >= 800) {
            desbloquear(context, ENERGIA_ALTA);
        }
    }

    public static void verificarPuntaje(Context context, int score) {
        if (score >= 20) {
            desbloquear(context, BUEN_PUNTAJE);
        }
    }

    private static String obtenerTituloPorId(String idLogro) {
        switch (idLogro) {
            case PRIMER_ALIMENTO:
                return "Primer alimento";
            case PRIMER_JUEGO:
                return "Jugador principiante";
            case RICO:
                return "Rico";
            case MASCOTA_FELIZ:
                return "Mascota feliz";
            case MASCOTA_LLENA:
                return "Mascota llena";
            case ENERGIA_ALTA:
                return "Energía al máximo";
            case BUEN_PUNTAJE:
                return "Buen puntaje";
            case COMELON:
                return "Comelon";
            default:
                return "Nuevo logro";
        }
    }
}