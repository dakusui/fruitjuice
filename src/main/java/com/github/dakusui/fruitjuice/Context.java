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
     * Generally called by FruitJuice's framework
     */
    Builder add(InjectionPoint injectionPoint);

    /**
     * Builds a {@code Context} object.
     */
    Context build();

    abstract class Base implements Builder {
      Map<InjectionRequest, Object> registry = new HashMap<>();

      @Override
      public Context.Builder add(InjectionPoint injectionPoint) {
        registry.put(
            injectionPoint.getRequest(),
            create(injectionPoint.getRequest())
        );
        return this;
      }

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

      protected abstract Object create(InjectionRequest request);
    }
  }
}
