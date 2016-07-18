package com.github.dakusui.fruitjuice;

import com.google.common.base.Throwables;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.AbstractList;

public interface InjectionPoint {
  InjectionRequest getRequest();

  Object getOwner();

  ValueFactory getValueFactory();

  enum Factory {
    ;

    static InjectionPoint createFromField(final Field targetField) {
      return new InjectionPoint() {
        @Override
        public InjectionRequest getRequest() {
          return InjectionRequest.Factory.createFromField(targetField);
        }

        @Override
        public Object getOwner() {
          return targetField;
        }

        @Override
        public ValueFactory getValueFactory() {
          return createValueFactory(this);
        }
      };
    }

    public static Iterable<InjectionPoint> createInjectionPointsFromConstructor(final Constructor targetConstructor) {
      return new AbstractList<InjectionPoint>() {
        @Override
        public InjectionPoint get(final int index) {
          return new InjectionPoint() {
            @Override
            public InjectionRequest getRequest() {
              return InjectionRequest.Factory.createFromParameter(
                  targetConstructor.getParameterTypes()[index],
                  targetConstructor.getParameterAnnotations()[index],
                  String.format("p%d", index)
              );
            }

            @Override
            public Object getOwner() {
              return targetConstructor;
            }

            @Override
            public ValueFactory getValueFactory() {
              return createValueFactory(this);
            }
          };
        }

        @Override
        public int size() {
          return targetConstructor.getParameterTypes().length;
        }
      };

    }

    private static ValueFactory createValueFactory(InjectionPoint injectionPoint) {
      try {
        //noinspection ConstantConditions
        return (ValueFactory) getAnnotatedAnnotation(injectionPoint, RequestInjection.class).value().newInstance().create(injectionPoint.getRequest());
      } catch (InstantiationException | IllegalAccessException e) {
        throw Throwables.propagate(e);
      }
    }

    private static <T extends Annotation> T getAnnotatedAnnotation(InjectionPoint injectionPoint, Class<T> annotation) {
      for (Annotation each : injectionPoint.getRequest().getAnnotations()) {
        if (each.annotationType().isAnnotationPresent(annotation)) {
          return each.annotationType().getAnnotation(annotation);
        }
      }
      return null;
    }

  }
}
