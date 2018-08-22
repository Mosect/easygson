package com.mosect.easygson.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mosect.easygson.EasyGson;
import com.mosect.easygson.GenericClass;

public class Main {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// 单个泛型
		GenericClass cls1 = GenericClass.create(A.class, String.class); // obj1对应的泛型类
		A<String> obj1 = (A<String>) cls1.newObject();
		System.out.println(obj1);
		System.out.println(cls1);

		// 多个泛型
		GenericClass cls2 = GenericClass.create(B.class, Integer.class, Date.class);
		B<Integer, Date> obj2 = (B<Integer, Date>) cls2.newObject();
		System.out.println(obj2);
		System.out.println(cls2);
		System.out.println();

		// 泛型继承
		GenericClass cls3 = GenericClass.create(C.class, String.class);
		C<String> obj3 = (C<String>) cls3.newObject();
		System.out.println(obj3);
		println(cls3);
		GenericClass cls4 = GenericClass.create(D.class, Date.class);
		D<Date> obj4 = (D<Date>) cls4.newObject();
		System.out.println(obj4);
		println(cls4);
		System.out.println();

		// 泛型数组
		GenericClass cls5 = GenericClass.create(A[].class, A.class, String.class);
		A<String>[] obj5 = (A<String>[]) cls5.newArray(2);
		obj5[0] = new A<String>();
		obj5[1] = new A<String>();
		System.out.println(Arrays.toString(obj5));
		System.out.println(cls5);
		System.out.println();

		// 泛型嵌套
		GenericClass cls6 = GenericClass.create(A.class, B.class, String.class, Date.class);
		A<B<String, Date>> obj6 = (A<B<String, Date>>) cls6.newObject();
		System.out.println(obj6);
		System.out.println(cls6);
		System.out.println();

		// 匿名子类
		A<Date> obj7 = new A<Date>() {
		};
		GenericClass cls7 = GenericClass.create(obj7.getClass());
		System.out.println(obj7);
		println(cls7);
		System.out.println();

		// json解析
		EasyGson easyGson = new EasyGson(new Gson());
		GenericClass jsonClass = GenericClass.create(TestEntity.class, String.class);
		String jsonText = readJsonText();
		System.out.println(jsonText);
		TestEntity<String> entity = (TestEntity<String>) easyGson.toObject(jsonText, jsonClass);
		// 转换成json
		JsonElement jsonElement = easyGson.toJsonElement(entity);
		System.out.println(jsonElement);
	}

	private static void println(GenericClass genericClass) {
		while (!genericClass.isRoot()) {
			System.out.println(genericClass);
			genericClass = genericClass.getSuperType();
		}
	}

	private static String readJsonText() {
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = Main.class.getResourceAsStream("/test.json");
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int len;
			while ((len = is.read(buffer)) > 0) {
				baos.write(buffer, 0, len);
			}
			return new String(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
}
