package com.mosect.easygson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vince on 2018/3/8. 泛型类
 */

public class GenericClass {

    public static final GenericClass booleanClass = create(boolean.class);
    public static final GenericClass booleanArrayClass = create(boolean[].class, boolean.class);
    public static final GenericClass BooleanClass = create(Boolean.class);
    public static final GenericClass BooleanArrayClass = create(Boolean[].class, Boolean.class);

    public static final GenericClass byteClass = create(byte.class);
    public static final GenericClass byteArrayClass = create(byte[].class, byte.class);
    public static final GenericClass ByteClass = create(Byte.class);
    public static final GenericClass ByteArrayClass = create(Byte[].class, Byte.class);

    public static final GenericClass shortClass = create(short.class);
    public static final GenericClass shortArrayClass = create(short[].class, short.class);
    public static final GenericClass ShortClass = create(Short.class);
    public static final GenericClass ShortArrayClass = create(Short[].class, Short.class);

    public static final GenericClass charClass = create(char.class);
    public static final GenericClass charArrayClass = create(char[].class, char.class);
    public static final GenericClass CharacterClass = create(Character.class);
    public static final GenericClass CharacterArrayClass = create(Character[].class, Character.class);

    public static final GenericClass intClass = create(int.class);
    public static final GenericClass intArrayClass = create(int[].class, int.class);
    public static final GenericClass IntegerClass = create(Integer.class);
    public static final GenericClass IntegerArrayClass = create(Integer[].class, Integer.class);

    public static final GenericClass longClass = create(long.class);
    public static final GenericClass longArrayClass = create(long[].class, long.class);
    public static final GenericClass LongClass = create(Long.class);
    public static final GenericClass LongArrayClass = create(Long[].class, Long.class);

    public static final GenericClass floatClass = create(float.class);
    public static final GenericClass floatArrayClass = create(float[].class, float.class);
    public static final GenericClass FloatClass = create(Float.class);
    public static final GenericClass FloatArrayClass = create(Float[].class, Float.class);

    public static final GenericClass doubleClass = create(double.class);
    public static final GenericClass doubleArrayClass = create(double[].class, double.class);
    public static final GenericClass DoubleClass = create(Double.class);
    public static final GenericClass DoubleArrayClass = create(Double[].class, Double.class);


    public static final GenericClass StringClass = create(String.class);
    public static final GenericClass StringArrayClass = create(String[].class, String.class);

    public static final GenericClass ObjectClass = create(Object.class);
    public static final GenericClass ObjectArrayClass = create(Object[].class, Object.class);

    public static final GenericClass ObjectListClass = create(List.class, Object.class);
    public static final GenericClass StringObjectMapClass = create(Map.class, String.class, Object.class);

    private final Class<?> target;
    private final GenericClass[] targetGenerics;
    private GenericClass componentType;
    private HashMap<String, GenericClass> targetGenericMap;
    private GenericClass superType;
    private HashMap<String, GenericField> fieldMap;
    private GenericField[] fields;
    private GenericClass[] genericInterfaces;

    public GenericClass(Class<?> target,
                        GenericClass targetComponent,
                        GenericClass... targetGenerics) {
        TypeVariable<?>[] types = target.getTypeParameters();
        if (null != types && types.length > 0) {
            if (null == targetGenerics || targetGenerics.length != types.length) {
                String format = "Target class need %d generics,but targetGenerics is %s";
                String tg = null == targetGenerics ? "null" : Arrays.toString(targetGenerics);
                throw new IllegalArgumentException(String.format(format, types.length, tg));
            }
            this.targetGenerics = targetGenerics;
        } else {
            this.targetGenerics = null;
        }
        if (target.isArray()) {
            if (null == targetComponent) {
                String msg = "Target class need a component type,but targetComponent is null";
                throw new IllegalArgumentException(msg);
            }
            componentType = targetComponent;
        }
        this.target = target;
    }

    /**
     * 获取泛型类型
     *
     * @param index 下标
     * @return 泛型类型
     */
    public GenericClass getGeneric(int index) {
        if (null != targetGenerics) {
            return targetGenerics[index];
        }
        return null;
    }

    /**
     * 获取泛型类型
     *
     * @param name 泛型名称
     * @return 泛型类型
     */
    public GenericClass getGeneric(String name) {
        if (null == targetGenericMap) {
            targetGenericMap = genTargetGenericMap();
        }
        return targetGenericMap.get(name);
    }

    /**
     * 获取泛型数量
     *
     * @return 泛型数量
     */
    public int getGenericCount() {
        return null == targetGenerics ? 0 : targetGenerics.length;
    }

    public GenericClass[] getGenericInterfaces() {
        checkGenericInterfaces();
        return genericInterfaces;
    }

    /**
     * 获取类型
     *
     * @return 类型
     */
    public Class<?> getType() {
        return target;
    }

    /**
     * 获取父类
     *
     * @return 父类
     */
    public GenericClass getSuperType() {
        if (null == superType) {
            superType = genSuperType();
        }
        return superType;
    }

    /**
     * 判断是否是根类型
     *
     * @return true，根类型
     */
    public boolean isRoot() {
        return null == target.getSuperclass();
    }

    /**
     * 判断是否为数组
     *
     * @return true，为数组
     */
    public boolean isArray() {
        return target.isArray();
    }

    /**
     * 判断是否为接口
     *
     * @return true，为接口
     */
    public boolean isInterface() {
        return target.isInterface();
    }

    /**
     * 获取所有字段
     *
     * @return 所有字段
     */
    public GenericField[] getFields() {
        checkFields();
        return fields;
    }

    /**
     * 获取字段
     *
     * @param name 字段名称
     * @return 字段
     */
    public GenericField getField(String name) {
        checkFields();
        return fieldMap.get(name);
    }

    /**
     * 新建数组对象（此类型必须是数组类型）
     *
     * @param length 长度
     * @return 数组对象
     */
    public Object newArray(int length) {
        return Array.newInstance(componentType.getType(), length);
    }

    /**
     * 调用无参构造创建对象
     *
     * @return 新建对象
     */
    public Object newObject() {
        try {
            return target.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkGenericInterfaces() {
        if (null == genericInterfaces) {
            Type[] types = this.target.getGenericInterfaces();
            int count = null == types ? 0 : types.length;
            genericInterfaces = new GenericClass[count];
            for (int i = 0; i < count; i++) {
                genericInterfaces[i] = createWithType(types[i], this);
            }
        }
    }

    private void checkFields() {
        if (null == fields) {
            fieldMap = new HashMap<String, GenericField>();
            Field[] fs = target.getDeclaredFields();
            int count = null == fs ? 0 : fs.length;
            fields = new GenericField[count];
            for (int i = 0; i < count; i++) {
                Field f = fs[i];
                Type gt = f.getGenericType();
                GenericClass type;
                if (null != gt) {
                    type = createWithType(gt, this);
                } else {
                    type = createWithClass(f.getType(), this);
                }
                fields[i] = new GenericField(type, f);
                fieldMap.put(f.getName(), fields[i]);
            }
        }
    }

    /**
     * 产生目标泛型Map
     *
     * @return 产生目标泛型Map
     */
    private HashMap<String, GenericClass> genTargetGenericMap() {
        TypeVariable<?>[] types = target.getTypeParameters();
        HashMap<String, GenericClass> targetGenericMap = new HashMap<String, GenericClass>();
        for (int i = 0; i < types.length; i++) {
            targetGenericMap.put(types[i].getName(), targetGenerics[i]);
        }
        return targetGenericMap;
    }

    /**
     * 产生父类类型
     *
     * @return 父类类型
     */
    private GenericClass genSuperType() {
        if (!isRoot()) {
            // 非根类型
            Type type = target.getGenericSuperclass();
            if (null != type) {
                return createWithType(type, this);
            } else {
                Class<?> superclass = target.getSuperclass();
                if (null != superclass) {
                    return createWithClass(superclass, this);
                }
            }
        }
        return null;
    }

    /**
     * 获取元素类型
     *
     * @return 元素类型，非数组返回null
     */
    public GenericClass getComponentType() {
        return componentType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (null == componentType) {
            builder.append(target.getName());
        } else {
            builder.append(componentType).append("[]");
        }
        if (null != targetGenerics && targetGenerics.length > 0) {
            builder.append("<").append(targetGenerics[0].toString());
            for (int i = 1; i < targetGenerics.length; i++) {
                builder.append(",").append(targetGenerics[i].toString());
            }
            builder.append(">");
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        int code = target.hashCode();
        if (null != targetGenerics && targetGenerics.length > 0) {
            for (GenericClass genericClass : targetGenerics) {
                code += genericClass.hashCode();
            }
        }
        return code;
    }

    /**
     * 查找指定层的泛型类型
     *
     * @param type 指定层的class
     * @return 指定层的泛型类型
     */
    public GenericClass getLayer(Class<?> type) {
        GenericClass cur = this;
        while (null != cur && !cur.getType().equals(type)) {
            cur = cur.getSuperType();
        }
        return cur;
    }

    /**
     * 创建泛型类型
     *
     * @param target         目标类
     * @param nestingClasses 嵌套的类型（包括数组的元素类型、泛型以及嵌套的元素类型、泛型）
     * @return 泛型类型
     */
    public static GenericClass create(Class<?> target, Class<?>... nestingClasses) {
        return create(target, nestingClasses, 0).genericClass;
    }

    private static CreateResult create(Class<?> target, Class<?>[] nestingClasses, int index) {
        GenericClass compType = null;
        if (target.isArray()) {
            CreateResult result = create(nestingClasses[index], nestingClasses, index + 1);
            compType = result.genericClass;
            index = result.index;
        }
        TypeVariable<?>[] types = target.getTypeParameters();
        int count = null == types ? 0 : types.length;
        GenericClass[] genericClasses = new GenericClass[count];
        for (int i = 0; i < count; i++) {
            CreateResult result = create(nestingClasses[index], nestingClasses, index + 1);
            genericClasses[i] = result.genericClass;
            index = result.index;
        }
        CreateResult result = new CreateResult();
        result.genericClass = new GenericClass(target, compType, genericClasses);
        result.index = index;
        return result;
    }

    /**
     * 产生泛型类型
     *
     * @param clazz 无泛型的类
     * @return 泛型类型
     */
    public static GenericClass createWithClass(Class<?> clazz) {
        return createWithClass(clazz, (GenericClass) null);
    }

    /**
     * 产生泛型类型
     *
     * @param clazz          类，不能是数组
     * @param genericClasses 泛型类（不能是嵌套泛型的类型）
     * @return 泛型类型
     */
    public static GenericClass createWithClass(Class<?> clazz, Class<?>... genericClasses) {
        if (null != genericClasses && genericClasses.length > 0) {
            GenericClass[] classes = new GenericClass[genericClasses.length];
            for (int i = 0; i < genericClasses.length; i++) {
                classes[i] = new GenericClass(genericClasses[i], null);
            }
            return new GenericClass(clazz, null, classes);
        }
        return new GenericClass(clazz, null);
    }

    /**
     * 新建泛型类型
     *
     * @param clazz 类
     * @param child 子类类型
     * @return 泛型类型
     */
    public static GenericClass createWithClass(Class<?> clazz, GenericClass child) {
        GenericClass comType = null;
        if (clazz.isArray()) {
            Class<?> comClass = clazz.getComponentType();
            comType = createWithClass(comClass, child);
        }
        Type[] types = clazz.getTypeParameters();
        if (null != types && types.length > 0) {
            GenericClass[] targetGenerics = new GenericClass[types.length];
            for (int i = 0; i < targetGenerics.length; i++) {
                targetGenerics[i] = createWithType(types[i], child);
            }
            return new GenericClass(clazz, comType, targetGenerics);
        }
        return new GenericClass(clazz, comType);
    }

    /**
     * 新的泛型类型
     *
     * @param type  类型
     * @param child 子类的类型
     * @return 泛型类型
     */
    public static GenericClass createWithType(Type type, GenericClass child) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = ((ParameterizedType) type);
            Type[] actualTypes = parameterizedType.getActualTypeArguments();
            if (null != actualTypes && actualTypes.length > 0) {
                GenericClass[] genericClasses = new GenericClass[actualTypes.length];
                for (int i = 0; i < genericClasses.length; i++) {
                    genericClasses[i] = createWithType(actualTypes[i], child);
                }
                return new GenericClass((Class<?>) parameterizedType
                        .getRawType(), null, genericClasses);
            } else {
                return new GenericClass((Class<?>) parameterizedType
                        .getRawType(), null);
            }
        } else if (type instanceof TypeVariable<?>) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) type;
            if (null != child) {
                return child.getGeneric(typeVariable.getName());
            } else {
                throw new IllegalArgumentException(
                        "Need a child type,but child is null!!!");
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type;
            GenericClass component = createWithType(genericArrayType
                    .getGenericComponentType(), child);
            return new GenericClass(Object[].class, component);
        } else if (type instanceof Class<?>) {
            return createWithClass((Class<?>) type, child);
        } else {
            throw new IllegalArgumentException("Can't supported type:" + type);
        }
    }

    private static class CreateResult {
        private int index;
        private GenericClass genericClass;
    }
}
