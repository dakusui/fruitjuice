package com.github.dakusui.fruitjuice.examples.executorservice;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MultiThreaded {
  int poolSize() default 2;
}
