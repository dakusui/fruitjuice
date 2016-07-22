package com.github.dakusui.fruitjuice.examples.calc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Add {
  int[] value() default {};
}
