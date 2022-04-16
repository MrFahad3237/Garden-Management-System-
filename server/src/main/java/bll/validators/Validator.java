package bll.validators;

public interface Validator<T> {
    
    boolean validate(T t);
}
