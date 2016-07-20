package com.github.dakusui.fruitjuice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A meta-annotation to let the FruitJuice framework know an annotation annotated
 * by this is one to make a dependency injection happen.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestInjection {
}
