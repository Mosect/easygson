package com.mosect.easygson.example;

import java.util.HashMap;
import java.util.List;

public class TestEntity<T> {

	private B<String, Integer> field1;
	private B<String, String>[] field2;
	private List<B<String, Long>> field3;

	private A<T> field4;
	private A<T>[] field5;
	private List<A<T>> field6;

	private Object field7;
	private HashMap<String, Boolean> field8;
	private List<String> field9;

	private Object field10;
	private Object field11;

	public B<String, Integer> getField1() {
		return field1;
	}

	public void setField1(B<String, Integer> field1) {
		this.field1 = field1;
	}

	public B<String, String>[] getField2() {
		return field2;
	}

	public void setField2(B<String, String>[] field2) {
		this.field2 = field2;
	}

	public List<B<String, Long>> getField3() {
		return field3;
	}

	public void setField3(List<B<String, Long>> field3) {
		this.field3 = field3;
	}

	public A<T> getField4() {
		return field4;
	}

	public void setField4(A<T> field4) {
		this.field4 = field4;
	}

	public A<T>[] getField5() {
		return field5;
	}

	public void setField5(A<T>[] field5) {
		this.field5 = field5;
	}

	public List<A<T>> getField6() {
		return field6;
	}

	public void setField6(List<A<T>> field6) {
		this.field6 = field6;
	}

	public Object getField7() {
		return field7;
	}

	public void setField7(Object field7) {
		this.field7 = field7;
	}

	public HashMap<String, Boolean> getField8() {
		return field8;
	}

	public void setField8(HashMap<String, Boolean> field8) {
		this.field8 = field8;
	}

	public List<String> getField9() {
		return field9;
	}

	public void setField9(List<String> field9) {
		this.field9 = field9;
	}

	public Object getField10() {
		return field10;
	}

	public void setField10(Object field10) {
		this.field10 = field10;
	}

	public Object getField11() {
		return field11;
	}

	public void setField11(Object field11) {
		this.field11 = field11;
	}

}
