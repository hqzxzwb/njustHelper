package com.njust.helper.library.mylib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class LibraryHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "library.db";
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
}