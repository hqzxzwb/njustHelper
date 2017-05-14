package com.njust.helper.course;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class CourseOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "course.db";
    private static final int VERSION = 4;

    public CourseOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table info (id varchar primary key, " +
                "name varchar, " +
                "teacher varchar)");
        db.execSQL("create table loc (id varchar," +
                "sec1 integer, " +
                "sec2 integer, " +
                "day integer, " +
                "classroom varchar, " +
                "week1 varchar, " +
                "week2 varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
            case 2:
            case 3:
                db.execSQL("drop table if exists detailclass");
                onCreate(db);
            default:
                break;
        }
    }
}
