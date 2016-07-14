package com.github.dakusui.fruitjuice.example;

import com.github.dakusui.fruitjuice.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Fixture {
  @Retention(RetentionPolicy.RUNTIME)
  @BindingAnnotation
  @interface InjectComponent {
  }
}
