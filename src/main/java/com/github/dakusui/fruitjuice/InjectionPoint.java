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

  /**
   * Injection point type. The constants of this enumerated type describe the
   * various types for injection points.
   */
  enum Type {
    /**
     * Injection points are for constructor parameters.
     */
    CONSTRUCTOR_PARAMETER,
    /**
     * Injection points are for fields.
     */
    FIELD
  }

  /**
   * An interface that represents an element an {@code InjectionPoint} targets.
   */
  interface TargetElement {

    /**
     * Returns a {@code ConstructorParameter} which this object represents.
     *
     * If the type of this object, returned by {@code getType()}, is not {@code CONSTRUCTOR_PARAMETER},
     * an {@code UnsupportedOperationException} will be thrown.
     */
    ConstructorParameter asConstructorParameter();

    /**
     * Returns a {@code ConstructorParameter} which this object represents.
     *
     * If the type of this object, returned by {@code getType()}, is not {@code FIELD},
     * an {@code UnsupportedOperationException} will be thrown.
     */
    Field asField();

    /**
     * Returns a type of this object.
     *
     * @see Type
     */
    Type getType();
  }

  /**
   * An interface that models a parameter of a constructor.
   *
   * This interface is introduced because Java SE7 or ealier doesn't have {@code Parameter}
   * interface.
   */
  interface ConstructorParameter {
    /**
     * Returns a type of this parameter.
     */
    Class<?> getType();

    /**
     * Return the {@code Constructor} which declares this parameter.
     */
    Constructor getDeclaringConstructor();

    /**
     * Returns an index which indicates the location of the this object in all the
     * parameters of the declaring constructor.
     */
    int getIndex();

    /**
     * Get the modifier flags for this the parameter represented by
     * this {@code Parameter} object.
     */
    int getModifiers();

  }


  /**
   * A factory class that creates {@code InjectionPoint} objects.
   */
  enum Factory {
    ;

    /**
     * Creates an {@code InjectionPoint} object from a field of a class.
     *
     * @param targetField A field for which an {@code InjectionPoint} is created.
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

            @Override
            public Type getType() {
              return Type.FIELD;
            }
          };
        }
      };
    }

    /**
     * Creates {@code InjectionPoint} objects from a constructor of a class.
     *
     * @param targetConstructor A constructor for which {@code InjectionPoint}s are created.
     */
    public static Iterable<InjectionPoint> createInjectionPointsFromConstructor(final Constructor targetConstructor) {
      return new AbstractList<InjectionPoint>() {
        @Override
        public int size() {
          return targetConstructor.getParameterTypes().length;
        }

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
                    public int getIndex() {
                      return index;
                    }

                    @Override
                    public int getModifiers() {
                      return targetConstructor.getModifiers();
                    }

                    /**
                     * Compares based on the executable and the index.
                     *
                     * @param obj The object to compare.
                     * @return Whether or not this is equal to the argument.
                     */
                    public boolean equals(Object obj) {
                      if(obj instanceof ConstructorParameter) {
                        ConstructorParameter other = (ConstructorParameter)obj;
                        return (other.getDeclaringConstructor().equals(targetConstructor) &&
                            other.getIndex() == index);
                      }
                      return false;
                    }

                    /**
                     * Returns a hash code based on the executable's hash code and the
                     * index.
                     *
                     * @return A hash code based on the executable's hash code.
                     */
                    public int hashCode() {
                      return targetConstructor.hashCode() ^ index;
                    }
                  };
                }

                @Override
                public Field asField() {
                  throw new UnsupportedOperationException("The target of this object is a field and not a constructor parameter");
                }

                @Override
                public Type getType() {
                  return Type.CONSTRUCTOR_PARAMETER;
                }
              };
            }
          };
        }
      };
    }
  }
}
