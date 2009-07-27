package ca.uhn.hunit.util;

public class Pair<T> {

	private T myValue1;
	private T myValue2;
	public Pair(T theValue1, T theValue2) {
		super();
		myValue1 = theValue1;
		myValue2 = theValue2;
	}
	public T getValue1() {
		return myValue1;
	}
	public T getValue2() {
		return myValue2;
	}
	public void setValue1(T theValue1) {
		myValue1 = theValue1;
	}
	public void setValue2(T theValue2) {
		myValue2 = theValue2;
	}
	
	
	
}
