package com.github.dakusui.fruitjuice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to let FruitJuice know the constructor annotated with this can
 * be used for dependency injection.
 *
 * At most only one constructor in a class can be annotated with this.
 * If no constructor is annotated with this annotation, A public constructor with
 * no parameter will be used for dependency injection.
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseForInjection {
}
