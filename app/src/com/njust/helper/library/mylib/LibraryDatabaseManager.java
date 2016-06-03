package com.njust.helper.library.mylib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.njust.helper.model.LibCollectItem;
import com.zwb.commonlibs.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;

public class LibraryDatabaseManager {
    private static LibraryDatabaseManager instance;
    private final LibraryHelper helper;

    private LibraryDatabaseManager(Context context) {
        helper = new LibraryHelper(context);
    }

    public static LibraryDatabaseManager getInstance(Context context) {
        if (instance == null) {
            synchronized (LibraryDatabaseManager.class) {
                if (instance == null) {
                    instance = new LibraryDatabaseManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public boolean addCollect(String id, String name, String code) {
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("code", code);
        values.put("time", System.currentTimeMillis());
        return database.insert("collection", null, values) > -1;
    }

    public boolean checkCollect(String id) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query("collection", new String[]{"id"},
                "id=?", new String[]{id}, null, null, null);
        boolean b = cursor.moveToNext();
        cursor.close();
        return b;
    }

    public void removeCollect(String id) {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete("collection", "id=?", new String[]{id});
    }

    public void removeCollects(List<String> ids) {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.beginTransaction();
        for (String id : ids)
            database.delete("collection", "id=?", new String[]{id});
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public ArrayList<LibCollectItem> findCollect() {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query("collection", new String[]{"id",
                "name", "time", "code"}, null, null, null, null, "time desc");
        ArrayList<LibCollectItem> list = DatabaseUtils.parseArray(cursor,
                LibCollectItem.class);
        cursor.close();
        return list;
    }
}
