# easygson
gson的二次封装，主要是对泛型的嵌套支持

## 主要类
	GenericClass	泛型类
	EasyGson		json与object之间的转换工具

## GenericClass
### 主要是描述含有泛型的类，可以使用静态方法GenericClass.create创建对应的泛型类：
	单个泛型：List<String>
		GenericClass.create(List.class, String.class);
	多个泛型：HashMap<String, Integer>	
		GenericClass.create(HashMap.class, String.class, Integer.class);
	泛型数组：List<String>[]
		GenericClass.create(List[].class, List.class, String.class);
		注意：数组类比较特殊，第一个参数必须是相应的数组类的class，紧接着是数组元素的class，最后才是数组元素的泛型
	泛型继承：A<T> extends HashMap<String, T>		A<Long>
		GenericClass.create(A.class, Long.class);
		其父类是：HashMap<String, Long>
	匿名内部类：
		HashMap<String, Integer> map = new HashMap<String, Integer>() {};
		GenericClass mapClass = GenericClass.create(map.getClass());
	泛型的嵌套：List<HashMap<String, Date>>
		GenericClass.create(List.class, HashMap.class, String.class, Date.class);
	泛型的嵌套+数组：List<HashMap<String, Date>>[]
		GenericClass.create(List[].class, List.class, HashMap.class, String.class, Date.class);
### 可以通过getLayer方法获取指定类的泛型：
	class A<T> extends ArrayList<T>
	class B extends A<String>
	GenericClass bcls = GenericClass.create(B.class);
	GenericClass acls = bcls.getLayer(A.class);
	System.out.println(acls); // 打印A<String>

## EasyGson
### json与object之间的转换工具
	class A<T> {
		T field1;
		T[] field2;
	}

	EasyGson easyGson = new EasyGson(new Gson());
	easyGson.toObject()
	GenericClass type = GenericClass.create(A.class, String.class);
	json转object
	A<String> object = (A<String>) easyGson.toObject(jsonText, type);
	object转json
	JsonElement json = easyGson.toJsonElement(object, type);
