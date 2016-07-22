package com.github.dakusui.fruitjuice;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static java.lang.String.format;

/**
 * An interface that represents a request that defines what object should be injected to
 * an injection point to which this object belongs.
 */
public interface InjectionRequest {
  /**
   * Returns true if an annotation for the specified type
   * is <em>present</em> on this element, else false.  This method
   * is designed primarily for convenient access to marker annotations.
   * <p>
   * <p>The truth value returned by this method is equivalent to:
   * {@code getAnnotation(annotationClass) != null}
   * <p>
   * <p>The body of the default method is specified to be the code
   * above.
   *
   * @param annotationClass the Class object corresponding to the
   *                        annotation type
   * @return true if an annotation for the specified annotation
   * type is present on this element, else false
   */
  boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

  /**
   * Returns this element's annotation for the specified type if
   * such an annotation is <em>present</em>, else null.
   *
   * @param <T>             the type of the annotation to query for and return if present
   * @param annotationClass the Class object corresponding to the
   *                        annotation type
   */
  <T extends Annotation> T getAnnotation(Class<T> annotationClass);

  /**
   * Returns a class of a value to be injected.
   */
  Class<?> getType();

  /**
   * Returns annotations that are <em>present</em> on this element.
   *
   * If there are no annotations <em>present</em> on this element, the return
   * value is an array of length 0.
   *
   * The caller of this method is free to modify the returned array; it will
   * have no effect on the arrays returned to other callers.
   */
  Iterable<Annotation> getAnnotations();

  /**
   * A base class of a {@code InjectionRequest}. Implementations of this interface is
   * created by {@code Factory}.
   *
   * @see Factory
   */
  abstract class Base implements InjectionRequest {
    final Iterable<Annotation> annotations;

    /**
     * Creates an object of this class.
     *
     * @param annotations annotation objects.
     */
    protected Base(Annotation[] annotations) {
      this.annotations = Arrays.asList(annotations);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

  /**
   * A factory class to create {@code InjectionRequest} objects.
   */
  enum Factory {
    ;

    /**
     * returns a new {@code InjectionRequest} object from a given {@code Field}.
     *
     * @param field A field from which {@code InjectionRequest} is created.
     */
    static InjectionRequest createFromField(final Field field) {
      Preconditions.checkNotNull(field);
      return new InjectionRequest.Base(field.getAnnotations()) {
        @Override
        public Class<?> getType() {
          return field.getType();
        }

        @Override
        public String toString() {
          return format(
              "%s:%s#%s %s",
              getType().getSimpleName(),
              field.getDeclaringClass().getSimpleName(),
              field.getName(),
              Iterables.toString(this.annotations)
          );
        }
      };
    }

    /**
     * returns a new {@code InjectionRequest} objects from a given {@code constructor}'s
     * parameter specified by {@code index}.
     *
     * @param constructor A constructor from which {@code InjectionRequest} is created.
     * @param index An index that specify a parameter of the constructor.
     */
    static InjectionRequest createFromConstructorParameter(final Constructor<?> constructor, final int index) {
      return new InjectionRequest.Base(constructor.getParameterAnnotations()[index]) {
        @Override
        public Class<?> getType() {
          return constructor.getParameterTypes()[index];
        }

        @Override
        public String toString() {
          return format(
              "%s:%s#<<init>>[%s] %s",
              getType().getSimpleName(),
              constructor.getDeclaringClass().getSimpleName(),
              index,
              Iterables.toString(this.annotations)
          );
        }
      };
    }
  }
}
