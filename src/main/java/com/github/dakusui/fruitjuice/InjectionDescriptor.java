package com.github.dakusui.fruitjuice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;

public interface InjectionDescriptor {
  <T extends Annotation> T getAnnotation(Class<T> annotationClass);

  <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass);

  enum Factory {
    ;

    InjectionDescriptor createFromField(final Field field) {
      Objects.requireNonNull(field);
      return new InjectionDescriptor() {
        public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
          return isAnnotationPresent(annotationClass)
              ? field.getAnnotation(annotationClass)
              : null;
        }

        public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass) {
          return field.isAnnotationPresent(annotationClass)
              && field.getAnnotation(annotationClass).getClass().isAnnotationPresent(BindingAnnotation.class);
        }
      };
    }
  }
}
