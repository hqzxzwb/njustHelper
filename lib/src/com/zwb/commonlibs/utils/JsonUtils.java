package com.zwb.commonlibs.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * 提供json解析，通过反射建立成员变量名对应关系减少代码量
 *
 * @author zwb
 */
public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();

    private static HashMap<Field, GetValue> getFieldMap(Field[] fields) {
        HashMap<Field, GetValue> map = new HashMap<>();
        for (Field field : fields) {
            if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) > 0) {
                continue;
            }
            field.setAccessible(true);
            Class<?> class1 = field.getType();
            final String name = field.getName();
            GetValue getValue = null;
            if (class1.equals(boolean.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(JSONObject object) throws Exception {
                        return object.getBoolean(name);
                    }
                };
            } else if (class1.equals(double.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(JSONObject object) throws Exception {
                        return object.getDouble(name);
                    }
                };
            } else if (class1.equals(int.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(JSONObject object) throws Exception {
                        return object.getInt(name);
                    }
                };
            } else if (class1.equals(long.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(JSONObject object) throws Exception {
                        return object.getLong(name);
                    }
                };
            } else if (class1.equals(String.class)) {
                getValue = new GetValue() {
                    @Override
                    public Object getValue(JSONObject object) throws Exception {
                        String string = object.getString(name);
                        if (string.equals("null")) {
                            return "";
                        }
                        return string;
                    }
                };
            }
            if (getValue != null) {
                map.put(field, getValue);
            }
        }
        return map;
    }

    /**
     * 解析bean
     *
     * @param json   json字符串
     * @param class1 需要解析的类
     * @return 解析结果
     * @throws Exception
     */
    public static <T> T parseBean(String json, Class<T> class1) throws Exception {
        return parseBean(new JSONObject(json), class1);
    }

    /**
     * 解析bean
     *
     * @param jsonObject 待解析数据
     * @param class1     需要解析的类
     * @return 解析结果
     * @throws Exception
     */
    public static <T> T parseBean(JSONObject jsonObject, Class<T> class1) throws Exception {
        T t = class1.getConstructor().newInstance();
        applyJsonToBean(jsonObject, t);
        return t;
    }

    private static void setField(HashMap<Field, GetValue> map, JSONObject object, Object t) throws Exception {
        for (Entry<Field, GetValue> entry : map.entrySet()) {
            Field key = entry.getKey();
            GetValue val = entry.getValue();
            try {
                key.set(t, val.getValue(object));
            } catch (Exception e) {
//                e.printStackTrace();
                LogUtils.w(TAG, key.getName() + "赋值失败\n" +
                        e.getClass().getSimpleName() + ":" + e.getMessage());
            }
        }
    }

    /**
     * 解析ArrayList
     *
     * @param json   json字符串
     * @param class1 需要解析的类
     * @return 解析结果
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> parseArray(String json, Class<T> class1) throws Exception {
        return parseArray(new JSONArray(json), class1);
    }

    /**
     * 解析ArrayList
     *
     * @param jsonArray 要解析的数据
     * @param class1    需要解析的类
     * @return 解析结果
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> parseArray(JSONArray jsonArray, Class<T> class1) throws Exception {
        ArrayList<T> list = new ArrayList<>(jsonArray.length());
        if (class1.isPrimitive() || class1.equals(String.class)) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add((T) jsonArray.get(i));
            }
        } else {
            HashMap<Field, GetValue> hashMap = getFieldMap(class1.getDeclaredFields());
            for (int i = 0; i < jsonArray.length(); i++) {
                T t = class1.getConstructor().newInstance();
                setField(hashMap, jsonArray.getJSONObject(i), t);
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 将jsonObject中的数据写入Bean
     *
     * @param jsonObject 数据
     * @param o          Bean
     * @throws Exception
     */
    public static void applyJsonToBean(JSONObject jsonObject, Object o) throws Exception {
        setField(getFieldMap(o.getClass().getDeclaredFields()), jsonObject, o);
    }

    /**
     * object转化为json，可迭代对象转换成jsonarray
     *
     * @param o 需要转化的对象
     * @return 转化后的json字符串，其中包含一个类名称字段
     */
    public static String jsonEncode(Object o) {
        return encodeJson(o).toString();
    }

    private static Object encodeJson(Object o) {
        Class<?> c = o.getClass();
        if (Iterable.class.isAssignableFrom(c)) {
            JSONArray jsonArray = new JSONArray();
            Iterable<?> iterable = (Iterable<?>) o;
            for (Object o1 : iterable) {
                if (o1 == null) {
                    jsonArray.put("null");
                } else if (o1.getClass().isPrimitive() || o1.getClass().equals(String.class)) {
                    jsonArray.put(o1);
                } else {
                    jsonArray.put(encodeJson(o1));
                }
            }
            return jsonArray;
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("className", o.getClass().getName());
                for (Field field : o.getClass().getDeclaredFields()) {
                    if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) > 0) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object o1 = field.get(o);
                    if (o1 == null) {
                        jsonObject.put(field.getName(), "null");
                    } else if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                        jsonObject.put(field.getName(), field.get(o));
                    } else {
                        jsonObject.put(field.getName(), encodeJson(field.get(o)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    /**
     * 解析一个带有类名的json字符串，解析为一个object或者一个list
     *
     * @param string 要解析的字符串
     * @return 解析得到的结果
     * @throws Exception 解析出错
     * @see #jsonEncode(Object)
     */
    public static Object jsonDecode(String string) throws Exception {
        try {
            JSONObject jsonObject = new JSONObject(string);
            Class<?> clazz = Class.forName(jsonObject.getString("className"));
            Object o = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if ((field.getModifiers() & (Modifier.STATIC | Modifier.TRANSIENT)) > 0) {
                    continue;
                }
                field.setAccessible(true);
                if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
                    field.set(o, jsonObject.get(field.getName()));
                } else {
                    field.set(o, jsonDecode(jsonObject.getString(field.getName())));
                }
            }
            return o;
        } catch (JSONException e) {
            try {
                ArrayList<Object> arrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(string);
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    arrayList.add(jsonDecode(jsonArray.getString(i)));
                }
                return arrayList;
            } catch (JSONException e1) {
                return string;
            }
        }
    }

    private interface GetValue {
        Object getValue(JSONObject object) throws Exception;
    }
}
