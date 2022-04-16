package request.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
Used to define access role(s) to a given controller class or controller method.
Annotation present on method has higher priority than on class.
It is defined with @Inherited which means that if a subclass does not contain the annotation
then the annotation is taken from the superclass, but if it contains the annotation then
the annotation is overridden in the subclass
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface RequestAccess {
    String[] role() default "all";
}
