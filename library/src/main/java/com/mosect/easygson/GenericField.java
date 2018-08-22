package com.mosect.easygson;

import com.google.gson.annotations.Expose;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 泛型字段
 * @author MoSect
 *
 */
public class GenericField {

    private final GenericClass type;
    private final Field field;
    private final boolean fromJson;
    private final boolean toJson;

    public GenericField(GenericClass type, Field field) {
        super();
        this.type = type;
        this.field = field;
        Expose expose = field.getAnnotation(Expose.class);
        if (null == expose) {
            fromJson = true;
            toJson = true;
        } else {
            toJson = expose.serialize();
            fromJson = expose.deserialize();
        }
    }

    public GenericClass getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    /**
     * 获取字段的值
     *
     * @param target 目标
     * @return 字段的值
     */
    public Object getValue(Object target) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置字段的值
     *
     * @param target 目标
     * @param value  值
     */
    public void setValue(Object target, Object value) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取字段的名称
     *
     * @return 名称
     */
    public String getName() {
        return field.getName();
    }

    public boolean isFromJson() {
        return fromJson;
    }

    public boolean isToJson() {
        return toJson;
    }

    /**
     * 判断是否为静态字段
     *
     * @return true，为静态字段
     */
    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    /**
     * 判断是否为final字段
     * @return true，final字段
     */
    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    @Override
    public String toString() {
        return String.format("%s %s", type.toString(), field.getName());
    }

    @Override
    public int hashCode() {
        return type.hashCode() + field.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof GenericField) {
            GenericField other = (GenericField) obj;
            return other.field.equals(this.field) && other.type.equals(this.type);
        }
        return false;
    }
}
