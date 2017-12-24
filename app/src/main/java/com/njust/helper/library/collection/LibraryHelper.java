package com.njust.helper.library.collection;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Legacy data of library collection.
 */
class LibraryHelper extends SQLiteOpenHelper {
    static final String DB_NAME = "library.db";
    private static final int VERSION = 8;

    public LibraryHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table collection (id varchar primary key,name varchar,time long,code varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
            case 2:
            case 3:
                db.execSQL("drop table if exists mylib");
            case 4:
                db.execSQL("create table collection (id varchar primary key,name varchar,time long)");
            case 5:
                db.execSQL("drop table if exists borrow");
            case 6:
                db.execSQL("drop table if exists history");
            case 7:
                db.execSQL("alter table collection add column code varchar");
        }
    }

    List<Object[]> getBindArgs() {
        SQLiteDatabase database = getReadableDatabase();
        List<Object[]> result = new ArrayList<>();
        try (Cursor cursor = database.query("collection",
                new String[]{"*"},
                null,
                null,
                null,
                null,
                null)) {
            int idColumn = cursor.getColumnIndex("id");
            int nameColumn = cursor.getColumnIndex("name");
            int codeColumn = cursor.getColumnIndex("code");
            int timeColumn = cursor.getColumnIndex("time");
            while (cursor.moveToNext()) {
                Object[] args = {
                        ensureNonNull(cursor.getString(idColumn)),
                        ensureNonNull(cursor.getString(nameColumn)),
                        ensureNonNull(cursor.getString(codeColumn)),
                        cursor.getLong(timeColumn)
                };
                result.add(args);
            }
        }
        return result;
    }

    private String ensureNonNull(String s) {
        return s != null ? s : "";
    }
}
