package com.ramonimerce.compra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper {

    private Context mCtx = null;
    private DataBaseHelperInternal mDbHelper = null;
    private SQLiteDatabase mDb = null;

    // Nom Base de Dades i Versió
    private static final String NOM_BD = "LLISTA_COMPRA";
    private static final int VERSIO_BD = 1;

    // Taula i Camps
    private static final String TAULA_COMPRA = "compra";
    public static final String ID = "id";
    public static final String ARTICLE = "article";
    public static final String QUANTITAT = "quantitat";
    public static final String COMPRAT = "comprat";

    // SQL de creació de la taula
    private static final String CREACIO_BD_LLISTA_COMPRA =
            "CREATE TABLE " + TAULA_COMPRA + " (" +
                    ID + " integer PRIMARY KEY," +
                    ARTICLE + " text NOT NULL," +
                    QUANTITAT + " integer DEFAULT 1," +
                    COMPRAT + " boolean DEFAULT false)";


    // Constructor
    public DataBaseHelper(Context ctx) {
        this.mCtx = ctx;
    }

    // Mètodes d'obertura i tancament de la base de dades
    public DataBaseHelper open(){
        mDbHelper = new DataBaseHelperInternal(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    // Obtenir tots els elements
    public Cursor getItems() {
        return mDb.query(TAULA_COMPRA, new String[]{ARTICLE, QUANTITAT, COMPRAT}, null, null, null, null, null);
    }

    // Crear element
    public long insertItem(String article, int quantitat, boolean comprat) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(ARTICLE, article);
        initialValues.put(QUANTITAT, quantitat);
        initialValues.put(COMPRAT, comprat);

        return mDb.insert(TAULA_COMPRA, null, initialValues);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Classe privada per el control de la Base de Dades SQLite
    private class DataBaseHelperInternal extends SQLiteOpenHelper{
        public DataBaseHelperInternal(Context context) {
            super(context, NOM_BD, null, VERSIO_BD);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREACIO_BD_LLISTA_COMPRA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
