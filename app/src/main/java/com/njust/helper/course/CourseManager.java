package com.njust.helper.course;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.njust.helper.model.Course;
import com.njust.helper.model.CourseInfo;
import com.njust.helper.model.CourseLoc;
import com.zwb.commonlibs.utils.DatabaseUtils;

import java.util.List;

public class CourseManager {
    private static CourseManager instance;
    private final CourseOpenHelper helper;

    private CourseManager(Context context) {
        helper = new CourseOpenHelper(context);
    }

    public static CourseManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CourseManager.class) {
                if (instance == null) {
                    instance = new CourseManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public synchronized void clear() {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete("info", null, null);
        database.delete("loc", null, null);
    }

    public synchronized void add(List<CourseInfo> infos, List<CourseLoc> locs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.beginTransaction();
        ContentValues values = new ContentValues();
        for (CourseInfo info : infos) {
            DatabaseUtils.putValues(values, info);
            database.insert("info", null, values);
        }
        for (CourseLoc loc : locs) {
            DatabaseUtils.putValues(values, loc);
            database.insert("loc", null, values);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public synchronized List<Course> getCourses() {
        SQLiteDatabase database = helper.getReadableDatabase();
        String[] columns = {"a.id", "a.name", "a.teacher", "b.classroom", "b.week1", "b.week2", "b.sec1", "b.sec2", "b.day"};
        Cursor cursor = database.query("info as a,loc as b", columns, "a.id=b.id", null, null, null, "b.day,b.sec1");
        List<Course> list = DatabaseUtils.parseArray(cursor, Course.class);
        cursor.close();
        return list;
    }

    public synchronized List<Course> getCourses(int dayOfSemester) {
        SQLiteDatabase database = helper.getReadableDatabase();
        int week = dayOfSemester / 7;
        int dayOfWeek = dayOfSemester % 7;
        String[] columns = {"a.id", "a.name", "a.teacher", "b.classroom", "b.week1", "b.week2", "b.sec1", "b.sec2", "b.day"};
        String[] args = {String.valueOf(dayOfWeek), "% " + ++week + " %"};
        Cursor cursor = database.query("info as a, loc as b", columns, "a.id=b.id and b.day=? and b.week2 like ?", args, null, null, "sec1");
        List<Course> list = DatabaseUtils.parseArray(cursor, Course.class);
        cursor.close();
        return list;
    }

    public synchronized int countCourses(int day) {
        SQLiteDatabase database = helper.getReadableDatabase();
        int week = day / 7;
        int dayOfWeek = day % 7;
        String[] columns = {"count(*)"};
        String[] args = {String.valueOf(dayOfWeek), "% " + ++week + " %"};
        Cursor cursor = database.query("info as a, loc as b", columns, "a.id=b.id and b.day=? and b.week2 like ?", args, null, null, "sec1");
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}
