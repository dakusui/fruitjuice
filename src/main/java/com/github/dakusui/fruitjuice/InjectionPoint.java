package com.github.dakusui.fruitjuice;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.AbstractList;

/**
 * An interface that represents a point at which a dependency injection by the
 * FruitJuice framework happens.
 *
 * @see Factory
 */
public interface InjectionPoint {
  /**
   * Returns an {@code InjectionRequest} object.
   *
   * @see InjectionRequest
   */
  InjectionRequest getRequest();

  /**
   * Returns an owner from which this object is created.
   */
  Object getOwner();

  /**
   * A factory class that creates {@code InjectionPoint} objects.
   */
  enum Factory {
    ;

    /**
     * Creates an {@code InjectionPoint} object from a field of a class.
     */
    public static InjectionPoint createFromField(final Field targetField) {
      return new InjectionPoint() {
        @Override
        public InjectionRequest getRequest() {
          return InjectionRequest.Factory.createFromField(targetField);
        }

        @Override
        public Object getOwner() {
          return targetField;
        }
      };
    }

    /**
     * Creates {@code InjectionPoint} objects from a constructor of a class.
     */
    public static Iterable<InjectionPoint> createInjectionPointsFromConstructor(final Constructor targetConstructor) {
      return new AbstractList<InjectionPoint>() {
        @Override
        public InjectionPoint get(final int index) {
          return new InjectionPoint() {
            @Override
            public InjectionRequest getRequest() {
              return InjectionRequest.Factory.createFromConstructorParameter(
                  targetConstructor,
                  index);
            }

            @Override
            public Object getOwner() {
              return targetConstructor;
            }

          };
        }

        @Override
        public int size() {
          return targetConstructor.getParameterTypes().length;
        }
      };
    }
  }
}
