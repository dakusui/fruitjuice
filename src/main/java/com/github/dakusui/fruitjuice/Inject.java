package com.github.dakusui.fruitjuice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to let Fruit-juice know elements annotated with this can be used
 * for dependency injection.
 *
 * At most only one constructor in a class can be annotated with this.
 * If no constructor is annotated with this annotation, A public constructor with
 * no parameter will be used for instantiation.
 *
 * Currently, this annotation can be used for constructors and fields only.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}
