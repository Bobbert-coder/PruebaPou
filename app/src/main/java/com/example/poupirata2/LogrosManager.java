package com.example.poupirata2;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

public class LogrosManager {

    public static final String PRIMER_ALIMENTO = "primer_alimento";
    public static final String COMELON = "comelon";
    public static final String PRIMER_JUEGO = "primer_juego";
    public static final String RICO = "rico";
    public static final String MASCOTA_FELIZ = "mascota_feliz";
    public static final String MASCOTA_LLENA = "mascota_llena";
    public static final String ENERGIA_ALTA = "energia_alta";
    public static final String BUEN_PUNTAJE = "buen_puntaje";

    public static ArrayList<Logro> obtenerLogros(Context context) {
        BaseDatos baseDatos = new BaseDatos(context);
        insertarLogrosBaseSiNoExisten(baseDatos);
        ArrayList<Logro> logros = new ArrayList<>();
        Cursor cursor = baseDatos.obtenerLogros();
        while (cursor.moveToNext()) {
            Logro logro = new Logro(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("desbloqueado")) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow("progreso")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("meta"))
            );
            logros.add(logro);
        }
        cursor.close();
        return logros;
    }

    private static void insertarLogrosBaseSiNoExisten(BaseDatos baseDatos) {
        insertarLogroBase(baseDatos, new Logro(
                PRIMER_ALIMENTO,
                "Primer alimento",
                "Alimenta a tu mascota por primera vez.",
                baseDatos.logroDesbloqueado(PRIMER_ALIMENTO),
                baseDatos.logroDesbloqueado(PRIMER_ALIMENTO) ? 1 : 0,
                1
        ));

        insertarLogroBase(baseDatos, new Logro(
                COMELON,
                "Comelón",
                "Alimenta a tu mascota 50 veces.",
                baseDatos.logroDesbloqueado(COMELON),
                0,
                50
        ));

        insertarLogroBase(baseDatos, new Logro(
                PRIMER_JUEGO,
                "Jugador principiante",
                "Juega el minijuego por primera vez.",
                baseDatos.logroDesbloqueado(PRIMER_JUEGO),
                baseDatos.logroDesbloqueado(PRIMER_JUEGO) ? 1 : 0,
                1
        ));

        insertarLogroBase(baseDatos, new Logro(
                RICO,
                "Rico",
                "Consigue 50 monedas.",
                baseDatos.logroDesbloqueado(RICO),
                baseDatos.logroDesbloqueado(RICO) ? 1 : 0,
                1
        ));

        insertarLogroBase(baseDatos, new Logro(
                MASCOTA_FELIZ,
                "Mascota feliz",
                "Sube la felicidad a 800 o más.",
                baseDatos.logroDesbloqueado(MASCOTA_FELIZ),
                baseDatos.logroDesbloqueado(MASCOTA_FELIZ) ? 1 : 0,
                1
        ));

        insertarLogroBase(baseDatos, new Logro(
                MASCOTA_LLENA,
                "Mascota llena",
                "Sube el hambre a 900 o más.",
                baseDatos.logroDesbloqueado(MASCOTA_LLENA),
                baseDatos.logroDesbloqueado(MASCOTA_LLENA) ? 1 : 0,
                1
        ));

        insertarLogroBase(baseDatos, new Logro(
                ENERGIA_ALTA,
                "Energía al máximo",
                "Sube la energía a 800 o más.",
                baseDatos.logroDesbloqueado(ENERGIA_ALTA),
                baseDatos.logroDesbloqueado(ENERGIA_ALTA) ? 1 : 0,
                1
        ));

        insertarLogroBase(baseDatos, new Logro(
                BUEN_PUNTAJE,
                "Buen puntaje",
                "Consigue 20 puntos en el minijuego.",
                baseDatos.logroDesbloqueado(BUEN_PUNTAJE),
                baseDatos.logroDesbloqueado(BUEN_PUNTAJE) ? 1 : 0,
                1
        ));
    }

    private static void insertarLogroBase(BaseDatos baseDatos, Logro logroNuevo) {
        ArrayList<Logro> logros = new ArrayList<>();
        Cursor cursor = baseDatos.obtenerLogros();
        boolean existe = false;
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            if (id.equals(logroNuevo.getId())) {
                existe = true;
                break;
            }
        }
        cursor.close();
        if (!existe) {
            baseDatos.guardarLogro(logroNuevo);
        }
    }

    public static void desbloquear(Context context, String idLogro) {
        BaseDatos baseDatos = new BaseDatos(context);
        insertarLogrosBaseSiNoExisten(baseDatos);
        boolean yaDesbloqueado = baseDatos.logroDesbloqueado(idLogro);
        if (!yaDesbloqueado) {
            baseDatos.desbloquearLogro(idLogro);
            Toast.makeText(
                    context,
                    "🏆 Logro desbloqueado: " + obtenerTituloPorId(idLogro),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public static void actualizarProgreso(Context context, String idLogro, int progreso) {
        BaseDatos baseDatos = new BaseDatos(context);
        insertarLogrosBaseSiNoExisten(baseDatos);
        baseDatos.actualizarProgresoLogro(idLogro, progreso);
    }

    public static void verificarLogros(Context context, Mascota mascota) {
        if (GameData.monedas >= 50) {
            desbloquear(context, RICO);
            actualizarProgreso(context, RICO, 1);
        }
        if (mascota.getFelicidad() >= 800) {
            desbloquear(context, MASCOTA_FELIZ);
            actualizarProgreso(context, MASCOTA_FELIZ, 1);
        }
        if (mascota.getHambre() >= 900) {
            desbloquear(context, MASCOTA_LLENA);
            actualizarProgreso(context, MASCOTA_LLENA, 1);
        }
        if (mascota.getEnergia() >= 800) {
            desbloquear(context, ENERGIA_ALTA);
            actualizarProgreso(context, ENERGIA_ALTA, 1);
        }
    }
    public static void verificarPuntaje(Context context, int score) {
        if (score >= 20) {
            desbloquear(context, BUEN_PUNTAJE);
            actualizarProgreso(context, BUEN_PUNTAJE, 1);
        }
    }
    private static String obtenerTituloPorId(String idLogro) {
        switch (idLogro) {
            case PRIMER_ALIMENTO:
                return "Primer alimento";
            case COMELON:
                return "Comelón";
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
            default:
                return "Nuevo logro";
        }
    }
}