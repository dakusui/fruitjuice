package com.github.dakusui.fruitjuice;

/**
 * An interface that defines method to create a new object to be injected to an
 * {@code InjectionPoint}.
 *
 * An implementation of this class must guarantee existence of non-parameter public
 * constructor.
 */
public interface ValueFactory {
  Object create(InjectionRequest request);
}
