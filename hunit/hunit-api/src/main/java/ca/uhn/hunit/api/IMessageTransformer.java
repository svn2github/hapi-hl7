package ca.uhn.hunit.api;

public interface IMessageTransformer<T> {

	public T transform(T theInput);
	
}
