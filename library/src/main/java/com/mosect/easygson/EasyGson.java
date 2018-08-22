package com.mosect.easygson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EasyGson {

    private Gson gson;
    private HashMap<GenericClass, ClassCache> cache = new HashMap<GenericClass, ClassCache>();

    public EasyGson() {
        this(new Gson());
    }

    public EasyGson(Gson gson) {
        this.gson = gson;
    }

    /**
     * 将json转换成object
     *
     * @param <T>      object的泛型
     * @param jsonText json文本
     * @param clazz    类
     * @return object对象
     */
    @SuppressWarnings("unchecked")
    public <T> T toObject(String jsonText, Class<T> clazz) {
        GenericClass genericClass = GenericClass.createWithClass(clazz);
        return (T) toObject(jsonText, genericClass);
    }

    /**
     * 将jsonElement转成对象
     *
     * @param jsonElement jsonElement
     * @param clazz       类
     * @param <T>         类的泛型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public <T> T toObject(JsonElement jsonElement, Class<T> clazz) {
        return (T) toObject(jsonElement, GenericClass.createWithClass(clazz));
    }

    /**
     * 将json转换成object
     *
     * @param jsonText     json文本
     * @param genericClass object的类
     * @return object对象
     */
    public Object toObject(String jsonText, GenericClass genericClass) {
        JsonElement jsonElement = gson.fromJson(jsonText, JsonElement.class);
        return toObject(jsonElement, genericClass);
    }

    /**
     * 将json转换成object
     *
     * @param jsonElement  json
     * @param genericClass object的类
     * @return object 对象
     */
    public Object toObject(JsonElement jsonElement, GenericClass genericClass) {
        genericClass = checkCache(genericClass).getGenericClass();
        return toObjectNotCheck(jsonElement, genericClass);
    }

    /**
     * 将json转换成object
     *
     * @param jsonElement  json
     * @param genericClass object的类
     * @return object 对象
     */
    @SuppressWarnings("unchecked")
    public static Object toObjectNotCheck(JsonElement jsonElement, GenericClass genericClass) {
        Class<?> type = genericClass.getType();
        if (boolean.class.equals(type)) {
            if (null == jsonElement) {
                return false;
            }
            return jsonElement.getAsBoolean();
        } else if (byte.class.equals(type)) {
            if (null == jsonElement) {
                return (byte) 0;
            }
            return jsonElement.getAsByte();
        } else if (short.class.equals(type)) {
            if (null == jsonElement) {
                return (short) 0;
            }
            return jsonElement.getAsShort();
        } else if (char.class.equals(type)) {
            if (null == jsonElement) {
                return (char) 0;
            }
            return jsonElement.getAsCharacter();
        } else if (int.class.equals(type)) {
            if (null == jsonElement) {
                return (int) 0;
            }
            return jsonElement.getAsInt();
        } else if (long.class.equals(type)) {
            if (null == jsonElement) {
                return 0L;
            }
            return jsonElement.getAsLong();
        } else if (float.class.equals(type)) {
            if (null == jsonElement) {
                return 0f;
            }
            return jsonElement.getAsFloat();
        } else if (double.class.equals(type)) {
            if (null == jsonElement) {
                return 0d;
            }
            return jsonElement.getAsDouble();
        } else {
            if (null != jsonElement && !jsonElement.isJsonNull()) {
                // 数字类型
                if (Boolean.class.equals(type)) {
                    return jsonElement.getAsBoolean();
                } else if (Byte.class.equals(type)) {
                    return jsonElement.getAsByte();
                } else if (Short.class.equals(type)) {
                    return jsonElement.getAsShort();
                } else if (Character.class.equals(type)) {
                    return jsonElement.getAsCharacter();
                } else if (Integer.class.equals(type)) {
                    return jsonElement.getAsInt();
                } else if (Long.class.equals(type)) {
                    return jsonElement.getAsLong();
                } else if (Float.class.equals(type)) {
                    return jsonElement.getAsFloat();
                } else if (Double.class.equals(type)) {
                    return jsonElement.getAsDouble();

                } else if (genericClass.isArray()) { // 数组
                    if (!jsonElement.isJsonNull()) {
                        JsonArray jsonArray = jsonElement.getAsJsonArray();
                        Object array = genericClass.newArray(jsonArray.size());
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonElement comJsonValue = jsonArray.get(i);
                            Array.set(array, i, toObjectNotCheck(comJsonValue, genericClass.getComponentType()));
                        }
                        return array;
                    }
                    return null;

                } else { // 构造类型
                    if (JsonElement.class.isAssignableFrom(type)) {
                        return jsonElement;
                    } else if (String.class.equals(type) || CharSequence.class.equals(type)) {
                        // 字符串
                        if (jsonElement.isJsonPrimitive()) {
                            // 基本类型
                            JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
                            if (jsonPrimitive.isString()) {
                                // String 类型
                                return jsonPrimitive.getAsString();
                            }
                        }
                        return jsonElement.toString(); // 其他类型，直接转成json字符串
                    } else { // 构造类型
                        if (Object.class.equals(genericClass.getType())) {
                            if (jsonElement.isJsonPrimitive()) {
                                // 基本类型
                                JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
                                if (jsonPrimitive.isBoolean()) {
                                    return toObjectNotCheck(jsonElement, GenericClass.booleanClass);
                                } else if (jsonPrimitive.isNumber()) {
                                    return toObjectNotCheck(jsonElement, GenericClass.longClass);
                                } else if (jsonPrimitive.isString()) {
                                    return toObjectNotCheck(jsonElement, GenericClass.StringClass);
                                }

                            } else if (jsonElement.isJsonArray()) {
                                // 数组使用转成Object，将使用List<Object>作为类型转换
                                return toObjectNotCheck(jsonElement, GenericClass.ObjectListClass);

                            } else {
                                // jsonObject类型转成Object，将使用Map<String, Object>作为类型转换
                                return toObjectNotCheck(jsonElement, GenericClass.StringObjectMapClass);
                            }
                        } else {
                            if (Collection.class.isAssignableFrom(type)) {
                                // 集合
                                JsonArray jsonArray = jsonElement.getAsJsonArray();
                                GenericClass comType = genericClass.getGeneric(0);
                                Collection<Object> collection;
                                if (genericClass.isInterface()) {
                                    collection = new ArrayList<Object>();
                                } else {
                                    collection = (Collection<Object>) genericClass.newObject();
                                }
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    collection.add(toObjectNotCheck(jsonArray.get(i), comType));
                                }
                                return collection;

                            } else if (Map.class.isAssignableFrom(type)) {
                                // Map
                                GenericClass keyType = genericClass.getGeneric(0);
                                GenericClass valueType = genericClass.getGeneric(1);
                                // Key 必须为String
                                if (String.class.equals(keyType.getType())) {
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    HashMap<String, Object> map;
                                    if (genericClass.isInterface()) {
                                        map = new HashMap<String, Object>();
                                    } else {
                                        map = (HashMap<String, Object>) genericClass.newObject();
                                    }
                                    Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
                                    for (Map.Entry<String, JsonElement> entry : entries) {
                                        map.put(entry.getKey(), toObjectNotCheck(entry.getValue(), valueType));
                                    }
                                    return map;
                                } else {
                                    throwUnsupported(genericClass);
                                }

                            } else {
                                // 构造类型
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                Object target = genericClass.newObject();
                                GenericClass cur = genericClass;
                                while (!cur.isRoot()) {
                                    GenericField[] fields = cur.getFields();
                                    for (GenericField field : fields) {
                                        if (!field.isStatic() && !field.isFinal() && field.isFromJson()) {
                                            JsonElement jsonValue = jsonObject.get(field.getName());
                                            Object value = toObjectNotCheck(jsonValue, field.getType());
                                            field.setValue(target, value);
                                        }
                                    }
                                    cur = cur.getSuperType();
                                }
                                return target;
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * 将object转换成json
     *
     * @param object 对象
     * @return json实体
     */
    public JsonElement toJsonElement(Object object) {
        return toJsonElementNotCheck(object);
    }

    /**
     * 转换成json字符串
     *
     * @param object 对象
     * @return json字符串
     */
    public String toJsonString(Object object) {
        return toJsonElement(object).toString();
    }

    /**
     * 将object转换成json
     *
     * @param <T>    泛型
     * @param object 对象
     * @param type   类型
     * @return json实体
     * @deprecated 已过时，请使用toJsonElement(Object)
     */
    public <T> JsonElement toJsonElement(T object, Class<? extends T> type) {
        return toJsonElementNotCheck(object);
    }

    /**
     * 将object转换成json
     *
     * @param object object对象
     * @param type   类型
     * @return json实体
     * @deprecated 已过时，请使用toJsonElement(Object)
     */
    public JsonElement toJsonElement(Object object, GenericClass type) {
        return toJsonElementNotCheck(object);
    }

    /**
     * 将object转换成json
     *
     * @param object object对象
     * @return json实体
     */
    @SuppressWarnings("unchecked")
    public static JsonElement toJsonElementNotCheck(Object object) {
        if (null == object) {
            return JsonNull.INSTANCE;
        } else {
            if (object instanceof Boolean) {
                return new JsonPrimitive((Boolean) object);
            } else if (object instanceof Character) {
                return new JsonPrimitive((Character) object);
            } else if (object instanceof Byte) {
                return new JsonPrimitive((Byte) object);
            } else if (object instanceof Integer) {
                return new JsonPrimitive((Integer) object);
            } else if (object instanceof Short) {
                return new JsonPrimitive((Short) object);
            } else if (object instanceof Long) {
                return new JsonPrimitive((Long) object);
            } else if (object instanceof Float) {
                return new JsonPrimitive((Float) object);
            } else if (object instanceof Double) {
                return new JsonPrimitive((Double) object);
            } else if (object instanceof CharSequence) {
                return new JsonPrimitive(object.toString());
            } else {
                Class<?> type = object.getClass();
                if (type.isArray()) {
                    // 数组
                    JsonArray jsonArray = new JsonArray();
                    int length = Array.getLength(object);
                    for (int i = 0; i < length; i++) {
                        Object comValue = Array.get(object, i);
                        JsonElement jsonValue = toJsonElementNotCheck(comValue);
                        jsonArray.add(jsonValue);
                    }
                    return jsonArray;
                } else if (object instanceof Collection<?>) {
                    // 集合
                    JsonArray jsonArray = new JsonArray();
                    Collection<?> collection = (Collection<?>) object;
                    for (Object comValue : collection) {
                        JsonElement jsonValue = toJsonElementNotCheck(comValue);
                        jsonArray.add(jsonValue);
                    }
                    return jsonArray;
                } else if (object instanceof Map) {
                    // Map，key必须是String
                    JsonObject jsonObject = new JsonObject();
                    Set<Map.Entry<String, Object>> entries = ((Map<String, Object>) object).entrySet();
                    for (Map.Entry<String, Object> entry : entries) {
                        JsonElement jsonValue = toJsonElementNotCheck(entry.getValue());
                        jsonObject.add(entry.getKey(), jsonValue);
                    }
                    return jsonObject;
                } else if (object instanceof JsonElement) {
                    // JsonElement
                    return (JsonElement) object;
                } else {
                    // 构造类型
                    JsonObject jsonObject = new JsonObject();
                    Class<?> cur = type;
                    while (null != cur.getSuperclass()) {
                        Field[] fields = cur.getDeclaredFields();
                        if (null != fields && fields.length > 0) {
                            for (Field field : fields) {
                                int modifiers = field.getModifiers();
                                boolean toJson = true;
                                Expose expose = field.getAnnotation(Expose.class);
                                if (null != expose) {
                                    toJson = expose.serialize();
                                }
                                if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && toJson) {
                                    if (!field.isAccessible()) {
                                        field.setAccessible(true);
                                    }
                                    try {
                                        Object comValue = field.get(object);
                                        JsonElement jsonValue = toJsonElementNotCheck(comValue);
                                        jsonObject.add(field.getName(), jsonValue);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                        cur = cur.getSuperclass();
                    }
                    return jsonObject;
                }
            }
        }
    }

    protected ClassCache checkCache(GenericClass genericClass) {
        ClassCache classCache = cache.get(genericClass);
        if (null == classCache) {
            classCache = new ClassCache(genericClass);
            cache.put(genericClass, classCache);
        }
        return classCache;
    }

    private static void throwUnsupported(GenericClass genericClass) {
        String format = "Unsupported type(%s) from json to object!!!";
        throw new IllegalArgumentException(String.format(format, genericClass));
    }
}
