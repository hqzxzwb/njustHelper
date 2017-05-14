package com.zwb.commonlibs.utils;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 提供从cursor获取ArrayList及从Bean获取ContentValues的方法
 *
 * @author zwb
 */
public class DatabaseUtils {
    private static HashMap<Field, GetValue> getFieldMap(Field[] fields, Cursor cursor) {
        HashMap<Field, GetValue> map = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> class1 = field.getType();
            String name = field.getName();
            final int column = cursor.getColumnIndex(name);
            if (column == -1) {
                continue;
            }
            GetValue getValue = null;
            if (class1.equals(short.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(Cursor cursor) {
                        return cursor.getShort(column);
                    }
                };
            } else if (class1.equals(double.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(Cursor cursor) {
                        return cursor.getDouble(column);
                    }
                };
            } else if (class1.equals(int.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(Cursor cursor) {
                        return cursor.getInt(column);
                    }
                };
            } else if (class1.equals(long.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(Cursor cursor) {
                        return cursor.getLong(column);
                    }
                };
            } else if (class1.equals(String.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(Cursor cursor) {
                        return cursor.getString(column);
                    }
                };
            }
            if (getValue != null) {
                map.put(field, getValue);
            }
        }
        return map;
    }

    private static void setFields(HashMap<Field, GetValue> map, Cursor cursor,
                                  Object t) throws Exception {
        for (Entry<Field, GetValue> entry : map.entrySet()) {
            Field key = entry.getKey();
            GetValue val = entry.getValue();
            key.set(t, val.getValue(cursor));
        }
    }

    /**
     * 由Cursor生成List
     *
     * @param cursor 包含数据的Cursor
     * @param class1 生成结果的类型
     * @return 生成的结果
     */
    public static <T> ArrayList<T> parseArray(Cursor cursor, Class<T> class1) {
        Constructor<T> constructor = null;
        try {
            constructor = class1.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        ArrayList<T> list = new ArrayList<>();
        Field[] fields = class1.getDeclaredFields();
        HashMap<Field, GetValue> map = getFieldMap(fields, cursor);
        try {
            while (cursor.moveToNext()) {
                //noinspection ConstantConditions
                T t = constructor.newInstance();
                setFields(map, cursor, t);
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 由Bean生成ContentValues
     *
     * @param values 要生成的ContentValues
     * @param t      含有数据的Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> void putValues(ContentValues values, T t) {
        Class<T> class1 = (Class<T>) t.getClass();
        Field[] fields = class1.getDeclaredFields();
        values.clear();
        try {
            Field mapField = ContentValues.class.getDeclaredField("mValues");
            mapField.setAccessible(true);
            HashMap<String, Object> map = (HashMap<String, Object>) mapField.get(values);
            for (Field field : fields) {
                if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) > 0) continue;
                field.setAccessible(true);
                try {
                    map.put(field.getName(), field.get(t));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private interface GetValue {
        Object getValue(Cursor cursor);
    }
}