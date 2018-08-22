package com.mosect.easygson;

/**
 * Created by Vince on 2018/3/9.
 * 泛型类的缓存
 */

public class ClassCache {

    private final GenericClass genericClass;

    public ClassCache(GenericClass genericClass) {
        GenericClass cur = genericClass;
        while (!cur.isRoot()) {
            cur = cur.getSuperType();
        }
        this.genericClass = genericClass;
    }

    public GenericClass getGenericClass() {
        return genericClass;
    }
}
