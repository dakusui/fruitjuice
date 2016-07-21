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
  TargetElement getTargetElement();

  Type getType();

  enum Type {
    CONSTRUCTOR_PARAMETER,
    FIELD;
  }

  interface TargetElement {
    ConstructorParameter asConstructorParameter();

    Field asField();
  }

  interface ConstructorParameter {
    Class<?> getType();

    Constructor getDeclaringConstructor();

    int getModifiers();
  }


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
        public TargetElement getTargetElement() {
          return new TargetElement() {
            @Override
            public ConstructorParameter asConstructorParameter() {
              throw new UnsupportedOperationException("The target of this object is a field and not a constructor parameter");
            }

            @Override
            public Field asField() {
              return targetField;
            }
          };
        }

        @Override
        public Type getType() {
          return Type.FIELD;
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
            public TargetElement getTargetElement() {
              return new TargetElement() {
                @Override
                public ConstructorParameter asConstructorParameter() {
                  return new ConstructorParameter() {
                    @Override
                    public Class<?> getType() {
                      return targetConstructor.getParameterTypes()[index];
                    }

                    @Override
                    public Constructor getDeclaringConstructor() {
                      return targetConstructor;
                    }

                    @Override
                    public int getModifiers() {
                      return targetConstructor.getModifiers();
                    }
                  };
                }

                @Override
                public Field asField() {
                  throw new UnsupportedOperationException("The target of this object is a field and not a constructor parameter");
                }
              };
            }

            @Override
            public Type getType() {
              return Type.CONSTRUCTOR_PARAMETER;
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
