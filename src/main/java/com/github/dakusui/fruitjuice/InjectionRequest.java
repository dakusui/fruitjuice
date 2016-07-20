package com.github.dakusui.fruitjuice;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

import static com.google.common.collect.Iterables.*;
import static java.lang.String.format;

/**
 * An interface that represents a request that defines what object should be injected to
 * an injection point to which this object belongs.
 */
public interface InjectionRequest {
  /**
   * Checks if annotation of {@code annotationClass} is present.
   */
  boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

  /**
   * Checks an annotation object of {@code annotationClass}.
   */
  <T extends Annotation> T getAnnotation(Class<T> annotationClass);

  /**
   * Returns a class of a value to be injected.
   */
  Class<?> getType();

  /**
   * Returns annotations
   */
  Iterable<Annotation> getAnnotations();

  /**
   * Returns an annotation object whose annotation type is annotated with {@code RequestInjection}.
   */
  <A extends Annotation> A getPrimaryAnnotation();

  /**
   * A base class of a {@code InjectionRequest}. Implementations of this interface is
   * created by {@code Factory}.
   *
   * @see Factory
   */
  abstract class Base implements InjectionRequest {
    private final Iterable<Annotation> annotations;

    /**
     * Creates an object of this class.
     *
     * @param annotations annotation objects.
     */
    protected Base(Annotation[] annotations) {
      this.annotations = Arrays.asList(annotations);
      int numFound;
      Preconditions.checkState(1 == (numFound = size(filter(
          this.annotations,
          new Predicate<Annotation>() {
            @Override
            public boolean apply(Annotation annotation) {
              return annotation.annotationType().isAnnotationPresent(RequestInjection.class);
            }
          }))),
          "There must be 1 and only 1 annotation annotated with '%s' (%s found)",
          RequestInjection.class.getSimpleName(),
          numFound
      );
    }

    /**
     * There should be one and only one such annotation in {@code annotations} of
     * this object.
     * Because {@code annotations} should be validated on instantiation.
     */
    @Override
    public <A extends Annotation> A getPrimaryAnnotation() {
      //noinspection unchecked
      return (A) find(this.annotations, new Predicate<Annotation>() {
        @Override
        public boolean apply(Annotation annotation) {
          return annotation.annotationType().isAnnotationPresent(RequestInjection.class);
        }
      });
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass) {
      //noinspection unchecked
      return !isEmpty(filter(this.annotations, new Predicate<Annotation>() {
        @Override
        public boolean apply(Annotation annotation) {
          return annotationClass.isInstance(annotation);
        }
      }));
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
      //noinspection unchecked
      return isAnnotationPresent(annotationClass)
          ? (T) Iterables.find(this.annotations,
          new Predicate<Annotation>() {
            @Override
            public boolean apply(Annotation annotation) {
              return annotationClass.isInstance(annotation);
            }
          })
          : null;
    }

    @Override
    public Iterable<Annotation> getAnnotations() {
      return this.annotations;
    }

    @Override
    public boolean equals(Object anotherObject) {
      if (!(anotherObject instanceof InjectionRequest))
        return false;
      InjectionRequest another = (InjectionRequest) anotherObject;
      return this.getType().equals(another.getType())
          && Iterables.elementsEqual(this.annotations, another.getAnnotations());
    }

    @Override
    public int hashCode() {
      return this.getType().hashCode();
    }

  }

  enum Factory {
    ;

    static InjectionRequest createFromField(final Field field) {
      Preconditions.checkNotNull(field);
      try {
        return new InjectionRequest.Base(field.getAnnotations()) {
          @Override
          public Class<?> getType() {
            return field.getType();
          }
        };
      } catch (RuntimeException e) {
        throw new RuntimeException(composeErrorMessageWhenCreationFromFieldFail(field, e), e);
      }
    }

    private static String composeErrorMessageWhenCreationFromFieldFail(Field field, RuntimeException e) {
      return format(
          "Failed to create an injection request for field: '%s' in '%s': %s",
          field.getName(),
          field.getDeclaringClass().getCanonicalName(),
          e.getMessage());
    }

    static InjectionRequest createFromConstructorParameter(Constructor<?> constructor, int index) {
      try {
        return createFromConstructorParameter(
            constructor.getDeclaringClass(),
            constructor.getParameterAnnotations()[index]
        );
      } catch (RuntimeException e) {
        throw new RuntimeException(composeErrorMessageWhenCreationFromConstructorParameterFail(constructor, index, e), e);
      }
    }

    private static InjectionRequest createFromConstructorParameter(final Class parameterType, Annotation[] annotations) {
      return new InjectionRequest.Base(annotations) {
        @Override
        public Class<?> getType() {
          return parameterType;
        }
      };
    }

    private static String composeErrorMessageWhenCreationFromConstructorParameterFail(Constructor<?> constructor, int index, RuntimeException e) {
      return format(
          "Failed to create an injection request for constructor (annotated with '%s') parameter %s in %s: %s",
          UseForInjection.class.getSimpleName(),
          index,
          constructor.getDeclaringClass().getCanonicalName(),
          e.getMessage()
      );
    }
  }
}
