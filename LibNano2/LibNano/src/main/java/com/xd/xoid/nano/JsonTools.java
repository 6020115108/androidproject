package com.xd.xoid.nano;

import android.util.Log;


import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 版权所有：XXX有限公司
 *
 * JsonTools
 *
 * @author zhou.wenkai ,Created on 2015-10-8 09:04:23
 * 		   Major Function：<b>JSON 操作 工具类</b>
 *
 *         注:如果您修改了本类请填写以下内容作为记录，如非本人操作劳烦通知，谢谢！！！
 * @author mender，Modified Date Modify Content:
 */
public class JsonTools {



    private static boolean DEBUG = true;

    // -------------------------------------------------
    // fastjson 小封装
    // -------------------------------------------------
    public static <T> List<T> getBeanList(String jsonArrayString, Class<T> cls) {

        List<T> beanList = new ArrayList<T>();
        beanList = JSON.parseArray(jsonArrayString, cls);
        return beanList;
    }

    public static <T> T getBean(String jsonString, Class<T> cls) {
        T t = null;
        t = JSON.parseObject(jsonString, cls);
        return t;
    }

    public static String toJson(Object bean) {

        if (bean == null) {
            return "";
        }
        return JSON.toJSONString(bean);
    }

    public static String toJsonPretty(Object bean) {

        if (bean == null) {
            return "";
        }
        return JSON.toJSONString(bean, true);
    }


    // -------------------------------------------------
    // 原生json
    // -------------------------------------------------

    /** 解析json时，对每一个字段来一个回调 */
    public interface OnDecodeCallback {
        void onDecodeFinish(boolean success, String name, String rawJson, String errorReason);
    }


    public static <T> T getBean(String jsonStr, Class<T> clazz, OnDecodeCallback callback) {

        if (jsonStr == null || jsonStr.equals("")
                || !(jsonStr.startsWith("{") && jsonStr.endsWith("}"))) {
            callback.onDecodeFinish(false, "", jsonStr, "根就错了，要的是对象，json却不是{}");
            return null;
        }

        try {
            JSONObject job = new JSONObject(jsonStr);
            return parseObject("", job, clazz, null, callback);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onDecodeFinish(false, "", jsonStr,
                    "json格式直接不对，例如括号没闭合之类的，具体信息：" + e.getMessage());
        }
        return null;
    }

    public static <T> List<T> getBeanList(String jsonStr, Class<T> clazz,
                                          OnDecodeCallback callback) {
        List<T> list = new ArrayList<>();
        if (jsonStr == null || jsonStr.equals("")
                || !(jsonStr.startsWith("[") && jsonStr.endsWith("]"))) {
            callback.onDecodeFinish(false, "", jsonStr, "根就错了，要的是列表，json却不是[]");
            return list;
        }

        try {
            JSONArray ja = new JSONArray(jsonStr);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject job = new JSONObject(jsonStr);
                T t = parseObject("", job, clazz, null, callback);
                list.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onDecodeFinish(false, "", jsonStr,
                    "json格式直接不对，例如括号没闭合之类的，具体信息：" + e.getMessage());
        }

        return list;
    }


    private static String spellFullName(String father, String name) {
        if (father == null || father.equals(""))
            return name;
        else
            return father + "." + name;
    }

    private static String getStringSilencly(JSONObject jo, String name) {
        try {
            return jo.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * JSONObject 封装到 对象实例
     *
     * @param job 待封装的JSONObject
     * @param c 待封装的实例对象class
     * @param v 待封装实例的外部类实例对象</br>
     *            只有内部类存在,外部类时传递null
     * @return T:封装数据的实例对象
     * @version 1.0
     * @date 2015-10-9
     * @Author zhou.wenkai
     */
    @SuppressWarnings("unchecked")
    private static <T, V> T parseObject(String father, JSONObject job, Class<T> c, V v,
                                        OnDecodeCallback callback) {
        // Log.i("jsonooss", "parseObject--T是 " + c.getName());
        T t = null;
        try {
            if (null == v) {
                t = c.newInstance();
            } else {
                /// 这里他妈的什么意思
                // Log.i("jsonooss", "parseObject--V是 " +
                // v.getClass().getName());
                // Constructor<?> constructor = c.getDeclaredConstructors()[0];
                // constructor.setAccessible(true);
                // t = (T) constructor.newInstance(v);
                t = c.newInstance();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e(JsonTools.class.getSimpleName(),
                    c.toString() + " should provide a default constructor "
                            + "(a public constructor with no arguments)");
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }

        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            String name = field.getName();

            // if the object don`t has a mapping for name, then continue
            if (!job.has(name)) {
                //callback.onDecodeFinish(false, spellFullName(father, name), "", "json里没有对应的字段");
                continue;
            }

            String typeName = type.getName();
            if (typeName.equals("java.lang.String")) {
                try {
                    String value = job.getString(name);
                    if (value != null && value.equals("null")) {
                        value = "";
                    }
                    field.set(t, value);
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                    callback.onDecodeFinish(false, spellFullName(father, name),
                            getStringSilencly(job, name), "getString错误--" + e.getMessage());
                    try {
                        field.set(t, "");
                    } catch (Exception e1) {
                        if (DEBUG)
                            e1.printStackTrace();
                        callback.onDecodeFinish(false, spellFullName(father, name),
                                getStringSilencly(job, name),
                                "field.set(obj, \"\")错误--" + e1.getMessage());
                    }
                }
            } else if (typeName.equals("int") || typeName.equals("java.lang.Integer")) {
                try {
                    field.set(t, job.getInt(name));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                    callback.onDecodeFinish(false, spellFullName(father, name),
                            getStringSilencly(job, name), "getInt 错误--" + e.getMessage());
                }
            } else if (typeName.equals("boolean") || typeName.equals("java.lang.Boolean")) {
                try {
                    field.set(t, job.getBoolean(name));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                    callback.onDecodeFinish(false, spellFullName(father, name),
                            getStringSilencly(job, name), "getBoolean 错误--" + e.getMessage());
                }
            } else if (typeName.equals("float") || typeName.equals("java.lang.Float")) {
                try {
                    field.set(t, Float.valueOf(job.getString(name)));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                    callback.onDecodeFinish(false, spellFullName(father, name),
                            getStringSilencly(job, name), "getFloat 错误--" + e.getMessage());
                }
            } else if (typeName.equals("double") || typeName.equals("java.lang.Double")) {
                try {
                    field.set(t, job.getDouble(name));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                    callback.onDecodeFinish(false, spellFullName(father, name),
                            getStringSilencly(job, name), "getDouble 错误--" + e.getMessage());
                }
            } else if (typeName.equals("long") || typeName.equals("java.lang.Long")) {
                try {
                    field.set(t, job.getLong(name));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                    callback.onDecodeFinish(false, spellFullName(father, name),
                            getStringSilencly(job, name), "getLong 错误--" + e.getMessage());
                }
            } else if (typeName.equals("java.util.List")
                    || typeName.equals("java.util.ArrayList")) {
                try {
                    Object obj = job.get(name);
                    Type genericType = field.getGenericType();
                    String className = genericType.toString().replace("<", "")
                            .replace(type.getName(), "").replace(">", "");
                    Class<?> clazz = Class.forName(className);
                    if (obj instanceof JSONArray) {
                        ArrayList<?> objList = parseArray(name, (JSONArray)obj, clazz, t, callback);
                        field.set(t, objList);
                    } else {
                        callback.onDecodeFinish(false, spellFullName(father, name),
                                getStringSilencly(job, name), "列表根本性错误，需要个[]");
                    }
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else {
                try {
                    Object obj = job.get(name);
                    Class<?> clazz = Class.forName(typeName);
                    if (obj instanceof JSONObject) {
                        Object parseJson = parseObject(spellFullName(father, name), (JSONObject)obj,
                                clazz, t, callback);
                        field.set(t, parseJson);
                    }
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }

            }
        }

        return t;
    }

    @SuppressWarnings("unchecked")
    private static <T, V> ArrayList<T> parseArray(String father, JSONArray array, Class<T> c, V v,
                                                  OnDecodeCallback callback) {
        ArrayList<T> list = new ArrayList<T>(array.length());
        try {
            for (int i = 0; i < array.length(); i++) {
                if (array.get(i) instanceof JSONObject) {
                    T t = parseObject(spellFullName(father, i + ""), array.getJSONObject(i), c, v,
                            callback);
                    list.add(t);
                } else {
                    list.add((T)array.get(i));
                }

            }
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }
        return list;
    }

    private static <T> String objectToJson(T t) {

        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder(fields.length << 4);
        sb.append("{");

        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            String name = field.getName();

            // 'this$Number' 是内部类的外部类引用(指针)字段
            if (name.contains("this$"))
                continue;

            String typeName = type.getName();
            if (typeName.equals("java.lang.String")) {
                try {
                    sb.append("\"" + name + "\":");
                    sb.append(stringToJson((String)field.get(t)));
                    sb.append(",");
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else if (typeName.equals("boolean") || typeName.equals("java.lang.Boolean")
                    || typeName.equals("int") || typeName.equals("java.lang.Integer")
                    || typeName.equals("float") || typeName.equals("java.lang.Float")
                    || typeName.equals("double") || typeName.equals("java.lang.Double")
                    || typeName.equals("long") || typeName.equals("java.lang.Long")) {
                try {
                    sb.append("\"" + name + "\":");
                    sb.append(field.get(t));
                    sb.append(",");
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else if (typeName.equals("java.util.List")
                    || typeName.equals("java.util.ArrayList")) {
                try {
                    List<?> objList = (List<?>)field.get(t);
                    if (null != objList && objList.size() > 0) {
                        sb.append("\"" + name + "\":");
                        sb.append("[");
                        String toJson = listToJson((List<?>)field.get(t));
                        sb.append(toJson);
                        sb.setCharAt(sb.length() - 1, ']');
                        sb.append(",");
                    }
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else {
                try {
                    sb.append("\"" + name + "\":");
                    sb.append("{");
                    sb.append(objectToJson(field.get(t)));
                    sb.setCharAt(sb.length() - 1, '}');
                    sb.append(",");
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            }

        }
        if (sb.length() == 1) {
            sb.append("}");
        }
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }

    /**
     * 将 List 对象编码为 JSON格式
     *
     * @param objList 待封装的对象集合
     * @return String:封装后JSONArray String格式
     * @version 1.0
     * @date 2015-10-11
     * @Author zhou.wenkai
     */
    private static <T> String listToJson(List<T> objList) {
        final StringBuilder sb = new StringBuilder();
        for (T t : objList) {
            if (t instanceof String) {
                sb.append(stringToJson((String)t));
                sb.append(",");
            } else if (t instanceof Boolean || t instanceof Integer || t instanceof Float
                    || t instanceof Double) {
                sb.append(t);
                sb.append(",");
            } else {
                sb.append(objectToJson(t));
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 将 String 对象编码为 JSON格式，只需处理好特殊字符
     *
     * @param str String 对象
     * @return String:JSON格式
     * @version 1.0
     * @date 2015-10-11
     * @Author zhou.wenkai
     */
    private static String stringToJson(final String str) {
        if (str == null || str.length() == 0) {
            return "\"\"";
        }
        final StringBuilder sb = new StringBuilder(str.length() + 2 << 4);
        sb.append('\"');
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);

            sb.append(c == '\"' ? "\\\""
                    : c == '\\' ? "\\\\"
                    : c == '/' ? "\\/"
                    : c == '\b' ? "\\b"
                    : c == '\f' ? "\\f"
                    : c == '\n' ? "\\n"
                    : c == '\r' ? "\\r"
                    : c == '\t' ? "\\t" : c);
        }
        sb.append('\"');
        return sb.toString();
    }

    /**
     * 由JSONObject生成Bean对象
     *
     * @param job
     * @param className 待生成Bean对象的名称
     * @param outCount 外部类的个数
     * @return LinkedList<String>: 生成的Bean对象
     * @version 1.0
     * @date 2015-10-16
     * @Author zhou.wenkai
     */
    private static String createObject(JSONObject job, String className, int outCount) {
        final StringBuilder sb = new StringBuilder();
        String separator = System.getProperty("line.separator");

        // 生成的Bean类前部的缩进空间
        String classFrontSpace = "";
        // 生成的Bean类字段前部的缩进空间
        String fieldFrontSpace = "    ";
        for (int i = 0; i < outCount; i++) {
            classFrontSpace += "    ";
            fieldFrontSpace += "    ";
        }

        sb.append(classFrontSpace + "public class " + className + " {");

        Iterator<?> it = job.keys();
        while (it.hasNext()) {
            String key = (String)it.next();
            try {
                Object obj = job.get(key);
                if (obj instanceof JSONArray) {
                    // 判断类是否为基本数据类型,如果为自定义类则字段类型取将key的首字母大写作为内部类名称
                    String fieldType = ((JSONArray)obj).get(0) instanceof JSONObject ? ""
                            : ((JSONArray)obj).get(0).getClass().getSimpleName();
                    if (fieldType == "") {
                        fieldType = String.valueOf(Character.isUpperCase(key.charAt(0))
                                ? key.charAt(0) : Character.toUpperCase(key.charAt(0)))
                                + key.substring(1);
                    }
                    sb.append(separator);
                    sb.append(fieldFrontSpace + "public List<" + fieldType + "> " + key + ";");

                    // 如果字段类型为自定义类类型,则取JSONArray中第一个JSONObject生成Bean
                    if (((JSONArray)obj).get(0) instanceof JSONObject) {
                        sb.append(separator);
                        sb.append(separator);
                        sb.append(fieldFrontSpace + "/** " + fieldType + " is the inner class of "
                                + className + " */");
                        sb.append(separator);
                        sb.append(createObject((JSONObject)((JSONArray)obj).get(0), fieldType,
                                outCount + 1));
                    }
                } else if (obj instanceof JSONObject) {
                    String fieldType = String.valueOf(Character.isUpperCase(key.charAt(0))
                            ? key.charAt(0) : Character.toUpperCase(key.charAt(0)))
                            + key.substring(1);
                    sb.append(separator);
                    sb.append(fieldFrontSpace + "public List<" + fieldType + "> " + key + ";");
                    sb.append(separator);
                    sb.append(separator);
                    sb.append(fieldFrontSpace + "/** " + fieldType + " is the inner class of "
                            + className + " */");
                    sb.append(separator);
                    sb.append(createObject((JSONObject)obj, fieldType, outCount + 1));
                } else {
                    String type = obj.getClass().getSimpleName();
                    sb.append(separator);
                    sb.append(fieldFrontSpace + "public " + type + " " + key + ";");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sb.append(separator);
        sb.append(classFrontSpace + "}");
        sb.append(separator);

        return sb.toString();
    }


    public static <T> T tryToObject(String json, Class<T> clazz) {
        try {
            return JsonTools.getBean(json, clazz, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> tryToList(String json, Class<T> clazz) {
        try {
            return JsonTools.getBeanList(json, clazz, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static OnDecodeCallback callback = new OnDecodeCallback() {
        @Override
        public void onDecodeFinish(boolean success, String name, String rawJson,
                                   String errorReason) {
            Log.e("jssssssss",
                    "解析错误，字段是(" + name + "), 原因: " + errorReason + ", 原始json是：" + rawJson);
        }
    };


    //--------------------------------------
    // json格式化
    //--------------------------------------
    /**
     * 单位缩进字符串。
     */
    private static String SPACE = "   ";

    /**
     * 返回格式化JSON字符串。
     *
     * @param json 未格式化的JSON字符串。
     * @return 格式化的JSON字符串。
     */
    public static String formatJson(String json) {
        try {
            return _formatJson(json);
        } catch (Exception e) {
            return json;
        }
    }

    private static String _formatJson(String json) {
        StringBuffer result = new StringBuffer();

        int length = json.length();
        int number = 0;
        char key = 0;
        // 遍历输入字符串。
        for (int i = 0; i < length; i++) {
            // 1、获取当前字符。
            key = json.charAt(i);

            // 2、如果当前字符是前方括号、前花括号做如下处理：
            if ((key == '[') || (key == '{')) {
                // （1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
                if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append(indent(number));
                }

                // （2）打印：当前字符。
                result.append(key);

                // （3）前方括号、前花括号，的后面必须换行。打印：换行。
                result.append('\n');

                // （4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
                number++;
                result.append(indent(number));

                // （5）进行下一次循环。
                continue;
            }

            // 3、如果当前字符是后方括号、后花括号做如下处理：
            if ((key == ']') || (key == '}')) {
                // （1）后方括号、后花括号，的前面必须换行。打印：换行。
                result.append('\n');

                // （2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
                number--;
                result.append(indent(number));

                // （3）打印：当前字符。
                result.append(key);

                // （4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
                if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                    result.append('\n');
                }

                // （5）继续下一次循环。
                continue;
            }

            // 4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
            if ((key == ',')) {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }

            // 5、打印：当前字符。
            result.append(key);
        }

        return result.toString();
    }

    /**
     * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
     *
     * @param number 缩进次数。
     * @return 指定缩进次数的字符串。
     */
    private static String indent(int number) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < number; i++) {
            result.append(SPACE);
        }
        return result.toString();
    }

}