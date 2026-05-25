package com.example.poupirata2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;

public class BaseDatos extends SQLiteOpenHelper {
    private static final String DB_NAME = "PouPirata.db";
    private static final int DB_VERSION = 5;
    public BaseDatos(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE mascota (" +
                        "id INTEGER PRIMARY KEY, " +
                        "hambre INTEGER, " +
                        "energia INTEGER, " +
                        "felicidad INTEGER, " +
                        "monedas INTEGER, " +
                        "ultimoTiempo INTEGER, " +
                        "durmiendo INTEGER, " +
                        "finTiempoGracia INTEGER)"
        );

        ContentValues valores = new ContentValues();
        valores.put("id", 1);
        valores.put("hambre", 50);
        valores.put("energia", 50);
        valores.put("felicidad", 50);
        valores.put("monedas", 0);
        valores.put("ultimoTiempo", System.currentTimeMillis());
        valores.put("durmiendo", 0);
        valores.put("finTiempoGracia", 0);
        db.insert("mascota", null, valores);

        db.execSQL(
                "CREATE TABLE inventario_comida (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre TEXT UNIQUE, " +
                        "cantidad INTEGER)"
        );

        insertarComidaInicial(db, "Manzana", 1);
        insertarComidaInicial(db, "Hamburguesa", 1);
        insertarComidaInicial(db, "Pizza", 1);
        insertarComidaInicial(db, "Sushi", 1);

        db.execSQL(
                "CREATE TABLE decoraciones_compradas (" +
                        "id TEXT PRIMARY KEY, " +
                        "nombre TEXT, " +
                        "habitacion TEXT, " +
                        "imagen INTEGER, " +
                        "x INTEGER, " +
                        "y INTEGER, " +
                        "ancho INTEGER, " +
                        "alto INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE logros (" +
                        "id TEXT PRIMARY KEY, " +
                        "titulo TEXT, " +
                        "descripcion TEXT, " +
                        "desbloqueado INTEGER, " +
                        "progreso INTEGER, " +
                        "meta INTEGER)"
        );

    }

    private void insertarComidaInicial(SQLiteDatabase db, String nombre, int cantidad) {
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("cantidad", cantidad);
        db.insert("inventario_comida", null, valores);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS inventario_comida (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "nombre TEXT UNIQUE, " +
                            "cantidad INTEGER)"
            );
            insertarComidaSiNoExiste(db, "Manzana", 1);
            insertarComidaSiNoExiste(db, "Hamburguesa", 1);
            insertarComidaSiNoExiste(db, "Pizza", 1);
            insertarComidaSiNoExiste(db, "Sushi", 1);
        }

        if (oldVersion < 3) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS decoraciones_compradas (" +
                            "id TEXT PRIMARY KEY, " +
                            "nombre TEXT, " +
                            "habitacion TEXT, " +
                            "imagen INTEGER, " +
                            "x INTEGER, " +
                            "y INTEGER, " +
                            "ancho INTEGER, " +
                            "alto INTEGER)"
            );
        }

        if (oldVersion < 4) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS logros (" +
                            "id TEXT PRIMARY KEY, " +
                            "titulo TEXT, " +
                            "descripcion TEXT, " +
                            "desbloqueado INTEGER, " +
                            "progreso INTEGER, " +
                            "meta INTEGER)"
            );
        }

        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE mascota ADD COLUMN finTiempoGracia INTEGER DEFAULT 0");
        }

    }

    public Cursor obtenerInventarioComida() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT nombre, cantidad FROM inventario_comida",
                null
        );
    }

    public void actualizarCantidadComida(String nombre, int cantidad) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("cantidad", cantidad);
        db.update(
                "inventario_comida",
                valores,
                "nombre = ?",
                new String[]{nombre}
        );
    }

    private void insertarComidaSiNoExiste(SQLiteDatabase db, String nombre, int cantidad) {
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("cantidad", cantidad);
        db.insertWithOnConflict(
                "inventario_comida",
                null,
                valores,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    public Cursor obtenerMascota() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM mascota WHERE id = 1", null);
    }
    public void guardarMascota(
            int hambre,
            int energia,
            int felicidad,
            int monedas,
            long ultimoTiempo,
            boolean durmiendo,
            long finTiempoGracia
    ) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("id", 1);
        valores.put("hambre", hambre);
        valores.put("energia", energia);
        valores.put("felicidad", felicidad);
        valores.put("monedas", monedas);
        valores.put("ultimoTiempo", ultimoTiempo);
        valores.put("durmiendo", durmiendo ? 1 : 0);
        valores.put("finTiempoGracia", finTiempoGracia);

        db.insertWithOnConflict(
                "mascota",
                null,
                valores,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public void guardarInventarioComida(ArrayList<Food> inventario) {
        SQLiteDatabase db = getWritableDatabase();
        for (Food food : inventario) {
            ContentValues valores = new ContentValues();
            valores.put("nombre", food.getNombre());
            valores.put("cantidad", food.getCantidad());
            db.insertWithOnConflict(
                    "inventario_comida",
                    null,
                    valores,
                    SQLiteDatabase.CONFLICT_REPLACE
            );
        }
    }

    public void actualizarUltimoTiempo(long ultimoTiempo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("ultimoTiempo", ultimoTiempo);
        db.update("mascota", valores, "id = 1", null);
    }
    public void actualizarDurmiendo(boolean durmiendo, long ultimoTiempo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("durmiendo", durmiendo ? 1 : 0);
        valores.put("ultimoTiempo", ultimoTiempo);

        db.update("mascota", valores, "id = 1", null);
    }

    public void guardarDecoracionComprada(Decoracion decoracion) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("id", decoracion.getId());
        valores.put("nombre", decoracion.getNombre());
        valores.put("habitacion", decoracion.getHabitacion());
        valores.put("imagen", decoracion.getImagen());
        valores.put("x", decoracion.getX());
        valores.put("y", decoracion.getY());
        valores.put("ancho", decoracion.getAncho());
        valores.put("alto", decoracion.getAlto());
        db.insertWithOnConflict(
                "decoraciones_compradas",
                null,
                valores,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public boolean decoracionComprada(String idDecoracion) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM decoraciones_compradas WHERE id = ?",
                new String[]{idDecoracion}
        );
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }
    public Cursor obtenerDecoracionesCompradas() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM decoraciones_compradas",
                null
        );
    }

    public void guardarLogro(Logro logro) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("id", logro.getId());
        valores.put("titulo", logro.getTitulo());
        valores.put("descripcion", logro.getDescripcion());
        valores.put("desbloqueado", logro.isDesbloqueado() ? 1 : 0);
        valores.put("progreso", logro.getProgreso());
        valores.put("meta", logro.getMeta());

        db.insertWithOnConflict(
                "logros",
                null,
                valores,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public boolean logroDesbloqueado(String idLogro) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT desbloqueado FROM logros WHERE id = ?",
                new String[]{idLogro}
        );
        boolean desbloqueado = false;
        if (cursor.moveToFirst()) {
            desbloqueado = cursor.getInt(
                    cursor.getColumnIndexOrThrow("desbloqueado")
            ) == 1;
        }
        cursor.close();
        return desbloqueado;
    }

    public void desbloquearLogro(String idLogro) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("desbloqueado", 1);
        db.update(
                "logros",
                valores,
                "id = ?",
                new String[]{idLogro}
        );
    }
    public void actualizarProgresoLogro(String idLogro, int progreso) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("progreso", progreso);
        db.update(
                "logros",
                valores,
                "id = ?",
                new String[]{idLogro}
        );
    }

    public Cursor obtenerLogros() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM logros",
                null
        );
    }


}