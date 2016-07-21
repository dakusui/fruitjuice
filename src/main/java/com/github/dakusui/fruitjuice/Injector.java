package com.github.dakusui.fruitjuice;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * The injector of the FruitJuice framework.
 */
public interface Injector {
  /**
   * Returns the appropriate instance for the given injection type, {@code targetClass}.
   *
   * @param <T>         A class of the returned object.
   * @param targetClass A class from which returned object is created.
   */
  <T> T getInstance(Class<T> targetClass);

  /**
   * A simple implementation of {@link Injector} interface.
   */
  class Impl implements Injector {
    private final Context.Builder builder;

    /**
     * Creates an object of this class.
     *
     * @param builder A builder object of {@link Context.Builder}.
     */
    public Impl(Context.Builder builder) {
      this.builder = Preconditions.checkNotNull(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getInstance(Class<T> targetClass) {
      Preconditions.checkNotNull(targetClass);
      Constructor<T> constructor = getAppropriateConstructorFrom(targetClass);
      Iterable<InjectionPoint> constructorInjectionPoints = getConstructorInjectionPoints(constructor);
      Iterable<InjectionPoint> fieldInjectionPoints = getFieldInjectionPoints(targetClass);
      for (InjectionPoint each : Iterables.concat(constructorInjectionPoints, fieldInjectionPoints)) {
        this.builder.add(each);
      }
      Context context = this.builder.build();
      T ret = createObject(context, constructor, constructorInjectionPoints);
      injectFields(context, ret, fieldInjectionPoints);
      return ret;
    }

    private <T> T createObject(final Context context, final Constructor<T> constructor, Iterable<InjectionPoint> constructorInjectionPoints) {
      try {
        return constructor.newInstance(toArray(
            transform(
                filter(constructorInjectionPoints,
                    new Predicate<InjectionPoint>() {
                      @Override
                      public boolean apply(InjectionPoint injectionPoint) {
                        return constructor.equals(injectionPoint.getTargetElement().asConstructorParameter().getDeclaringConstructor());
                      }
                    }
                ),
                new Function<InjectionPoint, Object>() {
                  @Override
                  public Object apply(InjectionPoint injectionPoint) {
                    return getInstanceFor(injectionPoint, context);
                  }
                }
            ),
            Object.class
            )
        );
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw Throwables.propagate(e);
      }
    }

    private <T> Constructor<T> getAppropriateConstructorFrom(Class<T> targetClass) {
      Preconditions.checkNotNull(targetClass);
      //noinspection unchecked
      Iterable<Constructor<T>> constructors = filter(
          asList((Constructor<T>[]) targetClass.getConstructors()),
          new Predicate<Constructor<T>>() {
            @Override
            public boolean apply(Constructor<T> constructor) {
              return constructor.isAnnotationPresent(Inject.class);
            }
          }
      );
      if (size(constructors) == 1) {
        return Iterables.get(constructors, 0);
      }
      if (size(constructors) == 0) {
        try {
          return targetClass.getConstructor();
        } catch (NoSuchMethodException e) {
          throw Throwables.propagate(e);
        }
      }
      throw new RuntimeException(format(
          "More than one constructors annotated with '%s' are found in '%s'",
          Inject.class.getSimpleName(),
          targetClass.getCanonicalName()
      ));
    }

    private <T> void injectFields(Context context, T target, Iterable<InjectionPoint> injectionPoints) {
      for (InjectionPoint eachInjectionPoint : injectionPoints) {
        InjectionPoint.TargetElement eachTargetElement = eachInjectionPoint.getTargetElement();
        if (InjectionPoint.Type.FIELD.equals(eachInjectionPoint.getType())) {
          Field f = eachTargetElement.asField();
          if (f.getDeclaringClass().isInstance(target)) {
            boolean accessible = f.isAccessible();
            f.setAccessible(true);
            try {
              f.set(target, getInstanceFor(eachInjectionPoint, context));
            } catch (IllegalAccessException e) {
              throw Throwables.propagate(e);
            } finally {
              f.setAccessible(accessible);
            }

          }
        }
      }
    }

    private Object getInstanceFor(InjectionPoint injectionPoint, Context context) {
      return context.lookup(injectionPoint.getRequest());
    }

    private <T> Iterable<InjectionPoint> getFieldInjectionPoints(Class<T> targetClass) {
      return Iterables.transform(
          Utils.getTargetFieldsFromClass(targetClass),
          new Function<Field, InjectionPoint>() {
            @Override
            public InjectionPoint apply(Field each) {
              return InjectionPoint.Factory.createFromField(each);
            }
          }
      );
    }

    private <T> Iterable<InjectionPoint> getConstructorInjectionPoints(final Constructor<T> targetConstructor) {
      return InjectionPoint.Factory.createInjectionPointsFromConstructor(targetConstructor);
    }

  }

  /**
   * A utility class for {@code Injector} mechanism.
   */
  enum Utils {
    ;

    /**
     * Returns all the "target" fields in {@code targetClass}, which are annotated
     * with {@link Inject}.
     *
     * @param targetClass A class from which returned fields are collected.
     */
    public static Iterable<Field> getTargetFieldsFromClass(Class<?> targetClass) {
      return filter(
          getAllFields(checkNotNull(targetClass)),
          new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
              return field.isAnnotationPresent(Inject.class);
            }
          }
      );
    }


    /**
     * Returns all the fields in {@code targetClass} defined directly in it and its
     * all the super-classes.
     *
     * @param targetClass A class from which returned fields are collected.
     */
    public static Iterable<Field> getAllFields(Class<?> targetClass) {
      if (targetClass == null)
        return Collections.emptyList();
      return Iterables.concat(getAllFields(targetClass.getSuperclass()), asList(targetClass.getDeclaredFields()));
    }
  }
}
