package com.github.dakusui.fruitjuice;

import java.util.HashMap;
import java.util.Map;

/**
 * An interface that represents an object context.
 * An object context holds objects to be injected to a target object as constructor arguments,
 * field values, etc.
 *
 * @see Builder
 */
public interface Context {
  /**
   * Returns a requested object that matches given {@code request}.
   * The returned object will be injected to the injection points to which the
   * {@code request} object belongs.
   */
  <T> T lookup(InjectionRequest request);

  /**
   * A builder of a {@code Context} object.
   * <p>
   * Values created by {@code ValueFactory}'s will be {@code add}ed to this object
   * and then {@code build()} method will be called by FruitJuice's framework.
   */
  interface Builder {
    /**
     * Adds given {@code injectionPoint} to this object.
     * Generally called by FruitJuice's framework.
     */
    Builder add(InjectionPoint injectionPoint);

    /**
     * Builds a {@code Context} object.
     */
    Context build();

    /**
     * A generic base class of {@link Builder}.
     */
    abstract class Base implements Builder {
      private Map<InjectionRequest, Object> registry = new HashMap<>();

      /**
       * {@inheritDoc}
       */
      @Override
      public Context.Builder add(InjectionPoint injectionPoint) {
        registry.put(
            injectionPoint.getRequest(),
            create(injectionPoint.getRequest())
        );
        return this;
      }

      /**
       * {@inheritDoc}
       * <p>
       * The implementation of {@code Context} returned by this method gives the object
       * created by the method call to {@code Context.Builder#add} with {@code request}
       * object held by {@code InjectionPoint} equal to the one given to {@code Context#lookup}
       * method.
       *
       * @see InjectionRequest#equals(Object)
       */
      @Override
      public Context build() {
        return new Context() {
          @Override
          public <V> V lookup(InjectionRequest request) {
            //noinspection unchecked
            return (V) registry.get(request);
          }
        };
      }

      /**
       * Creates and returns a value to be injected the injection points which are
       * equal to given {@code request}.
       *
       * @param request A request for which the returned value should be created.
       */
      protected abstract Object create(InjectionRequest request);
    }
  }
}
